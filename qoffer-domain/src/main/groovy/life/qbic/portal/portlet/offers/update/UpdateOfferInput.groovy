package life.qbic.portal.portlet.offers.update

import life.qbic.datamodel.dtos.business.Offer


/**
 * Input interface for the {@link life.qbic.portal.portlet.offers.update.UpdateOffer} use case
 *
 * @since: 1.0.0
 * @author: Tobias Koch
 */
interface UpdateOfferInput {

    /**
     * Updates an already existing offer with new information.
     *
     * Please be aware, that this starts the use case
     * {@link life.qbic.portal.portlet.offers.update.UpdateOffer} and therefore, will create
     * a new version for the existing offer.
     *
     * @param offer An offer with updated information
     */
    void update(Offer offer)
}
