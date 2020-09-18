package life.qbic.portal.qoffer2.customers

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
                List<Affiliation> affiliations = fetchAffiliationsForPerson(rs.getString(1).toInteger())
                def customer = new Customer(
                    "${rs.getString(2)}",
                    "${rs.getString(3)}",
                    "${rs.getString(4)}",
                    "${rs.getString(5)}",
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
        int customerId = createNewCustomer(customer)
        storeAffiliation(customerId, customer.affiliations)
    }

    private boolean customerExists(Customer customer) {
        return true
    }

    private int createNewCustomer(Customer customer) {
        String query = "INSERT INTO customer (first_name, last_name, title, email) " +
            "VALUES(?, ?, ?, ?)"
        Connection connection = databaseSession.getConnection()
        List<Integer> generatedKeys = []

        connection.withCloseable {
            def statement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)
            statement.setString(1, customer.firstName )
            statement.setString(2, customer.lastName)
            statement.setString(3, customer.title)
            statement.setString(4, customer.eMailAddress )
            statement.execute()
            generatedKeys.add(statement.getGeneratedKeys().getInt(1))
        }
        return generatedKeys[0]
    }

    private void storeAffiliation(int customerId, List<Affiliation> affiliations) {
        String query = "INSERT INTO customer_affiliation (affiliation_id, customer_id) " +
            "VALUES(?, ?)"
        Connection connection = databaseSession.getConnection()

        connection.withCloseable {
            affiliations.each {affiliation ->
                def affiliationId = getAffiliationId(affiliation)
                def statement = connection.prepareStatement(query)
                statement.setInt(1, affiliationId)
                statement.setInt(2, customerId)
                statement.execute()
            }
        }
    }

    private int getAffiliationId(Affiliation affiliation) {
        String query = "SELECT FROM affiliation WHERE organisation=? " +
            "AND address_addition=? " +
            "AND street=? " +
            "AND postal_code=? " +
            "AND city=?"
        Connection connection = databaseSession.getConnection()

        List<Integer> affiliationIds = []

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
                affiliationIds.add(rs.getInt(0))
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
}
