package life.qbic.portal.offermanager.components.offer.overview

import groovy.util.logging.Log4j2
import life.qbic.business.RefactorConverter
import life.qbic.business.offers.content.CreateOfferContentInput
import life.qbic.business.offers.fetch.FetchOfferInput
import life.qbic.datamodel.dtos.business.OfferId

/**
 * Controller class adapter from view information into use case input interface
 *
 * This class translates the information that was received from the OfferOverView into method calls to the use case
 *
 * @since: 1.0.0
 *
 */
@Log4j2
class OfferOverviewController {

    private final FetchOfferInput input

    private final CreateOfferContentInput offerContentInput

    private static final RefactorConverter refactorConverter = new RefactorConverter()

    OfferOverviewController(FetchOfferInput input, CreateOfferContentInput offerContentInput) {
        this.input = input
        this.offerContentInput = offerContentInput
    }

    /**
     * This method calls the Fetch Offer use case with the provided OfferId
     *
     * @param offerId The OfferId of the Offer to be retrieved
     */
    void fetchOffer(OfferId offerId) {
        this.input.fetchOffer(refactorConverter.toOfferId(offerId))
        this.offerContentInput.createOfferContent(refactorConverter.toOfferId(offerId))
    }
}
