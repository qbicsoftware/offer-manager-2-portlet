package life.qbic.portal.offermanager.components.person.update

import life.qbic.datamodel.dtos.business.Customer
import life.qbic.datamodel.dtos.general.Person

import life.qbic.portal.offermanager.dataresources.persons.PersonResourceService
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
 * instance of {@link life.qbic.portal.offermanager.communication.EventEmitter}'s emitter property.
 *
 * Everytime such an event is emitted, it loads the event data into its properties.
 *
 * @since: 1.0.0
 *
 */
class UpdatePersonViewModel extends CreatePersonViewModel{

    final private EventEmitter<Person> customerUpdate

    UpdatePersonViewModel(CustomerResourceService customerService,
            ProjectManagerResourceService managerResourceService,
            AffiliationResourcesService affiliationService,
            EventEmitter<Person> customerUpdate,
            PersonResourceService personResourceService) {
        super(customerService, managerResourceService, affiliationService, personResourceService)
        this.customerUpdate = customerUpdate

        this.customerUpdate.register((Person person) -> {
            loadData(person)
            setOutdatedPerson(person)
        })
    }

    private void loadData(Person person) {
        super.academicTitle = person.title
        super.firstName = person.firstName
        super.lastName = person.lastName
        super.email = person.emailAddress
        this.affiliation = person.affiliations.first()
    }
}