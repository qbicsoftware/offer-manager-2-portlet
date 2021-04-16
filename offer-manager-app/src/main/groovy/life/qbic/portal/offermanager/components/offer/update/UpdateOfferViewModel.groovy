package life.qbic.portal.offermanager.components.offer.update

import life.qbic.datamodel.dtos.business.Offer
import life.qbic.portal.offermanager.communication.EventEmitter
import life.qbic.portal.offermanager.components.offer.create.CreateOfferViewModel
import life.qbic.portal.offermanager.dataresources.persons.CustomerResourceService
import life.qbic.portal.offermanager.dataresources.persons.ProjectManagerResourceService
import life.qbic.portal.offermanager.dataresources.products.ProductsResourcesService

import life.qbic.portal.offermanager.components.offer.create.ProductItemViewModel


/**
 * Model with data for updating an existing offer.
 *
 * This specialisation of the {@link CreateOfferViewModel} updates its data upon a received
 * offer update event.
 *
 * With respect to its parent class, it contains an additional service and subscribes to an
 * instance of {@link life.qbic.portal.offermanager.communication.EventEmitter}'s emitter property.
 *
 * Everytime such an event is emitted, it loads the event data into its properties.
 *
 * @since 1.0.0
 */
//fixme this might not be a real extension of the create offer use case
// in any case the view model should not extend the other view model
// this extension makes it difficult to debug imo (TK)
class UpdateOfferViewModel extends CreateOfferViewModel{

    final private EventEmitter<Offer> offerUpdate

    UpdateOfferViewModel(CustomerResourceService customerResourceService,
                         ProjectManagerResourceService managerResourceService,
                         ProductsResourcesService productsService,
                         EventEmitter<Offer> offerUpdateEvent) {
        super(customerResourceService, managerResourceService, productsService)
        this.offerUpdate = offerUpdateEvent

        this.offerUpdate.register((Offer offer) -> {
            loadData(offer)
        })
    }

    private void loadData(Offer offer) {
        super.offerId = offer.identifier
        super.projectTitle = offer.projectTitle
        super.projectObjective = offer.projectObjective
        super.customer = offer.customer
        super.customerAffiliation = offer.selectedCustomerAffiliation
        super.projectManager = offer.projectManager
        super.experimentalDesign = offer.experimentalDesign.isPresent() ? offer.experimentalDesign.get() : ""
        super.productItems.clear()
        super.productItems.addAll(offer.items.collect {
            new ProductItemViewModel(it.quantity, it.product)})
        super.savedOffer = Optional.of(offer)
    }
}
