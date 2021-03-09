package life.qbic.business.projects.spaces

import life.qbic.business.Constants
import life.qbic.business.exceptions.DatabaseQueryException
import life.qbic.business.logging.Logger
import life.qbic.business.logging.Logging
import life.qbic.datamodel.dtos.projectmanagement.ProjectIdentifier
import life.qbic.datamodel.dtos.projectmanagement.ProjectSpace

/**
 * <h1>Creates the project space to store new projects in.</h1>
 * <br>
 * <p>A project needs to be assigned to a project space. If no such project space is available a new space needs to be created.</p>
 *
 * @since 1.0.0
 */

class CreateProjectSpace implements CreateProjectSpaceInput{

    private final CreateProjectSpaceOutput output
    private final CreateProjectSpaceDataSource dataSource

    private final Logging log = Logger.getLogger(CreateProjectSpace.class)


    CreateProjectSpace(CreateProjectSpaceOutput output, CreateProjectSpaceDataSource dataSource){
        this.output = output
        this.dataSource = dataSource
    }

    @Override
    void createProjectSpace(ProjectSpace projectSpace) {
        try{
            dataSource.createProjectSpace(projectSpace)
            output.projectSpaceCreated(projectSpace)
        }catch(ProjectSpaceExistsException existsException){
            log.error("The project space ${projectSpace.toString()} already exists in the database.",existsException)
            output.projectSpaceAlreadyExists(projectSpace)
        }catch(DatabaseQueryException databaseQueryException){
            log.error("An error occurred in the database while creating the project space${projectSpace.toString()}.",databaseQueryException)
            output.failNotification("The project space ${projectSpace.toString()} creation was not successful. The project space cannot be stored in the database.")
        }catch(Exception exception){
            log.error("An unexpected error occurred during the project space creation ${projectSpace.toString()}",exception)
            output.failNotification("An unexpected during the project creation occurred. " +
                    "Please contact ${Constants.QBIC_HELPDESK_EMAIL}.")
        }
    }
}
