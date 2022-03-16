package life.qbic.business.persons.create

import life.qbic.business.exceptions.DatabaseQueryException
import life.qbic.business.logging.Logger
import life.qbic.business.logging.Logging
import life.qbic.business.persons.Person
import life.qbic.business.persons.PersonExistsException
import life.qbic.business.persons.PersonNotFoundException

/**
 * This use case creates a customer in the system
 *
 * Information on persons such as affiliation and names can be added to the user database.
 *
 * @since: 1.0.0
 */
class CreatePerson implements CreatePersonInput {

  private CreatePersonDataSource dataSource
  private CreatePersonOutput output

  private final Logging log = Logger.getLogger(CreatePerson.class)


  CreatePerson(CreatePersonOutput output, CreatePersonDataSource dataSource) {
    this.output = output
    this.dataSource = dataSource
  }

  @Override
  void createPerson(Person person) {
    try {
      dataSource.addPerson(person)
      output.personCreated(person)
    } catch (DatabaseQueryException databaseQueryException) {
      output.failNotification(databaseQueryException.message)
    } catch (PersonExistsException personExistsException) {
      output.failNotification("Could not create ${person.firstName} ${person.lastName}. \n" + personExistsException.getMessage())
      log.error(personExistsException.message)
      log.debug(personExistsException.message, personExistsException)
    } catch (Exception unexpected) {
      log.error("Unexpected Exception: $unexpected.message")
      log.debug("Unexpected Exception: $unexpected.message", unexpected)
      output.failNotification("Could not create new person")
    }
  }

  @Override
  void updatePerson(Person outdatedPerson, Person personWithUpdate) {
    try {
      if (hasBasicPersonDataChanged(outdatedPerson, personWithUpdate)) {
        personWithUpdate.setUserId(outdatedPerson.getUserId())
        dataSource.updatePerson(outdatedPerson, personWithUpdate)
      } else {
        dataSource.updatePersonAffiliations(personWithUpdate)
      }
      output.personUpdated(personWithUpdate)
    } catch (PersonNotFoundException notFoundException) {
      String message = "Cannot update person entry for ${outdatedPerson.firstName} ${outdatedPerson.lastName}. \nPerson was not found. Please try again."
      log.error(message, notFoundException)
      output.personNotFound(outdatedPerson, message)
    } catch (DatabaseQueryException databaseQueryException) {
      String message = "Could not update ${outdatedPerson.firstName} ${outdatedPerson.lastName}. Please try again."
      log.error(message, databaseQueryException)
      output.failNotification(message)
    } catch (Exception unexpected) {
      String message = "Could not update person."
      log.error("$message : $unexpected.message", unexpected)
      output.failNotification(message)
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
