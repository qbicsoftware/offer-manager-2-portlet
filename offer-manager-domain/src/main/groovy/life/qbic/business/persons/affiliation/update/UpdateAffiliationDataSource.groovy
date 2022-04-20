package life.qbic.business.persons.affiliation.update

import life.qbic.business.exceptions.DatabaseQueryException
import life.qbic.business.persons.affiliation.Affiliation
import life.qbic.business.persons.affiliation.AffiliationNotFoundException

/**
 * The data source interface for the Update Affiliation use case.
 *
 * @since 1.3.0
 */
interface UpdateAffiliationDataSource {

    /**
     * Makes changes to an existing persistent affiliation
     * @param affiliation the affiliation to be updated
     * @throws DatabaseQueryException in case of technical errors
     * @throws AffiliationNotFoundException in case the affiliation was not found
     */
    void updateAffiliation(Affiliation affiliation) throws DatabaseQueryException, AffiliationNotFoundException
}
