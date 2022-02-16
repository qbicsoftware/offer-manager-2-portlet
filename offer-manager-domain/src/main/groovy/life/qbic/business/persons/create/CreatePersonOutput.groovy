package life.qbic.business.persons.create

import life.qbic.business.UseCaseFailure
import life.qbic.business.persons.Person

/**
 * Output interface for the {@link CreatePerson} use
 * case
 *
 * @since: 1.0.0
 * @author: Tobias Koch
 */
interface CreatePersonOutput extends UseCaseFailure {

    /**
     * Is called by the use case, when a new customer resource has been created
     * @param person The newly created person resource
     */
    void personCreated(Person person)

    /**
     * To be called if a person entry was not found in the database
     * @param notFoundPerson The person that was searched for but not found
     * @param message The message the should be propagated to the view
     */
    void personNotFound(Person notFoundPerson, String message)
}
