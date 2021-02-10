package life.qbic.portal.portlet.customers.update

import life.qbic.business.customers.update.*
import life.qbic.datamodel.dtos.business.AcademicTitle
import life.qbic.datamodel.dtos.business.Customer

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
  UpdateCustomerDataSource dataSource


  def setup() {
      output = Mock()
      dataSource = Mock()
  }

  def "given full information update the customer using a mocked data source"(){
      given: "A new update customer use case instance"
      UpdateCustomer useCase = new UpdateCustomer(output, dataSource)

      when: "The use case method is called"
      useCase.updateCustomer(customerId, customer)

      then: "The customer is updated using the data source"
      1 * dataSource.updateCustomer(customerId, customer)

      where:
      customer = new Customer.Builder("Test", "user", "test").title(AcademicTitle.NONE).build()
      customerId = new String("42")
  }

  def "datasource throwing an exception leads to fail notification on output"() {
      given: "a data source that throws an exception"
      dataSource.updateCustomer(_ as String, _ as Customer) >> { throw new Exception("Something went wrong.") }
      UpdateCustomer useCase = new UpdateCustomer(output, dataSource)

      when: "the use case is executed"
      useCase.updateCustomer(customerId, customer)

      then: "the output receives a failure notification"
      1 * output.failNotification(_ as String)
      0 * output.successNotification(_ as String)

      where:
      customer = new Customer.Builder("Test", "user", "test").title(AcademicTitle.NONE).build()
      customerId = new String("420")
  }
}
