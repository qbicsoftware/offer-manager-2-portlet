package life.qbic.portal.qoffer2.web.viewmodel

import life.qbic.datamodel.dtos.business.Offer
import life.qbic.portal.qoffer2.offers.OfferDbConnector
import life.qbic.portal.qoffer2.offers.OfferToPDFConverter
import life.qbic.portal.qoffer2.services.OverviewService
import life.qbic.portal.qoffer2.shared.OfferOverview

/**
 * Model for the offer overview view.
 *
 * Holds central properties for the offer overview view,
 * such as a list of available offer overviews and
 * a property for a selected offer overview.
 *
 * The model holds on public method to retrieve the
 * selected offer in PDF.
 *
 * @since 1.0.0
 */
class OfferOverviewModel {

    /**
     * A list with all available offer overviews
     */
    List<OfferOverview> offerOverviewList
    /**
     * The currently selected offer overview
     */
    Optional<OfferOverview> selectedOffer

    private final OverviewService service

    private final OfferDbConnector connector

    private final ViewModel viewModel

    OfferOverviewModel(OverviewService service,
                       OfferDbConnector connector,
                       ViewModel viewModel) {
        this.service = service
        this.connector = connector
        this.offerOverviewList = service.getOfferOverviewList()
        this.selectedOffer = Optional.empty()
        this.viewModel = viewModel
    }

    /**
     * Acquire the current selected offer in PDF
     * @return The offer PDF
     * @throws RuntimeException if the offer cannot be converted to PDF
     */
    InputStream getOfferAsPdf() throws RuntimeException {
        Optional<Offer> offer = loadOfferInfo()
        offer.map({
            def converter = new OfferToPDFConverter(it)
            return converter.getOfferAsPdf()
        }).orElseThrow(RuntimeException::new)
    }

    private Optional<Offer> loadOfferInfo() {
        Optional<Offer> offer = selectedOffer
                .map({ connector.getOffer(it.projectTitle) })
                .orElse(Optional.empty())
        return offer
    }
}
