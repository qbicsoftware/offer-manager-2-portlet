package life.qbic.business.customers.create

import life.qbic.business.customers.update.UpdateCustomer
import life.qbic.business.customers.update.UpdateCustomerOutput
import life.qbic.datamodel.dtos.business.Customer

import life.qbic.business.exceptions.DatabaseQueryException
import life.qbic.datamodel.dtos.general.Person

/**
 * This use case creates a customer in the system
 *
 * Information on persons such as affiliation and names can be added to the user database.
 *
 * @since: 1.0.0
 */
class CreateCustomer implements CreateCustomerInput, UpdateCustomerOutput {

  private CreateCustomerDataSource dataSource
  private CreateCustomerOutput output
  private UpdateCustomer updateCustomer


  CreateCustomer(CreateCustomerOutput output, CreateCustomerDataSource dataSource){
    this.output = output
    this.dataSource = dataSource
    this.updateCustomer = new UpdateCustomer(this,dataSource)
  }

  @Override
  void createCustomer(Customer customer) {
    try {
      dataSource.addCustomer(customer)
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
      output.failNotification("Could not create new customer")
    }
  }

  @Override
  void updateCustomer(Customer oldCustomer, Customer newCustomer) {
    int customerId = dataSource.findCustomer(oldCustomer).get()
    updateCustomer.updateCustomer(customerId,newCustomer)
  }

  @Override
  void customerUpdated(Person person) {
    output.customerCreated(person)
  }

  @Override
  void failNotification(String notification) {
    output.failNotification("Could not update customer")
  }
}
