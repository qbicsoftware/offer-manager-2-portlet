package life.qbic.business.offers.fetch

import life.qbic.datamodel.dtos.business.OfferId

/**
 * Input interface for the {@link life.qbic.business.offers.fetch.FetchOffer} use case
 *
 * @since: 1.0.0
 */
interface FetchOfferInput {

  /**
   * Saves an offer in a (persistent) datasource.
   *
   * Developers shall call this method to pass an OfferId
   * provided from the user in order to trigger the completion
   * of the business use case `Fetch Offer`,
   * which will apply business policies for offer retrieval
   * in a pre-configured, optimally persistent data-source.
   *
   * @param offerId {@link life.qbic.datamodel.dtos.business.OfferId}
   * @since 1.0.0
   */
  void fetchOffer(OfferId offerId)

}
