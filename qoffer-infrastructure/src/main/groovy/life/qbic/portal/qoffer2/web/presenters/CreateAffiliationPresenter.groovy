package life.qbic.portal.qoffer2.web.presenters

import life.qbic.portal.portlet.customers.affiliation.create.CreateAffiliationOutput
import life.qbic.portal.qoffer2.web.viewmodel.CreateAffiliationViewModel
import life.qbic.portal.qoffer2.web.viewmodel.ViewModel

/**
 * Presenter for the CreateAffiliationView
 *
 * This presenter handles the output of the CreateAffiliation use case and prepares it for the
 * CreateAffiliationView.
 *
 * @since: 1.0.0
 */
class CreateAffiliationPresenter implements CreateAffiliationOutput {
    private final ViewModel sharedViewModel
    private final CreateAffiliationViewModel createAffiliationViewModel

    CreateAffiliationPresenter(ViewModel sharedViewModel, CreateAffiliationViewModel createAffiliationViewModel) {
        this.sharedViewModel = sharedViewModel
        this.createAffiliationViewModel = createAffiliationViewModel
    }

    /**
     * clears the view model
     */
    private void clearAffiliationData() {
        this.createAffiliationViewModel.organisation = null
        this.createAffiliationViewModel.address = null
        this.createAffiliationViewModel.street = null
        this.createAffiliationViewModel.postalCode = null
        this.createAffiliationViewModel.city = null
        this.createAffiliationViewModel.country = null
        this.createAffiliationViewModel.affiliationCategory = null
    }

    @Override
    void successNotification(String notification) {
        sharedViewModel.successNotifications.add(notification)
//        TODO implement
//        List<Affiliation> reloadedAffiliation = //implement this
//        // it is important that the element is added and the list is not replaced. This triggers a status change event on the observable list
//        reloadedAffiliation.forEach{if (!sharedViewModel.affiliations.contains(it)) sharedViewModel.affiliations.add(it)}
        clearAffiliationData()
    }

    @Override
    void failNotification(String notification) {
        sharedViewModel.failureNotifications.add(notification)
    }
}
