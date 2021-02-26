package life.qbic.business.customers.create

import life.qbic.business.customers.update.UpdateCustomer
import life.qbic.business.customers.update.UpdateCustomerOutput
import life.qbic.business.logging.Logger
import life.qbic.business.logging.Logging
import life.qbic.business.exceptions.DatabaseQueryException
import life.qbic.datamodel.dtos.general.Person

/**
 * This use case creates a customer in the system
 *
 * Information on persons such as affiliation and names can be added to the user database.
 *
 * @since: 1.0.0
 */
class CreatePerson implements CreatePersonInput, UpdateCustomerOutput {

  private CreatePersonDataSource dataSource
  private CreatePersonOutput output
  private UpdateCustomer updateCustomer

  private final Logging log = Logger.getLogger(CreatePerson.class)


    CreatePerson(CreatePersonOutput output, CreatePersonDataSource dataSource){
    this.output = output
    this.dataSource = dataSource
    this.updateCustomer = new UpdateCustomer(this,dataSource)
  }

  @Override
  void createPerson(Person person) {
    try {
      dataSource.addPerson(person)
      try {
        output.customerCreated(person)
      } catch (Exception ignored) {
        log.error(ignored.stackTrace.toString())
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
  void updatePerson(Person oldPerson, Person newPerson) {
    int customerId = dataSource.findCustomer(oldPerson).get()
    updateCustomer.updateCustomer(customerId,newPerson)
  }

  @Override
  void customerUpdated(Person person) {
    output.customerCreated(person)
  }

  @Override
  void failNotification(String notification) {
    output.failNotification(notification)
  }
}
