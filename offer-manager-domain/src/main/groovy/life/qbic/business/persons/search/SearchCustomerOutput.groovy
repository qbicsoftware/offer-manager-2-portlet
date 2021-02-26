package life.qbic.business.persons.search

import life.qbic.datamodel.dtos.business.Customer
import life.qbic.business.UseCaseFailure

/**
 * Output interface for the {@link SearchCustomer} use
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