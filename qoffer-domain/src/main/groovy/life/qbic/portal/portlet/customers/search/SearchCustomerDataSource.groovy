package life.qbic.portal.portlet.customers.search

import life.qbic.datamodel.dtos.business.Customer
import life.qbic.portal.portlet.SearchCriteria
import life.qbic.portal.portlet.exceptions.DatabaseQueryException

/**
 * Retrieves data for the SearchCustomer use case
 *
 * This interface should be used to allow the use case to retrieve data from a data source that implements this use case
 * and still follow the correct dependency flow from infrastructure to the domain logic
 *
 * @since: 1.0.0
 *
 */
interface SearchCustomerDataSource {

    /**
     * This method returns a customer matching the given search criteria
     *
     * @param firstName The customer's first name
     * @param lastName The customer's last name
     * @return A list of matching customer entries with the given first and last name.
     * @throws DatabaseQueryException If the data source query fails for technical reasons, this
     * exception is thrown.
     *
     * @since 1.0.0
     */
    List<Customer> findCustomer(String firstName, String lastName) throws DatabaseQueryException
}
