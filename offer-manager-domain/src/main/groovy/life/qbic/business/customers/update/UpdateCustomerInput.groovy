package life.qbic.business.customers.update

import life.qbic.datamodel.dtos.business.Customer

/**
 * Input interface for the {@link UpdateCustomer} use case
 *
 * This interface describes the methods the use case exposes to its caller.
 *
 * @since: 1.0.0
 */
interface UpdateCustomerInput {
  
  /**
   * Updates an existing {@link Customer} in the customer database.
   * This may create a new customer row and inactivate the old version.
   * 
   * @param customer identifier of the customer that should be updated
   * @param customer which should be updated in the database
   */
  void updateCustomer(String customerId, Customer customer)
  
}