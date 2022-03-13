package life.qbic.portal.portlet.persons.update

import life.qbic.business.persons.Person
import life.qbic.business.persons.affiliation.Affiliation
import life.qbic.business.persons.affiliation.AffiliationCategory
import life.qbic.business.persons.create.CreatePersonDataSource
import life.qbic.business.persons.update.UpdatePerson
import life.qbic.business.persons.update.UpdatePersonOutput
import spock.lang.Specification

/**
 * <short description>
 *
 * <detailed description>
 *
 * @since: 1.0.0
 */
class UpdatePersonInputSpec extends Specification {
  UpdatePersonOutput output
  CreatePersonDataSource dataSource
  private static Person newPerson = new Person(null, "first", "last", "", "first.last@n.ame", [])
  private static Person existingPerson =  new Person("my-old-user-id", "test", "Mustermann", "Prof. Dr.", "abc@def.ge", [])
  private static Person existingPersonPlusAffiliations = new Person(existingPerson.getUserId(), existingPerson.getFirstName(), existingPerson.getLastName(), existingPerson.getTitle(), existingPerson.getEmail(), [new Affiliation("orga", "addressAddition", "street", "1234", "city", "country", AffiliationCategory.EXTERNAL)])


  def setup() {
    output = Mock()
    dataSource = Mock()
  }

  def "given customer changes, update the customer using a mocked data source"() {
    given: "A new update customer use case instance"
    UpdatePerson useCase = new UpdatePerson(output, dataSource)
    dataSource.updatePerson(oldPerson, updatedPerson) >> UpdatePersonInputSpec::updatedPersonWithPreservedUserId

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
    UpdatePerson useCase = new UpdatePerson(output, dataSource)
    dataSource.updatePerson(oldPerson, updatedPerson as Person) >> UpdatePersonInputSpec::updatedPersonWithPreservedUserId

    when: "The use case method is called"
    useCase.updatePerson(oldPerson, updatedPerson as Person)

    then: "Only customer affiliations are updated using the data source"
    0 * dataSource.updatePerson(oldPerson, updatedPerson)
    1 * dataSource.updatePersonAffiliations(updatedPerson)
    1 * output.personUpdated(expectedPerson)
    expectedPerson.isActive
    println "expectedPerson.affiliations = $expectedPerson.affiliations"

    where:
    oldPerson = existingPerson
    updatedPerson = existingPersonPlusAffiliations
    expectedPerson = existingPersonPlusAffiliations
  }

  def "datasource throwing an exception leads to fail notification on output"() {
      given: "a data source that throws an exception"
      dataSource._ >> { throw new RuntimeException("Something went wrong.") }
      UpdatePerson useCase = new UpdatePerson(output, dataSource)

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
    if (p1.userId != person.userId) return false
    return true
  }
  private static Person updatedPersonWithPreservedUserId(Person oldPerson, Person updatedPerson) {
    Person person = new Person(oldPerson.getUserId(), updatedPerson.getFirstName(), updatedPerson.getLastName(), updatedPerson.getTitle(), updatedPerson.getEmail(), updatedPerson.getAffiliations())
    person.setIsActive(true)
    return person
  }
}
