package life.qbic.portal.portlet.customers.create

/**
 * Input interface for the {@link life.qbic.portal.portlet.customers.create.CreateCustomer} use case
 *
 * @since: 1.0.0
 * @author: Tobias Koch
 */
interface CreateCustomerOutput {

    /**
     * Notifies the user about the created customer
     *
     * @param notification containing a message for the user
     */
    void createdCustomer(String notification)

}