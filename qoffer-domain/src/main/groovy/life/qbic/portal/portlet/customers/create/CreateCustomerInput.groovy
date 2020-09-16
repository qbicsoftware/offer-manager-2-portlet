package life.qbic.portal.portlet.customers.create

import life.qbic.portal.portlet.customers.Customer

/**
 * Input interface for the {@link life.qbic.portal.portlet.customers.create.CreateCustomer} use case
 *
 * This interface describes the methods the use case exposes to its caller.
 *
 * @since: 1.0.0
 * @author: Tobias Koch
 */
interface CreateCustomerInput {

  /**
   * Creates a new {@link Customer} for the customer database
   *
   * @param customer which should be added to the database
   */
  void createCustomer(Customer customer)

}