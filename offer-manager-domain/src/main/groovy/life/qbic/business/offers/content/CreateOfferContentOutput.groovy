package life.qbic.business.offers.content

import life.qbic.business.offers.OfferContent

/**
 * <h1>The output of {@link CreateOfferContent}</h1>
 *
 * <p>This interface is responsible to define what information leaves the use case</p>
 *
 * @since 1.1.0
 *
*/
interface CreateOfferContentOutput {

    void createdOfferContent(OfferContent offerContent)
    void failNotification(String notification)

}