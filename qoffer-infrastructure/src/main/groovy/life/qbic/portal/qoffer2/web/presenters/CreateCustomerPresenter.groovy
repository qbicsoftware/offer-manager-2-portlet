package life.qbic.portal.qoffer2.web.presenters

import groovy.util.logging.Log4j2
import life.qbic.portal.portlet.customers.create.CreateCustomerOutput
import life.qbic.portal.qoffer2.web.viewmodel.ViewModel
import life.qbic.portal.qoffer2.web.viewmodel.CreateCustomerViewModel

/**
 * Presenter for the CreateCustomerView
 *
 * This presenter handles the output of the CreateCustomer use case and prepares it for the
 * CreateCustomerView.
 *
 * @since: 1.0.0
 */
@Log4j2
class CreateCustomerPresenter implements CreateCustomerOutput{
    private final ViewModel viewModel
    private final CreateCustomerViewModel createCustomerViewModel

    CreateCustomerPresenter(ViewModel viewModel, CreateCustomerViewModel createCustomerViewModel) {
        this.viewModel = viewModel
        this.createCustomerViewModel = createCustomerViewModel
    }

    private void clearCustomerData() {
        createCustomerViewModel.academicTitle = null
        createCustomerViewModel.firstName = null
        createCustomerViewModel.lastName = null
        createCustomerViewModel.email = null
        createCustomerViewModel.affiliation = null

        createCustomerViewModel.academicTitleValid = null
        createCustomerViewModel.firstNameValid = null
        createCustomerViewModel.lastNameValid = null
        createCustomerViewModel.emailValid = null
        createCustomerViewModel.affiliationValid = null
    }

    @Override
    void failNotification(String notification) {
        viewModel.failureNotifications.add(notification)
    }

    @Override
    void customerCreated(String message) {
        try {
            viewModel.successNotifications.add(message)
            clearCustomerData()
        } catch (Exception e) {
            log.error(e)
        }
    }
}
