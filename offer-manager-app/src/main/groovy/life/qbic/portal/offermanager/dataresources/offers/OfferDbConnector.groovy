package life.qbic.portal.offermanager.dataresources.offers

import groovy.util.logging.Log4j2
import life.qbic.business.exceptions.DatabaseQueryException
import life.qbic.business.offers.create.CreateOfferDataSource
import life.qbic.business.offers.fetch.FetchOfferDataSource
import life.qbic.business.offers.identifier.TomatoIdFormatter
import life.qbic.datamodel.dtos.business.*
import life.qbic.datamodel.dtos.projectmanagement.ProjectCode
import life.qbic.datamodel.dtos.projectmanagement.ProjectIdentifier
import life.qbic.datamodel.dtos.projectmanagement.ProjectSpace
import life.qbic.portal.offermanager.dataresources.database.ConnectionProvider
import life.qbic.portal.offermanager.dataresources.persons.PersonDbConnector
import life.qbic.portal.offermanager.dataresources.products.ProductsDbConnector

import java.sql.*

/**
 * Handles the connection to the offer database
 *
 * Implements {@link CreateOfferDataSource}.
 * This connector is responsible for transferring data between the offer database and qOffer
 *
 * @since: 1.0.0
 * @author: Jennifer Bödker
 *
 */
@Log4j2
class OfferDbConnector implements CreateOfferDataSource, FetchOfferDataSource, ProjectAssistant, OfferOverviewDataSource{

    ConnectionProvider connectionProvider

    PersonDbConnector customerGateway
    ProductsDbConnector productGateway

    private static final String OFFER_INSERT_QUERY = "INSERT INTO offer (offerId, " +
            "creationDate, expirationDate, customerId, projectManagerId, projectTitle, " +
            "projectObjective, totalPrice, customerAffiliationId, vat, netPrice, overheads, totalDiscount, " +
            "checksum, experimentalDesign)"

    private static final String OFFER_SELECT_QUERY = "SELECT offerId, creationDate, expirationDate, customerId, projectManagerId, projectTitle," +
                                                        "projectObjective, totalPrice, customerAffiliationId, vat, netPrice, overheads, experimentalDesign FROM offer"


    OfferDbConnector(ConnectionProvider connectionProvider, PersonDbConnector personDbConnector, ProductsDbConnector productsDbConnector){
        this.connectionProvider = connectionProvider
        this.customerGateway = personDbConnector
        this.productGateway = productsDbConnector
    }

