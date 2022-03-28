package life.qbic.business.offers.content

import life.qbic.business.offers.identifier.OfferId

/**
 * <h1>Input for the {@link CreateOfferContent}</h1>
 *
 * <p>Defines which methods can be called to trigger the use case</p>
 *
 * @since 1.1.0
 *
*/
interface CreateOfferContentInput {

    void createOfferContent(OfferId offerId)
}
