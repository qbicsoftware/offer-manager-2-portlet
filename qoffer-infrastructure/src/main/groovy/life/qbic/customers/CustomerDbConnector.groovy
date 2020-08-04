package life.qbic.customers

import life.qbic.datamodel.persons.Person
import life.qbic.portal.portlet.customers.CustomerDbGateway

/**
 * Handles the connection to the customer database
 *
 * This class implements {@link CustomerDbGateway} and is responsible for transferring data from the database into qOffer
 *
 * @since: 1.0.0
 * @author: Jennifer Bödker
 *
 */
class CustomerDbConnector implements CustomerDbGateway{

  @Override
  Person searchCustomer(Map criteria) {
    return null
  }

  @Override
  void saveCustomer(Person customer) {

  }
}
