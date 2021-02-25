package life.qbic.business.offers.fetch

import life.qbic.business.UseCaseFailure
import life.qbic.datamodel.dtos.business.Offer

/**
 * Output interface for the {@link life.qbic.business.offers.fetch.FetchOffer} use case
 *
 * @since: 1.0.0
 */
interface FetchOfferOutput extends UseCaseFailure {

  /**
   * Confirms the retrieval of the offer associated with the provided OfferId by providing
   * the fetched Offer
   *
   * @param fetchedOffer {@link life.qbic.datamodel.dtos.business.Offer}
   * @since 1.0.0
   */
  void fetchedOffer(Offer fetchedOffer)
}
