package life.qbic.portal.portlet.customers.update

import life.qbic.datamodel.dtos.business.Customer
import life.qbic.portal.portlet.SearchCriteria
import life.qbic.portal.portlet.exceptions.DatabaseQueryException

/**
 * <short description>
 *
 * <detailed description>
 *
 * @since: 1.0.0
 */
interface UpdateCustomerDataSource {
    /**
     * This method returns a customer matching the given search criteria
     *
     * @param criteria a map with search criteria
     * @return a person with affiliation and contact information
     * @since 1.0.0
     */
    List<Customer> findCustomer(SearchCriteria criteria) throws DatabaseQueryException

    /**
     * Updates the information of a given customer specified by a customer ID
     *
     * @param customerId to specify the customer to be updated
     * @param updatedCustomer customer containing all information and the updates of a customer
     * @throws DatabaseQueryException When a customer could not been updated in the customer
     * database
     * @since 1.0.0
     */
    void updateCustomer(String customerId, Customer updatedCustomer) throws DatabaseQueryException

}
