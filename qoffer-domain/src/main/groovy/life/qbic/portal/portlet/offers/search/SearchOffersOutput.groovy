package life.qbic.portal.portlet.offers.search

import life.qbic.datamodel.dtos.business.Offer


/**
 * Output interface for the {@link life.qbic.portal.portlet.offers.search.SearchOffers} use case
 *
 * @since: 1.0.0
 * @author: Tobias Koch
 */
interface SearchOffersOutput {

  /**
   * Passes the found offers, after the search has been successfully
   * finished.
   *
   * If no offers matching the search criteria have been found, the list
   * will be empty.
   *
   * Developers shall call this method, if the search was technically successful
   * and no exceptions have been thrown during the process.
   *
   * @param offers The search result, containing zero or more offers.
   */
  void matchingOffers(List<Offer> offers)

}