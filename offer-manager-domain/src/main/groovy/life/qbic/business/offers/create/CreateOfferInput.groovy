package life.qbic.business.offers.create

import life.qbic.business.offers.OfferV2

/**
 * Input interface for the {@link CreateOffer} use case
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
   * There is no need to set the offer identifier in the passed content,
   * this will be determined and set by the implementation of the use case.
   *
   * If the identifier is passed with the content, it will be ignored.
   *
   * @param offer {@link OfferV2} the offer to be created
   * @since 1.0.0
   */
  void createOffer(OfferV2 offer)

  /**
   * Saves changes to an offer in the database.
   * This method causes the use case to fail in case the provided offer has an invalid identifier.
   * An identifier is valid if it is known to the system.
   * For offers with a valid identifier, the price information will be updated and offer
   * information will be stored in a persistent manner.
   * @param offer an offer containing information to be set
   */
  void updateOffer(OfferV2 offer)

}
