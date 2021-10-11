package life.qbic.portal.offermanager.components.person.create

import com.vaadin.event.ListenerMethod.MethodException
import groovy.util.logging.Log4j2
import life.qbic.business.persons.create.CreatePersonOutput
import life.qbic.datamodel.dtos.business.Customer
import life.qbic.datamodel.dtos.business.ProjectManager
import life.qbic.datamodel.dtos.general.Person
import life.qbic.portal.offermanager.components.AppViewModel

/**
 * AppPresenter for the CreatePersonView
 *
 * This presenter handles the output of the createPerson use case and prepares it for the
 * CreatePersonView.
 *
 * @since: 1.0.0
 */
@Log4j2
class CreatePersonPresenter implements CreatePersonOutput{
    private final AppViewModel viewModel
    private final CreatePersonViewModel createPersonViewModel

    CreatePersonPresenter(AppViewModel viewModel, CreatePersonViewModel createPersonViewModel) {
        this.viewModel = viewModel
        this.createPersonViewModel = createPersonViewModel
    }

    private void clearPersonData() {
        createPersonViewModel.reset()
    }

    @Override
    void failNotification(String notification) {
        viewModel.failureNotifications.add(notification)
    }

    @Override
    void personNotFound(Person notFoundPerson, String message) {
        createPersonViewModel.refreshPersonEntries()
        failNotification(message)
    }

    @Deprecated
    void personCreated(String message) {
        try {
            viewModel.successNotifications.add(message)
            clearPersonData()
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


    void personCreated(Person person) {
        Customer customer = translateToCustomer(person)
        ProjectManager manager = translateToProjectManager(person)
        try{
            if (createPersonViewModel.outdatedPerson) {
                    Iterator<Customer>  customerIterator = createPersonViewModel.customerService.iterator()
                    Customer outdatedCustomer = translateToCustomer(createPersonViewModel.outdatedPerson)
                    customerIterator.each {
                        if (it == outdatedCustomer) {
                            createPersonViewModel.customerService.removeFromResource(it)
                        }
                    }
                    Iterator<ProjectManager>  managerIterator = createPersonViewModel.managerResourceService.iterator()
                    ProjectManager outdatedManager = translateToProjectManager(createPersonViewModel.outdatedPerson)
                    managerIterator.each{
                    if(it == outdatedManager) {
                        createPersonViewModel.managerResourceService.removeFromResource(it)
                        }
                    }
                createPersonViewModel.personResourceService.removeFromResource(createPersonViewModel.outdatedPerson)
                }
        }catch(Exception e){
            log.error e.message
            log.error e.stackTrace.join("\n")
        }
        createPersonViewModel.customerService.addToResource(customer)
        createPersonViewModel.managerResourceService.addToResource(manager)
        createPersonViewModel.personResourceService.addToResource(person)
        //reset the view model
        clearPersonData()
        viewModel.successNotifications.add("Successfully created new person entry.")
    }

    private static Customer translateToCustomer(Person person){
        Customer customer = new Customer.Builder(person.firstName,
                person.lastName,
                person.emailAddress)
                .title(person.title)
                .affiliations(person.affiliations).build()
        return customer
    }

    private static ProjectManager translateToProjectManager(Person person){
        ProjectManager manager = new ProjectManager.Builder(person.firstName,
                person.lastName,
                person.emailAddress)
                .title(person.title)
                .affiliations(person.affiliations).build()
        return manager
    }

}

