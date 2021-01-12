package life.qbic.portal.qoffer2.services

import life.qbic.datamodel.dtos.business.Affiliation
import life.qbic.portal.qoffer2.customers.CustomerDbConnector
import life.qbic.portal.qoffer2.events.EventEmitter

/**
 * Customer service that holds resources about available affiliation information
 * and exposes an event emitter, that can be used to subscribe
 * to any update event of the underlying affiliation resource data.
 *
 * @since 1.0.0
 */
class AffiliationService implements Service {

    private final CustomerDbConnector dbConnector

    private final List<Affiliation> availableAffiliations

    final EventEmitter<List<Affiliation>> eventEmitter

    AffiliationService(CustomerDbConnector dbConnector) {
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
