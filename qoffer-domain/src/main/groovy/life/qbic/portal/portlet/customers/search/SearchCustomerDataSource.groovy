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
     * @param criteria containing the search criteria of the users request
     * @return a person with affiliation and contact information
     * @since 1.0.0
     */
    List<Customer> findCustomer(SearchCriteria criteria) throws DatabaseQueryException
}
