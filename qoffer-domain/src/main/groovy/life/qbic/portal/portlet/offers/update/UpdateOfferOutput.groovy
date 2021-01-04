package life.qbic.portal.portlet.offers.update

import life.qbic.datamodel.dtos.business.Offer


/**
 * Output interface for the {@link life.qbic.portal.portlet.offers.update.UpdateOffer} use case
 *
 * @since: 1.0.0
 * @author: Tobias Koch
 */
interface UpdateOfferOutput {

    /**
     * Gets called, when an offer has been successfully updated.
     * @param offer The updated offer
     */
    void onOfferUpdated(Offer offer)
}
