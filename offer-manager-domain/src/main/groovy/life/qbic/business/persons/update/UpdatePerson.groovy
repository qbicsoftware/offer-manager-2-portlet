package life.qbic.business.persons.update

import life.qbic.business.exceptions.DatabaseQueryException
import life.qbic.business.logging.Logger
import life.qbic.business.logging.Logging
import life.qbic.business.persons.Person
import life.qbic.business.persons.create.CreatePersonDataSource

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

  void updatePerson(Person outdatedPerson, Person personWithUpdate) {
    try {
      if(hasBasicPersonDataChanged(outdatedPerson, personWithUpdate)) {
        personWithUpdate.setUserId(outdatedPerson.getUserId())
        dataSource.updatePerson(outdatedPerson, personWithUpdate)
      } else {
        dataSource.updatePersonAffiliations(personWithUpdate)
      }
      //this exception catching is important to avoid displaying a wrong failure notification
      try {
        output.personUpdated(personWithUpdate)
      } catch (Exception e) {
        log.error(e.message)
        log.error(e.stackTrace.join("\n"))
      }
    } catch(DatabaseQueryException ignored){
      output.failNotification("Could not find person to updated, the entry changed in the database. Please try again.")
    } catch(Exception unexpected) {
      log.error(unexpected.message)
      log.error(unexpected.stackTrace.join("\n"))
      output.failNotification("Could not update person.")
    }
  }

  // determines if customer properties other than affiliations have changed
  private static boolean hasBasicPersonDataChanged(Person existingPerson, Person newPerson) {
    return !(existingPerson.firstName.equals(newPerson.firstName)
            && existingPerson.lastName.equals(newPerson.lastName)
            && existingPerson.email.equals(newPerson.email)
            && existingPerson.title.equals(newPerson.title))
  }
}
