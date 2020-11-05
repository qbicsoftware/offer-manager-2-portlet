package life.qbic.portal.portlet.offers.create

import life.qbic.datamodel.dtos.business.Offer
import life.qbic.portal.portlet.exceptions.DatabaseQueryException

/**
 * <interface short description - 1 Line!>
 *
 * <More detailed description - When to use, what it solves, etc.>
 *
 * @author Sven Fillinger
 * @since <versiontag>
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
  save(Offer offer) throws DatabaseQueryException

}