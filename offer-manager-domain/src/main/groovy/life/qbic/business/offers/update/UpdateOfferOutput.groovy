package life.qbic.business.offers.update

import life.qbic.business.UseCaseFailure
import life.qbic.datamodel.dtos.business.Offer

/**
 * Defines the output of the UpdateOffer use case
 *
 * After updating an offer the use case can e.g. inform implementing classes about the event through the listed methods
 *
 * @since: 1.0.0
 *
 */
interface UpdateOfferOutput extends UseCaseFailure{

    /**
     * Confirms the updating of an offer by providing
     * the original offer information including the assigned identifier.
     *
     * @param createdOffer {@link life.qbic.datamodel.dtos.business.Offer}
     * @since 1.0.0
     */
    void updatedNewOffer(Offer createdOffer)
}