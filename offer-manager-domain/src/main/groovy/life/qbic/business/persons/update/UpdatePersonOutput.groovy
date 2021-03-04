package life.qbic.business.persons.update

import life.qbic.business.UseCaseFailure
import life.qbic.datamodel.dtos.general.Person

/**
 * Output interface for the {@link UpdatePerson} use
 * case
 *
 * @since: 1.0.0
 */
interface UpdatePersonOutput extends UseCaseFailure {

    /**
     * Is called by the use case, when a customer resource has been updated
     * @param person The updated created person resource
     */
    void personUpdated(Person person)
}
