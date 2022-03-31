package life.qbic.business.projects.spaces.list

import life.qbic.datamodel.dtos.projectmanagement.ProjectSpace

/**
 * Provides functionality to list project spaces
 *
 * @since 1.0.0
 */
interface ListProjectSpacesDataSource {

    /**
     * Lists all available project spaces
     * @return a list of all project spaces
     * @since 1.0.0
     */
    List<ProjectSpace> listSpaces()
}
