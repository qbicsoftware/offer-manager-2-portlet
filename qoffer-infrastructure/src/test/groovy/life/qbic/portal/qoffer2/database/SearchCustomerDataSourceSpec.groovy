package life.qbic.portal.qoffer2.database

import groovy.sql.GroovyRowResult
import life.qbic.datamodel.dtos.business.AcademicTitleFactory
import life.qbic.datamodel.dtos.business.Customer
import life.qbic.portal.portlet.customers.search.SearchCustomerDataSource
import life.qbic.portal.qoffer2.customers.CustomerDbConnector
import org.apache.groovy.sql.extensions.SqlExtensions
import spock.lang.Specification

import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.ResultSet

/**
 * This class tests the implementation of SearchCustomerDataSource
 *
 * In this specification every behaviour described by the SearchCustomerDataSource interface should be tested
 *
 * @since: 1.0.0
 */
class SearchCustomerDataSourceSpec extends Specification{
    AcademicTitleFactory factory = new AcademicTitleFactory()

    def "CustomerDbConnector shall return matching customers given a first name and a last name"() {
        given: "a predefined query template"
        String expectedQuery = "SELECT id, first_name AS firstName, last_name AS lastName, title as academicTitle, email as eMailAddress FROM customer" +
                " WHERE firstName = ? AND lastName = ?"

        and: "a connection returning correct results only for matching firstname and lastname"
        // we need to stub the static SqlExtensions.toRowResult method because we do not provide an implemented RowResult
        GroovyMock(SqlExtensions, global: true)
        SqlExtensions.toRowResult(_ as ResultSet) >> new GroovyRowResult(["id":id, "firstName":firstName, "lastName":lastName, "academicTitle":academicTitle, "eMailAddress":emailAddress])
        // our statement should only be able to fill the template with the correct values
        PreparedStatement preparedStatement = Mock {
            setString(1 , firstName) >> void
            setString(2, lastName) >> void
            executeQuery() >> Stub(ResultSet,{it.next() >>> [true, false]})
        }
        // the connection must only provide precompiled statements for the expected query template
        Connection connection = Stub{
            prepareStatement(expectedQuery) >> preparedStatement
        }

        and: "a ConnectionProvider providing the stubbed connection"
        ConnectionProvider connectionProvider = Stub {
            connect() >> connection
        }

        and: "an implementation of the SearchCustomerDataSource with this connection provider"
        SearchCustomerDataSource dataSource = new CustomerDbConnector(connectionProvider)

        when: "the datasource is tasked with finding a customer with provided first and last name"
        List<Customer> foundCustomers = dataSource.findCustomer(firstName, lastName)

        then: "the returned customer information matches information provided by the ResultSet"
        foundCustomers.size() == 1
        Customer customer = foundCustomers.first()
        customer.firstName == firstName
        customer.lastName == lastName
        customer.title == factory.getForString(academicTitle)
        customer.eMailAddress == emailAddress
        //TODO also test for affiliations


        where: "customer information is as follows"
        id | firstName | lastName | academicTitle | emailAddress
        0 | "luke" | "skywalker" | "Dr." | "sith@jedicouncil.universe"
    }

}
