package life.qbic.portal.offermanager.components.offer.overview

import groovy.beans.Bindable
import life.qbic.business.offers.OfferContent
import life.qbic.datamodel.dtos.business.Offer
import life.qbic.datamodel.dtos.business.OfferId
import life.qbic.portal.offermanager.OfferToPDFConverter
import life.qbic.portal.offermanager.communication.EventEmitter
import life.qbic.portal.offermanager.components.AppViewModel
import life.qbic.portal.offermanager.dataresources.ResourcesService
import life.qbic.portal.offermanager.dataresources.offers.OfferOverview

import java.util.function.Predicate
import java.util.stream.Collectors

/**
 * Model for the offer overview view.
 *
 * Holds central properties for the offer overview view,
 * such as a list of available offer overviews and
 * a property for a selected offer overview.
 *
 * The model provides a public method to retrieve the
 * selected offer in PDF.
 *
 * @since 1.0.0
 */
class OfferOverviewModel extends Observable {

    /**
     * A list with all available offer overviews
     */
    ObservableList latestOfferOverviewList

    List<OfferOverview> offerVersionsForSelected

    List<OfferOverview> allOfferVersions

    OfferOverview selectedOverview

    Optional<Offer> offer = Optional.empty()

    Optional<OfferContent> offerContent = Optional.empty()

    private final ResourcesService<OfferOverview> service

    private final AppViewModel viewModel

    @Bindable
    boolean displaySpinner

    EventEmitter offerEventEmitter

    OfferOverviewModel(ResourcesService<OfferOverview> service,
                       AppViewModel viewModel,
                       EventEmitter<Offer> offerEventEmitter) {
        this.service = service
        this.allOfferVersions = new ArrayList<>(service.iterator().toList())
        this.latestOfferOverviewList = new ObservableList(filterForLatest(allOfferVersions))
        this.offerVersionsForSelected = new ArrayList<>()
        this.offerContent = Optional.empty()
        this.viewModel = viewModel
        this.displaySpinner = false
        this.offerEventEmitter = offerEventEmitter
        subscribeToOverviewService()
    }

    private void subscribeToOverviewService() {
        service.subscribe({
            allOfferVersions.clear()
            allOfferVersions.addAll(service.iterator())

            latestOfferOverviewList.clear()
            List<OfferOverview> filteredList = filterForLatest(allOfferVersions)
            latestOfferOverviewList.addAll(filteredList)

            this.offerVersionsForSelected.clear()
            if (this.selectedOverview != null) {
                this.offerVersionsForSelected.addAll(filterOfferVersionID(this.selectedOverview, allOfferVersions))
            }
            this.setChanged()
            this.notifyObservers()
        })
    }

    Offer getSelectedOffer() {
        if (offer.isPresent()) {
            return offer.get()
        } else {
            throw new RuntimeException("No offer is currently selected.")
        }
    }

    void setSelectedOverview(OfferOverview offerOverview) {
        this.selectedOverview = offerOverview
        this.offerVersionsForSelected.clear()
        this.offerVersionsForSelected.addAll(filterOfferVersionID(this.selectedOverview, allOfferVersions))
        this.setChanged()
        this.notifyObservers()
    }

    private List<OfferOverview> filterOfferVersionID(OfferOverview offerOverview, List<OfferOverview> allOverviews) {
        Predicate<OfferOverview> belongsToSameOffer = new IsVersionOfOffer(offerOverview)
        return allOverviews.stream()
                .filter(belongsToSameOffer)
                .sorted((o1, o2) ->
                        Integer.parseInt(o2.getOfferId().getVersion()) - Integer.parseInt(o1.getOfferId().getVersion()))
                .collect(Collectors.toList())
    }

    /**
     * Acquire the current selected offer in PDF
     * @return The offer PDF
     * @throws RuntimeException if the offer cannot be converted to PDF
     */
    InputStream getOfferAsPdf() throws RuntimeException {
        offerContent.map({
            OfferToPDFConverter converter = new OfferToPDFConverter(it)
            return converter.getOfferAsPdf()
        }).orElseThrow({
            new RuntimeException("The offer content seems to be empty, nothing to " +
                    "convert.")
        })
    }

    /**
     * Filters an OfferOverview list to return only the overview of latest offer version within a offer history.
     *
     * Latest means the one with the highest version number.
     *
     * @param offerOverviewList a list of offer versions to filter
     * @return the filtered list
     */
    private List<OfferOverview> filterForLatest(List<OfferOverview> offerOverviewList) {
        List<OfferOverview> latestOnly = new ArrayList<>()
        for (OfferOverview overview : offerOverviewList) {
            Predicate<OfferOverview> isVersionOf = new IsVersionOfOffer(overview)
            if (!latestOnly.stream().filter(isVersionOf).findAny().isPresent()) {
                // We filter for overviews that are part of the same project target ...
                List<OfferOverview> sameProjectOnly = offerOverviewList.stream().filter(isVersionOf).collect(Collectors.toList())
                // ... search for the latest within the version history ...
                OfferOverview latest = findLatest(overview, sameProjectOnly)
                // ... and add the latest one to our filtered list
                latestOnly.add(latest)
            }
        }
        return latestOnly
    }

    /**
     * Searches a list of offer overview and returns an overview with the highest version number.
     *
     * Note: Only the version number in the referenced offer id is taken into account!
     *
     * @param offerOverview
     * @param overviewList
     * @return
     */
    private OfferOverview findLatest(OfferOverview offerOverview, List<OfferOverview> overviewList) {
        OfferOverview latest = offerOverview
        for (OfferOverview currentOverview : overviewList) {
            Predicate<OfferId> isNewer = new IsNewerVersion(latest.getOfferId())
            if (isNewer.test(currentOverview.getOfferId())) {
                latest = currentOverview
            }
        }
        return latest
    }

    class IsVersionOfOffer implements Predicate<OfferOverview> {

        private final OfferOverview offerOverview

        IsVersionOfOffer(OfferOverview offerOverview) {
            this.offerOverview = offerOverview
        }

        @Override
        boolean test(OfferOverview anotherOverview) {
            return this.offerOverview.getOfferId().getProjectConservedPart().equals(anotherOverview.getOfferId().getProjectConservedPart())
                    && this.offerOverview.getOfferId().getRandomPart().equals(anotherOverview.getOfferId().getRandomPart())
        }
    }

    class IsNewerVersion implements Predicate<OfferId> {

        private final OfferId offerIdToCompare

        IsNewerVersion(OfferId offerIdToCompare) {
            this.offerIdToCompare = offerIdToCompare
        }

        @Override
        boolean test(OfferId offerId) {
            return Integer.parseInt(offerId.getVersion()) > Integer.parseInt(this.offerIdToCompare.getVersion())
        }
    }

}
