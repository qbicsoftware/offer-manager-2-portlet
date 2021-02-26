package life.qbic.business.customers.create

import life.qbic.datamodel.dtos.business.Affiliation
import life.qbic.datamodel.dtos.business.Customer
import life.qbic.business.exceptions.DatabaseQueryException
import life.qbic.datamodel.dtos.general.Person

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
     * @since 1.0.0
     */
    void addPerson(Person person) throws DatabaseQueryException

    /**
     * Updates the information of a given person specified by a person ID
     *
     * @param personId to specify the person to be updated
     * @param updatedPerson person containing all information and the updates of a person
     * @throws DatabaseQueryException When a person could not been updated in the person
     * database
     * @since 1.0.0
     */
    void updatePerson(int personId, Person updatedPerson) throws DatabaseQueryException

    /**
     * Returns a person given a person specified by a person ID
     *
     * @param personId to specify and existing customer
     */
    Customer getPerson(int personId)

    /**
     * Searches for a customer in a database and returns its id
     *
     * @param customer The customer that needs to be searched in the database
     * @return an optional containing the customer if found
     */
    Optional<Integer> findCustomer(Customer customer)

    /**
     * Updates affiliations of a person specified by a customer ID.
     *
     * @param personId to specify the person whose affiliations should be updated
     * @param affiliations that the person should be associated to
     */
    void updatePersonAffiliations(int personId, List<Affiliation> affiliations) throws DatabaseQueryException
}
