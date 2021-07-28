package life.qbic.portal.offermanager.components.offer.update

import life.qbic.datamodel.dtos.business.Customer
import life.qbic.datamodel.dtos.business.Offer
import life.qbic.datamodel.dtos.business.ProjectManager
import life.qbic.datamodel.dtos.business.services.Product
import life.qbic.datamodel.dtos.general.Person
import life.qbic.portal.offermanager.communication.EventEmitter
import life.qbic.portal.offermanager.components.offer.create.CreateOfferViewModel
import life.qbic.portal.offermanager.components.offer.create.ProductItemViewModel
import life.qbic.portal.offermanager.dataresources.ResourcesService

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
class UpdateOfferViewModel extends CreateOfferViewModel{

    final private EventEmitter<Offer> offerUpdate

    UpdateOfferViewModel(ResourcesService<Customer> customerResourceService,
                         ResourcesService<ProjectManager> managerResourceService,
                         ResourcesService<Product> productsService,
                         EventEmitter<Person> updatePersonEvent,
                         EventEmitter<Offer> offerUpdateEvent) {
        super(customerResourceService, managerResourceService, productsService, updatePersonEvent)
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
        super.experimentalDesign = offer.experimentalDesign.orElse("")
        super.productItems.clear()
        super.productItems.addAll(offer.items.collect {
            new ProductItemViewModel(it.quantity, it.product)})
        super.savedOffer = Optional.of(offer)
    }
}
