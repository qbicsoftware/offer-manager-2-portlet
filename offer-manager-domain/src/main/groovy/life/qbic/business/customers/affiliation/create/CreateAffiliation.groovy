package life.qbic.business.customers.affiliation.create

import life.qbic.datamodel.dtos.business.Affiliation
import life.qbic.business.exceptions.DatabaseQueryException
import life.qbic.business.logging.Logger
import life.qbic.business.logging.Logging

/**
 * This class implements the Create Affiliations use case.
 *
 * This use case will add an affiliation to the connected data source
 * and submit the result back to the output interface.
 *
 * @since 1.0.0
 */
class CreateAffiliation implements CreateAffiliationInput{

    private final CreateAffiliationDataSource dataSource
    private final CreateAffiliationOutput output
    private final Logging log = Logger.getLogger(this.class)

    /**
     * Creates a use case interactor for creating an affiliation in the provided customer database
     * @param dataSource the gateway to the database
     * @param output an output to publish the results to
     */
    CreateAffiliation(CreateAffiliationOutput output, CreateAffiliationDataSource dataSource) {
        this.dataSource = dataSource
        this.output = output
    }

    /** {@inheritdoc} */
    @Override
    void createAffiliation(Affiliation affiliation) {
        try {
            dataSource.addAffiliation(affiliation)
            output.affiliationCreated(affiliation)

            log.info("Successfully added new affiliation " + affiliation.organisation)
        } catch (DatabaseQueryException queryException) {
            output.failNotification("Could not create affiliation [$affiliation].\n" + queryException.message)
        } catch (Exception e) {
            log.error(e.message)
            log.error(e.stackTrace.join("\n"))
            output.failNotification("Could not create new affiliation $affiliation")
        }
    }
}
