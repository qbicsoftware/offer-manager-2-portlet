package life.qbic.portal.portlet.customers.search

import life.qbic.datamodel.dtos.business.Customer
import life.qbic.portal.portlet.UseCaseFailure

/**
 * Output interface for the {@link life.qbic.portal.portlet.customers.search.SearchCustomer} use
 * case.
 *
 * @since: 1.0.0
 *
 */
interface SearchCustomerOutput extends UseCaseFailure {

  /**
   * This method is called by the use case on success.
   *
   * It passes the search result for a given search query.
   *
   * @param foundCustomers A list of {@link Customer}.
   * @since 1.0.0
   */

  void successNotification(List<Customer> foundCustomers)
}