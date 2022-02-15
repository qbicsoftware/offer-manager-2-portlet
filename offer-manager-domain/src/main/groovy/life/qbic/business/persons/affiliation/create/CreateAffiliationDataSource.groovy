package life.qbic.business.persons.affiliation.create

import life.qbic.business.exceptions.DatabaseQueryException
import life.qbic.business.persons.Affiliation
import life.qbic.business.persons.affiliation.AffiliationExistsException

/**
 * <short description>
 *
 * <detailed description>
 *
 * @since: 1.0.0
 */
interface CreateAffiliationDataSource {
    /**
     * Adds an affiliation to the user database
     *
     * @param customer a person to be added to known persons
     * @throws DatabaseQueryException in case an affiliation could not been added to the customer database
     * @throws AffiliationExistsException in case the affiliation already exists
     * @since 1.0.0
     */
    void addAffiliation(Affiliation affiliation) throws DatabaseQueryException, AffiliationExistsException
}
