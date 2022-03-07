package life.qbic.business.projects.create

import life.qbic.business.UseCaseFailure
import life.qbic.business.offers.identifier.OfferId
import life.qbic.datamodel.dtos.projectmanagement.Project
import life.qbic.datamodel.dtos.projectmanagement.ProjectIdentifier

/**
 * <h1>Output interface for the Create Project use case</h1>
 *
 * Provides output methods that are called by the use case <code>Create Project</code>.
 *
 * @since 1.0.0
 */
interface CreateProjectOutput extends UseCaseFailure {

    /**
     * <p>Called when a project has been successfully created.</p>
     * <br>
     * <p>This output represents the <strong>ideal use case</strong> scenario.</p>
     *
     * @param project The project that has been created iqn QBiC's data management system.
     * @since 1.0.0
     */
    void projectCreated(Project project)

    /**
     * <p>Called when a project with a given project identifier already exists.</p>
     * <br>
     * <p>This reflects the scenario, when a user provided a pre-defined project code
     * which already exists in the underlying data source. In this case the project cannot be
     * created.</p>
     * @param projectIdentifier The project identifier that already exists.
     * @param linkedOffer The linked offer of the already existing project.
     * @since 1.0.0
     */
    void projectAlreadyExists(ProjectIdentifier projectIdentifier,
                              OfferId linkedOffer)

}
