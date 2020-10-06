package life.qbic.portal.qoffer2.customers

import groovy.util.logging.Log4j2
import life.qbic.datamodel.dtos.business.Affiliation
import life.qbic.datamodel.dtos.business.Customer
import life.qbic.portal.portlet.CriteriaType

import life.qbic.portal.portlet.SearchCriteria
import life.qbic.portal.portlet.customers.affiliation.create.CreateAffiliationDataSource
import life.qbic.portal.portlet.customers.affiliation.list.ListAffiliationsDataSource
import life.qbic.portal.portlet.customers.create.CreateCustomerDataSource
import life.qbic.portal.portlet.customers.update.UpdateCustomerDataSource
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
@Log4j2
class CustomerDbConnector implements CreateCustomerDataSource, UpdateCustomerDataSource, CreateAffiliationDataSource, ListAffiliationsDataSource {

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
        List<Customer> customer = databaseQueries.findPersonByName(searchCondition)
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
    try {
      databaseQueries.addCustomer(customer)
    } catch (DatabaseQueryException e) {
      throw new DatabaseQueryException(e.message)
    } catch (Exception e) {
      log.error(e)
      log.error(e.stackTrace.join("\n"))
      throw new DatabaseQueryException("The customer could not be created: ${customer.toString()}")
    }
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
  List<Affiliation> listAllAffiliations() {
    databaseQueries.getAffiliations()
  }
}
