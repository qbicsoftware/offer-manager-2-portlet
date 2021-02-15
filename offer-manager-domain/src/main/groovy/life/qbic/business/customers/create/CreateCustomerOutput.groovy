package life.qbic.business.customers.create

import life.qbic.business.UseCaseFailure
import life.qbic.datamodel.dtos.general.Person

/**
 * Output interface for the {@link CreateCustomer} use
 * case
 *
 * @since: 1.0.0
 * @author: Tobias Koch
 */
interface CreateCustomerOutput extends UseCaseFailure {

    /**
     * Is called by the use case, when a new customer has been created
     * @param message
     * @deprecated Use the more explicit #customerCreated(Person person) method
     */
    @Deprecated
    void customerCreated(String message)

    /**
     * Is called by the use case, when a new customer resource has been created
     * @param person The newly created person resource
     */
    void customerCreated(Person person)

}