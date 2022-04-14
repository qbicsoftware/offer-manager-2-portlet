package life.qbic.portal.offermanager.dataresources.offers

import life.qbic.business.RefactorConverter
import life.qbic.datamodel.dtos.business.Customer
import life.qbic.datamodel.dtos.business.Offer
import life.qbic.datamodel.dtos.business.ProjectManager
import life.qbic.portal.offermanager.communication.EventEmitter
import life.qbic.portal.offermanager.communication.Subscription
import life.qbic.portal.offermanager.dataresources.ResourcesService

/**
 * ResourcesService that represents available offer for downloads.
 *
 * This service offers an EventEmitter property that can be
 * used for inter component communication, when a new offer
 * source is available for download.
 *
 * @since 1.0.0
 */
class OfferResourcesService implements ResourcesService<Offer> {

    private final EventEmitter<Offer> offerResourceEvent
    private final ListOffersDataSource listOffersDataSource
    private final List<Offer> availableOffers

    OfferResourcesService(ListOffersDataSource listOffersDataSource, ResourcesService<Customer> customerService, ResourcesService<ProjectManager> managerService) {
        offerResourceEvent = new EventEmitter<>()
        customerService.subscribe(it -> reloadResources())
        managerService.subscribe(it -> reloadResources())
        // For now it is fine to not preload the content from the database (time intensive)
        availableOffers = []
        this.listOffersDataSource = listOffersDataSource
    }

    /**
     * The implementation of this method is currently not supported, it throws a UnsupportedOperationException exception upon calling
     */
    @Override
    void reloadResources() {
        availableOffers.clear()
        List<Offer> updatedOffers = listOffersDataSource.findAll().stream().map(RefactorConverter::toOfferDto).collect()
        updatedOffers.forEach(this::addToResource)
    }

    @Override
    void subscribe(Subscription<Offer> subscription) {
        offerResourceEvent.register(subscription)
    }

    @Override
    void unsubscribe(Subscription<Offer> subscription) {
        offerResourceEvent.unregister(subscription)
    }

    @Override
    void addToResource(Offer resourceItem) {
        availableOffers.add(resourceItem)
        offerResourceEvent.emit(resourceItem)
    }

    @Override
    void removeFromResource(Offer resourceItem) {
        availableOffers.remove(resourceItem)
        offerResourceEvent.emit(resourceItem)
    }

    @Override
    Iterator<Offer> iterator() {
        return new ArrayList(availableOffers).iterator()
    }
}
