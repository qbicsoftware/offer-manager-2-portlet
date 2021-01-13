package life.qbic.portal.qoffer2.offers

import groovy.util.logging.Log4j2
import life.qbic.datamodel.dtos.business.AcademicTitleFactory
import life.qbic.datamodel.dtos.business.AffiliationCategoryFactory
import life.qbic.datamodel.dtos.business.Customer
import life.qbic.datamodel.dtos.business.Offer
import life.qbic.datamodel.dtos.business.OfferId
import life.qbic.portal.portlet.exceptions.DatabaseQueryException
import life.qbic.portal.portlet.offers.create.CreateOfferDataSource
import life.qbic.portal.qoffer2.customers.CustomerDbConnector
import life.qbic.portal.qoffer2.database.ConnectionProvider
import life.qbic.portal.qoffer2.products.ProductsDbConnector
import life.qbic.portal.qoffer2.shared.OfferOverview
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

import java.sql.Connection
import java.sql.Date
import java.sql.PreparedStatement
import java.sql.ResultSet
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

    private static final AffiliationCategoryFactory CATEGORY_FACTORY = new AffiliationCategoryFactory()
    private static final AcademicTitleFactory TITLE_FACTORY = new AcademicTitleFactory()

    private static final String OFFER_INSERT_QUERY = "INSERT INTO offer (offerId, " +
            "creationDate, expirationDate, customerId, projectManagerId, projectTitle, " +
            "projectDescription, totalPrice, customerAffiliationId)"


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
        String sqlValues = "VALUE(?,?,?,?,?,?,?,?,?)"
        String queryTemplate = OFFER_INSERT_QUERY + " " + sqlValues
        def identifier = offer.identifier
        List<Integer> generatedKeys = []
        Connection connection = connectionProvider.connect()
        log.info("New offer with id: ${offer.identifier}")
        connection.withCloseable {
            PreparedStatement preparedStatement = it.prepareStatement(queryTemplate, Statement.RETURN_GENERATED_KEYS)
            preparedStatement.setString(1, offerIdToString(identifier))
            preparedStatement.setDate(2, new Date(offer.modificationDate.time))
            preparedStatement.setDate(3, new Date(offer.expirationDate.time))
            preparedStatement.setInt(4, customerId)
            preparedStatement.setInt(5, projectManagerId)
            preparedStatement.setString(6, offer.projectTitle)
            preparedStatement.setString(7, offer.projectDescription)
            preparedStatement.setDouble(8, offer.totalPrice)
            preparedStatement.setInt(9, affiliationId)

            preparedStatement.execute()

            def keys = preparedStatement.getGeneratedKeys()
            while (keys.next()) {
                generatedKeys.add(keys.getInt(1))
            }

            return generatedKeys[0]
        }
    }

    static String offerIdToString(OfferId id) {
        return "${id.getProjectConservedPart()}_${id.getRandomPart()}_${id.getVersion()}"
    }

    List<OfferOverview> loadOfferOverview() {
        List<OfferOverview> offerOverviewList = []

        String query = "SELECT offerId, creationDate, projectTitle, " +
                "totalPrice, first_name, last_name, email\n" +
                "FROM offer \n" +
                "LEFT JOIN person \n" +
                "ON offer.customerId = person.id"

        Connection connection = connectionProvider.connect()
        connection.withCloseable {
            PreparedStatement statement = it.prepareStatement(query)
            ResultSet resultSet = statement.executeQuery()
            while (resultSet.next()) {
                def customer = new Customer.Builder(
                        resultSet.getString("first_name"),
                        resultSet.getString("last_name"),
                        resultSet.getString("email"))
                        .build()
                def projectTitle = resultSet.getString("projectTitle")
                def totalCosts = resultSet.getDouble("totalPrice")
                def creationDate = resultSet.getDate("creationDate")
                def customerName = "${customer.getFirstName()} ${customer.getLastName()}"
                def offerId = parseOfferId(resultSet.getString("offerId"))
                OfferOverview offerOverview = new OfferOverview(offerId,
                        creationDate,projectTitle, "-",
                        customerName, totalCosts)
                offerOverviewList.add(offerOverview)
            }
        }
        return offerOverviewList
    }

    static OfferId parseOfferId(String offerId) {
        def splitId = offerId.split("_")
        def projectPart = splitId[0]
        def randomPart = splitId[1]
        def version = splitId[2]
        return new OfferId(projectPart, randomPart, version)
    }

    Optional<Offer> getOffer(String title) {
        Optional<Offer> offer = Optional.empty()

        String query = "SELECT * " +
                "FROM offer \n" +
                "WHERE projectTitle=?"

        Connection connection = connectionProvider.connect()
        connection.withCloseable {
            PreparedStatement statement = it.prepareStatement(query)
            statement.setString(1, title)
            ResultSet resultSet = statement.executeQuery()
            while (resultSet.next()) {
                /*
                Load the offer Id first
                 */
                def offerId = parseOfferId(resultSet.getString("offerId"))
                def offerPrimaryId = resultSet.getInt("id")
                /*
                Load customer and project manager info
                 */
                def customerId =  resultSet.getInt("customerId")
                def projectManagerId = resultSet.getInt("projectManagerId")
                def customer = customerGateway.getCustomer(customerId)
                def projectManager = customerGateway.getProjectManager(customerId)
                /*
                Load general offer info
                 */
                def projectTitle = resultSet.getString("projectTitle")
                def projectDescription = resultSet.getString("projectDescription")
                def totalCosts = resultSet.getDouble("totalPrice")
                def creationDate = resultSet.getDate("creationDate")
                def selectedAffiliationId = resultSet.getInt("customerAffiliationId")
                def selectedAffiliation = customerGateway.getAffiliation(selectedAffiliationId)
                def items = productGateway.getItemsForOffer(offerPrimaryId)

                offer = Optional.of(new Offer.Builder(
                        customer,
                        projectManager,
                        projectTitle,
                        projectDescription,
                        selectedAffiliation)
                        .identifier(offerId)
                        .items(items)
                        .build())
            }
        }
        return offer
    }
}
