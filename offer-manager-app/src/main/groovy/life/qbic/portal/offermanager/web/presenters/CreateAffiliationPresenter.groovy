package life.qbic.portal.offermanager.web.presenters

import groovy.util.logging.Log4j2
import life.qbic.datamodel.dtos.business.Affiliation
import life.qbic.business.customers.affiliation.create.CreateAffiliationOutput

/**
 * Presenter for the CreateAffiliationView
 *
 * This presenter handles the output of the CreateAffiliation use case and prepares it for the
 * CreateAffiliationView.
 *
 * @since: 1.0.0
 */
@Log4j2
class CreateAffiliationPresenter implements CreateAffiliationOutput {
    private final life.qbic.portal.offermanager.web.viewmodel.ViewModel sharedViewModel
    private final life.qbic.portal.offermanager.web.viewmodel.CreateAffiliationViewModel createAffiliationViewModel

    CreateAffiliationPresenter(life.qbic.portal.offermanager.web.viewmodel.ViewModel sharedViewModel, life.qbic.portal.offermanager.web.viewmodel.CreateAffiliationViewModel createAffiliationViewModel) {
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

    void successNotification(String notification) {
        sharedViewModel.successNotifications.add(notification)
        clearAffiliationData()
    }

    @Override
    void failNotification(String notification) {
        sharedViewModel.failureNotifications.add(notification)
    }

    /**
     * @inheritDoc
     */
    @Override
    void affiliationCreated(Affiliation affiliation) {
        createAffiliationViewModel.affiliationService.reloadResources()
    }
}
