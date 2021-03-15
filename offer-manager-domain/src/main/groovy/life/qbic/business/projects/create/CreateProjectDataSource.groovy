package life.qbic.business.projects.create


import life.qbic.business.exceptions.DatabaseQueryException
import life.qbic.datamodel.dtos.projectmanagement.Project
import life.qbic.datamodel.dtos.business.ProjectApplication
/**
 * <h1>Access to the project management datasource</h1>
 *
 * <p>This interface collects methods to interact with the project management
 * datasource in the context of the Create Project use case.</p>
 *
 * @since 1.0.0
 */
interface CreateProjectDataSource {

    /**
     * Creates a new QBiC project in the data management platform.
     *
     * @param projectApplication A project application with the information necessary for the
     * project registration
     *
     * @return Information about the created project
     *
     * @since 1.0.0
     * @throws ProjectExistsException If the application was denied. Reasons for denial are
     * currently:
     *  1. A project with the same project title already exists
     * @throws DatabaseQueryException If any technical interaction with the data source fails
     */
    Project createProject(ProjectApplication projectApplication)
            throws ProjectExistsException, DatabaseQueryException
}
