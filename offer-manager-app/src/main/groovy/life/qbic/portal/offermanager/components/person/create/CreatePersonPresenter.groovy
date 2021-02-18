package life.qbic.portal.offermanager.components.person.create

import com.vaadin.event.ListenerMethod.MethodException
import groovy.util.logging.Log4j2
import life.qbic.business.customers.create.CreateCustomerOutput
import life.qbic.datamodel.dtos.business.Customer
import life.qbic.datamodel.dtos.business.ProjectManager
import life.qbic.datamodel.dtos.general.Person
import life.qbic.portal.offermanager.components.AppViewModel

/**
 * AppPresenter for the CreatePersonView
 *
 * This presenter handles the output of the CreateCustomer use case and prepares it for the
 * CreatePersonView.
 *
 * @since: 1.0.0
 */
@Log4j2
class CreatePersonPresenter implements CreateCustomerOutput{
    private final AppViewModel viewModel
    private final CreatePersonViewModel createCustomerViewModel

    CreatePersonPresenter(AppViewModel viewModel, CreatePersonViewModel createCustomerViewModel) {
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
    @Deprecated
    void customerCreated(String message) {
        try {
            viewModel.successNotifications.add(message)
            clearCustomerData()
        } catch (MethodException listenerMethodException) {
            //fixme
            // Invocation of method selectionChange failed for `null`
            // See https://github.com/qbicsoftware/qoffer-2-portlet/issues/208
            log.error("Issue #208 $listenerMethodException.message")
        } catch (Exception e) {
            // do not propagate exceptions to the use case
            log.error(e)
        }
    }

    @Override
    void customerCreated(Person person) {
        Customer customer = new Customer.Builder(person.firstName,
                person.lastName,
                person.emailAddress)
                .title(person.title)
                .affiliations(person.affiliations).build()
        ProjectManager manager = new ProjectManager.Builder(person.firstName,
                person.lastName,
                person.emailAddress)
                .title(person.title)
                .affiliations(person.affiliations).build()

        //todo exception occurs here when adding to resource!
        createCustomerViewModel.customerService.addToResource(customer)

        if (createCustomerViewModel.outdatedCustomer) createCustomerViewModel.customerService.removeFromResource(createCustomerViewModel.outdatedCustomer)

        createCustomerViewModel.managerResourceService.addToResource(manager)

        //reset the view model
        clearCustomerData()
    }
}
