package life.qbic.portal.portlet.customers.affiliation.create

import life.qbic.datamodel.dtos.business.Affiliation
import life.qbic.portal.portlet.exceptions.DatabaseQueryException

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

    /**
     * Creates a use case interactor for creating an affiliation in the provided customer database
     * @param dataSource the gateway to the database
     * @param output an output to publish the results to
     */
    CreateAffiliation(CreateAffiliationOutput output, CreateAffiliationDataSource dataSource) {
        this.dataSource = dataSource
        this.output = output
    }

    /** {@InheritDoc} */
    @Override
    void createAffiliation(Affiliation affiliation) {
        try {
            dataSource.addAffiliation(affiliation)
            output.successNotification("Successfully added new affiliation " + affiliation.organisation)
            output.affiliationCreated(affiliation)
        } catch (DatabaseQueryException queryException) {
            throw new DatabaseQueryException(queryException.message)
        } catch (Exception ignored) {
            output.failNotification("Could not create new affiliation.")
        }
    }
}
