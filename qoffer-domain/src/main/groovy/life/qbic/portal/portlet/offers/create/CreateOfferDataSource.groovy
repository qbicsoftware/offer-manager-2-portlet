package life.qbic.portal.portlet.offers.create

import life.qbic.portal.portlet.exceptions.DatabaseQueryException
import life.qbic.portal.portlet.offers.Offer

/**
 * Provides methods to store offers in a data-source
 *
 * @author Sven Fillinger
 * @since 1.0.0
 */
interface CreateOfferDataSource {

  /**
   * Saves an offer in a persistent data-source.
   *
   * Developers should execute this method, when they
   * want to store an offer in f.e. a database.
   *
   * If the passed offer is already in the database (offer
   * content with the same offer identifier already exists),
   * then this method will throw a DatabaseQueryException.
   *
   * If the passed offer cannot be saved successfully, this method
   * will also throw a DatabaseQueryException.
   *
   * @param offer
   * @return
   * @throws DatabaseQueryException
   */
  store(Offer offer) throws DatabaseQueryException

}
