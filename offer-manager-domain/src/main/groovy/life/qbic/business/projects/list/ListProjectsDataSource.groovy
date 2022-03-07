package life.qbic.business.projects.list

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
    List<String> listProjects()
}
