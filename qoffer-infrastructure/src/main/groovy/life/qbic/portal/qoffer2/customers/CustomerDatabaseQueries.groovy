package life.qbic.portal.qoffer2.customers

import life.qbic.datamodel.people.Person
import life.qbic.datamodel.persons.Affiliation
import life.qbic.portal.portlet.customers.Customer
import life.qbic.portal.portlet.exceptions.DatabaseQueryException
import life.qbic.portal.qoffer2.database.DatabaseSession
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.ResultSet
import java.sql.SQLException

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
    List<Person> findPersonByName(String lastName){
        List<Person> res = []
        try{
            String sql = "SELECT id, first_name, family_name, email from persons WHERE family_name = ?"

            PreparedStatement statement = null
            try{
                statement = databaseConnection.prepareStatement(sql)
                statement.setString(1, lastName)
                ResultSet rs = statement.executeQuery()
                println rs

                while (rs.next()) {
                    fetchAffiliationForPerson(rs.getString(1).toInteger())
                    res <<  new Person(rs.getString(2),rs.getString(3),rs.getString(4))
                }
                return res
            } catch (DatabaseQueryException sqlException) {
                LOG.error("SQL operation unsuccessful: " + sqlException.getMessage())
                sqlException.printStackTrace()
            } finally {
                DatabaseSession.logout(databaseConnection)
            }
            return null
        }catch(DatabaseQueryException sqlException) {
            LOG.error sqlException
            DatabaseSession.logout(databaseConnection)
        }
        return null
    }

    //todo fetch the information for the affiliation by an working optimal sql statement
    Affiliation fetchAffiliationForPerson(int personId){
        //Affiliation affiliation = new Affiliation("group","acrony","orga","institute","faculty","contact","head","street","zip","city","country","webpage")
        //todo
        return null
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
