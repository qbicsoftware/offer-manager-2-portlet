package life.qbic.business.customers.create

import life.qbic.datamodel.dtos.business.Customer
import life.qbic.business.exceptions.DatabaseQueryException

/**
 * Creates a customer in the database for the CreateCustomer use case
 *
 * This interface should be used to allow the use case to forward data to a data source that implements this use case
 * and still follow the correct dependency flow from infrastructure to the domain logic
 *
 * @since: 1.0.0
 */
interface CreateCustomerDataSource {
    /**
     * Adds a customer to the user database
     *
     * @param customer a person to be added to known persons
     * @throws DatabaseQueryException When a customer could not been added to the customer database
     * @since 1.0.0
     */
    void addCustomer(Customer customer) throws DatabaseQueryException
}
