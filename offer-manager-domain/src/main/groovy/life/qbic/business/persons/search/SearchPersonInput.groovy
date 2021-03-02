package life.qbic.business.persons.search

/**
 * Input interface for the {@link SearchPerson} use case
 *
 * This interface describes the methods the use case exposes to its caller.
 *
 * @since: 1.0.0
 *
 */
interface SearchPersonInput {
    /**
    * This method triggers the search for a customer with matching firstname and lastname
    *
    * @param firstName: The first name of the customer.
    * @param lastName: The last name of the customer
    * @since: 1.0.0
    */
    void searchPerson(String firstName, String lastName)
}
