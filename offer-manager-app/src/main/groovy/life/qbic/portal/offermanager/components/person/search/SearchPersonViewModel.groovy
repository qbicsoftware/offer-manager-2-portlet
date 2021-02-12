package life.qbic.portal.offermanager.components.person.search

import life.qbic.datamodel.dtos.business.Customer
import life.qbic.datamodel.dtos.business.Offer
import life.qbic.datamodel.dtos.general.Person
import life.qbic.portal.offermanager.dataresources.persons.CustomerResourceService

/**
 * View model of the SearchPerson use case
 *
 * This model holds all the data that is displayed in the respective view {@link SearchPersonView}
 *
 * @since: 1.0.0
 *
 */
class SearchPersonViewModel {

    List<Customer> foundCustomers = new ObservableList(new ArrayList<Customer>())

    private final CustomerResourceService customerService

    Optional<Person> selectedPerson

    SearchPersonViewModel(CustomerResourceService customerService) {
        this.customerService = customerService
        fetchPersonData()
        subscribeToResources()
    }

    private void fetchPersonData() {
        this.foundCustomers.clear()
        this.foundCustomers.addAll(customerService.iterator())
    }

    private void subscribeToResources() {
        this.customerService.subscribe((Customer customer) -> {
            this.foundCustomers.add(customer)
        })
    }

    Person getSelectedPerson() {
        if(selectedPerson.isPresent()) {
            return selectedPerson.get()
        } else {
            throw new RuntimeException("No offer is currently selected.")
        }
    }

}
