package life.qbic.business.customers.create

import life.qbic.business.customers.update.UpdateCustomer
import life.qbic.datamodel.dtos.business.Customer

import life.qbic.business.exceptions.DatabaseQueryException

/**
 * This use case creates a customer in the system
 *
 * Information on persons such as affiliation and names can be added to the user database.
 *
 * @since: 1.0.0
 */
class CreateCustomer implements CreateCustomerInput {

  private CreateCustomerDataSource dataSource
  private CreateCustomerOutput output
  private UpdateCustomer updateCustomer


  CreateCustomer(CreateCustomerOutput output, CreateCustomerDataSource dataSource){
    this.output = output
    this.dataSource = dataSource
    this.updateCustomer = new UpdateCustomer(output,dataSource)
  }

  @Override
  void createCustomer(Customer customer) {
    try {
      int customerId = dataSource.findCustomer(customer)

      if(newCustomer(customerId)){
        dataSource.addCustomer(customer)
      }else{
        updateCustomer.updateCustomer(customerId.toString(),customer)
      }
      try {
        output.customerCreated("Successfully added new customer")
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
      output.failNotification("Could not create new customer")
    }
  }

  /**
   * A customer is already in the database if the customer id is -1
   * @param customerId
   * @return
   */
  private static boolean newCustomer(int customerId){
    return customerId == -1
  }
}
