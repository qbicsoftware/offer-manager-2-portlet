package life.qbic.portal.offermanager.components.offer.overview.projectcreation

import life.qbic.business.projects.create.CreateProjectOutput
import life.qbic.datamodel.dtos.business.OfferId
import life.qbic.datamodel.dtos.projectmanagement.Project
import life.qbic.datamodel.dtos.projectmanagement.ProjectIdentifier

/**
 * <h1>Presenter that deals with the Create Project use case output</h1>
 *
 * The presenter currently deals with three different use case output
 * scenarios:
 * <ol>
 *     <li>The project creation was successful</li>
 *     <li>A project with the given identifier already existed</li>
 *     <li>The project creation failed</li>
 * </ol>
 *
 * @since 1.0.0
 */
class CreateProjectPresenter implements CreateProjectOutput{

    /**
     * {@inheritDocs}
     */
    @Override
    void failNotification(String notification) {

    }

    /**
     * {@inheritDocs}
     */
    @Override
    void projectCreated(Project project) {

    }

    /**
     * {@inheritDocs}
     */
    @Override
    void projectAlreadyExists(ProjectIdentifier projectIdentifier, OfferId linkedOffer) {

    }
}
