package life.qbic.business.persons.affiliation.list

import life.qbic.business.persons.affiliation.Affiliation

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
