package life.qbic.portal.qoffer2.services

import life.qbic.datamodel.dtos.business.Offer
import life.qbic.portal.qoffer2.events.EventEmitter

/**
 * Service that represents an offer to be updated.
 *
 * This service offers an EventEmitter property that can be
 * used for inter component communication, when a new offer
 * source is available for update.
 *
 * @since 1.0.0
 */
class OfferUpdateService implements Service {

    EventEmitter<Offer> offerForUpdateEvent

    OfferUpdateService() {
        this.offerForUpdateEvent = new EventEmitter<>()
    }

    @Override
    void reloadResources() {
        // Nothing to reload
    }
}
