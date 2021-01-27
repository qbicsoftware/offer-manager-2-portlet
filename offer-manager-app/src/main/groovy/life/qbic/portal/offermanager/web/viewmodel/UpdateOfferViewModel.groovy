package life.qbic.portal.offermanager.web.viewmodel

import life.qbic.datamodel.dtos.business.Offer
import life.qbic.portal.offermanager.customers.PersonResourcesService
import life.qbic.portal.offermanager.services.OfferUpdateService


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

    final private life.qbic.portal.offermanager.services.OfferUpdateService offerUpdateService

    UpdateOfferViewModel(PersonResourcesService personService, life.qbic.portal.offermanager.products.ProductsResourcesService productsService,
                         life.qbic.portal.offermanager.services.OfferUpdateService offerUpdateService) {
        super(personService, productsService)
        this.offerUpdateService = offerUpdateService

        this.offerUpdateService.offerForUpdateEvent.register((Offer offer) -> {
            loadData(offer)
        })
    }

    private void loadData(Offer offer) {
        this.offerId = offer.identifier
        this.projectTitle = offer.projectTitle
        this.projectDescription = offer.projectDescription
        this.customer = offer.customer
        this.customerAffiliation = offer.selectedCustomerAffiliation
        this.projectManager = offer.projectManager
        this.productItems.clear()
        this.productItems.addAll(offer.items.collect {
            new ProductItemViewModel(it.quantity, it.product)})
    }
}
