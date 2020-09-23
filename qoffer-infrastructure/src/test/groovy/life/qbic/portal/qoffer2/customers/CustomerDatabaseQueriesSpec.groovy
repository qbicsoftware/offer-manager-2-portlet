package life.qbic.portal.qoffer2.customers
import life.qbic.datamodel.dtos.business.Customer
import spock.lang.Specification

/**
 * Test the database queries in terms of valid and invalid input
 *
 * This class should be used and updated whenever individual queries to the database are involved or changed
 *
 * @since: 1.0.0
 *
 */

//ToDo Find valid and invalid example in database for testing.
// Determine how update and addition cases should be handled without spamming the database


class CustomerDatabaseQueriesSpec extends Specification {


    final CustomerDatabaseQueries customerDatabaseQueries

    def "Find customer by valid last name is successful"() {
        given:
        String validName = "McScrooge"

        when:
        List<Customer> validCustomerList = customerDatabaseQueries.findPersonByName(validName)

        then:
        assert validCustomerList.is(List<Customer>.class)
        for (Customer in validCustomerList) {
            assert Customer.lastName = validName
        }

    }

    def "Find customer fails because last name doesn't occur in database"() {
        given:
        String invalidName = "Beagle_Boy123"

        when:
        List<Customer> invalidCustomerList = customerDatabaseQueries.findPersonByName(invalidName)

        then:
        assert invalidCustomerList.is(List<Customer>.class)
        assert invalidCustomerList.isEmpty()

    }

    def "Find customer by additional address is successful"() {
        given:
        String validAddAddress = "Data Management Facility"

        when:
        List<Customer> invalidCustomerList = customerDatabaseQueries.findPersonByName(validAddAddress)

        then:
        assert invalidCustomerList.is(List<Customer>.class)
        assert invalidCustomerList.isEmpty()

    }

    def "Find customer fails because additional address doesn't occur in database"() {
        given:
        String invalidAddAddress = "Space Station"

        when:
        List<Customer> invalidCustomerList = customerDatabaseQueries.findPersonByName(invalidAddAddress)

        then:
        assert invalidCustomerList.is(List<Customer>.class)
        assert invalidCustomerList.isEmpty()

    }

    def "Find customer by city is successful"() {
        given:
        String validCity = "Springfield"

        when:
        List<Customer> invalidCustomerList = customerDatabaseQueries.findPersonByName(validCity)

        then:
        assert invalidCustomerList.is(List<Customer>.class)
        assert invalidCustomerList.isEmpty()

    }

    def "Find customer fails because city doesn't occur in database"() {
        given:
        String invalidCity = "Shelbyville"

        when:
        List<Customer> invalidCustomerList = customerDatabaseQueries.findPersonByName(invalidCity)

        then:
        assert invalidCustomerList.is(List<Customer>.class)
        assert invalidCustomerList.isEmpty()

    }

    def "Find customer by group is successful"() {
        given:
        String validGroup = "QBiC"

        when:
        List<Customer> invalidCustomerList = customerDatabaseQueries.findPersonByName(validGroup)

        then:
        assert invalidCustomerList.is(List<Customer>.class)
        assert invalidCustomerList.isEmpty()

    }

    def "Find customer fails because group doesn't occur in database"() {
        given:
        String invalidGroup = "Illuminati"

        when:
        List<Customer> invalidCustomerList = customerDatabaseQueries.findPersonByName(invalidGroup)

        then:
        assert invalidCustomerList.is(List<Customer>.class)
        assert invalidCustomerList.isEmpty()

    }
}
