package life.qbic.business.projects.spaces.create

import life.qbic.business.UseCaseFailure
import life.qbic.datamodel.dtos.projectmanagement.ProjectSpace

/**
 * <h1>Describes the output interface of the Create Project Space use case.</h1>
 *
 * @since 1.0.0
 */
interface CreateProjectSpaceOutput extends UseCaseFailure {

    /**
     * <p>This method is called from the use case, after
     * successful project space creation in QBiC's data management
     * platform.</p>
     *
     * @param projectSpace The created project space
     * @since 1.0.0
     */
    void projectSpaceCreated(ProjectSpace projectSpace)

    /**
     * <p>Called when a project space with a given identifier already exists.</p>
     * <br>
     * <p>This reflects the scenario, when a user provided a pre-defined space name
     * which already exists in the underlying data source. In this case the space cannot be
     * created.</p>
     *
     * @param projectSpace The project space that already exists
     * @since 1.0.0
     */
    void projectSpaceAlreadyExists(ProjectSpace projectSpace)

}
