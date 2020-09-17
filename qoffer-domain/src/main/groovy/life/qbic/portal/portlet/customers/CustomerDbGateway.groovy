package life.qbic.portal.portlet.customers

import life.qbic.datamodel.dtos.business.Customer
import life.qbic.portal.portlet.SearchCriteria

/**
 * A gateway to access information from a customer database
 *
 * This class specifies how the application can access external resources.
 * It is meant to be implemented outside the domain layer.
 *
 * @since: 1.0.0
 * @author: Tobias Koch
 *
 */
interface CustomerDbGateway {

    /**
     * This method returns a customer matching the given search criteria
     *
     * @param criteria a map with search criteria
     * @return a person with affiliation and contact information
     */
    List<Customer> findCustomer(SearchCriteria criteria)

    /**
     * Adds a customer to the user database
     *
     * @param customer a person to be added to known customers
     */
    void addCustomer(Customer customer)


    /**
     * Updates the information of a given customer specified by a customer ID
     *
     * @param customerId to specify the customer to be updated
     * @param updatedCustomer customer containing all information and the updates of a customer
     */
    void updateCustomer(String customerId, Customer updatedCustomer)

}