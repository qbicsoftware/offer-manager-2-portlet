package life.qbic.business.persons.affiliation.update

import life.qbic.business.exceptions.DatabaseQueryException
import life.qbic.business.persons.affiliation.Affiliation
import life.qbic.business.persons.affiliation.AffiliationCategory

/**
 * The input interface for the Update Affiliation use case.
 *
 * @since 1.3.0
 */
interface UpdateAffiliationInput {

    /**
     * Updates an affiliation entry
     * @param affiliation The affiliation to be updated
     */
    void updateAffiliation(Affiliation affiliation)
}
