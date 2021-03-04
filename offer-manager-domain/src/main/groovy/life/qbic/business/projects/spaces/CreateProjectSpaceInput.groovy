package life.qbic.business.projects.spaces

/**
 * Input interface for the Create Project Space use case.
 *
 * @since 1.0.0
 */
interface CreateProjectSpaceInput {

    /**
     * Creates a new project space in QBiCs data management platform.
     *
     * A space is a logical grouping of projects that have the same context. The context is
     * defined by the project manager and is not a rule set in stone.
     *
     * @param spaceName
     */
    void createProjectSpace(String spaceName)

}
