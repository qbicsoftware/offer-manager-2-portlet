package life.qbic.business.persons.affiliation.archive

import life.qbic.business.exceptions.DatabaseQueryException
import life.qbic.business.logging.Logger
import life.qbic.business.logging.Logging
import life.qbic.business.persons.affiliation.Affiliation
import life.qbic.business.persons.affiliation.AffiliationExistsException
import life.qbic.business.persons.affiliation.update.UpdateAffiliationDataSource
import life.qbic.business.persons.update.UpdatePersonDataSource

/**
 * This class implements the Archive Affiliations use case.
 *
 * This use case will archive an affiliation from the connected data source
 * and submit the result back to the output interface.
 *
 * @since 1.0.0
 */
class ArchiveAffiliation implements ArchiveAffiliationInput {

    private final UpdateAffiliationDataSource dataSource
    private final ArchiveAffiliationOutput output

    private final Logging log = Logger.getLogger(this.class)

    private final UpdatePersonDataSource updatePersonDataSource

    /**
     * Creates a use case interactor for archiving an affiliation in the provided customer database
     * @param dataSource the gateway to the database
     * @param output an output to publish the results to
     */
    ArchiveAffiliation(ArchiveAffiliationOutput output, UpdateAffiliationDataSource dataSource,
                       UpdatePersonDataSource updatePersonDataSource) {
        this.dataSource = dataSource
        this.updatePersonDataSource = updatePersonDataSource
        this.output = output
    }

    /** {@inheritdoc} */
    @Override
    void archiveAffiliation(Affiliation affiliation) {
        affiliation.archive()
        try {
            dataSource.updateAffiliation(affiliation)
            // Since the affiliation was marked as archived, we need to remove
            // all existing relations from persons with this affiliation
            updatePersonDataSource.removeAffiliationFromAllPersons(affiliation.getId())
            output.affiliationArchived(affiliation)

            log.info("Successfully archived affiliation " + affiliation.getOrganization())
        } catch (DatabaseQueryException queryException) {
            output.failNotification("Could not archive affiliation [$affiliation].\n" + queryException.message)
        } catch (AffiliationExistsException existsException) {
            output.failNotification("Could not archive affiliation [$affiliation].\n" + existsException.message)
        } catch (Exception e) {
            log.error(e.message, e)
            output.failNotification("Could not archive affiliation $affiliation")
        }
    }
}
