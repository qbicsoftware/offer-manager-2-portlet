package life.qbic.portal.offermanager.dataresources.persons

import life.qbic.business.persons.affiliation.list.ListAffiliationsDataSource
import life.qbic.datamodel.dtos.business.Affiliation
import life.qbic.portal.offermanager.communication.EventEmitter
import life.qbic.portal.offermanager.communication.Subscription
import life.qbic.portal.offermanager.dataresources.ResourcesService

/**
 * Customer service that holds resources about available affiliations
 *
 * Customer service that holds resources about available affiliation information
 * and exposes an event emitter, that can be used to subscribe
 * to any update event of the underlying affiliation resource data.
 *
 * @since 1.0.0
 */
class AffiliationResourcesService implements ResourcesService<Affiliation> {

    private final ListAffiliationsDataSource listAffiliationsDataSource

    private final List<Affiliation> availableAffiliations

    private final EventEmitter<Affiliation> eventEmitter

    AffiliationResourcesService(ListAffiliationsDataSource listAffiliationsDataSource) {
        this.listAffiliationsDataSource = listAffiliationsDataSource
        this.availableAffiliations = listAffiliationsDataSource.listAllAffiliations()
        this.eventEmitter = new EventEmitter<>()
    }

    @Override
    void reloadResources() {
        //do it explicitly to trigger the service
        availableAffiliations.clear()

        List updatedEntries = listAffiliationsDataSource.listAllAffiliations()
        updatedEntries.each {
            addToResource(it)
        }
    }

    @Override
    void subscribe(Subscription<Affiliation> subscription) {
        eventEmitter.register(subscription)
    }

    @Override
    void unsubscribe(Subscription<Affiliation> subscription) {
        eventEmitter.unregister(subscription)
    }

    @Override
    void addToResource(Affiliation resourceItem) {
        this.availableAffiliations.add(resourceItem)
        this.eventEmitter.emit(resourceItem)
    }

    @Override
    void removeFromResource(Affiliation resourceItem) {
        this.availableAffiliations.remove(resourceItem)
        this.eventEmitter.emit(resourceItem)
    }

    @Override
    Iterator<Affiliation> iterator() {
        return new ArrayList(availableAffiliations).iterator()
    }
}
