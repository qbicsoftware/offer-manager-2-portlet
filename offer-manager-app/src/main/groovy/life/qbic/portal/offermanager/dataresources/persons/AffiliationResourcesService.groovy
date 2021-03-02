package life.qbic.portal.offermanager.dataresources.persons

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

    private final PersonDbConnector dbConnector

    private final List<Affiliation> availableAffiliations

    private final EventEmitter<Affiliation> eventEmitter

    AffiliationResourcesService(PersonDbConnector dbConnector) {
        this.dbConnector = dbConnector
        this.availableAffiliations = dbConnector.listAllAffiliations()
        this.eventEmitter = new EventEmitter<>()
    }

    @Override
    void reloadResources() {
        this.availableAffiliations.clear()
        this.availableAffiliations.addAll(dbConnector.listAllAffiliations())
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
