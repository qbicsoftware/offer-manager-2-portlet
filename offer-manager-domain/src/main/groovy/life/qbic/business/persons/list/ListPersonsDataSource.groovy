package life.qbic.business.persons.list

import life.qbic.datamodel.dtos.business.Customer
import life.qbic.datamodel.dtos.business.ProjectManager
import life.qbic.datamodel.dtos.general.Person

/**
 * Provides functionality to list persons
 *
 * @since 1.0.0
 */
interface ListPersonsDataSource {


    /**
     * Lists all active persons
     * @return a list of persons found
     * @since 1.0.0
     */
    List<Person> listActivePersons()

    /**
     * Lists all customers
     * @return a list of customers found
     * @since 1.0.0
     */
    List<Customer> listAllCustomers()

    /**
     * Lists all project managers
     * @return a list of project managers found
     * @since 1.0.0
     */
    List<ProjectManager> listAllProjectManagers()

}