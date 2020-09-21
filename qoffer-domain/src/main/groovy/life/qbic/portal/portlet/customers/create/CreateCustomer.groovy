package life.qbic.portal.portlet.customers.create

import life.qbic.datamodel.dtos.business.Customer
import life.qbic.portal.portlet.customers.CustomerDbGateway
import life.qbic.portal.portlet.exceptions.DatabaseQueryException

/**
 * This use case creates a customer in the system
 *
 * Information on customers such as affiliation and names can be added to the user database.
 *
 * @since: 1.0.0
 * @author: Tobias Koch
 */
class CreateCustomer implements CreateCustomerInput {

  private CustomerDbGateway customerDbGateway
  private CreateCustomerOutput output


  CreateCustomer(CreateCustomerOutput output, CustomerDbGateway customerDbGateway){
    this.output = output
    this.customerDbGateway = customerDbGateway

    List affilitations = customerDbGateway.getAllAffiliations()
    println affilitations
  }

  @Override
  void createCustomer(Customer customer) {
    try{
      customerDbGateway.addCustomer(customer)
      output.successNotification("Successfully added new Customer")
    }catch(DatabaseQueryException databaseQueryException){
      output.failNotification(databaseQueryException.message)
    }
  }
}
