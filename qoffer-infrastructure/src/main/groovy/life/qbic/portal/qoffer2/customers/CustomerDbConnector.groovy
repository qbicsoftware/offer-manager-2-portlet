package life.qbic.portal.qoffer2.customers

import life.qbic.datamodel.persons.Person
import life.qbic.portal.portlet.customers.Customer
import life.qbic.portal.portlet.customers.CustomerDbGateway
import life.qbic.portal.portlet.offers.SearchCriteria

/**
 * Provides operations on QBiC customer data
 *
 * This class implements {@link CustomerDbGateway} and is responsible for transferring data from the database into qOffer
 *
 * @since: 1.0.0
 * @author: Jennifer Bödker
 *
 */
class CustomerDbConnector implements CustomerDbGateway{

  @Override
  List<Customer> findCustomer(SearchCriteria criteria) {
    return null
  }

  @Override
  void addCustomer(Customer customer) {

  }

  @Override
  void updateCustomer(String customerId, Customer updatedCustomer) {

  }
}
