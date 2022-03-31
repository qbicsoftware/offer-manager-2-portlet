package life.qbic.business.persons.search

import life.qbic.business.exceptions.DatabaseQueryException
import life.qbic.business.persons.search.SearchPerson
import life.qbic.business.persons.search.SearchPersonDataSource
import life.qbic.business.persons.search.SearchPersonOutput
import life.qbic.datamodel.dtos.business.Customer
import spock.lang.Specification

/**
 * Test the search of persons
 *
 * Stubs the database connection and verifies that if a customer is found the {@link SearchPersonOutput} is verified or if not
 * an exception leads to a failure notification
 *
 * @since: 1.0.0
 *
 */
class SearchPersonSpec extends Specification{


    def "find a searched customer"(){
        given:
        SearchPersonOutput output = Mock(SearchPersonOutput.class)
        SearchPersonDataSource ds = Stub(SearchPersonDataSource.class)
        SearchPerson searchCustomer = new SearchPerson(output,ds)

        Customer luke = new Customer.Builder(firstName, lastName, "example@example.com").build()

        ds.findPerson(firstName, lastName) >> [luke]

        when:
        searchCustomer.searchPerson(firstName, lastName)

        then:
        1* output.successNotification(_)

        where:
        firstName | lastName
        "Luke" | "Skywalker"
    }

    def "notify of failure whenever the datasource throws an exception"(){
        given:
        SearchPersonOutput output = Mock(SearchPersonOutput.class)
        SearchPersonDataSource ds = Stub(SearchPersonDataSource.class)
        SearchPerson searchCustomer = new SearchPerson(output,ds)

        ds.findPerson(firstName, lastName) >> {throw new DatabaseQueryException("Customer not found")}

        when:
        searchCustomer.searchPerson(firstName, lastName)

        then:
        1* output.failNotification(_)

        where:
        firstName | lastName
        "Luke" | "Skywalker"
    }

}
