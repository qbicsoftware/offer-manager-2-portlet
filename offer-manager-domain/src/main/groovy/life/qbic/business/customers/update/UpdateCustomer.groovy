package life.qbic.business.customers.update

import life.qbic.datamodel.dtos.business.Customer

import life.qbic.business.exceptions.DatabaseQueryException

/**
 * This use case updates an existing customer in the system. New Affiliations of the customer are added to the respective table.
 * If other changes are made to the customer, a new customer is created in the system and the old customer is set to inactive.
 *
 * @since: 1.0.0
 * @author: Andreas Friedrich, Tobias Koch
 */
class UpdateCustomer implements UpdateCustomerInput {

  private UpdateCustomerDataSource dataSource
  private UpdateCustomerOutput output

  UpdateCustomer(UpdateCustomerOutput output, UpdateCustomerDataSource dataSource){
    this.output = output
    this.dataSource = dataSource
  }

  @Override
  void updateCustomer(String customerId, Customer customer) {
    Customer existingCustomer = dataSource.getCustomer(Integer.parseInt(customerId))

    boolean noFundamentalChange = existingCustomer.firstName.equals(customer.firstName)
    && existingCustomer.lastName.equals(customer.lastName)
    && existingCustomer.emailAddress.equals(customer.emailAddress)
    && existingCustomer.title.equals(customer.title)

    try {
      if(noFundamentalChange) {
        dataSource.updateCustomerAffiliations(customerId, customer.affiliations)
      } else {
        dataSource.updateCustomer(customerId, customer)
      }
      try {
        output.customerUpdated(customer)
      } catch (Exception ignored) {
        //quiet output message failed
      }
    } catch(DatabaseQueryException databaseQueryException){
      output.failNotification(databaseQueryException.message)
    } catch(Exception unexpected) {
      println "-------------------------"
      println "Unexpected Exception ...."
      println unexpected.message
      println unexpected.stackTrace.join("\n")
      output.failNotification("Could not update customer")
    }
  }
}
