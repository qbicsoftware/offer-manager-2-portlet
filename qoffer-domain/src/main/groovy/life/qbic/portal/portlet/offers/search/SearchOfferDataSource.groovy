package life.qbic.portal.portlet.offers.search

import life.qbic.datamodel.dtos.business.Offer
import life.qbic.datamodel.dtos.business.OfferId


/**
 * Provides methods to search for offers in an existing data-source.
 *
 * @author Sven Fillinger
 * @since 1.0.0
 */
interface SearchOfferDataSource {

  /**
   * Finds offers that contain the provided character sequence.
   *
   * This search is to be implemented as a full-text search and shall
   * not be restricted to certain properties of an offer only.
   *
   * @param offerContent The character sequence to search for in existing offers.
   * @return A search result containing zero or more matching offers.
   */
  List<Offer> findOffer(String offerContent)

  /**
   * Finds offers that have the provided offerId.
   *
   * This search is faster as than the full-text search, as only
   * the offer identifier will be used as search criteria.
   *
   * @param offerId The offer identifier to search for.
   * @return A search result containing zero or more matching offers.
   */
  List<Offer> findOffer(OfferId offerId)

}
