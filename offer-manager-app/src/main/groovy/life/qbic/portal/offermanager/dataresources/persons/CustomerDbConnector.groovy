package life.qbic.portal.offermanager.dataresources.persons

import groovy.util.logging.Log4j2
import life.qbic.datamodel.dtos.business.AcademicTitle
import life.qbic.datamodel.dtos.business.AcademicTitleFactory
import life.qbic.datamodel.dtos.business.Affiliation
import life.qbic.datamodel.dtos.business.AffiliationCategory
import life.qbic.datamodel.dtos.business.AffiliationCategoryFactory
import life.qbic.datamodel.dtos.business.Customer
import life.qbic.datamodel.dtos.business.ProjectManager
import life.qbic.datamodel.dtos.general.Person
import life.qbic.business.customers.affiliation.create.CreateAffiliationDataSource
import life.qbic.business.customers.affiliation.list.ListAffiliationsDataSource
import life.qbic.business.customers.create.CreateCustomerDataSource
import life.qbic.business.customers.search.SearchCustomerDataSource
import life.qbic.business.customers.update.UpdateCustomerDataSource
import life.qbic.business.exceptions.DatabaseQueryException
import life.qbic.portal.offermanager.dataresources.database.ConnectionProvider
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
   * A connection to the customer database used to create queries.
   */
  private final ConnectionProvider connectionProvider

  private static final AffiliationCategoryFactory CATEGORY_FACTORY = new AffiliationCategoryFactory()
  private static final AcademicTitleFactory TITLE_FACTORY = new AcademicTitleFactory()
  private static final String CUSTOMER_SELECT_QUERY = "SELECT id, first_name, last_name, title, email FROM person"
  private static final String PM_SELECT_QUERY = "SELECT * FROM person"
  private static final String AFFILIATION_SELECT_QUERY = "SELECT id, organization AS organisation, address_addition AS addressAddition, street, postal_code AS postalCode, city, country, category FROM affiliation"


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
    Connection connection = connectionProvider.connect()
    List<Customer> customerList = new ArrayList<>()
    connection.withCloseable {
      PreparedStatement preparedStatement = it.prepareStatement(queryTemplate)

      preparedStatement.setString(1, firstName)
      preparedStatement.setString(2, lastName)
      ResultSet resultSet = preparedStatement.executeQuery()
      while (resultSet.next()) {
        customerList.add(parseCustomerFromResultSet(resultSet))
      }
    }
    return customerList
  }
  
  @Override
  List<Customer> findActiveCustomer(String firstName, String lastName) throws DatabaseQueryException {
    String sqlCondition = "WHERE first_name = ? AND last_name = ? AND active = 1"
    String queryTemplate = CUSTOMER_SELECT_QUERY + " " + sqlCondition
    Connection connection = connectionProvider.connect()
    List<Customer> customerList = new ArrayList<>()
    connection.withCloseable {
      PreparedStatement preparedStatement = it.prepareStatement(queryTemplate)

      preparedStatement.setString(1, firstName)
      preparedStatement.setString(2, lastName)
      ResultSet resultSet = preparedStatement.executeQuery()
      while (resultSet.next()) {
        customerList.add(parseCustomerFromResultSet(resultSet))
      }
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
    String query = "INSERT INTO person (first_name, last_name, title, email, active) " +
            "VALUES(?, ?, ?, ?, ?)"

    List<Integer> generatedKeys = []

    def statement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)
    statement.setString(1, customer.firstName )
    statement.setString(2, customer.lastName)
    statement.setString(3, customer.title.value)
    statement.setString(4, customer.emailAddress )
    //a new customer is always active
    statement.setBoolean(5, true)
    statement.execute()
    def keys = statement.getGeneratedKeys()
    while (keys.next()){
      generatedKeys.add(keys.getInt(1))
    }

    return generatedKeys[0]
  }
  
  private void removeCustomerAffiliations(Connection connection, int customerId, List<Integer> affiliationIds) {
    String query = "DELETE FROM person_affiliation WHERE person_id = ? AND affiliation_id = ?"
    
    affiliationIds.each {affiliationId -> 
      statement.setInt(1, customerId)
      statement.setInt(2, affiliationId)
      statement.execute()
    }
  }

  private void storeAffiliation(Connection connection, int customerId, List<Affiliation>
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
      ResultSet rs = statement.executeQuery()
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
  
  @Override
  void updateCustomerAffiliations(String customerIdString, List<Affiliation> updatedAffiliations) {
    int customerId = Integer.parseInt(customerIdString)
    
    try {
    List<Affiliation> existingAffiliations = getAffiliationForPersonId(customerId)
    List<Affiliation> newAffiliations = new ArrayList<>();
    List<Integer> oldAffiliationIds = new ArrayList<>();
    
    // find added affiliations - could use set operations here, but we have lists...
    for(Affiliation affiliation : updatedAffiliations) {
      if(!existingAffiliations.contains(affiliation)) {
        newAffiliations.add(affiliation)
      }
    }
    
    // find removed affiliations
    for(Affiliation affiliation : existingAffiliations) {
      if(!updatedAffiliations.contains(affiliation)) {
        oldAffiliationIds.add(getAffiliationId(affiliation))
      }
    }
    Connection connection = connectionProvider.connect()
    connection.setAutoCommit(false)

    connection.withCloseable {it ->
      try {
       
    removeCustomerAffiliations(connection, customerId, oldAffiliationIds)
    storeAffiliation(connection, customerId, newAffiliations)
    
      } catch (Exception e) {
        log.error(e.message)
        log.error(e.stackTrace.join("\n"))
        connection.rollback()
        connection.close()
        throw new DatabaseQueryException("Could not update customer's affiliations.")
      }
    }
  } catch (Exception e) {
    log.error(e)
    log.error(e.stackTrace.join("\n"))
    throw new DatabaseQueryException("The customer's affiliations could not be updated.")
  }
    
  }

  private void changeCustomerActiveFlag(int customerId, boolean active) {
    String query = "UPDATE person SET active = ? WHERE id = ?";
    
    Connection connection = connectionProvider.connect()

    connection.withCloseable {
      def statement = connection.prepareStatement(query)
      statement.setBoolean(1, active)
      statement.setInt(2, customerId)
      statement.execute()
    }
  }
  
  /**
   * @inheritDoc
   * @param customerId
   * @param updatedCustomer
   */
  @Override
  void updateCustomer(String customerId, Customer updatedCustomer) {

        int oldCustomerId = Integer.parseInt(customerId)
    try {
      if (getCustomer(oldCustomerId)==null) {
        throw new DatabaseQueryException("Customer is not in the database and can't be updated.")
      }
            
      Connection connection = connectionProvider.connect()
      connection.setAutoCommit(false)

      connection.withCloseable {it ->
        try {
          int newCustomerId = createNewCustomer(it, updatedCustomer)
          storeAffiliation(it, newCustomerId, updatedCustomer.affiliations)
          connection.commit()
          
          // if our update is successful we set the old customer inactive
          changeCustomerActiveFlag(oldCustomerId, false)
          
        } catch (Exception e) {
          log.error(e.message)
          log.error(e.stackTrace.join("\n"))
          connection.rollback()
          connection.close()
          throw new DatabaseQueryException("Could not update customer.")
        }
      }
    } catch (Exception e) {
      log.error(e)
      log.error(e.stackTrace.join("\n"))
      throw new DatabaseQueryException("The customer could not be updated: ${customer.toString()}")
    }
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
    if (personId == -1) {
      def msg = "Could not find ${person.firstName} ${person.lastName} " +
              "(${person.emailAddress})."
      log.error(msg)
      throw new DatabaseQueryException(msg)
    }
    return personId
  }
  
  /**
   * Searches for a person and returns the matching ID from the person table if that person is set to active
   *
   * @param person The database entry is searched for a person
   * @return the ID of the database entry matching the person
   */
  int getActivePersonId(Person person) {
    String query = "SELECT id FROM person WHERE first_name = ? AND last_name = ? AND email = ? AND active = 1"
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
    if (personId == -1) {
      def msg = "Could not find ${person.firstName} ${person.lastName} " +
              "(${person.emailAddress}) in the list of active persons. They might be inactive."
      log.error(msg)
      throw new DatabaseQueryException(msg)
    }
    return personId
  }
  
  /**
   * List all available persons.
   * @return A list of persons
   */
  List<Customer> fetchAllCustomers() {
    List<Customer> customers = []
    String query = CUSTOMER_SELECT_QUERY
    Connection connection = connectionProvider.connect()
    connection.withCloseable {
      def preparedStatement = it.prepareStatement(query)
      ResultSet resultSet = preparedStatement.executeQuery()
      while(resultSet.next()) {
        Customer customer = parseCustomerFromResultSet(resultSet)
        customers.add(customer)
      }
    }
    return customers
  }
  
  /**
   * List all available persons that are set to active in the database.
   * @return A list of active persons
   */
  List<Customer> fetchAllActiveCustomers() {
    List<Customer> customers = []
    String query = CUSTOMER_SELECT_QUERY + " WHERE active = 1"
    Connection connection = connectionProvider.connect()
    connection.withCloseable {
      def preparedStatement = it.prepareStatement(query)
      ResultSet resultSet = preparedStatement.executeQuery()
      while(resultSet.next()) {
        Customer customer = parseCustomerFromResultSet(resultSet)
        customers.add(customer)
      }
    }
    return customers
  }
  
  /**
   * List all available project managers.
   * @return A list of project managers
   */
  List<ProjectManager> fetchAllProjectManagers() {
    List<ProjectManager> pms = []
    String query = PM_SELECT_QUERY
    Connection connection = connectionProvider.connect()
    connection.withCloseable {
      def preparedStatement = it.prepareStatement(query)
      ResultSet resultSet = preparedStatement.executeQuery()
      while(resultSet.next()) {
        ProjectManager projectManager = parseProjectManagerFromResultSet(resultSet)
        pms.add(projectManager)
      }
    }
    return pms
  }

  /**
   * This method creates a customer for a result set obtained with the CUSTOMER_SELECT_QUERY
   * @param resultSet
   * @return a Customer DTO containing the information from the query result set
   */
  private Customer parseCustomerFromResultSet(ResultSet resultSet) {
    int personId = resultSet.getInt(1)
    String titleValue = resultSet.getString('title')
    AcademicTitle title = TITLE_FACTORY.getForString(titleValue)
    String firstName = resultSet.getString('first_name')
    String lastName = resultSet.getString('last_name')
    String email = resultSet.getString('email')
    Customer.Builder customerBuilder = new Customer.Builder(firstName, lastName, email).title(title)
    List<Affiliation> affiliations = getAffiliationForPersonId(personId)
    return customerBuilder.affiliations(affiliations).build()
  }

  /**
   * This method creates a project manager for a result set obtained with the CUSTOMER_SELECT_QUERY
   * @param resultSet
   * @return a Customer DTO containing the information from the query result set
   */
  private ProjectManager parseProjectManagerFromResultSet(ResultSet resultSet) {
    int personId = resultSet.getInt(1)
    String firstName = resultSet.getString("first_name")
    String lastName = resultSet.getString("last_name")
    String email = resultSet.getString("email")
    AcademicTitle title = TITLE_FACTORY.getForString(resultSet.getString("title"))
    List<Affiliation> affiliations = getAffiliationForPersonId(personId)
    ProjectManager projectManager = new ProjectManager.Builder(firstName, lastName, email)
            .affiliations(affiliations).title(title).build()
    return projectManager
  }

  private List<Affiliation> getAffiliationForPersonId(int personId) {
    def affiliations = []
    String query = "SELECT *\n" +
            "FROM \n" +
            "    person_affiliation\n" +
            "    LEFT JOIN affiliation\n" +
            "    ON  person_affiliation.affiliation_id = affiliation.id\n" +
            "    WHERE person_affiliation.person_id = ?"
    Connection connection = connectionProvider.connect()
    connection.withCloseable {
      def preparedStatement = it.prepareStatement(query)
      preparedStatement.setInt(1, personId)
      ResultSet resultSet = preparedStatement.executeQuery()
      while (resultSet.next()) {
        def organization = resultSet.getString('organization')
        def addressAddition = resultSet.getString('address_addition')
        def street = resultSet.getString('street')
        def postalCode = resultSet.getString('postal_code')
        def city = resultSet.getString('city')
        def country = resultSet.getString('country')
        def category = determineAffiliationCategory(resultSet.getString('category'))
        def affiliation = new Affiliation.Builder(organization, street, postalCode, city)
                .addressAddition(addressAddition)
                .country(country)
                .category(category)
                .build()
        affiliations.add(affiliation)
      }
    }
    return affiliations
  }

  static AffiliationCategory determineAffiliationCategory(String value) {
    def category
    switch(value.toLowerCase()) {
      case "internal":
        category = AffiliationCategory.INTERNAL
        break
      case "external academic":
        category = AffiliationCategory.EXTERNAL_ACADEMIC
        break
      case "external":
        category = AffiliationCategory.EXTERNAL
        break
      default:
        category = AffiliationCategory.EXTERNAL
        break
    }
    return category
  }

  Customer getCustomer(int personPrimaryId) {
    String query = CUSTOMER_SELECT_QUERY + " " +"WHERE id=?"
    Connection connection = connectionProvider.connect()

    connection.withCloseable {
     PreparedStatement statement = it.prepareStatement(query)
     statement.setInt(1, personPrimaryId)
     ResultSet result = statement.executeQuery()
     Customer person = null
     while (result.next()) {
       person = parseCustomerFromResultSet(result)
     }
     return person
    }
  }

  ProjectManager getProjectManager(int personPrimaryId) {
    String query = PM_SELECT_QUERY + " " + "WHERE id=?"
    Connection connection = connectionProvider.connect()

    connection.withCloseable {
      PreparedStatement statement = it.prepareStatement(query)
      statement.setInt(1, personPrimaryId)
      ResultSet result = statement.executeQuery()
      ProjectManager person = null
      while (result.next()) {
        person = parseProjectManagerFromResultSet(result)
      }
      return person
    }
  }

  Affiliation getAffiliation(int affiliationPrimaryId) {
    String query = "SELECT * FROM affiliation WHERE id=?"
    Connection connection = connectionProvider.connect()

    connection.withCloseable {
      PreparedStatement statement = it.prepareStatement(query)
      statement.setInt(1, affiliationPrimaryId)
      ResultSet result = statement.executeQuery()
      Affiliation affiliation = null
      while (result.next()) {

        String organization = result.getString("organization")
        String address_addition = result.getString("address_addition")
        String street = result.getString("street")
        String city = result.getString("city")
        String postalCode = result.getString("postal_code")
        String country = result.getString("country")
        AffiliationCategory category = CATEGORY_FACTORY.getForString(result.getString("category"))
        affiliation = new Affiliation.Builder(organization, street, postalCode, city)
          .country(country).addressAddition(address_addition).category(category).build()
      }
      return affiliation
    }
  }
}
