package life.qbic.portal.offermanager.components.person.update

import life.qbic.datamodel.dtos.business.Customer
import life.qbic.datamodel.dtos.general.Person
import life.qbic.portal.offermanager.communication.Subscription
import life.qbic.portal.offermanager.components.person.create.CreatePersonViewModel
import life.qbic.portal.offermanager.dataresources.persons.AffiliationResourcesService
import life.qbic.portal.offermanager.dataresources.persons.CustomerResourceService
import life.qbic.portal.offermanager.dataresources.persons.PersonUpdateService
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

    final private PersonUpdateService customerUpdateService

    UpdatePersonViewModel(CustomerResourceService customerService, ProjectManagerResourceService managerResourceService, AffiliationResourcesService affiliationService
                          , PersonUpdateService customerUpdateService) {
        super(customerService, managerResourceService, affiliationService)
        this.customerUpdateService = customerUpdateService

        Subscription<Person> customerSubscription = new Subscription<Person>() {
            @Override
            void receive(Person person) {
                loadData(person)
                setOutdatedCustomer((Customer) person)
                println "I am here?!"
            }
        }

        this.customerUpdateService.subscribe(customerSubscription)
    }

    private void loadData(Person person) {
        super.academicTitle = person.title
        super.firstName = person.firstName
        super.lastName = person.lastName
        super.email = person.emailAddress
        this.affiliation = person.affiliations.first()
    }
}
