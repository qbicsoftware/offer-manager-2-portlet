package life.qbic.portal.offermanager.offers

import life.qbic.datamodel.dtos.business.Offer

/**
 * ResourcesService that represents available offer for downloads.
 *
 * This service offers an EventEmitter property that can be
 * used for inter component communication, when a new offer
 * source is available for download.
 *
 * @since 1.0.0
 */
class OfferResourcesService implements life.qbic.portal.offermanager.services.ResourcesService {

    final life.qbic.portal.offermanager.events.EventEmitter<Offer> offerCreatedEvent

    OfferResourcesService() {
        offerCreatedEvent = new life.qbic.portal.offermanager.events.EventEmitter<>()
    }

    @Override
    void reloadResources() {

    }
}
