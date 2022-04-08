package life.qbic.portal.offermanager.components.affiliation.create

import groovy.util.logging.Log4j2
import life.qbic.business.RefactorConverter
import life.qbic.business.persons.affiliation.create.CreateAffiliationInput
import life.qbic.datamodel.dtos.business.Affiliation
import life.qbic.datamodel.dtos.business.AffiliationCategory
import life.qbic.datamodel.dtos.business.AffiliationCategoryFactory

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

    CreateAffiliationController() {
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
        if (useCaseInput == null) {
            throw new RuntimeException("CreateAffiliationController was not setup properly.")
        }
        Affiliation.Builder affiliationBuilder
        affiliationBuilder = new Affiliation.Builder(organisation, street, postalCode, city)
        if (addressAddition && addressAddition?.length() > 0) {
            affiliationBuilder.setAddressAddition(addressAddition)
        }
        affiliationBuilder.setCountry(country)
        AffiliationCategoryFactory categoryFactory = new AffiliationCategoryFactory()

        AffiliationCategory affiliationCategory
        assert category && ! category.isEmpty()
        affiliationCategory = categoryFactory.getForString(category)
        affiliationBuilder.setCategory(affiliationCategory)
        Affiliation affiliation = affiliationBuilder.build()
        useCaseInput.createAffiliation(new RefactorConverter().toAffiliation(affiliation))
    }

    void setUseCaseInput(CreateAffiliationInput useCaseInput) {
        this.useCaseInput = useCaseInput
    }
}
