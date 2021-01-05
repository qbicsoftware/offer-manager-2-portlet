package life.qbic.portal.qoffer2.web.viewmodel

import life.qbic.datamodel.dtos.business.Offer
import life.qbic.portal.qoffer2.services.OfferUpdateService
import life.qbic.portal.qoffer2.services.PersonService


/**
 * Model with data for updating an existing offer.
 *
 * This specialisation of the {@link CreateOfferViewModel} updates its data upon a received
 * offer update event.
 *
 * With respect to its parent class, it contains an additional service and subscribes to an
 * instance of {@link OfferUpdateService}'s event emitter property.
 *
 * Everytime such an event is emitted, it loads the event data into its properties.
 *
 * @since 1.0.0
 */
class UpdateOfferViewModel extends CreateOfferViewModel{

    final private OfferUpdateService offerUpdateService

    UpdateOfferViewModel(PersonService personService,
                         OfferUpdateService offerUpdateService) {
        super(personService)
        this.offerUpdateService = offerUpdateService

        this.offerUpdateService.offerForUpdateEvent.register( (Offer offer) -> {
            loadData(offer)
        })
    }

    private void loadData(Offer offer) {
        this.projectTitle = offer.projectTitle
        this.projectDescription = offer.projectDescription
        this.productItems.addAll(offer.items.collect {
            new ProductItemViewModel(it.quantity, it.product)})
    }
}
