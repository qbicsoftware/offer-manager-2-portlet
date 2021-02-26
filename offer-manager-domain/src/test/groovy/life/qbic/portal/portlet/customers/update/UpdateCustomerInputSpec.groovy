package life.qbic.portal.portlet.customers.update

import life.qbic.business.customers.create.CreatePersonDataSource
import life.qbic.business.customers.update.*
import life.qbic.datamodel.dtos.business.AcademicTitle
import life.qbic.datamodel.dtos.business.Customer
import life.qbic.datamodel.dtos.business.Affiliation

import spock.lang.Specification

/**
 * <short description>
 *
 * <detailed description>
 *
 * @since: 1.0.0
 */
class UpdateCustomerInputSpec extends Specification {
    UpdateCustomerOutput output
    CreatePersonDataSource dataSource


  def setup() {
      output = Mock()
      dataSource = Mock()
  }

  def "given customer changes, update the customer using a mocked data source"(){
      given: "A new update customer use case instance"
      UpdateCustomer useCase = new UpdateCustomer(output, dataSource)
      dataSource.getPerson(42) >> new Customer.Builder("Test", "user", "oldmail").title(AcademicTitle.NONE).build()

      when: "The use case method is called"
      useCase.updateCustomer(customerId, customer)

      then: "The customer is updated using the data source"
      1 * dataSource.updatePerson(customerId, customer)
      0 * dataSource.updatePersonAffiliations(_ as String, _ as List<Affiliation>)

      where:
      customer = new Customer.Builder("Test", "user", "newmail").title(AcademicTitle.NONE).build()
      customerId = 42
  }
  
  def "given no customer changes, update the affiliations using a mocked data source"(){
      given: "A new update customer use case instance"
      UpdateCustomer useCase = new UpdateCustomer(output, dataSource)
      dataSource.getPerson(42) >> new Customer.Builder("Test", "user", "oldmail").title(AcademicTitle.NONE).affiliation(affiliation1).build()

      when: "The use case method is called"
      useCase.updateCustomer(customerId, customer)

      then: "The customer affiliations are updated using the data source"
      0 * dataSource.updatePerson(_ as String, _ as Customer)
      1 * dataSource.updatePersonAffiliations(customerId, twoAffiliations)

      where:
      affiliation1 = new Affiliation.Builder(
        "org", "street", "zip", "city").build()
      twoAffiliations = new ArrayList<Affiliation>(Arrays.asList(new Affiliation.Builder(
        "other org", "other street", "zip", "city").build(), affiliation1))
      customer = new Customer.Builder("Test", "user", "oldmail").title(AcademicTitle.NONE).affiliations(twoAffiliations).build()
      customerId = 42
  }
  
  def "datasource throwing an exception leads to fail notification on output"() {
      given: "a data source that throws an exception"
      dataSource.getPerson(_ as Integer) >> new Customer.Builder("Test", "user", "oldmail").title(AcademicTitle.NONE).build()
      dataSource.updatePerson(_ as Integer, _ as Customer) >> { throw new Exception("Something went wrong.") }
      UpdateCustomer useCase = new UpdateCustomer(output, dataSource)

      when: "the use case is executed"
      useCase.updateCustomer(customerId, customer)

      then: "the output receives a failure notification"
      1 * output.failNotification(_ as String)
      0 * output.customerUpdated(_ as Customer)

      where:
      customer = new Customer.Builder("Test", "user", "newmail").title(AcademicTitle.NONE).build()
      customerId = 420
  }
}
