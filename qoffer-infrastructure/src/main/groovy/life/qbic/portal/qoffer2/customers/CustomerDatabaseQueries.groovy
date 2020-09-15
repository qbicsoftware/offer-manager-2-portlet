package life.qbic.portal.qoffer2.customers

import life.qbic.datamodel.dtos.business.Affiliation
import life.qbic.datamodel.dtos.business.Customer

import life.qbic.portal.portlet.exceptions.DatabaseQueryException
import life.qbic.portal.qoffer2.database.DatabaseSession
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.ResultSet

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

    private final Connection databaseConnection

    private static final Logger LOG = LogManager.getLogger(CustomerDatabaseQueries.class)

    CustomerDatabaseQueries(Connection connection){
        this.databaseConnection = connection
    }

    /**
     * Searches for a customer based on its last name
     *
     * @param lastName of the customer
     * @return a list of customers with a matching last name
     */
    List<Customer> findPersonByName(String lastName){
        List<Customer> result = []
        String sql = "SELECT id, first_name, last_name, title, email from persons WHERE " +
            "last_name = ?"

        databaseConnection.withCloseable {
            def statement = it.prepareStatement(sql)
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

    //todo fetch the information for the affiliation by an working optimal sql statement
    private List<Affiliation> fetchAffiliationsForPerson(int personId){
        def affiliations = []
        def affiliationIds = getAffiliationIdsForPerson(personId)
        affiliationIds.each {affiliationId ->
            Affiliation affiliation = fetchAffiliation(affiliationId)
            affiliations.add(affiliation)
        }
        return affiliations
    }

    private List<Integer> getAffiliationIdsForPerson(int personId) {
        return [1]
    }

    private Affiliation fetchAffiliation(int affiliationId) {
        return new Affiliation.Builder(
            "dummy",
            "dummy",
            "dummy",
            "dummy")
            .build()
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
    void addCustomer(Customer customer){

    }

    /**
     * Searches for a customer by its ID and updates the customer information according to the new information
     *
     * @param customerId which identifies the user to be updated
     * @param updatedInformation containing the new information about the user
     */
    void updateCustomer(String customerId, Customer updatedInformation){

    }
}
