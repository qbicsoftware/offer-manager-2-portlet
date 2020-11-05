package life.qbic.portal.portlet.offers.create

import life.qbic.datamodel.dtos.business.Offer


/**
 * Input interface for the {@link life.qbic.portal.portlet.offers.create.CreateOffer} use case
 *
 * @since: 1.0.0
 * @author: Tobias Koch
 */
interface CreateOfferInput {

  /**
   * Saves an offer in a (persistent) datasource.
   *
   * Developers shall call this method to pass offer content
   * provided from the user in order to trigger the completion
   * of the business use case `Create Offer`,
   * which will apply business policies for offer creation and storage
   * in a pre-configured, optimally persistent data-source.
   *
   * @param offer {@link life.qbic.datamodel.dtos.business.Offer}
   */
  void saveOffer(Offer offer)

}