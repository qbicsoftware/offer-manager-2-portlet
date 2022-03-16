package life.qbic.portal.portlet.persons.create

import life.qbic.business.persons.Person
import life.qbic.business.persons.PersonExistsException
import life.qbic.business.persons.affiliation.Affiliation
import life.qbic.business.persons.affiliation.AffiliationCategory
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
  CreatePersonOutput output = Mock()
  CreatePersonDataSource dataSource = Mock()

  private static Person newPerson = new Person(null, "first", "last", "", "first.last@n.ame", [])
  private static Person existingPerson =  new Person("my-old-user-id", "test", "Mustermann", "Prof. Dr.", "abc@def.ge", [])
  private static Person existingPersonPlusAffiliations = new Person(existingPerson.getUserId(), existingPerson.getFirstName(), existingPerson.getLastName(), existingPerson.getTitle(), existingPerson.getEmail(), [new Affiliation("orga", "addressAddition", "street", "1234", "city", "country", AffiliationCategory.EXTERNAL)])



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

  def "given customer changes, update the customer using a mocked data source"() {
    given: "A new update customer use case instance"
    CreatePerson useCase = new CreatePerson(output, dataSource)
    dataSource.updatePerson(oldPerson, updatedPerson) >> CreatePersonSpec::updatedPersonWithPreservedUserId

    when: "The use case method is called"
    useCase.updatePerson(oldPerson, updatedPerson)

    then: "The customer is updated using the data source"
    with(dataSource) {
      1 * updatePerson(oldPerson, updatedPerson)
      0 * updatePersonAffiliations(_)
    }
    1 * output.personUpdated({ Person it -> hasSamePersonInformation(it, expectedPerson) })
    expectedPerson.isActive

    where:
    updatedPerson = newPerson
    oldPerson = existingPerson
    expectedPerson = updatedPersonWithPreservedUserId(oldPerson, updatedPerson)
  }

  def "given no person specific data changes, update the affiliations using a mocked data source"() {
    given: "A new update customer use case instance"
    CreatePerson useCase = new CreatePerson(output, dataSource)
    dataSource.updatePerson(oldPerson, expectedPerson) >> CreatePersonSpec::updatedPersonWithPreservedUserId

    when: "The use case method is called"
    useCase.updatePerson(oldPerson, updatedPerson)

    then: "Only customer affiliations are updated using the data source"
    1 * dataSource.updatePersonAffiliations(updatedPerson)
    0 * dataSource.updatePerson(oldPerson, expectedPerson)
    1 * output.personUpdated(updatedPerson)
    expectedPerson.isActive
    println "expectedPerson.affiliations = $expectedPerson.affiliations"

    where:
    oldPerson = existingPerson
    updatedPerson = existingPersonPlusAffiliations
    expectedPerson = updatedPersonWithPreservedUserId(existingPerson, existingPersonPlusAffiliations)
  }

  def "given a failing datasource, when a person is updated, then a fail notification is sent"() {
    given: "a data source that throws an exception"
    dataSource._ >> { throw new RuntimeException("Something went wrong.") }
    CreatePerson useCase = new CreatePerson(output, dataSource)

    when: "the use case is executed"
    useCase.updatePerson(oldPerson, updatedPerson)

    then: "the output receives a failure notification"
    1 * output.failNotification(_ as String)
    0 * output.personUpdated(_ as Person)

    where:
    oldPerson = existingPerson
    updatedPerson << [
            newPerson,
            existingPersonPlusAffiliations
    ]
  }

  private static boolean hasSamePersonInformation(Person p1, Person p2) {
    if (p1.is(p2)) return true
    if (!(p2 instanceof Person)) return false

    Person person = (Person) p2

    if (p1.isActive != person.isActive) return false
    if (p1.affiliations != person.affiliations) return false
    if (p1.email != person.email) return false
    if (p1.firstName != person.firstName) return false
    if (p1.id != person.id) return false
    if (p1.lastName != person.lastName) return false
    if (p1.title != person.title) return false
    if (p1.userId && p2.userId) { // if one or both userIds are not set, ignore them
      if (p1.userId != person.userId) return false
    }
    return true
  }

  private static Person updatedPersonWithPreservedUserId(Person oldPerson, Person updatedPerson) {
    Person person = new Person(oldPerson.getUserId(), updatedPerson.getFirstName(), updatedPerson.getLastName(), updatedPerson.getTitle(), updatedPerson.getEmail(), updatedPerson.getAffiliations())
    person.setIsActive(true)
    return person
  }

}
