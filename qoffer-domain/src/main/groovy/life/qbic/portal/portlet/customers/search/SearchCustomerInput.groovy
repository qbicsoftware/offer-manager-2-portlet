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

    void searchCustomer(String firstName, String lastName)
}
