package life.qbic.business.customers.create

import life.qbic.datamodel.dtos.business.Customer


/**
 * Input interface for the {@link CreateCustomer} use case
 *
 * This interface describes the methods the use case exposes to its caller.
 *
 * @since: 1.0.0
 */
interface CreateCustomerInput {

  /**
   * Creates a new {@link Customer} for the customer database
   *
   * @param customer which should be added to the database
   */
  void createCustomer(Customer customer)
}