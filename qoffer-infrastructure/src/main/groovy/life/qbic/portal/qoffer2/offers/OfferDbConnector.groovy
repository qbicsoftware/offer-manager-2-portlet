package life.qbic.portal.qoffer2.offers

import groovy.util.logging.Log4j2
import life.qbic.datamodel.dtos.business.Offer
import life.qbic.portal.portlet.exceptions.DatabaseQueryException
import life.qbic.portal.portlet.offers.create.CreateOfferDataSource
import life.qbic.portal.qoffer2.customers.CustomerDbConnector
import life.qbic.portal.qoffer2.database.ConnectionProvider
import life.qbic.portal.qoffer2.products.ProductsDbConnector
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

import java.sql.Connection
import java.sql.Date
import java.sql.PreparedStatement
import java.sql.Statement

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

    CustomerDbConnector customerGateway
    ProductsDbConnector productGateway

    private static final Logger LOG = LogManager.getLogger(OfferDbConnector.class)

    private static final String OFFER_INSERT_QUERY = "INSERT INTO offer (modificationDate, expirationDate, customerId, projectManagerId, projectTitle, projectDescription, totalPrice, customerAffiliationId)"


    OfferDbConnector(ConnectionProvider connectionProvider, CustomerDbConnector customerDbConnector, ProductsDbConnector productsDbConnector){
        this.connectionProvider = connectionProvider
        this.customerGateway = customerDbConnector
        this.productGateway = productsDbConnector
    }

    @Override
    void store(Offer offer) throws DatabaseQueryException {
        Connection connection = connectionProvider.connect()
        connection.setAutoCommit(false)

        connection.withCloseable { it ->
            try {
                int projectManagerId = customerGateway.getPersonId(offer.projectManager)
                int customerId = customerGateway.getPersonId(offer.customer)
                int affiliationId = customerGateway.getAffiliationId(offer.selectedCustomerAffiliation)

                int offerId = storeOffer(offer, projectManagerId, customerId, affiliationId)

                productGateway.createOfferItems(offer.items, offerId)
                connection.commit()
            } catch (DatabaseQueryException e) {
                // We can safely proxy DatabaseQueryExceptions back to the use case
                throw new DatabaseQueryException(e.message)
            } catch (Exception e) {
                log.error(e.message)
                log.error(e.stackTrace.join("\n"))
                connection.rollback()
                throw new DatabaseQueryException("Could not store offer {$offer.identifier}.")
            }
        }
    }

    /**
     * The method stores the offer in the QBiC database
     *
     * @param offer with the information of the offer to be stored
     * @return the id of the stored offer in the database
     */
    private int storeOffer(Offer offer, int projectManagerId, int customerId, int affiliationId){
        String sqlValues = "VALUE(?,?,?,?,?,?,?,?)"
        String queryTemplate = OFFER_INSERT_QUERY + " " + sqlValues

        List<Integer> generatedKeys = []
        Connection connection = connectionProvider.connect()

        connection.withCloseable {
            PreparedStatement preparedStatement = it.prepareStatement(queryTemplate, Statement.RETURN_GENERATED_KEYS)
            preparedStatement.setDate(1, new Date(offer.modificationDate.time))
            preparedStatement.setDate(2, new Date(offer.expirationDate.time))
            preparedStatement.setInt(3, customerId)
            preparedStatement.setInt(4, projectManagerId)
            preparedStatement.setString(5, offer.projectTitle)
            preparedStatement.setString(6, offer.projectDescription)
            preparedStatement.setDouble(7, offer.totalPrice)
            preparedStatement.setInt(8, affiliationId)

            preparedStatement.execute()

            def keys = preparedStatement.getGeneratedKeys()
            while (keys.next()) {
                generatedKeys.add(keys.getInt(1))
            }

            return generatedKeys[0]
        }
    }
}
