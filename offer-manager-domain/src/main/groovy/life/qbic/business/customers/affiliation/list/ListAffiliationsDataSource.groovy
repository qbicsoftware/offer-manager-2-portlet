package life.qbic.business.customers.affiliation.list

import life.qbic.datamodel.dtos.business.Affiliation

/**
 * <short description>
 *
 * <detailed description>
 *
 * @since: <versiontag>
 */
interface ListAffiliationsDataSource {

    /**
     * Fetches all available affiliations from the database
     *
     * @return A list of available affiliations
     * @since 1.0.0
     */
    List<Affiliation> listAllAffiliations()
}
