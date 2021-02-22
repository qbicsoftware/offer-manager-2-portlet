package life.qbic.portal.offermanager.components.person.update

import life.qbic.datamodel.dtos.business.Customer
import life.qbic.datamodel.dtos.general.Person
import life.qbic.portal.offermanager.communication.EventEmitter
import life.qbic.portal.offermanager.components.person.create.CreatePersonViewModel
import life.qbic.portal.offermanager.dataresources.persons.AffiliationResourcesService
import life.qbic.portal.offermanager.dataresources.persons.CustomerResourceService

import life.qbic.portal.offermanager.dataresources.persons.ProjectManagerResourceService

/**
 * Model with data for updating an existing person.
 *
 * This specialisation of the {@link life.qbic.portal.offermanager.components.person.create.CreatePersonViewModel} updates its data upon a received
 * customer update event.
 *
 * With respect to its parent class, it contains an additional service and subscribes to an
 * instance of {@link life.qbic.portal.offermanager.dataresources.offers.OfferUpdateService}'s event emitter property.
 *
 * Everytime such an event is emitted, it loads the event data into its properties.
 *
 * @since: 1.0.0
 *
 */
class UpdatePersonViewModel extends CreatePersonViewModel{

    final private EventEmitter<Person> customerUpdate

    UpdatePersonViewModel(CustomerResourceService customerService, ProjectManagerResourceService managerResourceService, AffiliationResourcesService affiliationService
                          , EventEmitter<Person> customerUpdate) {
        super(customerService, managerResourceService, affiliationService)
        this.customerUpdate = customerUpdate

        this.customerUpdate.register((Person person) -> {
            loadData(person)
            this.outdatedCustomer = (Customer) person
        })
    }

    private void loadData(Person person) {
        this.academicTitle = person.title
        this.firstName = person.firstName
        this.lastName = person.lastName
        this.email = person.emailAddress
        this.affiliation = person.affiliations.first()
    }
}
