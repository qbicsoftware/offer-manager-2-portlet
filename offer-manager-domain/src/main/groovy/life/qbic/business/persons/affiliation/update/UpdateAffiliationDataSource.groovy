package life.qbic.business.persons.affiliation.update

import life.qbic.business.exceptions.DatabaseQueryException
import life.qbic.business.persons.affiliation.Affiliation
import life.qbic.business.persons.affiliation.AffiliationCategory

/**
 * The data source interface for the Update Affiliation use case.
 *
 * @since 1.3.0
 */
interface UpdateAffiliationDataSource {

    /**
     * Updates an affiliation entry in the database
     * @param oldAffiliation The affiliation to be updated
     * @param newAffiliation The new affiliation entry
     * @throws DatabaseQueryException
     */
    void updateAffiliationCategory(Affiliation oldAffiliation, Affiliation newAffiliation) throws DatabaseQueryException
}
