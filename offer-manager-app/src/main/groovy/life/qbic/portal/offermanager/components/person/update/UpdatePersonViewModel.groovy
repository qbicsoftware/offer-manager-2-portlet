package life.qbic.portal.offermanager.components.person.update

import groovy.beans.Bindable
import life.qbic.datamodel.dtos.business.Affiliation
import life.qbic.datamodel.dtos.business.Customer
import life.qbic.datamodel.dtos.business.ProjectManager
import life.qbic.datamodel.dtos.general.Person
import life.qbic.portal.offermanager.communication.EventEmitter
import life.qbic.portal.offermanager.components.Resettable
import life.qbic.portal.offermanager.components.person.create.CreatePersonViewModel
import life.qbic.portal.offermanager.dataresources.ResourcesService

import java.util.function.Function

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
 * @since 1.0.0
 */
class UpdatePersonViewModel extends CreatePersonViewModel implements Resettable{

    final private EventEmitter<Person> customerUpdate
    ObservableList affiliationList

    @Bindable
    Boolean personUpdated

    @Bindable
    Boolean affiliationsValid

    UpdatePersonViewModel(ResourcesService<Customer> customerService,
                          ResourcesService<ProjectManager> managerResourceService,
                          ResourcesService<Affiliation> affiliationService,
                          EventEmitter<Person> customerUpdate,
                          ResourcesService<Person> personResourceService) {
        super(customerService, managerResourceService, affiliationService, personResourceService)
        this.customerUpdate = customerUpdate
        affiliationList = new ObservableList(new ArrayList<Affiliation>())

        this.customerUpdate.register((Person person) -> {
            if (person) {
                setOutdatedPerson(person)
                loadData(person)
            } else {
                reset()
            }
        })
    }

    private void loadData(Person person) {
        academicTitle = person.title
        firstName = person.firstName
        lastName = person.lastName
        email = person.emailAddress
        //obtain the affiliations
        affiliationList.clear()
        affiliationList.addAll(person.affiliations)
    }

    boolean personChanged() {
        Optional<Person> person = Optional.ofNullable(outdatedPerson)
        Function<Person, Boolean> changed = { Person it ->
            boolean titleChanged = it.title.toString() != academicTitle
            boolean firstNameChanged = it.firstName != firstName
            boolean lastNameChanged = it.lastName != lastName
            boolean emailChanged = it.emailAddress != email
            boolean affiliationsChanged = it.affiliations.sort() != affiliationList.sort()
            boolean result = titleChanged || firstNameChanged || lastNameChanged || emailChanged || affiliationsChanged
            return result
        }
        return person.map(changed).orElse(false)
    }

    @Override
    void reset() {
        super.reset()
        affiliationList.clear()
        setOutdatedPerson(null)
    }
}
