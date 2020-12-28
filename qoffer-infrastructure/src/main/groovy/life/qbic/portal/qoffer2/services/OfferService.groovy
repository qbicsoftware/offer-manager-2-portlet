package life.qbic.portal.qoffer2.services

import life.qbic.datamodel.dtos.business.Offer
import life.qbic.portal.qoffer2.events.EventEmitter

/**
 * Service that represents available offer for downloads.
 *
 * This service offers an EventEmitter property that can be
 * used for inter component communication, when a new offer
 * source is available for download.
 *
 * <More detailed description - When to use, what it solves, etc.>
 *
 * @since 1.0.0
 */
class OfferService implements Service {

    final EventEmitter<Offer> offerCreatedEvent

    OfferService() {
        offerCreatedEvent = new EventEmitter<>()
    }

    @Override
    void reloadResources() {

    }
}
