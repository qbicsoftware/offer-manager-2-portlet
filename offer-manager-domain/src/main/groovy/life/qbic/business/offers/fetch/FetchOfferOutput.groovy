package life.qbic.business.offers.fetch

import life.qbic.business.UseCaseFailure
import life.qbic.business.offers.OfferV2

/**
 * Output interface for the {@link life.qbic.business.offers.fetch.FetchOffer} use case
 *
 * @since: 1.0.0
 */
interface FetchOfferOutput extends UseCaseFailure {

  /**
   * Returns the offer associated with the provided OfferId.
   * The fetched offer has updated price information according to the latest business logic.
   *
   * @param fetchedOffer {@link life.qbic.datamodel.dtos.business.Offer}
   * @since 1.0.0
   */
  void fetchedOffer(OfferV2 fetchedOffer)
}
