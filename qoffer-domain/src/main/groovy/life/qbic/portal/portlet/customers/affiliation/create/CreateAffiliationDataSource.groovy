package life.qbic.portal.portlet.customers.affiliation.create

import life.qbic.datamodel.dtos.business.Affiliation
import life.qbic.datamodel.dtos.business.Customer
import life.qbic.portal.portlet.exceptions.DatabaseQueryException

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
     * @param customer a person to be added to known customers
     * @throws life.qbic.portal.portlet.exceptions.DatabaseQueryException in case an affiliation could not been added to the customer database
     * @since 1.0.0
     */
    void addAffiliation(Affiliation affiliation) throws DatabaseQueryException
}
