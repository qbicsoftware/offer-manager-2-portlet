package life.qbic.portal.offermanager.components.affiliation.create

import groovy.util.logging.Log4j2
import life.qbic.business.persons.affiliation.Affiliation
import life.qbic.business.persons.affiliation.create.CreateAffiliationInput
import life.qbic.datamodel.dtos.business.AffiliationCategory

/**
 * Controller class adapter from view information into use case input interface
 *
 * This class translates the information that was received from the view into method calls to the use case
 *
 * @since: 1.0.0
 */
@Log4j2
class CreateAffiliationController {
    private CreateAffiliationInput useCaseInput

    CreateAffiliationController() {}

    /**
     * Calls the use case with the provided affiliation
     *
     * @param affiliation the affiliation that shall be created.
     * @see AffiliationCategory
     */
    void createAffiliation(Affiliation affiliation) {
        this.useCaseInput.createAffiliation(affiliation)
    }

    void setUseCaseInput(CreateAffiliationInput useCaseInput) {
        this.useCaseInput = useCaseInput
    }
}
