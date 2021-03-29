package life.qbic.portal.offermanager.components.person.update


import groovy.util.logging.Log4j2
import life.qbic.datamodel.dtos.business.Affiliation
import life.qbic.portal.offermanager.components.AppViewModel
import life.qbic.portal.offermanager.components.person.create.CreatePersonController
import life.qbic.portal.offermanager.components.person.create.CreatePersonView

/**
 * <h1>This view is an extension of the {@link CreatePersonView} and adjusts the view components to reflect the update person use case</h1>
 * <br>
 * <p>Since both views should look the same changes of the {@link CreatePersonView} should also be reflected in the {@link UpdatePersonView}</p>
 *
 * @since 1.0.0
 *
*/
@Log4j2
class UpdatePersonView extends CreatePersonView{
    private final UpdatePersonViewModel updatePersonViewModel
    private final AppViewModel sharedViewModel


    UpdatePersonView(CreatePersonController controller, AppViewModel sharedViewModel, UpdatePersonViewModel updatePersonViewModel) {
        super(controller, sharedViewModel, updatePersonViewModel)
        this.updatePersonViewModel = updatePersonViewModel
        this.sharedViewModel = sharedViewModel
        adjustViewElements()
        registerListener()
    }

    private void adjustViewElements() {
        submitButton.caption = "Update Person"
        abortButton.caption = "Abort Person Update"
    }

    private void registerListener(){
        submitButtonClickListenerRegistration.remove()
        submitButton.addClickListener({
            try {
                // we assume that the view model and the view always contain the same information
                String title = updatePersonViewModel.academicTitle
                String firstName = updatePersonViewModel.firstName
                String lastName = updatePersonViewModel.lastName
                String email = updatePersonViewModel.email
                List<Affiliation> affiliations = new ArrayList()
                affiliations.add(updatePersonViewModel.affiliation)

                if(updatePersonViewModel.outdatedPerson){
                    affiliations.addAll(updatePersonViewModel.outdatedPerson.affiliations)
                    controller.updatePerson(updatePersonViewModel.outdatedPerson, firstName, lastName, title, email, affiliations)
                }

            } catch (IllegalArgumentException illegalArgumentException) {
                log.error("Illegal arguments for person update. ${illegalArgumentException.getMessage()}")
                log.debug("Illegal arguments for person update. ${illegalArgumentException.getMessage()}", illegalArgumentException)
                sharedViewModel.failureNotifications.add("Could not update the person. Please verify that your input is correct and try again.")
            } catch (Exception e) {
                log.error("Unexpected error after person update form submission.", e)
                sharedViewModel.failureNotifications.add("An unexpected error occurred. We apologize for any inconveniences. Please inform us via email to support@qbic.zendesk.com.")
            }
        })
    }
}
