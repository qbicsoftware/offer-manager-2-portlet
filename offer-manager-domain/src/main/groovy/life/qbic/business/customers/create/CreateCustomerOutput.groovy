package life.qbic.business.customers.create

import life.qbic.business.UseCaseFailure

/**
 * Output interface for the {@link CreateCustomer} use
 * case
 *
 * @since: 1.0.0
 * @author: Tobias Koch
 */
interface CreateCustomerOutput extends UseCaseFailure {

    void customerCreated(String message)
}