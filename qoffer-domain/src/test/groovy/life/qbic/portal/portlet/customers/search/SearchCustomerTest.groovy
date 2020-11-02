package life.qbic.portal.portlet.customers.search

import life.qbic.datamodel.dtos.business.AcademicTitle
import life.qbic.datamodel.dtos.business.Affiliation
import life.qbic.datamodel.dtos.business.Customer
import life.qbic.portal.portlet.CriteriaType
import life.qbic.portal.portlet.SearchCriteria
import life.qbic.portal.portlet.exceptions.DatabaseQueryException
import org.mockito.ArgumentMatcher
import org.mockito.internal.progress.ArgumentMatcherStorageImpl
import spock.lang.Specification

import static org.mockito.Mockito.*
import static org.junit.Assert.*

/**
 * <class short description - One Line!>
 *
 * <More detailed description - When to use, what it solves, etc.>
 *
 * @since: 1.0.0
 *
 */
class SearchCustomerTest extends Specification{

    def "find a searched customer"(){
        given:
        SearchCustomerOutput output = mock(SearchCustomerOutput.class)
        SearchCustomerDataSource ds = mock(SearchCustomerDataSource.class)
        SearchCustomer searchCustomer = new SearchCustomer(output,ds)

        Customer luke = new Customer("Luke","Skywalker", AcademicTitle.DOCTOR, "a.b@c.de", new ArrayList<Affiliation>())

        when(ds.findCustomer(any())).thenReturn([luke])

        when:
        searchCustomer.searchCustomer("Luke","Skywalker")

        then:
        verify(output).successNotification("Found 1 customers matching Luke Skywalker")
    }

    def "throw an exception if a customer is not found"(){
        given:
        SearchCustomerOutput output = mock(SearchCustomerOutput.class)
        SearchCustomerDataSource ds = mock(SearchCustomerDataSource.class)
        SearchCustomer searchCustomer = new SearchCustomer(output,ds)

        when(ds.findCustomer(any())).thenThrow(new DatabaseQueryException("Customer not found"))

        when:
        searchCustomer.searchCustomer("Luke","Skywalker")

        then:
        verify(output).failNotification("Customer not found")
    }

}
