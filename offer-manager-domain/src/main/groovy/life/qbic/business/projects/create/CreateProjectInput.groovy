package life.qbic.business.projects.create

import life.qbic.datamodel.dtos.business.Offer

/**
 * Input interface for the <span>Create Project</span> use case.
 *
 * @since 1.0.0
 */
interface CreateProjectInput {

    /**
     * Creates a new project based on the offer information
     *
     * Calling this method executes the <span>Create Project</span> use case.
     * The output will be returned via the {@link CreateProjectOutput} interface.
     *
     * @param offer The offer with information about the planned project.
     * @since 1.0.0
     */
    void createProject(Offer offer)

}
