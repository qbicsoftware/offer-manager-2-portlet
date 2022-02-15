package life.qbic.portal.offermanager.dataresources.persons

import life.qbic.business.persons.Person
import life.qbic.business.persons.list.ListPersonsDataSource
import life.qbic.portal.offermanager.communication.EventEmitter
import life.qbic.portal.offermanager.communication.Subscription
import life.qbic.portal.offermanager.dataresources.ResourcesService

/**
 * Customer service that holds resources about available customers
 *
 * This service holds resources about customer information and can be used to subscribe to any
 * update event of the underlying resource data.
 *
 * @since 1.0.0
 */
class CustomerResourceService implements ResourcesService<Person> {

    private final ListPersonsDataSource listPersonsDataSource

    private final List<Person> customerList

    private final EventEmitter<Person> eventEmitter

    CustomerResourceService(ListPersonsDataSource listPersonsDataSource) {
        this.listPersonsDataSource = listPersonsDataSource
        this.customerList = listPersonsDataSource.listPersons()
        this.eventEmitter = new EventEmitter<>()
    }

    @Override
    void reloadResources() {
        customerList.clear()

        List<Person> updatedEntries = listPersonsDataSource.listPersons()
        updatedEntries.each {
            addToResource(it)
        }
    }

    @Override
    void subscribe(Subscription<Person> subscription) {
        eventEmitter.register(subscription)
    }

    @Override
    void unsubscribe(Subscription<Person> subscription) {
        eventEmitter.unregister(subscription)
    }

    @Override
    void addToResource(Person resourceItem) {
        customerList.add(resourceItem)
        eventEmitter.emit(resourceItem)
    }

    @Override
    void removeFromResource(Person resourceItem) {
        customerList.remove(resourceItem)
        eventEmitter.emit(resourceItem)
    }

    @Override
    Iterator<Person> iterator() {
        return new ArrayList(customerList).iterator()
    }
}
