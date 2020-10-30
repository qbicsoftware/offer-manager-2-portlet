package life.qbic.portal.portlet.customers.search

/**
 * Input interface for the {@link life.qbic.portal.portlet.customers.search.SearchCustomer} use case
 *
 * This interface describes the methods the use case exposes to its caller.
 *
 * @since: 1.0.0
 *
 */
interface SearchCustomerInput {

    /**
     * This method triggers the search for a customer based on the full name
     * @param firstName of the customer
     * @param lastName of the customer
     * @since 1.0.0
     */
    void searchCustomer(String firstName, String lastName)
}
