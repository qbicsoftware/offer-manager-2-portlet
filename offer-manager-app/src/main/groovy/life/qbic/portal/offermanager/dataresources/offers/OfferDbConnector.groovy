package life.qbic.portal.offermanager.dataresources.offers


import groovy.util.logging.Log4j2
import life.qbic.business.exceptions.DatabaseQueryException
import life.qbic.business.offers.OfferExistsException
import life.qbic.business.offers.OfferV2
import life.qbic.business.offers.create.CreateOfferDataSource
import life.qbic.business.offers.fetch.FetchOfferDataSource
import life.qbic.datamodel.dtos.business.OfferId
import life.qbic.datamodel.dtos.projectmanagement.ProjectIdentifier
import life.qbic.portal.offermanager.dataresources.database.ConnectionProvider
import life.qbic.portal.offermanager.dataresources.database.SessionProvider
import life.qbic.portal.offermanager.dataresources.persons.PersonDbConnector
import life.qbic.portal.offermanager.dataresources.products.ProductsDbConnector
import org.hibernate.HibernateException
import org.hibernate.Session

import javax.persistence.Query
import java.util.stream.Collectors

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
class OfferDbConnector implements CreateOfferDataSource, FetchOfferDataSource, ProjectAssistant, OfferOverviewDataSource {

    SessionProvider sessionProvider

    ConnectionProvider connectionProvider

    PersonDbConnector customerGateway

    ProductsDbConnector productGateway

    private static final String OFFER_INSERT_QUERY = "INSERT INTO offer (offerId, " +
            "creationDate, expirationDate, customerId, projectManagerId, projectTitle, " +
            "projectObjective, totalPrice, customerAffiliationId, vat, netPrice, overheads, itemDiscount, " +
            "checksum, experimentalDesign)"

    private static final String OFFER_SELECT_QUERY = "SELECT offerId, creationDate, expirationDate, customerId, projectManagerId, projectTitle," +
            "projectObjective, totalPrice, customerAffiliationId, vat, netPrice, overheads, experimentalDesign FROM offer"


    /**
     * Creates a new instance of OfferDbConnector
     *
     * @param connectionProvider
     * @param personDbConnector
     * @param productsDbConnector
     * @deprecated since 1.3.0, please use {@link OfferDbConnector#OfferDbConnector(ConnectionProvider, PersonDbConnector, ProductsDbConnector, SessionProvider)}
     */
    @Deprecated
    OfferDbConnector(ConnectionProvider connectionProvider, PersonDbConnector personDbConnector, ProductsDbConnector productsDbConnector) {
        this.connectionProvider = connectionProvider
        this.customerGateway = personDbConnector
        this.productGateway = productsDbConnector
        this.sessionProvider = null
    }

    /**
     * Creates a new instance of OfferDbConnector
     *
     * @param connectionProvider
     * @param personDbConnector
     * @param productsDbConnector
     * @param sessionProvider
     * @since 1.3.0
     */
    OfferDbConnector(ConnectionProvider connectionProvider, PersonDbConnector personDbConnector, ProductsDbConnector productsDbConnector, SessionProvider sessionProvider) {
        this.connectionProvider = connectionProvider
        this.customerGateway = personDbConnector
        this.productGateway = productsDbConnector
        this.sessionProvider = sessionProvider
    }


    /**
     * Searches for an offer with the same content in the database.
     * The method performs an equality check based on the content, not based on the aggregate identity.
     * @param offer offer to use for the search
     * @return true, if an offer with equal content exists in the database, else false
     */
    protected boolean equalOfferExists(OfferV2 offer) {
        String checksum = offer.getChecksum()
        return offerChecksumExists(checksum)
    }

    private boolean offerChecksumExists(String checksum) {
        boolean checksumPresent
        try (Session session = sessionProvider.getCurrentSession()) {
            Query query = session.createQuery("Select o FROM OfferV2 o where o.checksum=:checksumOfInterest ", OfferV2.class)
            query.setParameter("checksumOfInterest", checksum)
            checksumPresent = !query.list().isEmpty()
            session.getTransaction().commit()
        }
        return checksumPresent
    }

