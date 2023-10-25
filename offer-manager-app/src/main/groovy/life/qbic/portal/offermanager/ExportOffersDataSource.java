package life.qbic.portal.offermanager;

import java.util.List;
import life.qbic.business.offers.OfferV2;

/**
 * Datasource for exporting offers
 */
public interface ExportOffersDataSource {

  /**
   *
   * @return a list containing all offers
   */
  List<OfferV2> findAllOffers();
}
