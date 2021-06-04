package life.qbic.portal.offermanager.components.offer.overview.projectcreation

import life.qbic.business.projects.create.CreateProjectInput
import life.qbic.datamodel.dtos.business.Offer
import life.qbic.datamodel.dtos.business.ProjectApplication
import life.qbic.datamodel.dtos.projectmanagement.ProjectCode
import life.qbic.datamodel.dtos.projectmanagement.ProjectIdentifier
import life.qbic.datamodel.dtos.projectmanagement.ProjectSpace

/**
 * <h1>Starts the use case 'Create Project'</h1>
 *
 * <p>Connects the create project component with the use case <code>Create Projects</code>.</p>
 *
 * @since 1.0.0
 */
class CreateProjectController {

    private final CreateProjectInput createProjectInput

    /**
     * Constructor with an create project use case input instance.
     * @param createProjectInput The create project use case input.
     */
    CreateProjectController(CreateProjectInput createProjectInput) {
        this.createProjectInput = createProjectInput
    }

    /**
     * Requests a project registration
     * @param offer The offer associated with the project
     * @param projectIdentifier The desired project identifier (space + code)
     */
    void createProject(Offer offer,
                       ProjectIdentifier projectIdentifier) {
        ProjectApplication application = createApplication(offer, projectIdentifier.projectSpace,
                projectIdentifier.projectCode)
        createProjectInput.createProject(application)
    }

    /**
     * Requests a project registration with a new project space creation
     * @param offer The offer associated with the project
     * @param projectSpace The desired project space
     * @param projectCode The desired project code
     */
    void createProjectAndSpace(Offer offer,
                               ProjectSpace projectSpace,
                               ProjectCode projectCode) {
        ProjectApplication application = createApplication(offer, projectSpace, projectCode)
        createProjectInput.createProjectWithSpace(application)
    }

    private static ProjectApplication createApplication(Offer offer,
                                                 ProjectSpace projectSpace,
                                                 ProjectCode projectCode) {
        def experimentalDesign = offer.experimentalDesign.orElse("No experimental design described.")
        return new ProjectApplication(
                offer.identifier,
                offer.projectTitle,
                offer.projectObjective,
                experimentalDesign,
                offer.projectManager,
                projectSpace,
                offer.customer,
                projectCode
        )
    }
}
