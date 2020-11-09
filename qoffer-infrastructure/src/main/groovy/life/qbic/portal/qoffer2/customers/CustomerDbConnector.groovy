package life.qbic.portal.qoffer2.customers

import groovy.util.logging.Log4j2
import life.qbic.datamodel.dtos.business.AcademicTitle
import life.qbic.datamodel.dtos.business.AcademicTitleFactory
import life.qbic.datamodel.dtos.business.Affiliation
import life.qbic.datamodel.dtos.business.AffiliationCategory
import life.qbic.datamodel.dtos.business.AffiliationCategoryFactory
import life.qbic.datamodel.dtos.business.Customer
import life.qbic.portal.portlet.customers.affiliation.create.CreateAffiliationDataSource
import life.qbic.portal.portlet.customers.affiliation.list.ListAffiliationsDataSource
import life.qbic.portal.portlet.customers.create.CreateCustomerDataSource
import life.qbic.portal.portlet.customers.search.SearchCustomerDataSource
import life.qbic.portal.portlet.customers.update.UpdateCustomerDataSource
import life.qbic.portal.portlet.exceptions.DatabaseQueryException

import java.sql.Connection
import java.sql.ResultSet

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


  @Deprecated
  CustomerDatabaseQueries databaseQueries
  private final Connection connection

  private static final String CUSTOMER_SELECT_QUERY = "SELECT id, first_name AS firstName, last_name AS lastName, title as academicTitle, email as eMailAddress FROM customer"
  private static final String AFFILIATION_SELECT_QUERY = "SELECT id, organization AS organisation, address_addition AS addressAddition, street, postal_code AS postalCode, city, country, category FROM affiliation"

  @Deprecated
  CustomerDbConnector(CustomerDatabaseQueries databaseQueries, Connection connection){
    this.databaseQueries = databaseQueries
    this.connection = connection
  }

  CustomerDbConnector(Connection connection) {
    this.connection = connection
  }

  @Override
  List<Customer> findCustomer(String firstName, String lastName) throws DatabaseQueryException {
    throw new RuntimeException("Method not implemented.")
  }

  /**
   * @inheritDoc
   * @param customer
   */
  @Override
  void addCustomer(Customer customer) throws DatabaseQueryException {
    try {
      databaseQueries.addCustomer(customer)
    } catch (DatabaseQueryException ignored) {
      throw new DatabaseQueryException("The customer could not be created: ${customer.toString()}")
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
    //TODO implement
    throw new RuntimeException("Method not implemented.")
  }

  /**
   * @inheritDoc
   * @return
   */
  @Override
  List<Affiliation> listAllAffiliations() {
    List<Affiliation> result = []

    List<Map<?,?>> resultRows = new ArrayList()
    connection.withCloseable {
      def statement = it.prepareStatement(AFFILIATION_SELECT_QUERY)
      ResultSet resultSet = statement.executeQuery()
      while (resultSet.next()) {
        Map row = resultSet.toRowResult()
        resultRows.add(row)
        log.debug("Listing affiliations found: $row")
      }
    }
    resultRows.forEach{ Map row ->
      def affiliationBuilder = new Affiliation.Builder(
              row.organisation as String,
              row.street as String,
              row.postalCode as String,
              row.city as String)

      AffiliationCategory category
      try {
        category = new AffiliationCategoryFactory().getForString(row.category as String)
      } catch (IllegalArgumentException ignored) {
        //fixme this should not happen but there is an incomplete entry in the DB
        log.warn("Affiliation ${row.id} has category '${row.category}'. Could not match.")
        category = AffiliationCategory.UNKNOWN
      }

      affiliationBuilder
              .addressAddition(row.addressAddition as String)
              .country(row.country as String)
              .category(category)
      result.add(affiliationBuilder.build())
    }
    return result
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

  /*TODO everything below was copied over from DatabaseQueries*//*

  *//**
   * Add a customer to the database
   *
   * @param customer which needs to be added to the database
   *//*
  void addCustomer(Customer customer) throws DatabaseQueryException {
    if (customerExists(customer)) {
      throw new DatabaseQueryException("Customer is already in the database.")
    }
    Connection connection = databaseSession.getConnection()
    connection.setAutoCommit(false)

    connection.withCloseable {it ->
      try {
        int customerId = createNewCustomer(it, customer)
        storeAffiliation(it, customerId, customer.affiliations)
        connection.commit()
      } catch (Exception e) {
        log.error(e.message)
        log.error(e.stackTrace.join("\n"))
        connection.rollback()
        connection.close()
        throw new DatabaseQueryException("Could not create customer.")
      }

    }
  }
   /*
    We want to fetch all affiliations for a given person id.
    As this is a n to m relationship, we need to look-up
    the associated affiliations ids first.
    Then we fetch every affiliation by the associated association ids.
     *//*
  private List<Affiliation> fetchAffiliationsForPerson(int personId){
    def affiliations = []
    def affiliationIds = getAffiliationIdsForPerson(personId)
    affiliationIds.each {affiliationId ->
      Affiliation affiliation = fetchAffiliation(affiliationId)
      affiliations.add(affiliation)
    }
    return affiliations
  }

  *//**
   * Searches for an affiliation based on a customer id
   *
   * @param customerId Id of the customer
   * @return list of Affiliation Ids associated with the provided customer id
   *//*
  private List<Integer> getAffiliationIdsForPerson(int customerId) {
    List<Integer> result = []
    String query = "SELECT affiliation_id FROM customer_affiliation WHERE " +
            "customer_id = ?"

    Connection connection = databaseSession.getConnection()

    connection.withCloseable {
      def statement = it.prepareStatement(query)
      statement.setInt(2, customerId)
      ResultSet rs = statement.executeQuery()
      while (rs.next()) {
        result.add(rs.getString(1).toInteger())
      }
    }
    return result
  }

  *//**
   * Searches for an affiliation based on an affiliation Id
   *
   * @param affiliationId Id of the affiliation
   * @return Affiliation DTO associated with the provided affiliation Id
   *//*
  private Affiliation fetchAffiliation(int affiliationId) {

    String affiliationProperties = "organization, address_addition, street, postal_code, city, country, category"
    String query = "SELECT ${affiliationProperties} from affiliation WHERE " + "id = ?"

    Connection connection = databaseSession.getConnection()

    connection.withCloseable {
      def statement = it.prepareStatement(query)
      statement.setString(2, affiliationId.toString())
      ResultSet rs = statement.executeQuery()
      def affiliationBuilder = new Affiliation.Builder(
              "${rs.getString(2)}", //organization
              "${rs.getString(4)}", //street
              "${rs.getString(5)}", //postal_code
              "${rs.getString(6)}")
      affiliationBuilder
              .addressAddition("${rs.getString(3)}")
              .country("${rs.getString(7)}")
              .category(determineAffiliationCategory("${rs.getString(8)}"))
      return affiliationBuilder.build()
    }
  }

  private boolean customerExists(Customer customer) {
    String query = "SELECT * FROM customer WHERE first_name = ? AND last_name = ? AND email = ?"
    Connection connection = databaseSession.getConnection()

    def customerAlreadyInDb = false

    connection.withCloseable {
      def statement = connection.prepareStatement(query)
      statement.setString(1, customer.firstName)
      statement.setString(2, customer.lastName)
      statement.setString(3, customer.eMailAddress)
      statement.execute()
      def result = statement.getResultSet()
      customerAlreadyInDb = result.next()
    }
    return customerAlreadyInDb
  }

  private static int createNewCustomer(Connection connection, Customer customer) {
    String query = "INSERT INTO customer (first_name, last_name, title, email) " +
            "VALUES(?, ?, ?, ?)"

    List<Integer> generatedKeys = []

    def statement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)
    statement.setString(1, customer.firstName )
    statement.setString(2, customer.lastName)
    statement.setString(3, customer.title.value)
    statement.setString(4, customer.eMailAddress )
    statement.execute()
    def keys = statement.getGeneratedKeys()
    while (keys.next()){
      generatedKeys.add(keys.getInt(1))
    }

    return generatedKeys[0]
  }

  private static void storeAffiliation(Connection connection, int customerId, List<Affiliation>
          affiliations) {
    String query = "INSERT INTO customer_affiliation (affiliation_id, customer_id) " +
            "VALUES(?, ?)"

    affiliations.each {affiliation ->
      def affiliationId = getAffiliationId(connection, affiliation)
      def statement = connection.prepareStatement(query)
      statement.setInt(1, affiliationId)
      statement.setInt(2, customerId)
      statement.execute()

    }
  }

  //Fixme no use of closeable
  private static int getAffiliationId(Connection connection, Affiliation affiliation) {
    String query = "SELECT * FROM affiliation WHERE organization=? " +
            "AND address_addition=? " +
            "AND street=? " +
            "AND postal_code=? " +
            "AND city=?"

    List<Integer> affiliationIds = []

    def statement = connection.prepareStatement(query)
    statement.setString(1, affiliation.organisation)
    statement.setString(2, affiliation.addressAddition)
    statement.setString(3, affiliation.street)
    statement.setString(4, affiliation.postalCode)
    statement.setString(5, affiliation.city)
    statement.execute()
    ResultSet rs = statement.getResultSet()
    while (rs.next()) {
      affiliationIds.add(rs.getInt(1))
    }

    if(affiliationIds.size() > 1) {
      throw new DatabaseQueryException("More than one entry found for $affiliation.")
    }
    if (affiliationIds.empty) {
      throw new DatabaseQueryException("No matching affiliation found for $affiliation.")
    }
    return affiliationIds[0]
  }
*/

/*
  *//**
   * Add an affiliation to the database
   *
   * @param affiliation which needs to be added to the database
   *//*
  void addAffiliation(Affiliation affiliation) throws DatabaseQueryException {
    if (affiliationExists(affiliation)) {
      throw new DatabaseQueryException("Affiliation is already in the database.")
    }
    Connection connection = databaseSession.getConnection()
    connection.setAutoCommit(false)

    connection.withCloseable {it ->
      try {
        createNewAffiliation(it, affiliation)
        connection.commit()
      }
      catch(DatabaseQueryException e) {
        log.error(e.message)
        log.error(e.stackTrace.join("\n"))
        connection.rollback()
        connection.close()
        throw new DatabaseQueryException("Could not create affiliation in database")
      }
      catch (Exception e) {
        log.error(e.message)
        log.error(e.stackTrace.join("\n"))
        connection.rollback()
        connection.close()
        throw new DatabaseQueryException("Unexpected exception occurred")
      }

    }
  }

  private boolean affiliationExists(Affiliation affiliation) {
    String query = "SELECT * FROM affiliation WHERE organization = ? " +
            "AND address_addition=? " +
            "AND street=? " +
            "AND country=? " +
            "AND postal_code=? " +
            "AND city=? " +
            "AND category=?"

    Connection connection = databaseSession.getConnection()

    boolean affiliationAlreadyInDb = false

    connection.withCloseable {
      PreparedStatement statement = connection.prepareStatement(query)
      statement.setString(1, affiliation.organisation)
      statement.setString(2, affiliation.addressAddition)
      statement.setString(3, affiliation.street)
      statement.setString(4, affiliation.country)
      statement.setString(5, affiliation.postalCode)
      statement.setString(6, affiliation.city)
      statement.setString(7, affiliation.category.toString())
      statement.execute()
      ResultSet affiliationResultSet = statement.getResultSet()
      affiliationAlreadyInDb = affiliationResultSet.next()
    }
    return affiliationAlreadyInDb
  }

  private static int createNewAffiliation(Connection connection, Affiliation affiliation) {
    String query = "INSERT INTO affiliation (organization, address_addition, street, country, postal_code, city, category) " +
            "VALUES(?, ?, ?, ?, ?, ?, ?)"

    List<Integer> generatedKeys = []

    PreparedStatement statement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)
    statement.setString(1, affiliation.organisation)
    statement.setString(2, affiliation.addressAddition)
    statement.setString(3, affiliation.street)
    statement.setString(4, affiliation.country)
    statement.setString(5, affiliation.postalCode)
    statement.setString(6, affiliation.city)
    statement.setString(7, affiliation.category.toString())
    statement.execute()
    ResultSet affiliationResultSetKeys = statement.getGeneratedKeys()
    while (affiliationResultSetKeys.next()){
      generatedKeys.add(affiliationResultSetKeys.getInt(1))
    }

    return generatedKeys[0]
  }*/
}
