package life.qbic.business.persons.affiliation.create

import life.qbic.datamodel.dtos.business.Affiliation
import life.qbic.business.UseCaseFailure

/**
 * Output interface for the {@link CreateAffiliation} use
 * case
 *
 * @since: 1.0.0
 */
interface CreateAffiliationOutput extends UseCaseFailure {
    /**
     * This method informs the output that the provided affiliation was created
     * @param affiliation the affiliation that was created.
     * @since 1.0.0
     */
    void affiliationCreated(Affiliation affiliation)
}
