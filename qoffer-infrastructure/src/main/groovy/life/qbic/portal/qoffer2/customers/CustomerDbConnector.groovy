package life.qbic.portal.qoffer2.customers

import groovy.util.logging.Log4j2
import life.qbic.datamodel.dtos.business.AcademicTitle
import life.qbic.datamodel.dtos.business.AcademicTitleFactory
import life.qbic.datamodel.dtos.business.Affiliation
import life.qbic.datamodel.dtos.business.AffiliationCategory
import life.qbic.datamodel.dtos.business.AffiliationCategoryFactory
import life.qbic.datamodel.dtos.business.Customer
import life.qbic.datamodel.dtos.general.Person
import life.qbic.portal.portlet.customers.affiliation.create.CreateAffiliationDataSource
import life.qbic.portal.portlet.customers.affiliation.list.ListAffiliationsDataSource
import life.qbic.portal.portlet.customers.create.CreateCustomerDataSource
import life.qbic.portal.portlet.customers.search.SearchCustomerDataSource
import life.qbic.portal.portlet.customers.update.UpdateCustomerDataSource
import life.qbic.portal.portlet.exceptions.DatabaseQueryException
import life.qbic.portal.qoffer2.database.ConnectionProvider
import org.apache.groovy.sql.extensions.SqlExtensions

