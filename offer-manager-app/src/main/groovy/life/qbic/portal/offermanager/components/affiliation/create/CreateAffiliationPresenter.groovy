package life.qbic.portal.offermanager.components.affiliation.create

import groovy.util.logging.Log4j2
import life.qbic.business.RefactorConverter
import life.qbic.business.persons.affiliation.create.CreateAffiliationOutput
import life.qbic.datamodel.dtos.business.Affiliation
import life.qbic.portal.offermanager.components.AppViewModel

/**
 * AppPresenter for the CreateAffiliationView
 *
 * This presenter handles the output of the CreateAffiliation use case and prepares it for the
 * CreateAffiliationView.
 *
 * @since: 1.0.0
 */
@Log4j2
class CreateAffiliationPresenter implements CreateAffiliationOutput {
    private final AppViewModel sharedViewModel
    private final CreateAffiliationViewModel createAffiliationViewModel

    CreateAffiliationPresenter(AppViewModel sharedViewModel, CreateAffiliationViewModel createAffiliationViewModel) {
        this.sharedViewModel = sharedViewModel
        this.createAffiliationViewModel = createAffiliationViewModel
    }

    /**
     * clears the view model
     */
    private void clearAffiliationData() {
        this.createAffiliationViewModel.organisation = null
        this.createAffiliationViewModel.addressAddition = null
        this.createAffiliationViewModel.street = null
        this.createAffiliationViewModel.postalCode = null
        this.createAffiliationViewModel.city = null
        this.createAffiliationViewModel.country = null
        this.createAffiliationViewModel.affiliationCategory = null

        this.createAffiliationViewModel.organisationValid = null
        this.createAffiliationViewModel.addressAdditionValid = null
        this.createAffiliationViewModel.streetValid = null
        this.createAffiliationViewModel.postalCodeValid = null
        this.createAffiliationViewModel.cityValid = null
        this.createAffiliationViewModel.countryValid = null
        this.createAffiliationViewModel.affiliationCategoryValid = null
    }

    @Override
    void failNotification(String notification) {
        sharedViewModel.failureNotifications.add(notification)
    }


    @Override
    void affiliationCreated(life.qbic.business.persons.affiliation.Affiliation affiliation) {
        Affiliation affiliationDto = new RefactorConverter().toAffiliationDto(affiliation)

        createAffiliationViewModel.affiliationService.addToResource(affiliationDto)
        sharedViewModel.successNotifications.add("Successfully added new affiliation " + affiliationDto.organisation)
        clearAffiliationData()
    }
}
