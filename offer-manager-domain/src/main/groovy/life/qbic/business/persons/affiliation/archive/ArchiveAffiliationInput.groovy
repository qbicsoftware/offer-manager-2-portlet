package life.qbic.business.persons.affiliation.archive

import life.qbic.business.persons.affiliation.Affiliation

/**
 * The input interface for the Archive Affiliation use case.
 *
 * To start the use case, only one method needs to be called.
 *
 * @since 1.0.0
 */
interface ArchiveAffiliationInput {

    /**
     * Archives an {@link Affiliation} in the customer database
     * @param affiliation The affiliation to be archived
     */
    void archiveAffiliation(Affiliation affiliation)

}
