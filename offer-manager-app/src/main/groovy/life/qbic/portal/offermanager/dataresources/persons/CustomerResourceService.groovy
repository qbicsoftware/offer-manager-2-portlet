package life.qbic.portal.offermanager.dataresources.persons

import life.qbic.datamodel.dtos.business.Customer
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
class CustomerResourceService implements ResourcesService<Customer>{

    private final PersonDbConnector dbConnector

    private final List<Customer> customerList

    private final EventEmitter<Customer> eventEmitter

    CustomerResourceService(PersonDbConnector dbConnector) {
        this.dbConnector = dbConnector
        this.customerList = dbConnector.fetchAllActivePersons()
        this.eventEmitter = new EventEmitter<>()
    }

    @Override
    void reloadResources() {

    }

    @Override
    void subscribe(Subscription<Customer> subscription) {
        eventEmitter.register(subscription)
    }

    @Override
    void unsubscribe(Subscription<Customer> subscription) {
        eventEmitter.unregister(subscription)
    }

    @Override
    void addToResource(Customer resourceItem) {
        customerList.add(resourceItem)
        eventEmitter.emit(resourceItem)
    }

    @Override
    void removeFromResource(Customer resourceItem) {
        customerList.remove(resourceItem)
        eventEmitter.emit(resourceItem)
    }

    @Override
    Iterator<Customer> iterator() {
        return new ArrayList(customerList).iterator()
    }
}
