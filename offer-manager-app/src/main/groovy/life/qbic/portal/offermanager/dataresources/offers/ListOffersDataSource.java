package life.qbic.portal.offermanager.dataresources.offers;

import java.util.Collection;
import life.qbic.business.offers.OfferV2;

public interface ListOffersDataSource {

  /**
   * Finds all offers
   *
   * @return an unordered list of offers
   * @since 1.4.0
   */
  Collection<OfferV2> findAll();
}
