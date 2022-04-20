package life.qbic.business.persons.affiliation.update

import life.qbic.business.persons.affiliation.Affiliation

/**
 * The input interface for the Update Affiliation use case.
 *
 * @since 1.3.0
 */
interface UpdateAffiliationInput {

    /**
     * Updates an affiliation entry
     * @param affiliation the requested affiliation state to be made persistent
     */
    void updateAffiliation(Affiliation affiliation)
}
