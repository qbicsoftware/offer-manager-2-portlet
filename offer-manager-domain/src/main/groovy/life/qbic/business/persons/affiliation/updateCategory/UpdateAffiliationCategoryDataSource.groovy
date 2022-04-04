package life.qbic.business.persons.affiliation.updateCategory

import life.qbic.business.exceptions.DatabaseQueryException
import life.qbic.business.persons.affiliation.Affiliation
import life.qbic.business.persons.affiliation.AffiliationCategory

/**
 * The data source interface for the Update Affiliation Category use case.
 *
 * @since 1.3.0
 */
interface UpdateAffiliationCategoryDataSource {

    /**
     * Updates the affiliation category of an affiliation in the database
     * @param affiliation The affiliation to be updated
     * @param category The new category
     * @throws DatabaseQueryException
     */
    void updateAffiliationCategory(Affiliation affiliation, AffiliationCategory category) throws DatabaseQueryException
}
