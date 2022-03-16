package life.qbic.business.persons.create

import life.qbic.business.exceptions.DatabaseQueryException
import life.qbic.business.persons.Person
import life.qbic.business.persons.PersonExistsException
import life.qbic.business.persons.PersonNotFoundException

/**
 * Creates a person in the database for the CreatePerson use case
 *
 * This interface should be used to allow the use case to forward data to a data source that implements this use case
 * and still follow the correct dependency flow from infrastructure to the domain logic
 *
 * @since: 1.0.0
 */
interface CreatePersonDataSource {

    /**
     * Adds a person to the user database
     *
     * @param person a person to be added to known persons
     * @throws DatabaseQueryException When a person could not been added to the person database
     * @throws PersonExistsException When a person already exists in the database
     * @since 1.0.0
     */
    void addPerson(Person person) throws DatabaseQueryException, PersonExistsException

    /**
     * Updates the information of a given person specified by a person ID
     *
     * @param outdatedPersonData specifies the person to be updated
     * @param updatedPersonData person containing all information and the updates of a person
     * @throws DatabaseQueryException When a person could not been updated in the person
     * database
     * @since 1.0.0
     */
    void updatePerson(Person outdatedPersonData, Person updatedPersonData) throws DatabaseQueryException, PersonNotFoundException

    /**
     * Updates affiliations of a person specified by a customer ID.
     *
     * @param personId to specify the person whose affiliations should be updated
     */
    void updatePersonAffiliations(Person person) throws DatabaseQueryException
}
