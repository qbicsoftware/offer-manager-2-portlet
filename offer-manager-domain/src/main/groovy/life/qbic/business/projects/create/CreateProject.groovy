package life.qbic.business.projects.create

import life.qbic.business.exceptions.DatabaseQueryException
import life.qbic.business.logging.Logger
import life.qbic.business.logging.Logging
import life.qbic.datamodel.dtos.projectmanagement.Project
import life.qbic.datamodel.dtos.projectmanagement.ProjectApplication
import life.qbic.datamodel.dtos.projectmanagement.ProjectIdentifier

/**
 * <h1>A new project is created in the database. </h1>
 * <br>
 * <p><detailed description></p>
 *
 * @since 1.0.0
 *
 */
class CreateProject implements CreateProjectInput{

    private final CreateProjectOutput output
    private final CreateProjectDataSource dataSource

    private final Logging log = Logger.getLogger(CreateProject.class)

    CreateProject(CreateProjectOutput output, CreateProjectDataSource dataSource){
        this.output = output
        this.dataSource = dataSource
    }


    @Override
    void createProject(ProjectApplication projectApplication) {
        //create project code is valid
        //check for project space if new or already existing
        try{
            Project createdProject = dataSource.createProject(projectApplication)
            //todo create project here or in DB?
            output.projectCreated(createdProject)
        }catch(ProjectExistsException projectExistsException){
            log.error projectExistsException.stackTrace.toString()
            output.projectAlreadyExists(new ProjectIdentifier(projectApplication.projectSpace,projectApplication.projectCode), projectApplication.linkedOffer)
        }catch(DatabaseQueryException e){
            log.error e.stackTrace.toString()
            output.failNotification("The project application was not successful. It could not be stored in the database")
        }catch(Exception exception){
            log.error exception.stackTrace.toString()
            output.failNotification("An error occurred. Please contact ...")
        }
    }
}
