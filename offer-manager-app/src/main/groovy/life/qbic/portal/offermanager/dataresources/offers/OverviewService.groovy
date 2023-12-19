package life.qbic.portal.offermanager.dataresources.offers

import life.qbic.business.RefactorConverter
import life.qbic.datamodel.dtos.business.Offer
import life.qbic.datamodel.dtos.projectmanagement.Project
import life.qbic.portal.offermanager.communication.EventEmitter
import life.qbic.portal.offermanager.communication.Subscription
import life.qbic.portal.offermanager.dataresources.ResourcesService

/**
 * Service that contains basic overview data about available offers.
 *
 * This service offers an EventEmitter property that can be
 * used for inter component communication, when a new offer overview
 * source is available for download.
 *
 * @since 1.0.0
 */
class OverviewService implements ResourcesService<OfferOverview> {

    private List<OfferOverview> offerOverviewList

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
        this.offerOverviewList = overviewDataSource.listOfferOverviews()
        subscribeToNewOffers()
        subscribeToNewProjects()
    }

    private void subscribeToNewProjects() {
        /*
        Whenever a new project is created, we want to update the associated
        offer overview with the project identifier detail
         */
        projectCreatedEvent.register({ Project project ->
            OfferOverview affectedOffer = offerOverviewList.find {
                it.offerId.equals(project.linkedOffer)
            }
            if (affectedOffer) {
                offerOverviewList.remove(affectedOffer)
                OfferOverview updatedOverview = new OfferOverview(
                        affectedOffer.offerId,
                        affectedOffer.modificationDate,
                        affectedOffer.projectTitle,
                        affectedOffer.customer.toString(),
                        affectedOffer.projectManager.toString(),
                        affectedOffer.totalPrice,
                        project.projectId, affectedOffer.affiliation)
                this.addToResource(updatedOverview)
            }
        })
    }

    private void subscribeToNewOffers() {
        /*
        Whenever a new offer is created, we want
        to update the offer overview content.
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

    @Override
    void reloadResources() {

    }

    @Override
    void addToResource(OfferOverview resourceItem) {
        offerOverviewList.add(resourceItem)
        updatedOverviewEvent.emit(resourceItem)
    }

    @Override
    void removeFromResource(OfferOverview resourceItem) {
        offerOverviewList.remove(resourceItem)
        updatedOverviewEvent.emit(resourceItem)
    }

    @Override
    Iterator<OfferOverview> iterator() {
        return new ArrayList(offerOverviewList).iterator()
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
