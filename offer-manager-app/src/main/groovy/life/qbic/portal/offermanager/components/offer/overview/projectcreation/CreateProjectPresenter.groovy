package life.qbic.portal.offermanager.components.offer.overview.projectcreation

import life.qbic.business.projects.create.CreateProjectOutput
import life.qbic.datamodel.dtos.business.OfferId
import life.qbic.datamodel.dtos.projectmanagement.Project
import life.qbic.datamodel.dtos.projectmanagement.ProjectIdentifier
import life.qbic.portal.offermanager.components.AppViewModel

/**
 * <h1>Presenter that deals with the Create Project use case output</h1>
 *
 * The presenter currently deals with three different use case output
 * scenarios:
 * <ol>
 *     <li>The project creation was successful</li>
 *     <li>A project with the given identifier already existed</li>
 *     <li>The project creation failed</li>
 * </ol>
 *
 * @since 1.0.0
 */
class CreateProjectPresenter implements CreateProjectOutput{

    private final CreateProjectViewModel createProjectViewModel

    private final AppViewModel appViewModel

    CreateProjectPresenter(CreateProjectViewModel createProjectViewModel, AppViewModel appViewModel) {
        this.createProjectViewModel = createProjectViewModel
        this.appViewModel = appViewModel
    }

    /**
     * {@inheritDocs}
     */
    @Override
    void failNotification(String notification) {
        this.appViewModel.failureNotifications.add("This should not have happened. Please " +
                "contact the QBiC helpdesk. \n${notification}")
    }

    /**
     * {@inheritDocs}
     */
    @Override
    void projectCreated(Project project) {
        this.createProjectViewModel.setProjectCreated(true)
        this.appViewModel.successNotifications.add("Project ${project.projectId} created.")
    }

    /**
     * {@inheritDocs}
     */
    @Override
    void projectAlreadyExists(ProjectIdentifier projectIdentifier, OfferId linkedOffer) {
        this.appViewModel.failureNotifications.add("A project with the id ${projectIdentifier} " +
                "already exists.")
    }
}
