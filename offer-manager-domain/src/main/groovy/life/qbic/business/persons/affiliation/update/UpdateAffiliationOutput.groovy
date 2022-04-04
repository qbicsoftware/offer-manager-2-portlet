package life.qbic.business.persons.affiliation.update

import life.qbic.business.exceptions.DatabaseQueryException
import life.qbic.business.persons.affiliation.Affiliation
import life.qbic.business.persons.affiliation.AffiliationCategory

/**
 * The output interface for the Update Affiliation use case.
 *
 * @since 1.3.0
 */
interface UpdateAffiliationOutput {

    /**
     * Updates an affiliation entry
     * @param affiliation The affiliation to be updated
     */
    void updatedAffiliation(Affiliation affiliation)
}
