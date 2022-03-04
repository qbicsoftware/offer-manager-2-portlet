package life.qbic.portal.offermanager.components.offer.overview

import groovy.beans.Bindable
import life.qbic.business.offers.OfferContent
import life.qbic.datamodel.dtos.business.Offer
import life.qbic.portal.offermanager.OfferToPDFConverter
import life.qbic.portal.offermanager.communication.EventEmitter
import life.qbic.portal.offermanager.components.AppViewModel
import life.qbic.portal.offermanager.dataresources.ResourcesService
import life.qbic.portal.offermanager.dataresources.offers.OfferOverview

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
class OfferOverviewModel {

    /**
     * A list with all available offer overviews
     */
    ObservableList offerOverviewList

    Optional<Offer> offer = Optional.empty()

    Optional<OfferContent> offerContent = Optional.empty()

    private final ResourcesService<OfferOverview> service

    private final AppViewModel viewModel

    @Bindable boolean displaySpinner

    EventEmitter offerEventEmitter

    OfferOverviewModel(ResourcesService<OfferOverview> service,
                       AppViewModel viewModel,
                       EventEmitter<Offer> offerEventEmitter) {
        this.service = service
        this.offerOverviewList = new ObservableList(new ArrayList(service.iterator().toList()))
        this.offerContent = Optional.empty()
        this.viewModel = viewModel
        this.displaySpinner = false
        this.offerEventEmitter = offerEventEmitter
        subscribeToOverviewService()
    }

    private void subscribeToOverviewService() {
        service.subscribe({
            offerOverviewList.clear()
            Iterator<OfferOverview> iterator = service.iterator()
            offerOverviewList.addAll(iterator)
        })
    }

    Offer getSelectedOffer() {
        if(offer.isPresent()) {
            return offer.get()
        } else {
            throw new RuntimeException("No offer is currently selected.")
        }
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
        }).orElseThrow({new RuntimeException("The offer content seems to be empty, nothing to " +
                "convert.")})
    }

}
