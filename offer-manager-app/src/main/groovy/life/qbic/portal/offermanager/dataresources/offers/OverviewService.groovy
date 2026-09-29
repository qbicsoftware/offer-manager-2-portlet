package life.qbic.portal.offermanager.dataresources.offers

import com.vaadin.data.provider.SortOrder
import life.qbic.business.RefactorConverter
import life.qbic.datamodel.dtos.business.Offer
import life.qbic.datamodel.dtos.business.OfferId
import life.qbic.datamodel.dtos.projectmanagement.Project
import life.qbic.portal.offermanager.communication.EventEmitter
import life.qbic.portal.offermanager.communication.Subscription
import life.qbic.portal.offermanager.dataresources.ResourcesService

/**
 * Service that provides paged access to basic overview data about available offers.
 *
 * <p>Offer overviews are loaded lazily from the underlying data source, so only the currently
 * requested page is fetched. When a new offer is created or an offer is linked to a project, an
 * event is emitted so that components can refresh their data.</p>
 *
 * @since 1.0.0
 */
class OverviewService implements ResourcesService<OfferOverview> {

    private final OfferOverviewDataSource overviewDataSource

    private final ResourcesService<Offer> offerService

    private final EventEmitter<OfferOverview> updatedOverviewEvent

    private final EventEmitter<Project> projectCreatedEvent

    OverviewService(OfferOverviewDataSource overviewDataSource,
                    ResourcesService<Offer> offerService,
                    EventEmitter<Project> projectCreatedEvent) {
        this.overviewDataSource = overviewDataSource
        this.updatedOverviewEvent = new EventEmitter<>()
        this.offerService = offerService
        this.projectCreatedEvent = projectCreatedEvent
        subscribeToNewOffers()
        subscribeToNewProjects()
    }

    private void subscribeToNewProjects() {
        /*
        Whenever a new project is created, the associated offer overview changes (it now points
        to a project). Emitting an event lets lazy consumers refresh and fetch the updated data.
         */
        projectCreatedEvent.register({ Project project ->
            updatedOverviewEvent.emit(null)
        })
    }

    private void subscribeToNewOffers() {
        /*
        Whenever a new offer is created, we want to update the offer overview content.
         */
        offerService.subscribe({
            def newOfferOverview = createOverviewFromOffer(it)
            addToResource(newOfferOverview)
        })
    }

    static OfferOverview createOverviewFromOffer(Offer offer) {
        return new OfferOverview(
                offer.identifier,
                offer.getModificationDate(),
                offer.projectTitle,
                "",
                "${offer.customer.firstName} ${offer.customer.lastName}",
                "${offer.projectManager.firstName} ${offer.projectManager.lastName}",
                offer.totalPrice as double,
                RefactorConverter.toAffiliation(offer.selectedCustomerAffiliation)
        )
    }

    /**
     * Fetches a page of the latest offer version for each offer, applying the given server-side
     * filter and sort order.
     */
    List<OfferOverview> fetchLatestOverviews(int offset, int limit, OfferFilter filter,
                                             List<SortOrder<String>> sortOrders) {
        return overviewDataSource.fetchLatestOverviews(offset, limit, filter, sortOrders)
    }

    /**
     * Counts the latest offer versions that match the given filter.
     */
    int countLatestOverviews(OfferFilter filter) {
        return overviewDataSource.countLatestOverviews(filter)
    }

    /**
     * Fetches all offer versions that belong to the same offer family as the given offer id.
     */
    List<OfferOverview> fetchVersionsOfOffer(OfferId familyId) {
        return overviewDataSource.fetchVersionsOfOffer(familyId)
    }

    @Override
    void reloadResources() {
        updatedOverviewEvent.emit(null)
    }

    @Override
    void addToResource(OfferOverview resourceItem) {
        updatedOverviewEvent.emit(resourceItem)
    }

    @Override
    void removeFromResource(OfferOverview resourceItem) {
        updatedOverviewEvent.emit(resourceItem)
    }

    @Override
    Iterator<OfferOverview> iterator() {
        return Collections.emptyIterator()
    }

    @Override
    void subscribe(Subscription<OfferOverview> subscription) {
        updatedOverviewEvent.register(subscription)
    }

    @Override
    void unsubscribe(Subscription<OfferOverview> subscription) {
        updatedOverviewEvent.unregister(subscription)
    }
}
