package life.qbic.business.persons.affiliation.updateCategory

import life.qbic.business.exceptions.DatabaseQueryException
import life.qbic.business.persons.affiliation.Affiliation
import life.qbic.business.persons.affiliation.AffiliationCategory

/**
 * The output interface for the Update Affiliation Category use case.
 *
 * @since 1.3.0
 */
interface UpdateAffiliationCategoryOutput {

    /**
     * Calles after the affiliation category of an affiliation was updated
     * @param affiliation The affiliation to be updated
     * @param category The new category
     * @throws life.qbic.business.exceptions.DatabaseQueryException
     */
    void updatedAffiliationCategory(Affiliation affiliation, AffiliationCategory category) throws DatabaseQueryException
}
