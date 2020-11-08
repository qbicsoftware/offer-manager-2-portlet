package life.qbic.portal.qoffer2.customers

import groovy.util.logging.Log4j2
import life.qbic.datamodel.dtos.business.Affiliation
import life.qbic.datamodel.dtos.business.Customer
import life.qbic.portal.portlet.customers.affiliation.create.CreateAffiliationDataSource
import life.qbic.portal.portlet.customers.affiliation.list.ListAffiliationsDataSource
import life.qbic.portal.portlet.customers.create.CreateCustomerDataSource
import life.qbic.portal.portlet.customers.search.SearchCustomerDataSource
import life.qbic.portal.portlet.customers.update.UpdateCustomerDataSource
import life.qbic.portal.portlet.exceptions.DatabaseQueryException

/**
 * Provides operations on QBiC customer data
 *
 * This class implements the data sources of the different use cases and is responsible for transferring data from the database towards them
 *
 * @since: 1.0.0
 * @author: Jennifer Bödker
 *
 */
@Log4j2
class CustomerDbConnector implements CreateCustomerDataSource, UpdateCustomerDataSource, SearchCustomerDataSource, CreateAffiliationDataSource, ListAffiliationsDataSource {


  CustomerDatabaseQueries databaseQueries

  CustomerDbConnector(CustomerDatabaseQueries databaseQueries){
    this.databaseQueries = databaseQueries
  }

  @Override
  List<Customer> findCustomer(String firstName, String lastName) throws DatabaseQueryException {
    throw new Exception("Method not implemented.")
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

  /**
   *@inheritDoc
   */
  @Override
  void addAffiliation(Affiliation affiliation) {
    try {
      databaseQueries.addAffiliation(affiliation)
    } catch (DatabaseQueryException e) {
      throw new DatabaseQueryException(e.message)
    } catch (Exception e) {
      log.error(e)
      log.error(e.stackTrace.join("\n"))
      throw new DatabaseQueryException("The affiliation could not be created: ${affiliation.toString()}")
    }
  }
}
