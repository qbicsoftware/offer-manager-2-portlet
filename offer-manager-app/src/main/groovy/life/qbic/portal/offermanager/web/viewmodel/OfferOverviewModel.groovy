package life.qbic.portal.offermanager.web.viewmodel

import groovy.beans.Bindable
import life.qbic.datamodel.dtos.business.Offer
import life.qbic.portal.offermanager.offers.OfferDbConnector
import life.qbic.portal.offermanager.services.OverviewService

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
    List<life.qbic.portal.offermanager.shared.OfferOverview> offerOverviewList
    /**
     * The currently selected offer overview
     */
    Optional<life.qbic.portal.offermanager.shared.OfferOverview> selectedOffer

    private Optional<Offer> offer

    private final OverviewService service

    private final OfferDbConnector connector

    private final ViewModel viewModel

    private boolean downloadButtonActive

    @Bindable boolean displaySpinner

    OfferOverviewModel(OverviewService service,
                       OfferDbConnector connector,
                       ViewModel viewModel) {
        this.service = service
        this.connector = connector
        this.offerOverviewList = service.getOfferOverviewList()
        this.selectedOffer = Optional.empty()
        this.viewModel = viewModel
        this.downloadButtonActive = false
        this.displaySpinner = false
        subscribeToOverviewService()
    }

    private void subscribeToOverviewService() {
        service.updatedOverviewEvent.register({
            offerOverviewList.clear()
            offerOverviewList.addAll(service.getOfferOverviewList())
        })
    }

    void setSelectedOffer(life.qbic.portal.offermanager.shared.OfferOverview selectedOffer) {
        this.selectedOffer = Optional.ofNullable(selectedOffer)
        this.downloadButtonActive = false
        if (this.selectedOffer.isPresent()) {
            this.offer = loadOfferInfo()
        }
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
        offer.map({
            life.qbic.portal.offermanager.offers.OfferToPDFConverter converter = new life.qbic.portal.offermanager.offers.OfferToPDFConverter(it)
            return converter.getOfferAsPdf()
        }).orElseThrow({new RuntimeException("The offer content seems to be empty, nothing to " +
                "convert.")})
    }

    private Optional<Offer> loadOfferInfo() {
        Optional<Offer> offer = selectedOffer
                .map({ connector.getOffer(it.offerId) })
                .orElse(Optional.empty())
        return offer
    }
}
