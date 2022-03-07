package life.qbic.business.projects.spaces.list

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
    List<String> listSpaces()
}
