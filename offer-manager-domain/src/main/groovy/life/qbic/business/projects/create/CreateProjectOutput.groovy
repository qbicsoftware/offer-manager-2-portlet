package life.qbic.business.projects.create

/**
 * Output interface for the <span>Create Project</span> use case.
 *
 * @since 1.0.0
 */
interface CreateProjectOutput {

    /**
     * Called when a project has been successfully created.
     *
     * @param project The project that has been created in QBiC's data management system.
     */
    void projectCreated(Project project)

}
