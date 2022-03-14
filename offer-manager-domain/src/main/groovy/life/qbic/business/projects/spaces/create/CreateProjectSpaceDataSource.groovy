package life.qbic.business.projects.spaces.create

import life.qbic.business.exceptions.DatabaseQueryException
import life.qbic.datamodel.dtos.projectmanagement.ProjectSpace

/**
 * <h1>Access to the project management datasource</h1>
 *
 * <p>This interface collects methods to interact with the project management
 * datasource in the context of the Create Project Space use case.</p>
 *
 * @since 1.0.0
 */
interface CreateProjectSpaceDataSource {

    /**
     * Creates a new space with the given name in QBiC's data management system
     *
     * @param projectSpace The projectspace that should be created
     * @since 1.0.0
     * @throws ProjectSpaceExistsException If the project space name already exists
     * @throws DatabaseQueryException If a technical issue occurs during the data source interaction
     */
    void createProjectSpace(ProjectSpace projectSpace) throws ProjectSpaceExistsException,
            DatabaseQueryException

}
