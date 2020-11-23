package life.qbic.portal.qoffer2.offers

import groovy.util.logging.Log4j2
import life.qbic.datamodel.dtos.business.Affiliation
import life.qbic.datamodel.dtos.business.Offer
import life.qbic.datamodel.dtos.general.Person
import life.qbic.portal.portlet.exceptions.DatabaseQueryException
import life.qbic.portal.portlet.offers.create.CreateOfferDataSource
import life.qbic.portal.qoffer2.database.ConnectionProvider
import life.qbic.portal.qoffer2.database.DatabaseSession
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

import java.sql.Connection
import java.sql.Date
import java.sql.PreparedStatement
import java.sql.ResultSet

/**
 * Handles the connection to the offer database
 *
 * Implements {@link CreateOfferDataSource} and is responsible for transferring data between the offer database and qOffer
 *
 * @since: 1.0.0
 * @author: Jennifer Bödker
 *
 */
@Log4j2
class OfferDbConnector implements CreateOfferDataSource{

    DatabaseSession session
    ConnectionProvider connectionProvider

    private static final Logger LOG = LogManager.getLogger(OfferDbConnector.class)

    private static final String OFFER_INSERT_QUERY = "INSERT INTO offer (modificationDate, expirationDate, customerId, projectManagerId, projectTitle, projectDescription, totalPrice, customerAffiliationId)"


    OfferDbConnector(DatabaseSession session){
        this.session = session
    }

    /**
     * Constructor for a CustomerDbConnector
     * @param connection a connection to the customer db
     * @see Connection
     */
    OfferDbConnector(ConnectionProvider connectionProvider) {
        this.connectionProvider = connectionProvider
    }

    @Override
    void store(Offer offer) throws DatabaseQueryException {
        try {
            Connection connection = connectionProvider.connect()
            connection.setAutoCommit(false)

            connection.withCloseable {it ->
                try {
                    int projectManagerId = getPersonId(offer.projectManager)
                    int customerId = getPersonId(offer.customer)
                    int affiliationId = getAffiliationId(offer.selectedCustomerAffiliation)
                    createOffer(it, offer, projectManagerId, customerId, affiliationId)
                    connection.commit()
                } catch (Exception e) {
                    log.error(e.message)
                    log.error(e.stackTrace.join("\n"))
                    connection.rollback()
                    connection.close()
                    throw new DatabaseQueryException("Could not create offer.")
                }
            }
        } catch (DatabaseQueryException ignored) {
            throw new DatabaseQueryException("The offer could not be created: ${offer.toString()}")
        } catch (Exception e) {
            log.error(e)
            log.error(e.stackTrace.join("\n"))
            throw new DatabaseQueryException("The offer could not be created: ${offer.toString()}")
        }
    }

    /**
     *
     * @param offer
     * @return
     */
    private static int createOffer(Connection connection, Offer offer, int projectManagerId, int customerId, int affiliationId){
        String sqlValues = "VALUE(?,?,?,?,?,?,?,?)"
        String queryTemplate = OFFER_INSERT_QUERY + " " + sqlValues
        List<Integer> generatedKeys = []

        connection.withCloseable {
            PreparedStatement preparedStatement = it.prepareStatement(queryTemplate)
            preparedStatement.setDate(1, offer.modificationDate as Date)
            preparedStatement.setDate(2,offer.expirationDate as Date)
            preparedStatement.setInt(3,customerId)
            preparedStatement.setInt(4,projectManagerId)
            preparedStatement.setString(5,offer.projectTitle)
            preparedStatement.setString(6,offer.projectDescription)
            preparedStatement.setDouble(7,offer.totalPrice)
            preparedStatement.setInt(8,affiliationId)

            preparedStatement.execute()
            def keys = preparedStatement.getGeneratedKeys()
            while (keys.next()){
                generatedKeys.add(keys.getInt(1))
            }
        }


        return generatedKeys[0]
    }

    /**
     * Searches for a person and returns the matching ID from the person table
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

    //todo the same method as in customerDbconnector
    int getAffiliationId(Affiliation affiliation) {
        String query = "SELECT id FROM affiliation WHERE organization=? " +
                "AND address_addition=? " +
                "AND street=? " +
                "AND postal_code=? " +
                "AND city=?"

        Connection connection = connectionProvider.connect()

        List<Integer> affiliationIds = []

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

        if(affiliationIds.size() > 1) {
            throw new DatabaseQueryException("More than one entry found for $affiliation.")
        }
        if (affiliationIds.empty) {
            throw new DatabaseQueryException("No matching affiliation found for $affiliation.")
        }
        return affiliationIds[0]
    }
}
