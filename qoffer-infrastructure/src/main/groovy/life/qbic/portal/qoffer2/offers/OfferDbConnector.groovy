package life.qbic.portal.qoffer2.offers

import groovy.util.logging.Log4j2
import life.qbic.datamodel.dtos.business.Offer
import life.qbic.portal.portlet.exceptions.DatabaseQueryException
import life.qbic.portal.portlet.offers.create.CreateOfferDataSource
import life.qbic.portal.qoffer2.database.ConnectionProvider
import life.qbic.portal.qoffer2.database.DatabaseSession
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

import java.sql.Connection
import java.sql.Date
import java.sql.PreparedStatement
import java.sql.SQLException
import java.time.Instant

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

    ConnectionProvider connectionProvider

    OfferToCustomerGateway offerToCustomerGateway
    OfferToProductGateway offerToProductGateway

    private static final Logger LOG = LogManager.getLogger(OfferDbConnector.class)

    private static final String OFFER_INSERT_QUERY = "INSERT INTO offer (modificationDate, expirationDate, customerId, projectManagerId, projectTitle, projectDescription, totalPrice, customerAffiliationId)"


    OfferDbConnector(ConnectionProvider connectionProvider, OfferToCustomerGateway offerToCustomerGateway, OfferToProductGateway offerToProductGateway){
        this.connectionProvider = connectionProvider
        this.offerToCustomerGateway = offerToCustomerGateway
        this.offerToProductGateway = offerToProductGateway
    }

    @Override
    void store(Offer offer) throws DatabaseQueryException {
        try {
            Connection connection = connectionProvider.connect()
            connection.setAutoCommit(false)

            connection.withCloseable {it ->
                try {
                    int projectManagerId = offerToCustomerGateway.getPersonId(offer.projectManager)
                    int customerId = offerToCustomerGateway.getPersonId(offer.customer)
                    int affiliationId = offerToCustomerGateway.getAffiliationId(offer.selectedCustomerAffiliation)

                    int offerId = createOffer(offer, projectManagerId, customerId, affiliationId)

                    offerToProductGateway.createOfferItems(offer.items, offerId)
                    connection.commit()
                } catch (Exception e) {
                    log.error(e.message)
                    log.error(e.stackTrace.join("\n"))
                    connection.rollback()
                    connection.close()
                    throw new DatabaseQueryException("Could not store offer.")
                }
            }
        } catch (DatabaseQueryException ignored) {
            throw new DatabaseQueryException("The offer could not be stored: ${offer.toString()}")
        } catch (Exception e) {
            log.error(e)
            log.error(e.stackTrace.join("\n"))
            throw new DatabaseQueryException("The offer could not be stored: ${offer.toString()}")
        }
    }

    /**
     * The todo describe
     *
     * @param offer
     * @return
     */
    private int createOffer(Offer offer, int projectManagerId, int customerId, int affiliationId){
        String sqlValues = "VALUE(?,?,?,?,?,?,?,?)"
        String queryTemplate = OFFER_INSERT_QUERY + " " + sqlValues

        try{
            List<Integer> generatedKeys = []
            Connection connection = connectionProvider.connect()

            connection.withCloseable {
                PreparedStatement preparedStatement = it.prepareStatement(queryTemplate)
                preparedStatement.setDate(1, new Date(offer.modificationDate.time))
                preparedStatement.setDate(2, new Date(offer.expirationDate.time))
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

                return generatedKeys[0]
            }
        }catch(Exception exception){
            log.error(exception)
            throw new DatabaseQueryException("The offer could not be created: ${offer.toString()}")
        }
    }
}
