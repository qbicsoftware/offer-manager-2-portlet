package life.qbic.portal.portlet.customers.search

import life.qbic.datamodel.dtos.business.Customer
import life.qbic.portal.portlet.CriteriaType
import life.qbic.portal.portlet.SearchCriteria
import life.qbic.portal.portlet.exceptions.DatabaseQueryException

/**
 * A use case which describes how a customer is searched in the database
 *
 * A customer can be searched by its first and last name. The user gets a list with all customers matching the search.
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
            int numberOfFoundCustomers = foundCustomer.size()
            output.successNotification("Found $numberOfFoundCustomers customers matching $firstName $lastName")
        } catch (DatabaseQueryException ignored) {
            output.failNotification("Could not find a customer matching $firstName $lastName")
        } catch (Exception ignored) {
            output.failNotification("Unexpected error when searching for the customer $firstName $lastName")
        }


    }
}
