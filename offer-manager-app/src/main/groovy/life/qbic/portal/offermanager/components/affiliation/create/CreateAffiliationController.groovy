package life.qbic.portal.offermanager.components.affiliation.create

import groovy.util.logging.Log4j2
import life.qbic.business.persons.affiliation.Affiliation
import life.qbic.business.persons.affiliation.AffiliationCategory
import life.qbic.business.persons.affiliation.create.CreateAffiliationInput

/**
 * Controller class adapter from view information into use case input interface
 *
 * This class translates the information that was received from the view into method calls to the use case
 *
 * @since: 1.0.0
 */
@Log4j2
class CreateAffiliationController {
    private final CreateAffiliationInput useCaseInput

    CreateAffiliationController(CreateAffiliationInput useCaseInput) {
        this.useCaseInput = useCaseInput
    }

    /**
     * Creates an affiliation DTO and calls the use case with the data transfer object
     * @param organisation Organisation of the affiliation
     * @param street part of the address of the affiliation
     * @param postalCode part of the address of the affiliation
     * @param city part of the address of the affiliation
     * @param category a string corresponding to an AffiliationCategory
     *
     * @see AffiliationCategory
     */
    void createAffiliation(String organisation, String addressAddition, String street, String postalCode, String city, String country, String category) {
        if (!addressAddition) {
            addressAddition = ""
        }
        if (!country) {
            country = "Germany"
        }
        if (!category) {
            category = "external"
        }
        Affiliation affiliation = new Affiliation(organisation, addressAddition, street, postalCode, city, country, category)
        useCaseInput.createAffiliation(affiliation)
    }
}
