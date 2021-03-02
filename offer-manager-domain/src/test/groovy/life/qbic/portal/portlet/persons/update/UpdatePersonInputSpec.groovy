package life.qbic.portal.portlet.persons.update

import life.qbic.business.persons.create.CreatePersonDataSource
import life.qbic.business.persons.update.*
import life.qbic.datamodel.dtos.business.AcademicTitle
import life.qbic.datamodel.dtos.business.Customer
import life.qbic.datamodel.dtos.business.Affiliation
import life.qbic.datamodel.dtos.general.CommonPerson
import life.qbic.datamodel.dtos.general.Person
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


  def setup() {
      output = Mock()
      dataSource = Mock()
  }

  def "given customer changes, update the customer using a mocked data source"(){
      given: "A new update customer use case instance"
      UpdatePerson useCase = new UpdatePerson(output, dataSource)
      dataSource.getPerson(42) >> new CommonPerson.Builder("Test", "user", "oldmail").title(AcademicTitle.NONE).build()

      when: "The use case method is called"
      useCase.updatePerson(customerId, customer)

      then: "The customer is updated using the data source"
      1 * dataSource.updatePerson(customerId, customer)
      0 * dataSource.updatePersonAffiliations(_ as String, _ as List<Affiliation>)

      where:
      customer = new CommonPerson.Builder("Test", "user", "newmail").title(AcademicTitle.NONE).build()
      customerId = 42
  }
  
  def "given no customer changes, update the affiliations using a mocked data source"(){
      given: "A new update customer use case instance"
      UpdatePerson useCase = new UpdatePerson(output, dataSource)
      dataSource.getPerson(42) >> new CommonPerson.Builder("Test", "user", "oldmail").title(AcademicTitle.NONE).affiliation(affiliation1).build()

      when: "The use case method is called"
      useCase.updatePerson(customerId, customer)

      then: "The customer affiliations are updated using the data source"
      0 * dataSource.updatePerson(_ as String, _ as Customer)
      1 * dataSource.updatePersonAffiliations(customerId, twoAffiliations)

      where:
      affiliation1 = new Affiliation.Builder(
        "org", "street", "zip", "city").build()
      twoAffiliations = new ArrayList<Affiliation>(Arrays.asList(new Affiliation.Builder(
        "other org", "other street", "zip", "city").build(), affiliation1))
      customer = new CommonPerson.Builder("Test", "user", "oldmail").title(AcademicTitle.NONE).affiliations(twoAffiliations).build()
      customerId = 42
  }
  
  def "datasource throwing an exception leads to fail notification on output"() {
      given: "a data source that throws an exception"
      dataSource.getPerson(_ as Integer) >> new CommonPerson.Builder("Test", "user", "oldmail").title(AcademicTitle.NONE).build()
      dataSource.updatePerson(_ as Integer, _ as Person) >> { throw new Exception("Something went wrong.") }
      UpdatePerson useCase = new UpdatePerson(output, dataSource)

      when: "the use case is executed"
      useCase.updatePerson(customerId, customer)

      then: "the output receives a failure notification"
      1 * output.failNotification(_ as String)
      0 * output.personUpdated(_ as Person)

      where:
      customer = new CommonPerson.Builder("Test", "user", "newmail").title(AcademicTitle.NONE).build()
      customerId = 420
  }
}
