package life.qbic.portal.qoffer2.customers

import groovy.util.logging.Log4j2
import life.qbic.datamodel.dtos.business.AcademicTitle
import life.qbic.datamodel.dtos.business.AcademicTitleFactory
import life.qbic.datamodel.dtos.business.Affiliation
import life.qbic.datamodel.dtos.business.AffiliationCategory
import life.qbic.datamodel.dtos.business.Customer

import life.qbic.portal.portlet.exceptions.DatabaseQueryException
import life.qbic.portal.qoffer2.database.DatabaseSession
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

import java.sql.Connection
import java.sql.ResultSet
import java.sql.Statement

/**
 * This class contains all queries on the customer database
 *
 * All database queries for the customer database are collected here. Only here the {@link DatabaseSession} is used to connect
 * to the database
 *
 * @since: 1.0.0
 * @author: Jennifer Bödker
 *
 */
@Log4j2
class CustomerDatabaseQueries {

    private final DatabaseSession databaseSession
    private static final Logger LOG = LogManager.getLogger(CustomerDatabaseQueries.class)

    CustomerDatabaseQueries(DatabaseSession databaseSession){
        this.databaseSession = databaseSession
    }

    /**
     * Searches for a customer based on its last name
     *
     * @param lastName of the customer
     * @return a list of customers with a matching last name
     */
    List<Customer> findPersonByName(String lastName){
        List<Customer> result = []
        String query = "SELECT id, first_name, last_name, title, email from customer WHERE " +
            "last_name = ?"

        Connection connection = databaseSession.getConnection()

        connection.withCloseable {
            def statement = it.prepareStatement(query)
            statement.setString(2, lastName)
            ResultSet rs = statement.executeQuery()
            while (rs.next()) {
                AcademicTitleFactory academicTitleFactory = new AcademicTitleFactory()
                List<Affiliation> affiliations = fetchAffiliationsForPerson(rs.getString(1).toInteger())
                String firstName = "${rs.getString(2)}"
                String emailAddress = "${rs.getString(5)}";
                AcademicTitle academicTitle = academicTitleFactory.getForString("${rs.getString(4)}")
                def customer = new Customer(
                    firstName,
                    lastName,
                    academicTitle,
                    emailAddress,
                    affiliations
                )
                result.add(customer)
            }
        }
        return result
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
    /**
     * Searches for an affiliation based on an affiliation Id
     *
     * @param affiliationId Id of the affiliation
     * @return Affiliation DTO associated with the provided affiliation Id
     */
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

    private static AffiliationCategory determineAffiliationCategory(String value) {
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
                category = AffiliationCategory.UNKNOWN
                break
        }
        return category
    }

    /**
     * Searches for a customer based on an additional address, which can be either an department or an institute
     *
     * @param addAddress of the customer specifying his location
     * @return a list of customers with a matching additional address
     */
    List<Customer> findCustomerByAdditionalAddress(String addAddress){

        return null
    }

    /**
     * Searches for a customer based on a city in which the customers group is located
     *
     * @param cityName of the customer specifying his location
     * @return a list of customers with a matching city
     */
    List<Customer> findCustomerByCity(String cityName){

        return null
    }

    /**
     * Searches for a customer based on a e.g research group
     *
     * @param groupName of the group of which the customer is part of
     * @return a list of customers with a matching group name
     */
    List<Customer> findCustomerByGroup(String groupName){

        return null
    }

    /**
     * Add a customer to the database
     *
     * @param customer which needs to be added to the database
     */
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



    /**
     * Searches for a customer by its ID and updates the customer information according to the new information
     *
     * @param customerId which identifies the user to be updated
     * @param updatedInformation containing the new information about the user
     */
    void updateCustomer(String customerId, Customer updatedInformation){

    }

    List<Affiliation> getAffiliations() {
        List<Affiliation> result = []
        String query = "SELECT * from affiliation"

        Connection connection = databaseSession.getConnection()

        connection.withCloseable {
            def statement = it.prepareStatement(query)
            ResultSet rs = statement.executeQuery()
            while (rs.next()) {
                def affiliationBuilder = new Affiliation.Builder(
                    "${rs.getString(2)}", //organization
                    "${rs.getString(4)}", //street
                    "${rs.getString(5)}", //postal_code
                    "${rs.getString(6)}")
                affiliationBuilder
                    .addressAddition("${rs.getString(3)}")
                    .country("${rs.getString(7)}")
                    .category(determineAffiliationCategory("${rs.getString(8)}"))
                result.add(affiliationBuilder.build())
            }
        }
        return result
    }

    /**
     * Add an affiliation to the database
     *
     * @param affiliation which needs to be added to the database
     */
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
            } catch (Exception e) {
                log.error(e.message)
                log.error(e.stackTrace.join("\n"))
                connection.rollback()
                connection.close()
                throw new DatabaseQueryException("Could not create affiliation.")
            }

        }
    }

    private boolean affiliationExists(Affiliation affiliation) {
        String query = "SELECT * FROM affililation WHERE organization = ? " +
                "AND address_addition=? " +
                "AND street=? " +
                "AND country=? " +
                "AND postal_code=? " +
                "AND city=? " +
                "AND category=?"

        Connection connection = databaseSession.getConnection()

        boolean affiliationAlreadyInDb = false

        connection.withCloseable {
            def statement = connection.prepareStatement(query)
            statement.setString(1, affiliation.organisation)
            statement.setString(2, affiliation.addressAddition)
            statement.setString(3, affiliation.street)
            statement.setString(4, affiliation.country)
            statement.setString(5, affiliation.postalCode)
            statement.setString(6, affiliation.city)
            statement.setString(7, affiliation.category.toString())
            statement.execute()
            def result = statement.getResultSet()
            affiliationAlreadyInDb = result.next()
        }
        return affiliationAlreadyInDb
    }

    private static int createNewAffiliation(Connection connection, Affiliation affiliation) {
        String query = "INSERT INTO affiliation (organization, address_addition, street, country, postal_code, city, category) " +
                "VALUES(?, ?, ?, ?, ?, ?, ?)"

        List<Integer> generatedKeys = []

        def statement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)
        statement.setString(1, affiliation.organisation)
        statement.setString(2, affiliation.addressAddition)
        statement.setString(3, affiliation.street)
        statement.setString(4, affiliation.country)
        statement.setString(5, affiliation.postalCode)
        statement.setString(6, affiliation.city)
        statement.setString(7, affiliation.category.toString())
        statement.execute()
        def keys = statement.getGeneratedKeys()
        while (keys.next()){
            generatedKeys.add(keys.getInt(1))
        }

        return generatedKeys[0]
    }

}
