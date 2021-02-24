package life.qbic.business.customers.update

import life.qbic.business.UseCaseFailure
import life.qbic.datamodel.dtos.general.Person

/**
 * Output interface for the {@link life.qbic.business.customers.update.UpdateCustomer} use
 * case
 *
 * @since: 1.0.0
 */
interface UpdateCustomerOutput extends UseCaseFailure {

    /**
     * Is called by the use case, when a customer resource has been updated
     * @param person The updated created person resource
     */
    void customerUpdated(Person person)
}
