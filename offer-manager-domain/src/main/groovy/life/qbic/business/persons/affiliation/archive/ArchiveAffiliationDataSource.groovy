package life.qbic.business.persons.affiliation.archive

import life.qbic.business.exceptions.DatabaseQueryException
import life.qbic.business.persons.affiliation.Affiliation
import life.qbic.business.persons.affiliation.AffiliationExistsException

/**
 * <short description>
 *
 * <detailed description>
 *
 * @since: 1.0.0
 */
interface ArchiveAffiliationDataSource {
    /**
     * Archives an affiliation in the database
     *
     * @param affiliation to archive affiliation
     * @throws DatabaseQueryException in case an affiliation could not been added to the customer database
     * @throws AffiliationExistsException in case the affiliation already exists
     * @since 1.0.0
     */
    void archive(Affiliation affiliation) throws DatabaseQueryException, AffiliationExistsException
}
