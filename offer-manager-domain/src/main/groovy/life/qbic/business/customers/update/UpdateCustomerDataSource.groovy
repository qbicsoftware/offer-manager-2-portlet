package life.qbic.business.customers.update

import life.qbic.datamodel.dtos.business.Customer
import life.qbic.business.exceptions.DatabaseQueryException

/**
 * <short description>
 *
 * <detailed description>
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

}
