package life.qbic.portal.portlet.offers.create

import life.qbic.datamodel.dtos.business.Offer


/**
 * Output interface for the {@link life.qbic.portal.portlet.offers.create.CreateOffer} use case
 *
 * @since: 1.0.0
 * @author: Tobias Koch
 */
interface CreateOfferOutput {

  /**
   * Confirms the creation and saving of a new offer by providing
   * the original offer information including the assigned identifier.
   *
   * @param createdOffer {@link life.qbic.datamodel.dtos.business.Offer}
   */
  void createdNewOffer(Offer createdOffer)

}
