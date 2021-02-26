package life.qbic.portal.portlet.customers.create

import life.qbic.business.customers.create.CreatePerson
import life.qbic.business.customers.create.CreatePersonDataSource
import life.qbic.business.customers.create.CreatePersonOutput
import life.qbic.datamodel.dtos.business.AcademicTitle
import life.qbic.datamodel.dtos.business.Customer
import life.qbic.datamodel.dtos.general.Person
import spock.lang.Specification

/**
 * This test class tests for the use case functionality
 *
 * Given information about a customer a user wants to create the customer in the system
 *
 * @since: 1.0.0
 * @author: Tobias Koch
 */
class CreatePersonSpec extends Specification {
    CreatePersonOutput output
    CreatePersonDataSource dataSource


    def setup() {
        output = Mock()
        dataSource = Mock()
    }

    def "given full information add the customer using a mocked data source"(){
        given: "A new create customer use case instance"
        CreatePerson useCase = new CreatePerson(output, dataSource)

        when: "The use case method is called"
        useCase.createPerson(customer)

        then: "The customer is added using the data source"
        1 * dataSource.addPerson(customer)

        where:
        customer = new Customer.Builder("Test", "user", "test").title(AcademicTitle.NONE).build()
    }

    def "datasource throwing an exception leads to fail notification on output"() {
        given: "a data source that throws an exception"
        dataSource.addPerson(_ as Customer) >> { throw new Exception("Something went wrong.") }
        CreatePerson useCase = new CreatePerson(output, dataSource)

        when: "the use case is executed"
        useCase.createPerson(customer)

        then: "the output receives a failure notification"
        1 * output.failNotification(_ as String)
        0 * output.customerCreated(_ as Person)

        where:
        customer = new Customer.Builder("Test", "user", "test").title(AcademicTitle.NONE).build()
    }

}
