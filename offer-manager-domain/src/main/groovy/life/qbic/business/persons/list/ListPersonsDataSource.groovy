package life.qbic.business.persons.list

import life.qbic.business.persons.Person

/**
 * Provides functionality to list persons
 *
 * @since 1.0.0
 */
interface ListPersonsDataSource {

    /**
     * A collection of active persons
     * @return a list of persons
     * @since 1.3.0
     */
    List<Person> listPersons()

}
