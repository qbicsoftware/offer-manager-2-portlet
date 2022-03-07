package life.qbic.portal.portlet.persons.create

import life.qbic.business.persons.Person
import life.qbic.business.persons.PersonExistsException
import life.qbic.business.persons.create.CreatePerson
import life.qbic.business.persons.create.CreatePersonDataSource
import life.qbic.business.persons.create.CreatePersonOutput
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

  def "given full information add the customer using a mocked data source"() {
    given: "A new create customer use case instance"
    CreatePerson useCase = new CreatePerson(output, dataSource)

    when: "The use case method is called"
    useCase.createPerson(customer)

    then: "The customer is added using the data source"
    1 * dataSource.addPerson(customer)

    where:
    customer = new Person("my.user@id.de", "FirstName", "LastName", "Title", "email", [])
  }


  def "datasource throwing an exception leads to fail notification on output"() {
    given: "a data source that throws an exception"
    dataSource.addPerson(_ as Person) >> { throw new RuntimeException("Some unexpected runtime exception.") }
    CreatePerson useCase = new CreatePerson(output, dataSource)

    when: "the use case is executed"
    useCase.createPerson(customer)

    then: "the output receives a failure notification"
    1 * output.failNotification(_ as String)
    0 * output.personCreated(_ as Person)

    where:
    customer = new Person("my.user@id.de", "FirstName", "LastName", "Title", "email", [])
  }

  def "when the person already exists, then a failure notification is send"() {
    when: "the person already exists"
    dataSource.addPerson(_ as Person) >> { throw new PersonExistsException("The person already exists.") }
    output.failNotification(_ as String) >> { println it}
    CreatePerson useCase = new CreatePerson(output, dataSource)

    useCase.createPerson(customer)
    then: "a failure notification is send"
    0 * output.personCreated(_)
    1 * output.failNotification(_)

    where:
    customer = new Person("my.user@id.de", "FirstName", "LastName", "Title", "email", [])

  }


}
