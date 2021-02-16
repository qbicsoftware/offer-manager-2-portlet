package life.qbic.business.customers.update

import life.qbic.business.customers.create.CreateCustomerDataSource
import life.qbic.business.customers.create.CreateCustomerOutput
import life.qbic.datamodel.dtos.business.Customer

import life.qbic.business.exceptions.DatabaseQueryException

/**
 * This use case updates an existing customer in the system. New Affiliations of the customer are added to the respective table.
 * If other changes are made to the customer, a new customer is created in the system and the old customer is set to inactive.
 *
 * @since: 1.0.0
 */
class UpdateCustomer{

  private CreateCustomerDataSource dataSource
  private CreateCustomerOutput output

  UpdateCustomer(CreateCustomerOutput output, CreateCustomerDataSource dataSource){
    this.output = output
    this.dataSource = dataSource
  }

  void updateCustomer(int customerId, Customer customer) {
    Customer existingCustomer = dataSource.getCustomer(customerId)
    boolean customerChanged = hasBasicCustomerDataChanged(existingCustomer, customer)
    try {
      if(customerChanged) {
        dataSource.updateCustomer(customerId, customer)
      } else {
        dataSource.updateCustomerAffiliations(customerId, customer.affiliations)
      }
      //this exception catching is important to avoid displaying a wrong failure notification
      try {
        output.customerCreated(customer)
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

  // determines if customer properties other than affiliations have changed
  private static boolean hasBasicCustomerDataChanged(Customer existingCustomer, Customer newCustomer) {
    boolean noFundamentalChange = existingCustomer.firstName.equals(newCustomer.firstName)
    && existingCustomer.lastName.equals(newCustomer.lastName)
    && existingCustomer.emailAddress.equals(newCustomer.emailAddress)
    && existingCustomer.title.equals(newCustomer.title)
    
    return !noFundamentalChange
  }
}
