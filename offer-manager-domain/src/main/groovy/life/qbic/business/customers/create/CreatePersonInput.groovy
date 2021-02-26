package life.qbic.business.customers.create

import life.qbic.datamodel.dtos.general.Person


/**
 * Input interface for the {@link CreatePerson} use case
 *
 * This interface describes the methods the use case exposes to its caller.
 *
 * @since: 1.0.0
 */
interface CreatePersonInput {

  /**
   * Creates a new {@link Person} for the customer database
   *
   * @param customer which should be added to the database
   */
  void createPerson(Person person)

  /**
   * Updates the entry of a person. Fundamental changes of the person like their email address will lead to
   * creating a new person and deactivating the old entry
   * @param oldPerson The person that needs to be updated
   * @param newPerson The person with the updated information
   */
  void updatePerson(Person oldPerson, Person newPerson)
}
