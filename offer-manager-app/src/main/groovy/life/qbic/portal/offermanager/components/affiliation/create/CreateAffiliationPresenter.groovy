package life.qbic.portal.offermanager.components.affiliation.create

import groovy.util.logging.Log4j2
import life.qbic.business.RefactorConverter
import life.qbic.business.persons.affiliation.create.CreateAffiliationOutput
import life.qbic.datamodel.dtos.business.Affiliation
import life.qbic.portal.offermanager.components.AppViewModel
import life.qbic.portal.offermanager.dataresources.ResourcesService

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
    private final CreateAffiliationView createAffiliationView
    private final ResourcesService<Affiliation> affiliationResourcesService

    CreateAffiliationPresenter(AppViewModel sharedViewModel, CreateAffiliationView createAffiliationView, ResourcesService<Affiliation> affiliationResourcesService) {
        this.sharedViewModel = sharedViewModel
        this.createAffiliationView = createAffiliationView
        this.affiliationResourcesService = affiliationResourcesService
    }

    @Override
    void failNotification(String notification) {
        sharedViewModel.failureNotifications.add(notification)
    }


    @Override
    void affiliationCreated(life.qbic.business.persons.affiliation.Affiliation affiliation) {
        Affiliation affiliationDto = new RefactorConverter().toAffiliationDto(affiliation)

        affiliationResourcesService.addToResource(affiliationDto)
        sharedViewModel.successNotifications.add("Successfully added new affiliation " + affiliationDto.organisation)
        createAffiliationView.reset()
    }
}
