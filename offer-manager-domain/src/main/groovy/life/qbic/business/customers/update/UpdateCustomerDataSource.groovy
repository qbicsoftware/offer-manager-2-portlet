package life.qbic.business.customers.update

import life.qbic.datamodel.dtos.business.Customer
import life.qbic.datamodel.dtos.business.Affiliation
import life.qbic.business.exceptions.DatabaseQueryException

/**
 * Updates a customer in the database for the UpdateCustomer use case
 *
 * This interface should be used to allow the use case to forward data to a data source that implements this use case
 * and still follow the correct dependency flow from infrastructure to the domain logic
 *
 * @since: 1.0.0
 */
interface UpdateCustomerDataSource {

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
    
    /**
     * Returns a customer given a customer specified by a customer ID
     * 
     * @param customerId to specify and existing customer
     */
    Customer getCustomer(int customerId)
        
    /**
     * Updates affiliations of a customer specified by a customer ID.
     * 
     * @param customerId to specify the customer whose affiliations should be updated
     * @param affiliations that the customer should be associated to
     */
    void updateCustomerAffiliations(String customerId, List<Affiliation> affiliations) throws DatabaseQueryException

}
