package life.qbic.portal.offermanager.dataresources.customers

import life.qbic.datamodel.dtos.business.Affiliation
import life.qbic.portal.offermanager.communication.EventEmitter
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
class AffiliationResourcesService implements ResourcesService {

    private final CustomerDbConnector dbConnector

    private final List<Affiliation> availableAffiliations

    final EventEmitter<List<Affiliation>> eventEmitter

    AffiliationResourcesService(CustomerDbConnector dbConnector) {
        this.dbConnector = dbConnector
        this.availableAffiliations = dbConnector.listAllAffiliations()
        this.eventEmitter = new EventEmitter<>()
    }

    @Override
    void reloadResources() {
        this.availableAffiliations.clear()
        this.availableAffiliations.addAll(dbConnector.listAllAffiliations())
        this.eventEmitter.emit(availableAffiliations.asList())
    }
}
