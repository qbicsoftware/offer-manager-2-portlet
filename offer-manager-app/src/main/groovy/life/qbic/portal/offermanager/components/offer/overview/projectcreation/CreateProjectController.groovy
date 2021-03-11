package life.qbic.portal.offermanager.components.offer.overview.projectcreation

import life.qbic.business.projects.create.CreateProjectInput
import life.qbic.datamodel.dtos.business.Offer
import life.qbic.datamodel.dtos.projectmanagement.ProjectCode
import life.qbic.datamodel.dtos.projectmanagement.ProjectIdentifier
import life.qbic.datamodel.dtos.projectmanagement.ProjectSpace

/**
 * <h1>Starts the use case 'Create Project'</h1>
 *
 * <p>Connects the create project component with the use case {@link CreateProject}.</p>
 *
 * @since 1.0.0
 */
class CreateProjectController {

    private final CreateProjectInput createProjectInput

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

    }

}
