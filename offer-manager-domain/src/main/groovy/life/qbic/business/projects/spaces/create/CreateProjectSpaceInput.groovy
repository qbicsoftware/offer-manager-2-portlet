package life.qbic.business.projects.spaces.create

import life.qbic.datamodel.dtos.projectmanagement.ProjectSpace

/**
 * <h1>Input interface for the Create Project Space use case.</h1>
 *
 * @since 1.0.0
 */
interface CreateProjectSpaceInput {

    /**
     * <p>Creates a new project space in QBiCs data management platform.</p>
     * <br>
     * <p>A space is a logical grouping of projects that have the same context. The context is
     * defined by the project manager and is not a rule set in stone.</p>
     *
     * @param The desired project space to be created
     * @since 1.0.0
     */
    void createProjectSpace(ProjectSpace projectSpace)

}
