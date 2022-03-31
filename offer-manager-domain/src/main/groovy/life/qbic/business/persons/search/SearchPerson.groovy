package life.qbic.business.persons.search

import life.qbic.business.persons.Person

/**
 * A use case which describes how a customer is searched in the database
 *
 * A customer can be searched by its first and last name. The user gets a list with all persons matching the search.
 *
 * @since: 1.0.0
 *
 */
class SearchPerson implements SearchPersonInput{
    SearchPersonDataSource dataSource
    SearchPersonOutput output

    SearchPerson(SearchPersonOutput output, SearchPersonDataSource dataSource){
        this.output = output
        this.dataSource = dataSource
    }

    @Override
    void searchPerson(String firstName, String lastName) {
        try {
            List<Person> foundCustomer = dataSource.findPerson(firstName, lastName)

            if (foundCustomer.isEmpty()) {
                output.failNotification("Could not find a person for $firstName $lastName")
            } else {
                output.successNotification(foundCustomer)
            }
        } catch (Exception ignored) {
            output.failNotification("Unexpected error when searching for the person $firstName $lastName")
        }
    }
}
