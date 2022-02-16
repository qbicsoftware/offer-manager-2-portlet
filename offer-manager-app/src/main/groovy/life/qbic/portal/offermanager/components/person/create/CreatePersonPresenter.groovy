package life.qbic.portal.offermanager.components.person.create


import groovy.util.logging.Log4j2
import life.qbic.business.persons.Person
import life.qbic.business.persons.create.CreatePersonOutput
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
class CreatePersonPresenter implements CreatePersonOutput {
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
    void personCreated(Person person) {
        try {
            if (createPersonViewModel.outdatedPerson) {
                createPersonViewModel.personResourceService.removeFromResource(createPersonViewModel.outdatedPerson)
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e)
        }
        createPersonViewModel.personResourceService.addToResource(person)
        //reset the view model
        clearPersonData()
        viewModel.successNotifications.add("Successfully created new person entry.")
    }

    @Override
    void personNotFound(Person notFoundPerson, String message) {
        createPersonViewModel.refreshPersonEntries()
        failNotification(message)
    }
}
