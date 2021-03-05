package life.qbic.business.projects.spaces

import life.qbic.business.UseCaseFailure
import life.qbic.datamodel.dtos.projectmanagement.ProjectSpace

/**
 * Describes the output interface of the <span>Create Project Space</span> use case.
 *
 * @since 1.0.0
 */
interface CreateProjectSpaceOutput extends UseCaseFailure {

    /**
     * This method is called from the use case, after
     * successful project space creation in QBiC's data management
     * platform.
     *
     * @param projectSpace The created project space
     * @since 1.0.0
     */
    void projectSpaceCreated(ProjectSpace projectSpace)

}
