package life.qbic.business.projects.create

import life.qbic.business.Constants
import life.qbic.business.exceptions.DatabaseQueryException
import life.qbic.business.logging.Logger
import life.qbic.business.logging.Logging
import life.qbic.datamodel.dtos.projectmanagement.Project
import life.qbic.datamodel.dtos.projectmanagement.ProjectApplication
import life.qbic.datamodel.dtos.projectmanagement.ProjectIdentifier

/**
 * <h1>A new project is created and linked to an offer</h1>
 * <br>
 * <p>A new project is created and stored in the database.
 * If a project with the respective project code already exists the database throws an {@link life.qbic.business.projects.create.ProjectExistsException}</p>
 *
 * @since 1.0.0
 */
class CreateProject implements CreateProjectInput {

    private final CreateProjectOutput output
    private final CreateProjectDataSource dataSource

    private final Logging log = Logger.getLogger(CreateProject.class)

    CreateProject(CreateProjectOutput output, CreateProjectDataSource dataSource) {
        this.output = output
        this.dataSource = dataSource
    }


    @Override
    void createProject(ProjectApplication projectApplication) {

        try {
            Project createdProject = dataSource.createProject(projectApplication)
            output.projectCreated(createdProject)
        } catch (ProjectExistsException projectExistsException) {
            log.error("The project ${projectApplication.projectCode} already exists in the database.",projectExistsException)
            output.projectAlreadyExists(new ProjectIdentifier(projectApplication.projectSpace, projectApplication.projectCode), projectApplication.linkedOffer)
        } catch (DatabaseQueryException e) {
            log.error("An error occurred in the database while creating the project ${projectApplication.projectCode}.",e)
            output.failNotification("The project application for ${projectApplication.projectCode} was not successful. The project can not be stored in the database.")
        } catch (Exception exception) {
            log.error("An unexpected error occurred during the project creation of project ${projectApplication.projectCode}",exception)
            output.failNotification("An unexpected during the project creation occurred. " +
                    "Please contact ${Constants.QBIC_HELPDESK_EMAIL}.")
        }

    }
}
