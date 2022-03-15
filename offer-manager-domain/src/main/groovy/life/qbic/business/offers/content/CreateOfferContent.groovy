package life.qbic.business.offers.content


import life.qbic.business.offers.OfferV2
import life.qbic.business.offers.fetch.FetchOfferOutput
import life.qbic.business.offers.identifier.OfferId

/**
 * <h1>Creates the content for an offer export</h1>
 *
 * <p>This use case aggregates all information that needs to be present in an offer. This use case should be called to forward the offer information
 * </p>
 *
 * @since 1.1.0
 *
*/
class CreateOfferContent implements CreateOfferContentInput, FetchOfferOutput{
    //TODO replace use case with fetch offer

    @Override
    void createOfferContent(OfferId offerId) {

    }

    /**
     * Returns the offer associated with the provided OfferId
     * the fetched Offer
     *
     * @param fetchedOffer {@link life.qbic.datamodel.dtos.business.Offer}
     * @since 1.0.0
     */
    @Override
    void fetchedOffer(OfferV2 fetchedOffer) {

    }

    /**
     * Sends failure notifications that have been
     * recorded during the use case.
     * @param notification containing a failure message
     * @since 1.0.0
     */
    @Override
    void failNotification(String notification) {

    }
}
