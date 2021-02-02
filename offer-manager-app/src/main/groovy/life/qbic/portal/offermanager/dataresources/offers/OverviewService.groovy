package life.qbic.portal.offermanager.dataresources.offers

import life.qbic.datamodel.dtos.business.Offer
import life.qbic.portal.offermanager.communication.EventEmitter
import life.qbic.portal.offermanager.communication.Subscription
import life.qbic.portal.offermanager.dataresources.offers.OfferDbConnector
import life.qbic.portal.offermanager.dataresources.offers.OfferResourcesService
import life.qbic.portal.offermanager.dataresources.ResourcesService
import life.qbic.portal.offermanager.dataresources.offers.OfferOverview

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

    private final OfferDbConnector offerDbConnector

    private final OfferResourcesService offerService

    private final EventEmitter<OfferOverview> updatedOverviewEvent

    OverviewService(OfferDbConnector offerDbConnector,
                    OfferResourcesService offerService) {
        this.offerDbConnector = offerDbConnector
        this.updatedOverviewEvent = new EventEmitter<>()
        this.offerService = offerService
        this.offerOverviewList = offerDbConnector.loadOfferOverview()
        subscribeToNewOffers()
    }

    private void subscribeToNewOffers(){
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
                offer.totalPrice
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

    /**
     * Returns a list of available offer overviews.
     * @return A list of available offer overviews.
     */
    List<OfferOverview> getOfferOverviewList() {
        final def overview = []
        /*
        We do not want to return a reference to the
        internal list, as this would make the list
        vulnerable for external changes.
        The list however contains immutable objects, these
        can be passed as reference.
         */
        overview.addAll(offerOverviewList.asList())
        return overview
    }
}
