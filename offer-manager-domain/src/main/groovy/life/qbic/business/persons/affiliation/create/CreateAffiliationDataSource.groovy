package life.qbic.business.persons.affiliation.create

import life.qbic.datamodel.dtos.business.Affiliation
import life.qbic.business.exceptions.DatabaseQueryException

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
     * @since 1.0.0
     */
    void addAffiliation(Affiliation affiliation) throws DatabaseQueryException
}
