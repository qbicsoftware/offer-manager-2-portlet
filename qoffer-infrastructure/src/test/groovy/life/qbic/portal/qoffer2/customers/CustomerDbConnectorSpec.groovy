package life.qbic.portal.qoffer2.customers

import life.qbic.datamodel.dtos.business.AcademicTitle
import life.qbic.datamodel.dtos.business.Customer
import life.qbic.portal.portlet.CriteriaType
import life.qbic.portal.portlet.SearchCriteria
import life.qbic.portal.portlet.customers.CustomerDbGateway
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


class CustomerDbConnectorSpec extends Specification {
//https://www.baeldung.com/spock-stub-mock-spy

    static class Publisher {
        List<Subscriber> subscribers = new ArrayList<>()

        void addSubscriber(Subscriber subscriber) {
            subscribers.add(subscriber)
        }

        void send(String message) {
            for (Subscriber subscriber : subscribers)
                subscriber.receive(message);
        }
    }

    static interface Subscriber {
        String receive(String message)
    }

    static class MySubscriber implements Subscriber {
        @Override
        String receive(String message) {
            if (message ==~ /[A-Za-z ]+/)
                return "ok"
            return "uh-oh"
        }
    }

    Subscriber realSubscriber1 = new MySubscriber()
    Subscriber realSubscriber2 = new MySubscriber()
    Publisher publisher = new Publisher(subscribers: [realSubscriber1, realSubscriber2])

    def "Mocks can simulate behaviour and have interactions"() {
        given:
        def mockSubscriber = Mock(Subscriber) {
            3 * receive(_) >>> ["hey", "ho"]
        }
        publisher.addSubscriber(mockSubscriber)

        when:
        publisher.send("Hello subscribers")
        publisher.send("Anyone there?")

        then: "check interactions"
        1 * mockSubscriber.receive("Hello subscribers")
        1 * mockSubscriber.receive("Anyone there?")

        and: "check behaviour exactly 3 times"
        mockSubscriber.receive("foo") == "hey"
        mockSubscriber.receive("bar") == "ho"
        mockSubscriber.receive("zot") == "ho"
    }
    CustomerDatabaseQueries customerDbQueries = new CustomerDatabaseQueries()
    CustomerDbConnector customerDbConnector = new CustomerDbConnector(customerDbQueries)

    def "Find customer by valid last name is successful"() {
        given:
        SearchCriteria validName = new SearchCriteria(CriteriaType.LAST_NAME,"McDuck")
        def mockDbGateway = Mock(CustomerDbGateway) {
            1 * findCustomer(validName)
        }

        when:
        def foundDudes = customerDbQueries.findPersonByName(validName.criteriaValue) >> [new Customer('Scrooge', 'McDuck', AcademicTitle.DOCTOR, "Scrooge@McDuck.com",[])]
        then: "check interactions"
        1 * customerDbQueries.findPersonByName("McDuck")

        and: "check behaviour"
        assert foundDudes[0] == "McDuck"
    }
}



/*
    CustomerDatabaseQueries customerDatabaseQueries
    CustomerDbConnector customerDbConnector

   def setup(){

        def mockCustomerDatabaseQueries = Mock(CustomerDatabaseQueries)
        customerDbConnector = new CustomerDbConnector(mockCustomerDatabaseQueries)

    }

    def "Find customer by valid last name is successful"() {

        given: "a SearchCriteria containing a valid last name example"

        SearchCriteria validName = new SearchCriteria(CriteriaType.LAST_NAME,"McDuck")

        and: "A connector class providing the methods necessary for backend access"
        CustomerDatabaseQueries customerDatabaseQueries = Stub(CustomerDatabaseQueries.class)
        customerDatabaseQueries.findPersonByName(validName.criteriaValue) >> [new Customer('Scrooge', 'McDuck', AcademicTitle.DOCTOR, "Scrooge@McDuck.com",[])]

        when: "we try to find the customer in the database with the given searchCriteria"
        List<Customer> validCustomerList = customerDbConnector.findCustomer(validName)

        then: "we retrieve a List of customer DTOs corresponding to the given search criteria containing the valid lastName"
        assert validCustomerList[0].lastName == "McDuck"
    }

        //customerDbConnector.findCustomer(validName) > [new Customer('Scrooge', 'McDuck', AcademicTitle.DOCTOR, "Scrooge@McDuck.com",[])]
        //customerDatabaseQueries.findPersonByName(validName) >> [new Customer('Scrooge', 'McDuck', AcademicTitle.DOCTOR, "Scrooge@McDuck.com",[])]
    }
*/
/*
    def "Find customer fails because last name doesn't occur in database"() {
        given:
        String invalidName = "Beagle_Boy123"

        when:
        List<Customer> invalidCustomerList = customerDatabaseQueries.findPersonByName(invalidName)

        then:
        assert invalidCustomerList.is(List)
        assert invalidCustomerList.isEmpty()

    }

    def "Find customer by additional address is successful"() {
        given:
        String validAddAddress = "Data Management Facility"

        when:
        List<Customer> invalidCustomerList = customerDatabaseQueries.findPersonByName(validAddAddress)

        then:
        assert invalidCustomerList.is(List)
        assert invalidCustomerList.isEmpty()

    }

    def "Find customer fails because additional address doesn't occur in database"() {
        given:
        String invalidAddAddress = "Space Station"

        when:
        List<Customer> invalidCustomerList = customerDatabaseQueries.findPersonByName(invalidAddAddress)

        then:
        assert invalidCustomerList.is(List)
        assert invalidCustomerList.isEmpty()

    }

    def "Find customer by city is successful"() {
        given:
        String validCity = "Springfield"

        when:
        List<Customer> invalidCustomerList = customerDatabaseQueries.findPersonByName(validCity)

        then:
        assert invalidCustomerList.is(List)
        assert invalidCustomerList.isEmpty()

    }

    def "Find customer fails because city doesn't occur in database"() {
        given:
        String invalidCity = "Shelbyville"

        when:
        List<Customer> invalidCustomerList = customerDatabaseQueries.findPersonByName(invalidCity)

        then:
        assert invalidCustomerList.is(List)
        assert invalidCustomerList.isEmpty()

    }

    def "Find customer by group is successful"() {
        given:
        String validGroup = "QBiC"

        when:
        List<Customer> invalidCustomerList = customerDatabaseQueries.findPersonByName(validGroup)

        then:
        assert invalidCustomerList.is(List)
        assert invalidCustomerList.isEmpty()

    }

    def "Find customer fails because group doesn't occur in database"() {
        given:
        String invalidGroup = "Illuminati"

        when:
        List<Customer> invalidCustomerList = customerDatabaseQueries.findPersonByName(invalidGroup)

        then:
        assert invalidCustomerList.is(List)
        assert invalidCustomerList.isEmpty()

    }
}
*/