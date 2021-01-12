package life.qbic.portal.qoffer2.offers

import life.qbic.datamodel.dtos.business.Offer
import life.qbic.portal.qoffer2.events.EventEmitter
import life.qbic.portal.qoffer2.services.ResourcesService

/**
 * ResourcesService that represents available offer for downloads.
 *
 * This service offers an EventEmitter property that can be
 * used for inter component communication, when a new offer
 * source is available for download.
 *
 * @since 1.0.0
 */
class OfferResourcesService implements ResourcesService {

    final EventEmitter<Offer> offerCreatedEvent

    OfferResourcesService() {
        offerCreatedEvent = new EventEmitter<>()
    }

    @Override
    void reloadResources() {

    }
}
