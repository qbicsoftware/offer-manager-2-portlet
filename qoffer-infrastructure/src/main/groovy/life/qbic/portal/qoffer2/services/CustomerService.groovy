package life.qbic.portal.qoffer2.services

import life.qbic.datamodel.dtos.business.Customer
import life.qbic.portal.qoffer2.customers.CustomerDatabaseQueries
import life.qbic.portal.qoffer2.customers.CustomerDbConnector
import life.qbic.portal.qoffer2.events.EventEmitter

/**
 * Customer service that holds resources about customer information
 * and can exposes an event emitter, that can be used to subscribe
 * to any update event of the underlying resource data.
 *
 * @since 1.0.0
 */
class CustomerService implements Service {

    final private CustomerDbConnector dbConnector

    final private List<Customer> persons

    final EventEmitter<List<Customer>> eventEmitter

    CustomerService(CustomerDbConnector dbConnector) {
        this.dbConnector = dbConnector
        this.persons = dbConnector.fetchAllCustomers()
        this.eventEmitter = new EventEmitter<>()
        this.eventEmitter.emit(persons.asList())
    }

    @Override
    void reloadResources() {
        this.persons.clear()
        this.persons.addAll(dbConnector.fetchAllCustomers())
        this.eventEmitter.emit(persons.asList())
    }
}
