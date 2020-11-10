package life.qbic.portal.qoffer2.database

import life.qbic.datamodel.dtos.business.AcademicTitleFactory
import life.qbic.datamodel.dtos.business.Customer
import life.qbic.portal.portlet.customers.search.SearchCustomerDataSource
import life.qbic.portal.qoffer2.customers.CustomerDbConnector
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
        given:
        String expectedQuery = "SELECT id, first_name AS firstName, last_name AS lastName, title as academicTitle, email as eMailAddress FROM customer" +
                " WHERE firstName = ? AND lastName = ?"

        ResultSet expectedResult = Mock {
            next() >> false
            getInt("id") >> id
            getString("firstName") >> firstName
            getString("lastName") >> lastName
            getString("academicTitle") >> academicTitle
            getString("eMailAddress") >> emailAddress
        }
        expectedResult.toRowResult() >> ["id":id, "firstName":firstName, "lastName":lastName, "academicTitle":academicTitle, "eMailAddress":emailAddress]

        PreparedStatement preparedStatement = Mock {
            setString(_ as int, _ as String) >> void
            executeQuery() >> Stub(ResultSet)
        }

        Connection connection = Stub{
            prepareStatement(expectedQuery) >> preparedStatement
        }
        ConnectionProvider connectionProvider = Stub {
            connect() >> connection
        }


        SearchCustomerDataSource dataSource = new CustomerDbConnector(connectionProvider)

        when:

        List<Customer> foundCustomers = dataSource.findCustomer(firstName, lastName)
        Customer customer = foundCustomers.first()

        then:
        foundCustomers.size() == 1
        customer.firstName == firstName
        customer.lastName == lastName
        customer.title == factory.getForString(academicTitle)
        customer.eMailAddress == emailAddress


        where:
        id | firstName | lastName | academicTitle | emailAddress
        0 | "luke" | "skywalker" | "Dr." | "sith@jedicouncil.universe"
    }

}
