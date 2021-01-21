package life.qbic.portal.qoffer2.customers

import life.qbic.datamodel.dtos.business.Customer
import life.qbic.datamodel.dtos.business.ProjectManager
import life.qbic.portal.qoffer2.events.EventEmitter
import life.qbic.portal.qoffer2.services.ResourcesService

/**
 * Customer service that holds resources about customer and project manager
 *
 * This service holds resources about customer and project manager information
 * and exposes an event emitter, that can be used to subscribe to any update
 * event of the underlying resource data.
 *
 * @since 1.0.0
 */
class PersonResourcesService implements ResourcesService {

    private final CustomerDbConnector dbConnector

    private final List<Customer> customers

    private final List<ProjectManager> projectManagers

    final EventEmitter<List<Customer>> customerEvent

    final EventEmitter<List<ProjectManager>> projectManagerEvent

    PersonResourcesService(CustomerDbConnector dbConnector) {
        this.dbConnector = dbConnector
        this.customers = dbConnector.fetchAllCustomers()
        this.projectManagers = dbConnector.fetchAllProjectManagers()
        this.customerEvent = new EventEmitter<>()
        this.projectManagerEvent = new EventEmitter<>()
        this.customerEvent.emit(customers.asList())
        this.projectManagerEvent.emit(projectManagers.asList())
    }

    @Override
    void reloadResources() {
        this.customers.clear()
        this.customers.addAll(dbConnector.fetchAllCustomers())
        this.customerEvent.emit(customers.asList())

        this.projectManagers.clear()
        this.projectManagers.addAll(dbConnector.fetchAllProjectManagers())
        this.projectManagerEvent.emit(projectManagers.asList())
    }

    List<Customer> getCustomers() {
        def customerList = []
        customerList.addAll(customers.toList())
        return customerList
    }

    List<ProjectManager> getProjectManagers() {
        def projectManagerList = []
        projectManagerList.addAll(projectManagers.toList())
        return projectManagerList
    }
}
