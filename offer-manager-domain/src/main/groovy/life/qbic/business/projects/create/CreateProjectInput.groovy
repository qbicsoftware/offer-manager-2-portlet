package life.qbic.business.projects.create

import life.qbic.datamodel.dtos.projectmanagement.ProjectApplication

/**
 * <h1>Input interface for the <span>Create Project</span> use case.</h1>
 *
 * @since 1.0.0
 */
interface CreateProjectInput {

    /**
     * <p>Creates a new project based on a {@link  life.qbic.datamodel.dtos.business.ProjectApplication}</p>
     * <br>
     * <p>Calling this method executes the <span>Create Project</span> use case.
     * The output will be returned via the {@link CreateProjectOutput} interface.
     * </p>
     * @param projectApplication The project application with information about the planned project.
     * @since 1.0.0
     */
    void createProject(ProjectApplication projectApplication)

    /**
     * <p>Creates a new project and a project space based on a {@link  life.qbic.datamodel.dtos.business.ProjectApplication}</p>
     * <br>
     * <p>Calling this method executes the <span>Create Project</span> and <span>Create Project Space</span> use case.
     * The output will be returned via the {@link CreateProjectOutput} interface.
     * </p>
     * @param projectApplication The project application with information about the planned project.
     * @since 1.0.0
     */
    void createProjectWithSpace(ProjectApplication projectApplication)

}
