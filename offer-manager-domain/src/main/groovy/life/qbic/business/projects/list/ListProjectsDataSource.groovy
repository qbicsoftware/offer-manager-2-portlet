package life.qbic.business.projects.list

import life.qbic.datamodel.dtos.projectmanagement.ProjectIdentifier

/**
 * Provides functionality to list projects
 *
 * @since 1.0.0
 */
interface ListProjectsDataSource {

    /**
     * Lists all available projects
     * @return a list of all projects
     * @since 1.0.0
     */
    List<ProjectIdentifier> listProjects()
}