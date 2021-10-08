package life.qbic.business.persons.update

import life.qbic.business.persons.create.CreatePersonDataSource
import life.qbic.business.logging.Logger
import life.qbic.business.logging.Logging
import life.qbic.business.exceptions.DatabaseQueryException
import life.qbic.datamodel.dtos.general.Person

/**
 * This use case updates an existing customer in the system. New Affiliations of the customer are added to the respective table.
 * If other changes are made to the customer, a new customer is created in the system and the old customer is set to inactive.
 *
 * @since: 1.0.0
 */
class UpdatePerson {

  private static final Logging log = Logger.getLogger(UpdatePerson)

  private CreatePersonDataSource dataSource
  private UpdatePersonOutput output

  UpdatePerson(UpdatePersonOutput output, CreatePersonDataSource dataSource){
    this.output = output
    this.dataSource = dataSource
  }

  void updatePerson(int personId, Person person) {
    Person existingCustomer = dataSource.getPerson(personId)

    if(! existingCustomer) throw new IllegalArgumentException("Could not find person to updated, the entry changed in the database. Please try again.")

    boolean customerChanged = hasBasicPersonDataChanged(existingCustomer, person)
    try {
      if(customerChanged) {
        dataSource.updatePerson(personId, person)
      } else {
        dataSource.updatePersonAffiliations(personId, person.affiliations)
      }
      //this exception catching is important to avoid displaying a wrong failure notification
      try {
        output.personUpdated(person)
      } catch (Exception e) {
        log.error(e.message)
        log.error(e.stackTrace.join("\n"))
      }
    } catch(DatabaseQueryException databaseQueryException){
      output.failNotification("Could not find person to updated, the entry changed in the database. Please try again.")
      log.error databaseQueryException.stackTrace.join("\n")
    } catch(Exception unexpected) {
      log.error(unexpected.message)
      log.error(unexpected.stackTrace.join("\n"))
      output.failNotification("Could not update person.")
    }
  }

  // determines if customer properties other than affiliations have changed
  private static boolean hasBasicPersonDataChanged(Person existingPerson, Person newPerson) {
    boolean noFundamentalChange = existingPerson.firstName.equals(newPerson.firstName)
    && existingPerson.lastName.equals(newPerson.lastName)
    && existingPerson.emailAddress.equals(newPerson.emailAddress)
    && existingPerson.title.equals(newPerson.title)
    
    return !noFundamentalChange
  }
}
