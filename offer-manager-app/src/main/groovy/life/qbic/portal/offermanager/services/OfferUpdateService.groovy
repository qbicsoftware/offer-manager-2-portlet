package life.qbic.portal.offermanager.services

import life.qbic.datamodel.dtos.business.Offer

/**
 * Service that represents an offer to be updated.
 *
 * This service offers an EventEmitter property that can be
 * used for inter component communication, when a new offer
 * source is available for update.
 *
 * @since 1.0.0
 */
class OfferUpdateService implements ResourcesService {

    life.qbic.portal.offermanager.events.EventEmitter<Offer> offerForUpdateEvent

    OfferUpdateService() {
        this.offerForUpdateEvent = new life.qbic.portal.offermanager.events.EventEmitter<>()
    }

    @Override
    void reloadResources() {
        // Nothing to reload
    }
}
