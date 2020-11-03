package life.qbic.portal.portlet.customers.search

import life.qbic.datamodel.dtos.business.AcademicTitle
import life.qbic.datamodel.dtos.business.Affiliation
import life.qbic.datamodel.dtos.business.Customer
import life.qbic.portal.portlet.SearchCriteria
import life.qbic.portal.portlet.exceptions.DatabaseQueryException
import spock.lang.Specification


/**
 * Test the search of customers
 *
 * Stubs the database connection and verifies that if a customer is found the {@link SearchCustomerOutput} is verified or if not
 * an exception leads to a failure notification
 *
 * @since: 1.0.0
 *
 */
class SearchCustomerTest extends Specification{


    def "find a searched customer"(){
        given:
        SearchCustomerOutput output = Mock(SearchCustomerOutput.class)
        SearchCustomerDataSource ds = Stub(SearchCustomerDataSource.class)
        SearchCustomer searchCustomer = new SearchCustomer(output,ds)

        Customer luke = new Customer("Luke","Skywalker", AcademicTitle.DOCTOR, "a.b@c.de", new ArrayList<Affiliation>())

        ds.findCustomer(_ as SearchCriteria) >> [luke]

        when:
        searchCustomer.searchCustomer("Luke","Skywalker")

        then:
        1* output.successNotification(_)
    }

    def "notify of failure whenever the datasource throws an exception"(){
        given:
        SearchCustomerOutput output = Mock(SearchCustomerOutput.class)
        SearchCustomerDataSource ds = Stub(SearchCustomerDataSource.class)
        SearchCustomer searchCustomer = new SearchCustomer(output,ds)

        ds.findCustomer(_ as SearchCriteria) >> {throw new DatabaseQueryException("Customer not found")}

        when:
        searchCustomer.searchCustomer("Luke","Skywalker")

        then:
        1* output.failNotification("Customer not found")
    }

}