import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.ResultSet
import java.sql.Statement

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


  /**
   * This field should no longer be used but instead {@link #connectionProvider} should be provided
   */
  @Deprecated
  CustomerDatabaseQueries databaseQueries
  /**
   * A connection to the customer database used to create queries.
   */
  private final ConnectionProvider connectionProvider

  private static final AffiliationCategoryFactory CATEGORY_FACTORY = new AffiliationCategoryFactory()
  private static final AcademicTitleFactory TITLE_FACTORY = new AcademicTitleFactory()
  private static final String CUSTOMER_SELECT_QUERY = "SELECT id, first_name AS firstName, last_name AS lastName, title as academicTitle, email as eMailAddress FROM person"
  private static final String AFFILIATION_SELECT_QUERY = "SELECT id, organization AS organisation, address_addition AS addressAddition, street, postal_code AS postalCode, city, country, category FROM affiliation"

  /**
   * This method should be replaced by C
   * @param databaseQueries
   * @param connectionProvider {@link #connectionProvider}
   * @see CustomerDbConnector#CustomerDbConnector(ConnectionProvider)
   */
  @Deprecated
  CustomerDbConnector(CustomerDatabaseQueries databaseQueries, ConnectionProvider connectionProvider){
    this.databaseQueries = databaseQueries
    this.connectionProvider = connectionProvider
  }

  /**
   * Constructor for a CustomerDbConnector
   * @param connection a connection to the customer db
   * @see Connection
   */
  CustomerDbConnector(ConnectionProvider connectionProvider) {
    this.connectionProvider = connectionProvider
  }

  @Override
  List<Customer> findCustomer(String firstName, String lastName) throws DatabaseQueryException {
    String sqlCondition = "WHERE first_name = ? AND last_name = ?"
    String queryTemplate = CUSTOMER_SELECT_QUERY + " " + sqlCondition
    List<Map> resultRows = new ArrayList()
    Connection connection = connectionProvider.connect()
    connection.withCloseable {
      PreparedStatement preparedStatement = it.prepareStatement(queryTemplate)

      preparedStatement.setString(1, firstName)
      preparedStatement.setString(2, lastName)
      ResultSet resultSet = preparedStatement.executeQuery()
      while (resultSet.next()) {
        resultRows.add(SqlExtensions.toRowResult(resultSet))
      }
    }
    List<Customer> customerList = new ArrayList<>()
    resultRows.forEach {Map row ->
      AcademicTitle title = TITLE_FACTORY.getForString(row.academicTitle as String)
      List<Affiliation> affiliations = fetchAffiliationsForPerson(row.id as int)
      Customer customer = new Customer.Builder(row.firstName as String, row.lastName as String, row.eMailAddress as String).title(title).affiliations(affiliations).build()
      customerList.add(customer)
    }
    return customerList
  }

  /*
    We want to fetch all affiliations for a given person id.
    As this is a n to m relationship, we need to look-up
    the associated affiliations ids first.
    Then we fetch every affiliation by the associated association ids.
     */
  private List<Affiliation> fetchAffiliationsForPerson(int personId){
    def affiliations = []
    def affiliationIds = getAffiliationIdsForPerson(personId)
    affiliationIds.each {affiliationId ->
      Affiliation affiliation = fetchAffiliation(affiliationId)
      affiliations.add(affiliation)
    }
    return affiliations
  }
  /**
   * Searches for an affiliation based on a customer id
   *
   * @param customerId Id of the customer
   * @return list of Affiliation Ids associated with the provided customer id
   */
  private List<Integer> getAffiliationIdsForPerson(int customerId) {
    List<Integer> result = []
    String query = "SELECT affiliation_id FROM person_affiliation " +
            "WHERE person_id = ?"

    Connection connection = connectionProvider.connect()

    connection.withCloseable {
      def statement = it.prepareStatement(query)
      statement.setInt(1, customerId)
      ResultSet rs = statement.executeQuery()
      while (rs.next()) {
        result.add(rs.getString(1).toInteger())
      }
    }
    return result
  }
  /**
   * Searches for an affiliation based on an affiliation Id
   *
   * @param affiliationId Id of the affiliation
   * @return Affiliation DTO associated with the provided affiliation Id
   */
  private Affiliation fetchAffiliation(int affiliationId) {
    String affiliationProperties = "organization, address_addition, street, postal_code, city, country, category"
    String query = "SELECT ${affiliationProperties} FROM affiliation " +
    "WHERE id = ?"

    Map row
    Connection connection = connectionProvider.connect()
    connection.withCloseable {
      def statement = it.prepareStatement(query)
      statement.setString(1, affiliationId.toString())
      statement.execute()
      ResultSet rs = statement.getResultSet()
      while (rs.next()) {
        row = SqlExtensions.toRowResult(rs)
      }
    }
    AffiliationCategory category = CATEGORY_FACTORY.getForString(row.category as String)
    Affiliation affiliation = new Affiliation.Builder(
            row.organization as String,
            row.street as String,
            row.postal_code as String,
            row.city as String)
            .country(row.country as String)
            .addressAddition(row.address_addition as String)
            .category(category)
            .build()

    return affiliation
  }

  /**
   * @inheritDoc
   * @param customer
   */
  @Override
  void addCustomer(Customer customer) throws DatabaseQueryException {
    try {
      if (customerExists(customer)) {
        throw new DatabaseQueryException("Customer is already in the database.")
      }
      Connection connection = connectionProvider.connect()
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
    } catch (DatabaseQueryException ignored) {
      throw new DatabaseQueryException("The customer could not be created: ${customer.toString()}")
    } catch (Exception e) {
      log.error(e)
      log.error(e.stackTrace.join("\n"))
      throw new DatabaseQueryException("The customer could not be created: ${customer.toString()}")
    }
  }

  private boolean customerExists(Customer customer) {
    String query = "SELECT * FROM person WHERE first_name = ? AND last_name = ? AND email = ?"
    Connection connection = connectionProvider.connect()

    def customerAlreadyInDb = false

    connection.withCloseable {
      def statement = connection.prepareStatement(query)
      statement.setString(1, customer.firstName)
      statement.setString(2, customer.lastName)
      statement.setString(3, customer.emailAddress)
      statement.execute()
      def result = statement.getResultSet()
      customerAlreadyInDb = result.next()
    }
    return customerAlreadyInDb
  }

  private static int createNewCustomer(Connection connection, Customer customer) {
    String query = "INSERT INTO person (first_name, last_name, title, email) " +
            "VALUES(?, ?, ?, ?)"

    List<Integer> generatedKeys = []

    def statement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)
    statement.setString(1, customer.firstName )
    statement.setString(2, customer.lastName)
    statement.setString(3, customer.title.value)
    statement.setString(4, customer.emailAddress )
    statement.execute()
    def keys = statement.getGeneratedKeys()
    while (keys.next()){
      generatedKeys.add(keys.getInt(1))
    }

    return generatedKeys[0]
  }

  private static void storeAffiliation(Connection connection, int customerId, List<Affiliation>
          affiliations) {
    String query = "INSERT INTO person_affiliation (person_id, affiliation_id) " +
            "VALUES(?, ?)"

    affiliations.each {affiliation ->
      def affiliationId = getAffiliationId(affiliation)
      def statement = connection.prepareStatement(query)
      statement.setInt(1, customerId)
      statement.setInt(2, affiliationId)
      statement.execute()

    }
  }
  
  int getAffiliationId(Affiliation affiliation) {
    String query = "SELECT * FROM affiliation WHERE organization=? " +
            "AND address_addition=? " +
            "AND street=? " +
            "AND postal_code=? " +
            "AND city=?"

    List<Integer> affiliationIds = []
    Connection connection = connectionProvider.connect()

    connection.withCloseable {
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
    }


    if(affiliationIds.size() > 1) {
      throw new DatabaseQueryException("More than one entry found for $affiliation.")
    }
    if (affiliationIds.empty) {
      throw new DatabaseQueryException("No matching affiliation found for $affiliation.")
    }
    return affiliationIds[0]
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
    Connection connection = this.connectionProvider.connect()

    connection.withCloseable {
      def statement = it.prepareStatement(AFFILIATION_SELECT_QUERY)
      ResultSet resultSet = statement.executeQuery()
      while (resultSet.next()) {
        Map row = SqlExtensions.toRowResult(resultSet)
        resultRows.add(row)
        log.debug("Listing affiliations found: $row")
      }
    }
    //TODO separate the part below in static parser
    resultRows.forEach{ Map row ->
      def affiliationBuilder = new Affiliation.Builder(
              row.organisation as String,
              row.street as String,
              row.postalCode as String,
              row.city as String)

      AffiliationCategory category
      try {
        category = CATEGORY_FACTORY.getForString(row.category as String)
      } catch (IllegalArgumentException ignored) {
        log.warn("Affiliation ${row.id} has category '${row.category}'. Could not match.")
        throw new DatabaseQueryException("Could not list Affiliation details for ${row.organization}")
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
      if (affiliationExists(affiliation)) {
        throw new DatabaseQueryException("Affiliation is already in the database.")
      }
      Connection connection = connectionProvider.connect()
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
    } catch (DatabaseQueryException ignored) {
      throw new DatabaseQueryException("The affiliation could not be created: ${affiliation.toString()}")
    } catch (Exception e) {
      log.error(e)
      log.error(e.stackTrace.join("\n"))
      throw new DatabaseQueryException("The affiliation could not be created: ${affiliation.toString()}")
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

    Connection connection = connectionProvider.connect()

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
  }

  /**
   * Searches for a person and returns the matching ID from the person table
   *
   * @param person The database entry is searched for a person
   * @return the ID of the database entry matching the person
   */
  int getPersonId(Person person) {
    String query = "SELECT id FROM person WHERE first_name = ? AND last_name = ? AND email = ?"
    Connection connection = connectionProvider.connect()

    int personId = -1

    connection.withCloseable {
      def statement = connection.prepareStatement(query)
      statement.setString(1, person.firstName)
      statement.setString(2, person.lastName)
      statement.setString(3, person.emailAddress)

      ResultSet result = statement.executeQuery()
      while (result.next()){
        personId = result.getInt(1)
      }
    }
    return personId
  }
}