    @Override
    void store(Offer offer) throws DatabaseQueryException {

        if (offerAlreadyInDataSource(offer)) {
            throw new DatabaseQueryException("Offer with equal content of ${offer.identifier.toString()} already exists.")
        }

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

    @Override
    List<OfferId> fetchAllVersionsForOfferId(OfferId id) {
        String query = OFFER_SELECT_QUERY + " WHERE offerId LIKE ? "
        Connection connection = null
        List<OfferId> ids = []
        String idPattern = TomatoIdFormatter.removeVersion(TomatoIdFormatter.formatAsOfferId(id))

        try{
            connection = connectionProvider.connect()
            connection.withCloseable {
                PreparedStatement preparedStatement = it.prepareStatement(query)
                preparedStatement.setString(1, "${idPattern}_%")
                ResultSet resultSet = preparedStatement.executeQuery()

                while (resultSet.next()) {
                    String resultID = resultSet.getString(1)
                    OfferId offerId = parseOfferId(resultID)
                    ids.add(offerId)
                }

                return ids
            }
        }catch(Exception e){
            log.error(e.message)
            log.error(e.stackTrace.join("\n"))
            connection.rollback()
            throw new DatabaseQueryException("Could fetch offer versions for id ${id.toString()}.")
        }

    }

    private boolean offerAlreadyInDataSource(Offer offer) {
        String query = "SELECT checksum FROM offer WHERE checksum=?"
        Connection connection = null
        boolean isAlreadyInDataSource = false

        try{
            connection = connectionProvider.connect()
            connection.withCloseable {
                PreparedStatement preparedStatement = it.prepareStatement(query)
                preparedStatement.setString(1, "${offer.checksum}")
                ResultSet resultSet = preparedStatement.executeQuery()
                int numberOfRows = 0
                while(resultSet.next()) {
                    numberOfRows++
                }
                if (numberOfRows > 0) {
                    isAlreadyInDataSource = true
                }
            }
        } catch(Exception e){
            log.error(e.message)
            log.error(e.stackTrace.join("\n"))
            connection.rollback()
            throw new DatabaseQueryException("Could not check if offer ${offer.identifier} is " +
                    "already in the database.")
        }
        return isAlreadyInDataSource
    }

    /**
     * The method stores the offer in the QBiC database
     *
     * @param offer with the information of the offer to be stored
     * @return the id of the stored offer in the database
     */
    private int storeOffer(Offer offer, int projectManagerId, int customerId, int affiliationId){
        String sqlValues = "VALUE(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)"
        String queryTemplate = OFFER_INSERT_QUERY + " " + sqlValues
        def identifier = offer.identifier
        List<Integer> generatedKeys = []
        Connection connection = connectionProvider.connect()
        log.info("New offer with id: ${offer.identifier}")
        connection.withCloseable {
            String experimentalDesign = offer.experimentalDesign.orElse(null)
            PreparedStatement preparedStatement = it.prepareStatement(queryTemplate, Statement.RETURN_GENERATED_KEYS)
            preparedStatement.setString(1, identifier.toString())
            preparedStatement.setDate(2, new Date(offer.modificationDate.time))
            preparedStatement.setDate(3, new Date(offer.expirationDate.time))
            preparedStatement.setInt(4, customerId)
            preparedStatement.setInt(5, projectManagerId)
            preparedStatement.setString(6, offer.projectTitle)
            preparedStatement.setString(7, offer.projectObjective)
            preparedStatement.setDouble(8, offer.totalPrice)
            preparedStatement.setInt(9, affiliationId)
            preparedStatement.setDouble(10, offer.taxes)
            preparedStatement.setDouble(11, offer.netPrice)
            preparedStatement.setDouble(12, offer.overheads)
            preparedStatement.setDouble(13,offer.totalDiscountPrice)
            preparedStatement.setString(14, offer.checksum)
            preparedStatement.setString(15, experimentalDesign)


            preparedStatement.execute()

            def keys = preparedStatement.getGeneratedKeys()
            while (keys.next()) {
                generatedKeys.add(keys.getInt(1))
            }

            return generatedKeys[0]
        }
    }

    @Override
    List<OfferOverview> listOfferOverviews() {
        loadOfferOverview()
    }

    private List<OfferOverview> loadOfferOverview() {
        List<OfferOverview> offerOverviewList = []

        String query = "SELECT offerId, creationDate, projectTitle, " +
                "totalPrice, first_name, last_name, email, projectManagerId, associatedProject\n" +
                "FROM offer \n" +
                "LEFT JOIN person \n" +
                "ON offer.customerId = person.id"

        Connection connection = connectionProvider.connect()
        connection.withCloseable {
            PreparedStatement statement = it.prepareStatement(query)
            ResultSet resultSet = statement.executeQuery()
            while (resultSet.next()) {
                Customer customer = new Customer.Builder(
                        resultSet.getString("first_name"),
                        resultSet.getString("last_name"),
                        resultSet.getString("email"))
                        .build()
                int projectManagerId = resultSet.getInt("projectManagerId")
                ProjectManager projectManager = customerGateway.getProjectManager(projectManagerId)
                def projectTitle = resultSet.getString("projectTitle")
                def totalCosts = resultSet.getDouble("totalPrice")
                def creationDate = resultSet.getDate("creationDate")
                def customerName = "${customer.getFirstName()} ${customer.getLastName()}"
                def projectManagerName = "${projectManager.getFirstName()} ${projectManager.getLastName()}"
                def offerId = parseOfferId(resultSet.getString("offerId"))
                Optional<ProjectIdentifier> projectIdentifier = parseProjectIdentifier(
                        resultSet.getString("associatedProject"))

                OfferOverview offerOverview
                if (projectIdentifier.isPresent()) {
                    offerOverview = new OfferOverview(offerId,
                            creationDate,projectTitle,
                            customerName, projectManagerName ,totalCosts, projectIdentifier.get())
                } else {
                    offerOverview = new OfferOverview(offerId,
                            creationDate,projectTitle, "",
                            customerName, projectManagerName, totalCosts)
                }
                offerOverviewList.add(offerOverview)
            }
        }
        return offerOverviewList
    }

    static OfferId parseOfferId(String offerId) {
        def splitId = offerId.split("_")
        // The first entry [0] contains the id prefix, no need to parse it.
        def projectPart = splitId[1]
        def randomPart = splitId[2]
        def version = splitId[3]
        return new OfferId(projectPart, randomPart, version)
    }

    @Override
    Optional<Offer> getOffer(OfferId offerId) {
        Optional<Offer> offer = Optional.empty()

        String query = "SELECT * " +
                "FROM offer \n" +
                "WHERE offerId=?"

        Connection connection = connectionProvider.connect()
        connection.withCloseable {
            PreparedStatement statement = it.prepareStatement(query)
            statement.setString(1, TomatoIdFormatter.formatAsOfferId(offerId))
            ResultSet resultSet = statement.executeQuery()
            while (resultSet.next()) {
                /*
                Load the offer Id first
                 */
                OfferId fetchedOfferId = parseOfferId(resultSet.getString("offerId"))
                int offerPrimaryId = resultSet.getInt("id")
                /*
                Load customer and project manager info
                 */
                int customerId =  resultSet.getInt("customerId")
                int projectManagerId = resultSet.getInt("projectManagerId")
                Customer customer = customerGateway.getCustomer(customerId)
                ProjectManager projectManager = customerGateway.getProjectManager(projectManagerId)
                /*
                Load general offer info
                 */
                String projectTitle = resultSet.getString("projectTitle")
                String projectObjective = resultSet.getString("projectObjective")
                double totalCosts = resultSet.getDouble("totalPrice")
                double vat = resultSet.getDouble("vat")
                double overheads = resultSet.getDouble("overheads")
                double net = resultSet.getDouble("netPrice")
                Date creationDate = resultSet.getDate("creationDate")
                Date expirationDate = resultSet.getDate("expirationDate")
                int selectedAffiliationId = resultSet.getInt("customerAffiliationId")
                Affiliation selectedAffiliation = customerGateway.getAffiliation(selectedAffiliationId)
                List<ProductItem> items = productGateway.getItemsForOffer(offerPrimaryId)
                String checksum = resultSet.getString("checksum")
                String associatedProject = resultSet.getString("associatedProject")
                String experimentalDesign = resultSet.getString("experimentalDesign")

                def offerBuilder = new Offer.Builder(
                        customer,
                        projectManager,
                        projectTitle,
                        projectObjective,
                        selectedAffiliation)
                        .modificationDate(creationDate)
                        .expirationDate(expirationDate)
                        .identifier(fetchedOfferId)
                        .items(items)
                        .totalPrice(totalCosts)
                        .taxes(vat)
                        .overheads(overheads)
                        .netPrice(net)
                        .checksum(checksum)
                Optional<ProjectIdentifier> projectIdentifier = parseProjectIdentifier(associatedProject)
                if (projectIdentifier.isPresent()) {
                    offerBuilder.associatedProject(projectIdentifier.get())
                }
                if(experimentalDesign){
                    offerBuilder.experimentalDesign(experimentalDesign)
                }
                offer = Optional.of(offerBuilder.build())
            }
        }
        return offer
    }

    private static Optional<ProjectIdentifier> parseProjectIdentifier(String projectIdentifier) {
        Optional<ProjectIdentifier> identifier = Optional.empty()
        if (!projectIdentifier) {
            return identifier
        }
        try {
            /*
            A full openBIS project ID has the format: '/<space>/<project>', where
            <space> and <project> are placeholders for real space and project names.
             */
            def splittedIdentifier = projectIdentifier.split("/")
            if (splittedIdentifier.length != 3) {
                throw new RuntimeException(
                        "Project identifier has an unexpected number of separators: ${projectIdentifier}. " +
                                "The expected format must follow this schema: \'/<space>/<project>\'")
            }
            def space = new ProjectSpace(splittedIdentifier[1])
            def code = new ProjectCode(splittedIdentifier[2])
            identifier = Optional.of(new ProjectIdentifier(space, code))
        } catch (Exception e) {
            log.error(e.message)
            log.error(e.stackTrace.join("\n"))
        }
        return identifier
    }

    /**
     * {@inheritDocs}
     */
    @Override
    void linkOfferWithProject(OfferId offerId, ProjectIdentifier projectIdentifier) {
        String query = "UPDATE offer SET associatedProject = ? WHERE offerId = ?"

        Connection connection = connectionProvider.connect()
        connection.withCloseable {
            PreparedStatement statement = it.prepareStatement(query)
            statement.setString(1, projectIdentifier.toString())
            statement.setString(2, TomatoIdFormatter.formatAsOfferId(offerId))
            statement.executeUpdate()
        }
    }
}
