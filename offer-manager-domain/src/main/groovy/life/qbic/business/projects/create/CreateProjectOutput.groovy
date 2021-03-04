package life.qbic.business.projects.create

import life.qbic.business.UseCaseFailure

/**
 * Output interface for the <span>Create Project</span> use case.
 *
 * @since 1.0.0
 */
interface CreateProjectOutput extends UseCaseFailure {

    /**
     * Called when a project has been successfully created.
     *
     * @param project The project that has been created in QBiC's data management system.
     */
    void projectCreated(Project project)

}
