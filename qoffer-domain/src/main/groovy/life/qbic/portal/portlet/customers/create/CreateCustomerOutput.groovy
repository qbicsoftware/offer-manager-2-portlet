package life.qbic.portal.portlet.customers.create

/**
 * Output interface for the {@link life.qbic.portal.portlet.customers.create.CreateCustomer} use
 * case
 *
 * @since: 1.0.0
 * @author: Tobias Koch
 */
interface CreateCustomerOutput {

    /**
     * Sends success notifications that have been
     * recorded during the customer creation use case.
     * @param notification containing a success message
     */
    void successNotification(String notification)

    /**
     * Sends failure notifications that have been
     * recorded during the customer creation use case.
     * @param notification containing a failure message
     */
    void failNotification(String notification)

}