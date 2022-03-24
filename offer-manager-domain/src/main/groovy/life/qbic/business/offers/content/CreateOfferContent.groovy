package life.qbic.business.offers.content

import life.qbic.business.RefactorConverter
import life.qbic.business.offers.OfferContent
import life.qbic.business.offers.OfferV2
import life.qbic.business.offers.fetch.FetchOffer
import life.qbic.business.offers.fetch.FetchOfferDataSource
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
class CreateOfferContent implements CreateOfferContentInput, FetchOfferOutput {
    private final CreateOfferContentOutput output
    private final FetchOffer fetchOffer //fixme remove concrete implementation
    private final static RefactorConverter refactorConverter = new RefactorConverter()

    CreateOfferContent(CreateOfferContentOutput output, FetchOfferDataSource fetchOfferDataSource) {
        this.output = output
        this.fetchOffer = new FetchOffer(fetchOfferDataSource, this)
    }

    @Override
    void createOfferContent(OfferId offerId) {
        fetchOffer.fetchOffer(offerId)
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
        //collect the content for the offerpdf
        OfferContent offerContent = OfferContent.from(fetchedOffer)
        output.createdOfferContent(offerContent)
    }

    @Override
    void failNotification(String notification) {
        output.failNotification(notification)
    }
}
