package life.qbic.portal.offermanager.dataresources.offers

import com.vaadin.data.provider.SortOrder
import com.vaadin.shared.data.sort.SortDirection
import groovy.util.logging.Log4j2
import life.qbic.business.exceptions.DatabaseQueryException
import life.qbic.business.offers.OfferExistsException
import life.qbic.business.offers.OfferV2
import life.qbic.business.offers.create.CreateOfferDataSource
import life.qbic.business.offers.fetch.FetchOfferDataSource
import life.qbic.datamodel.dtos.business.OfferId
import life.qbic.datamodel.dtos.projectmanagement.ProjectIdentifier
import life.qbic.portal.offermanager.ExportOffersDataSource
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
class OfferDbConnector implements CreateOfferDataSource, FetchOfferDataSource, ProjectAssistant, OfferOverviewDataSource, ExportOffersDataSource {

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
            session.beginTransaction()
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
    List<OfferOverview> fetchLatestOverviews(int offset, int limit, OfferFilter filter,
                                             List<SortOrder<String>> sortOrders) {
        try (Session session = sessionProvider.getCurrentSession()) {
            session.beginTransaction()
            List<OfferV2> offerV2List = loadLatestOfferPage(session, offset, limit, filter, sortOrders)
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
    int countLatestOverviews(OfferFilter filter) {
        try (Session session = sessionProvider.getCurrentSession()) {
            session.beginTransaction()
            int count = countLatestOverviews2(session, filter)
            session.getTransaction().commit()
            return count
        } catch (HibernateException e) {
            log.error(e.message, e)
            throw new DatabaseQueryException("Unable to count offer overviews.")
        }
    }

    /**
     * {@inheritDocs}
     */
    @Override
    List<OfferOverview> fetchVersionsOfOffer(OfferId familyId) {
        String familyPrefix = "O_" + familyId.projectConservedPart + "_" + familyId.randomPart
        try (Session session = sessionProvider.getCurrentSession()) {
            session.beginTransaction()
            org.hibernate.query.Query<OfferV2> query = session.createQuery(
                    "SELECT offer FROM OfferV2 offer WHERE offer.offerId LIKE :family ORDER BY offer.creationDate DESC", OfferV2.class)
            query.setParameter("family", familyPrefix + "_%")
            List<OfferOverview> overviewList = createOverviewList(query.list())
            session.getTransaction().commit()
            return overviewList
        } catch (HibernateException e) {
            log.error(e.message, e)
            throw new DatabaseQueryException("Unable to load offer versions for ${familyId}.")
        }
    }

    private static List<OfferOverview> createOverviewList(List<OfferV2> offerV2List) {
        return offerV2List.stream().map(OfferOverview::from).collect() as List<OfferOverview>
    }

    /**
     * Loads a single page of the latest offer version for each offer, applying the given
     * server-side filter and sort order.
     */
    private static List<OfferV2> loadLatestOfferPage(Session session, int offset, int limit,
                                                     OfferFilter filter,
                                                     List<SortOrder<String>> sortOrders) {
        Set<Integer> latestIds = latestVersionIds(session)
        StringBuilder hql = new StringBuilder("SELECT offer FROM OfferV2 offer WHERE offer.id IN (:latestIds)")
        Map<String, Object> parameters = new LinkedHashMap<>()
        applyFilters(hql, parameters, filter)
        applySorting(hql, sortOrders)
        org.hibernate.query.Query<OfferV2> query = session.createQuery(hql.toString(), OfferV2.class)
        query.setParameterList("latestIds", latestIds)
        parameters.forEach(query::setParameter)
        query.setFirstResult(offset)
        query.setMaxResults(limit)
        return query.list()
    }

    /**
     * Counts the latest offer versions that match the given filter.
     */
    private static int countLatestOverviews2(Session session, OfferFilter filter) {
        Set<Integer> latestIds = latestVersionIds(session)
        StringBuilder hql = new StringBuilder("SELECT COUNT(offer) FROM OfferV2 offer WHERE offer.id IN (:latestIds)")
        Map<String, Object> parameters = new LinkedHashMap<>()
        applyFilters(hql, parameters, filter)
        org.hibernate.query.Query<Long> query = session.createQuery(hql.toString(), Long.class)
        query.setParameterList("latestIds", latestIds)
        parameters.forEach(query::setParameter)
        return query.uniqueResult()?.intValue() ?: 0
    }

    /**
     * Resolves the database ids of the latest version of each offer family using a MariaDB
     * window function. The offerId has the form {@code O_<project>_<random>_<version>}, so the
     * family is everything before the last underscore and the version is the trailing integer.
     */
    private static Set<Integer> latestVersionIds(Session session) {
        String sql = "SELECT id FROM (" +
                "  SELECT id, ROW_NUMBER() OVER (" +
                "    PARTITION BY SUBSTRING_INDEX(offerId, '_', 3)" +
                "    ORDER BY CAST(SUBSTRING_INDEX(offerId, '_', -1) AS UNSIGNED) DESC" +
                "  ) AS rn FROM offers" +
                ") t WHERE t.rn = 1"
        List<Number> ids = session.createNativeQuery(sql).list()
        return ids.stream().map(Number::intValue).collect(Collectors.toSet())
    }

    /**
     * Appends the filter clauses to the HQL query and collects their parameters.
     */
    private static void applyFilters(StringBuilder hql, Map<String, Object> parameters, OfferFilter filter) {
        if (!filter) {
            return
        }
        if (filter.offerId) {
            hql.append(" AND lower(offer.offerId) LIKE :offerId")
            parameters.put("offerId", containsPattern(filter.offerId))
        }
        if (filter.projectTitle) {
            hql.append(" AND lower(offer.projectTitle) LIKE :projectTitle")
            parameters.put("projectTitle", containsPattern(filter.projectTitle))
        }
        if (filter.customer) {
            hql.append(" AND lower(concat(offer.customer.firstName, ' ', offer.customer.lastName)) LIKE :customer")
            parameters.put("customer", containsPattern(filter.customer))
        }
        if (filter.affiliationCategory) {
            hql.append(" AND lower(offer.selectedCustomerAffiliation.category) LIKE :affiliationCategory")
            parameters.put("affiliationCategory", containsPattern(filter.affiliationCategory))
        }
        if (filter.organisation) {
            hql.append(" AND lower(offer.selectedCustomerAffiliation.organization) LIKE :organisation")
            parameters.put("organisation", containsPattern(filter.organisation))
        }
        if (filter.addressAddition) {
            hql.append(" AND lower(offer.selectedCustomerAffiliation.addressAddition) LIKE :addressAddition")
            parameters.put("addressAddition", containsPattern(filter.addressAddition))
        }
        if (filter.projectManager) {
            hql.append(" AND lower(concat(offer.projectManager.firstName, ' ', offer.projectManager.lastName)) LIKE :projectManager")
            parameters.put("projectManager", containsPattern(filter.projectManager))
        }
        if (filter.projectId) {
            hql.append(" AND lower(offer.associatedProject) LIKE :projectId")
            parameters.put("projectId", containsPattern(filter.projectId))
        }
        if (filter.creationDate) {
            hql.append(" AND offer.creationDate = :creationDate")
            parameters.put("creationDate", filter.creationDate)
        }
    }

    /**
     * Appends the ORDER BY clause based on the given Vaadin sort orders.
     */
    private static void applySorting(StringBuilder hql, List<SortOrder<String>> sortOrders) {
        if (!sortOrders) {
            hql.append(" ORDER BY offer.creationDate DESC")
            return
        }
        List<String> orderParts = sortOrders.collect { order ->
            String property = sortProperty(order.sorted)
            String direction = order.direction == SortDirection.ASCENDING ? "ASC" : "DESC"
            "${property} ${direction}"
        }
        hql.append(" ORDER BY ").append(orderParts.join(", "))
    }

    /**
     * Maps a Vaadin sort property id (the grid column id) to the corresponding HQL property path.
     */
    private static String sortProperty(String columnId) {
        switch (columnId) {
            case "OfferId": return "offer.offerId"
            case "ProjectTitle": return "offer.projectTitle"
            case "Customer": return "offer.customer.lastName"
            case "AffiliationCategory": return "offer.selectedCustomerAffiliation.category"
            case "Organisation": return "offer.selectedCustomerAffiliation.organization"
            case "AddressAddition": return "offer.selectedCustomerAffiliation.addressAddition"
            case "ProjectManager": return "offer.projectManager.lastName"
            case "ProjectID": return "offer.associatedProject"
            case "CreationDate": return "offer.creationDate"
            default: return "offer.creationDate"
        }
    }

    private static String containsPattern(String value) {
        return "%${value.toLowerCase()}%"
    }

    /**
     * {@inheritDocs}
     */
    @Override
    void linkOfferWithProject(OfferId offerId, ProjectIdentifier projectIdentifier) {
        String businessOfferId = life.qbic.business.offers.identifier.OfferId.from(offerId.toString()).toString()
        List<OfferV2> result = []
        try (Session session = sessionProvider.getCurrentSession()) {
            session.beginTransaction()
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
            session.beginTransaction()
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
        String searchTerm = "%" + project + "_" + randomIdPart + "%"

        try (Session session = sessionProvider.getCurrentSession()) {
            session.beginTransaction()
            Query query = session.createQuery("SELECT offer FROM OfferV2 offer WHERE offer.offerId LIKE :id", OfferV2.class)
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
            Optional<OfferV2> firstOffer = result ? Optional.ofNullable(result.get(0)) : Optional.empty() as Optional<OfferV2>
            return firstOffer
        }
    }

    @Override
    List<OfferV2> findAllOffers() {
        try (Session session = sessionProvider.getCurrentSession()) {
            session.beginTransaction()
            List<OfferV2> offerV2List = session.createQuery("Select offer FROM OfferV2 offer", OfferV2.class).list()
            session.getTransaction().commit()
            return offerV2List
        } catch (HibernateException e) {
            throw new DatabaseQueryException("Unable to load offer overviews.", e)
        }
    }
}