    /**
     * {@inheritDocs}
     */
    @Override
    List<OfferOverview> listOfferOverviews() {
        loadOfferOverview()
    }

    private static List<OfferOverview> createOverviewList(List<OfferV2> offerV2List) {
        return offerV2List.stream().map(OfferOverview::from).collect() as List<OfferOverview>
    }

    private List<OfferOverview> loadOfferOverview() {
        try (Session session = sessionProvider.getCurrentSession()) {
            session.beginTransaction()
            List<OfferV2> offerV2List = session.createQuery("Select offer FROM OfferV2 offer", OfferV2.class).list()
            List<OfferOverview> overviewList = createOverviewList(offerV2List)
            session.getTransaction().commit()
            return overviewList
        } catch (HibernateException e) {
            log.error(e.message, e)
            throw new DatabaseQueryException("Unable to load offer overviews.")
        }
    }

    /**
     * {@inheritDocs}
     */
    @Override
    void linkOfferWithProject(OfferId offerId, ProjectIdentifier projectIdentifier) {
        String businessOfferId = life.qbic.business.offers.identifier.OfferId.from(offerId.toString()).toString()
        List<OfferV2> result = []
        try (Session session = sessionProvider.getCurrentSession()) {
            Query query = session.createQuery("select offer from OfferV2 offer where offer.offerId=:offerIdToMatch", OfferV2.class)
            query.setParameter("offerIdToMatch", businessOfferId)
            result.addAll(query.list() as List<OfferV2>)
            if (result.isEmpty()) {
                throw new DatabaseQueryException("Cannot find offer with the id: " + offerId.toString())
            }
            OfferV2 offer = result.get(0)
            offer.setAssociatedProject(projectIdentifier)

            session.save(offer)
            session.getTransaction().commit()
        }
    }

    @Override
    void store(OfferV2 offer) throws OfferExistsException {
        if (equalOfferExists(offer)) {
            throw new OfferExistsException("Offer with equal content of ${offer.identifier.toString()} already exists.")
        }
        try (Session session = sessionProvider.getCurrentSession()) {
            session.save(offer)
        } catch (HibernateException e) {
            log.error(e.getMessage(), e)
            throw new DatabaseQueryException("Unexpected error. Something went wrong during the offer saving.")
        }
    }


    @Override
    List<life.qbic.business.offers.identifier.OfferId> fetchAllVersionsForOfferId(life.qbic.business.offers.identifier.OfferId id) {
        String project = id.getProjectPart()
        String randomIdPart = id.getRandomPart()
        String searchTerm = project + "_" + randomIdPart

        try (Session session = sessionProvider.getCurrentSession()) {
            Query query = session.createQuery("select offer from OfferV2 offer where offer.offerId like '%:id%'", OfferV2.class)
            query.setParameter("id", searchTerm)
            List<OfferV2> result = query.list()
            return result.stream().map((OfferV2 offer) -> offer.getIdentifier()).collect(Collectors.toList())
        } catch (HibernateException e) {
            log.error(e.message, e)
            throw new DatabaseQueryException("Unexpected exception during the search for all versions of offer " + id.toString())
        }
    }

    @Override
    Optional<OfferV2> getOffer(life.qbic.business.offers.identifier.OfferId oldId) {
        try (Session session = sessionProvider.getCurrentSession()) {
            session.beginTransaction()
            Query query = session.createQuery("select offer from OfferV2 offer where offer.offerId = :idOfInterest", OfferV2.class)
            query.setParameter("idOfInterest", oldId.toString())
            List<OfferV2> result = query.list()
            Optional<OfferV2> firstOffer = result ? Optional.ofNullable(result.get(0)) : Optional.empty()
            firstOffer.ifPresent(OfferV2::getItems)
            return firstOffer
        }
    }
}
