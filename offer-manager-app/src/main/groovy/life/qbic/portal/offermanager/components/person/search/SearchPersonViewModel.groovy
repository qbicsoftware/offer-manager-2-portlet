package life.qbic.portal.offermanager.components.person.search


import life.qbic.datamodel.dtos.general.Person
import life.qbic.portal.offermanager.dataresources.persons.PersonResourceService
import life.qbic.portal.offermanager.communication.EventEmitter

/**
 * View model of the SearchPerson use case
 *
 * This model holds all the data that is displayed in the respective view {@link SearchPersonView}
 *
 * @since: 1.0.0
 *
 */
class SearchPersonViewModel {

    ObservableList availablePersons

    private final PersonResourceService personService

    Optional<Person> selectedPerson
    EventEmitter<Person> personEvent

    SearchPersonViewModel(PersonResourceService personService,
            EventEmitter<Person> personEvent) {
        this.personService = personService
        this.personEvent = personEvent
        fetchPersonData()
        subscribeToResources()
    }

    private void fetchPersonData() {
        availablePersons.clear()
        availablePersons.addAll(personService.iterator())
    }

    private void subscribeToResources() {
        this.personService.subscribe((Person customer) -> {
            this.fetchPersonData()
        })
    }

    Person getSelectedPerson() {
        if(selectedPerson.isPresent()) {
            return selectedPerson.get()
        } else {
            throw new RuntimeException("No person is currently selected.")
        }
    }

}
