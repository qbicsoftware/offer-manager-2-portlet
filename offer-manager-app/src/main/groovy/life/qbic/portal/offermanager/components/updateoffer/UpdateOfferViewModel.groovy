package life.qbic.portal.offermanager.components.updateoffer

import life.qbic.datamodel.dtos.business.Offer
import life.qbic.portal.offermanager.components.createoffer.CreateOfferViewModel
import life.qbic.portal.offermanager.dataresources.customers.PersonResourcesService
import life.qbic.portal.offermanager.dataresources.products.ProductsResourcesService
import life.qbic.portal.offermanager.dataresources.offers.OfferUpdateService
import life.qbic.portal.offermanager.web.viewmodel.ProductItemViewModel


/**
 * Model with data for updating an existing offer.
 *
 * This specialisation of the {@link life.qbic.portal.offermanager.components.createoffer.CreateOfferViewModel} updates its data upon a received
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

    UpdateOfferViewModel(PersonResourcesService personService, ProductsResourcesService productsService,
                         OfferUpdateService offerUpdateService) {
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
