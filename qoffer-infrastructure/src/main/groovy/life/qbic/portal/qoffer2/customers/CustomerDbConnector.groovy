package life.qbic.portal.qoffer2.customers

import life.qbic.datamodel.dtos.business.Affiliation
import life.qbic.datamodel.dtos.business.Customer
import life.qbic.datamodel.people.Person
import life.qbic.portal.portlet.CriteriaType

import life.qbic.portal.portlet.customers.CustomerDbGateway
import life.qbic.portal.portlet.SearchCriteria
import life.qbic.portal.portlet.exceptions.DatabaseQueryException
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger


/**
 * Provides operations on QBiC customer data
 *
 * This class implements {@link CustomerDbGateway} and is responsible for transferring data from the database into qOffer
 *
 * @since: 1.0.0
 * @author: Jennifer Bödker
 *
 */
class CustomerDbConnector implements CustomerDbGateway {

  CustomerDatabaseQueries databaseQueries

  private static final Logger LOG = LogManager.getLogger(CustomerDbConnector.class)

  CustomerDbConnector(CustomerDatabaseQueries databaseQueries){
    this.databaseQueries = databaseQueries
  }

  /**
   * @inheritDoc
   * @param criteria
   * @return
   */
  @Override
  List<Customer> findCustomer(SearchCriteria criteria) {
    String searchCondition = criteria.criteriaValue

    switch(criteria.criteriaType){
      case CriteriaType.LAST_NAME:
        //todo create a Customer
        List<Person> person = databaseQueries.findPersonByName(searchCondition)
            return null
      case CriteriaType.GROUP_NAME:
            return databaseQueries.findCustomerByGroup(searchCondition)
      case CriteriaType.ADD_ADDRESS:
            return databaseQueries.findCustomerByAdditionalAddress(searchCondition)
      case CriteriaType.CITY:
            return databaseQueries.findCustomerByCity(searchCondition)
      default:
        //todo throw an exception
        return null
    }

  }

  /**
   * @inheritDoc
   * @param customer
   */
  @Override
  void addCustomer(Customer customer) throws DatabaseQueryException {
    databaseQueries.addCustomer(customer)

  }
  /**
   * @inheritDoc
   * @param customerId
   * @param updatedCustomer
   */
  @Override
  void updateCustomer(String customerId, Customer updatedCustomer) {
    databaseQueries.updateCustomer(customerId,updatedCustomer)

  }

  /**
   * @inheritDoc
   * @return
   */
  @Override
  List<Affiliation> getAllAffiliations() {
    databaseQueries.getAffiliations()
  }
}
