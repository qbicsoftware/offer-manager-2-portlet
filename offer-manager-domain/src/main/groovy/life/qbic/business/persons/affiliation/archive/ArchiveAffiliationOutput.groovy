package life.qbic.business.persons.affiliation.archive

import life.qbic.business.UseCaseFailure
import life.qbic.business.persons.affiliation.Affiliation

/**
 * Output interface for the {@link life.qbic.business.persons.affiliation.archive.ArchiveAffiliation} use
 * case
 *
 * @since: 1.0.0
 */
interface ArchiveAffiliationOutput extends UseCaseFailure {
    /**
     * This method informs the output that the provided affiliation was archived
     * @param affiliation the affiliation that was archived.
     * @since 1.0.0
     */
    void affiliationArchived(Affiliation affiliation)
}
