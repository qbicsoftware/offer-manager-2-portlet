package life.qbic.portal.offermanager.components.offer.overview

import groovy.beans.Bindable
import life.qbic.datamodel.dtos.business.Offer
import life.qbic.portal.offermanager.communication.EventEmitter
import life.qbic.portal.offermanager.components.AppViewModel
import life.qbic.portal.offermanager.OfferToPDFConverter
import life.qbic.portal.offermanager.dataresources.offers.OverviewService
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
    /**
     * The currently selected offer overview
     */
    Optional<OfferOverview> selectedOffer

    Optional<Offer> offer

    private final OverviewService service

    private final AppViewModel viewModel

    private boolean downloadButtonActive

    @Bindable boolean displaySpinner

    EventEmitter offerEventEmitter

    OfferOverviewModel(OverviewService service,
                       AppViewModel viewModel,
                       EventEmitter<Offer> offerEventEmitter) {
        this.service = service
        this.offerOverviewList = new ObservableList(new ArrayList(service.iterator().toList()))
        this.selectedOffer = Optional.empty()
        this.viewModel = viewModel
        this.downloadButtonActive = false
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
        this.downloadButtonActive = false
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
        offer.map({
            OfferToPDFConverter converter = new OfferToPDFConverter(it)
            return converter.getOfferAsPdf()
        }).orElseThrow({new RuntimeException("The offer content seems to be empty, nothing to " +
                "convert.")})
    }

}
