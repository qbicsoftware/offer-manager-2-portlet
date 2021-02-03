package life.qbic.business.offers.update

import life.qbic.datamodel.dtos.business.Offer

/**
 * Input interface for the {@link life.qbic.business.offers.update.UpdateOffer} use case
 *
 * @since: 1.0.0
 */
interface UpdateOfferInput {

    /**
     * Searches for and existing offer and creates a new version with the provided information.
     * The output of the use case is then informed about the successful creation of a new offer version
     * <br>
     * //<a href=" https://github.com/qbicsoftware/offer-manager-2-portlet/issues/222">TODO this information needs to contain changes to the previous offer</a>
     * <br>
     * <b>Precondition</b>:    Offer with identifier exists
     * <br>
     * <b>Postcondition</b>:   Offer is updated and has new version

     * @param offerContent
     * @since 1.0.0
     */
    void updateExistingOffer(Offer offerContent)

    /**
     * Searches for and existing offer and creates a new version with the provided information only if the offer is different
     * to the old offer.
     * The output of the use case is then informed about the successful creation of a new offer version
     * <br>
     * <br>
     * <b>Precondition</b>:    Offer with identifier exists
     * <br>
     * <b>Postcondition</b>:   Offer is updated and has new version

     * @param offerContent
     * @since 1.0.0
     */
    void updateExistingOffer(Offer newOfferContent, Offer oldOfferContent)

}
