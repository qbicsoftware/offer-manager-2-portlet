package life.qbic.portal.portlet.customers.search

import life.qbic.business.customers.search.SearchCustomer
import life.qbic.business.customers.search.SearchCustomerDataSource
import life.qbic.business.customers.search.SearchCustomerOutput
import life.qbic.datamodel.dtos.business.Customer
import life.qbic.business.exceptions.DatabaseQueryException
import spock.lang.Specification

/**
 * Test the search of customers
 *
 * Stubs the database connection and verifies that if a customer is found the {@link life.qbic.business.customers.search.SearchCustomerOutput} is verified or if not
 * an exception leads to a failure notification
 *
 * @since: 1.0.0
 *
 */
class SearchCustomerSpec extends Specification{


    def "find a searched customer"(){
        given:
        SearchCustomerOutput output = Mock(SearchCustomerOutput.class)
        SearchCustomerDataSource ds = Stub(SearchCustomerDataSource.class)
        SearchCustomer searchCustomer = new SearchCustomer(output,ds)

        Customer luke = new Customer.Builder(firstName, lastName, "example@example.com").build()

        ds.findCustomer(firstName, lastName) >> [luke]

        when:
        searchCustomer.searchCustomer(firstName, lastName)

        then:
        1* output.successNotification(_)

        where:
        firstName | lastName
        "Luke" | "Skywalker"
    }

    def "notify of failure whenever the datasource throws an exception"(){
        given:
        SearchCustomerOutput output = Mock(SearchCustomerOutput.class)
        SearchCustomerDataSource ds = Stub(SearchCustomerDataSource.class)
        SearchCustomer searchCustomer = new SearchCustomer(output,ds)

        ds.findCustomer(firstName, lastName) >> {throw new DatabaseQueryException("Customer not found")}

        when:
        searchCustomer.searchCustomer(firstName, lastName)

        then:
        1* output.failNotification(_)

        where:
        firstName | lastName
        "Luke" | "Skywalker"
    }

}
