package life.qbic.business.persons.search

import life.qbic.datamodel.dtos.business.Customer

/**
 * A use case which describes how a customer is searched in the database
 *
 * A customer can be searched by its first and last name. The user gets a list with all persons matching the search.
 *
 * @since: 1.0.0
 *
 */
class SearchCustomer implements SearchCustomerInput{
    SearchCustomerDataSource dataSource
    SearchCustomerOutput output

    SearchCustomer(SearchCustomerOutput output, SearchCustomerDataSource dataSource){
        this.output = output
        this.dataSource = dataSource
    }

    @Override
    void searchCustomer(String firstName, String lastName) {
        try {
            List<Customer> foundCustomer = dataSource.findCustomer(firstName, lastName)
            if (foundCustomer.isEmpty()) {
                output.failNotification("Could not find a customer for $firstName $lastName")
            } else {
                output.successNotification(foundCustomer)
            }
        } catch (Exception ignored) {
            output.failNotification("Unexpected error when searching for the customer $firstName $lastName")
        }
    }
}
