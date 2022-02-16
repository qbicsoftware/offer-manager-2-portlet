package life.qbic.business.persons.create

import life.qbic.business.exceptions.DatabaseQueryException
import life.qbic.business.logging.Logger
import life.qbic.business.logging.Logging
import life.qbic.business.persons.Person
import life.qbic.business.persons.PersonExistsException
import life.qbic.business.persons.PersonNotFoundException
import life.qbic.business.persons.update.UpdatePerson
import life.qbic.business.persons.update.UpdatePersonOutput

/**
 * This use case creates a customer in the system
 *
 * Information on persons such as affiliation and names can be added to the user database.
 *
 * @since: 1.0.0
 */
class CreatePerson implements CreatePersonInput, UpdatePersonOutput {

  private CreatePersonDataSource dataSource
  private CreatePersonOutput output
  private UpdatePerson updatePerson

  private final Logging log = Logger.getLogger(CreatePerson.class)


    CreatePerson(CreatePersonOutput output, CreatePersonDataSource dataSource){
    this.output = output
    this.dataSource = dataSource
    this.updatePerson = new UpdatePerson(this,dataSource)
  }

  @Override
  void createPerson(Person person) {
    try {
      dataSource.addPerson(person)
      try {
        output.personCreated(person)
      } catch (Exception unexpectedException) {
        log.error(unexpectedException.getMessage(), unexpectedException)
      }
    } catch (DatabaseQueryException databaseQueryException) {
      output.failNotification(databaseQueryException.message)
    } catch (PersonExistsException personExistsException) {
      output.failNotification("Could not create ${person.firstName} ${person.lastName}. \n" + personExistsException.getMessage())
    } catch(Exception unexpected) {
      log.error("Unexpected Exception: $unexpected.message")
      log.debug("Unexpected Exception: $unexpected.message", unexpected)
      output.failNotification("Could not create new person")
    }
  }

  @Override
  void updatePerson(Person oldPerson, Person newPerson) {
    try{
      int personId = dataSource.findPerson(oldPerson).get()
      updatePerson.updatePerson(personId,newPerson)
    }
    catch(PersonNotFoundException ignore){
      output.personNotFound(oldPerson, "Cannot update person entry for ${oldPerson.firstName} ${oldPerson.lastName}. \n" +
              "Person was not found. Please try again.")
    }
    catch(NullPointerException ignore){
      output.personNotFound(oldPerson, "Cannot update person entry for ${oldPerson.firstName} ${oldPerson.lastName}. \n" +
              "Person was not found. Please try again.")
    }
    catch(Exception ignore){
      output.failNotification("Cannot update person entry for ${oldPerson.firstName} ${oldPerson.lastName}. \n" +
              "Please try again.")
    }
  }

  @Override
  void personUpdated(Person person) {
    output.personCreated(person)
  }

  @Override
  void failNotification(String notification) {
    output.failNotification(notification)
  }
}
