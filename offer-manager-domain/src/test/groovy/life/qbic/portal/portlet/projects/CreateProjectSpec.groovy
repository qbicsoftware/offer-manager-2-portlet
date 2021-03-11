package life.qbic.portal.portlet.projects

import life.qbic.business.exceptions.DatabaseQueryException
import life.qbic.business.projects.create.CreateProject
import life.qbic.business.projects.create.CreateProjectDataSource
import life.qbic.business.projects.create.CreateProjectOutput
import life.qbic.business.projects.create.ProjectExistsException
import life.qbic.business.projects.spaces.CreateProjectSpaceDataSource
import life.qbic.datamodel.dtos.business.Customer
import life.qbic.datamodel.dtos.business.OfferId
import life.qbic.datamodel.dtos.business.ProjectManager
import life.qbic.datamodel.dtos.projectmanagement.Project
import life.qbic.datamodel.dtos.projectmanagement.ProjectApplication
import life.qbic.datamodel.dtos.projectmanagement.ProjectCode
import life.qbic.datamodel.dtos.projectmanagement.ProjectIdentifier
import life.qbic.datamodel.dtos.projectmanagement.ProjectSpace
import spock.lang.Shared
import spock.lang.Specification

/**
 * <h1>Adds tests for the {@link life.qbic.business.projects.create.CreateProject} use case</h1>
 *
 * @since 1.0.0
 *
*/

class CreateProjectSpec extends Specification{

    @Shared CreateProject createProject
    @Shared CreateProjectOutput output
    @Shared CreateProjectDataSource dataSource
    @Shared CreateProjectSpaceDataSource spaceDataSource

    @Shared ProjectManager projectManager
    @Shared Customer customer
    @Shared OfferId offerId

    @Shared ProjectApplication projectApplication
    @Shared ProjectSpace space
    @Shared ProjectIdentifier projectIdentifier
    @Shared ProjectCode projectCode

    def setup(){
        dataSource = Stub(CreateProjectDataSource)
        spaceDataSource = Stub(CreateProjectSpaceDataSource)
        output = Mock(CreateProjectOutput)
        createProject = new CreateProject(output,dataSource,spaceDataSource)

        offerId = new OfferId("my-project","abab","1")
        projectManager = new ProjectManager.Builder("Max","Mustermann","a.b@c.d").build()
        customer = new Customer.Builder("Maxine","Mustermann","e.b@c.d").build()

        space = new ProjectSpace("my-space-name")
        projectCode = new ProjectCode("QABCD")
        projectIdentifier = new ProjectIdentifier(space,projectCode)
    }

    def "A valid project application creates a new project"(){
        given: "a project application is provided"
        projectApplication = new ProjectApplication(offerId,"Title","objective","exp. design",projectManager,space, customer, projectCode)
        Project project = new Project(projectIdentifier,projectApplication.projectTitle,offerId)

        and: "the data source is able to create the project"
        dataSource.createProject(projectApplication) >> project

        when: "the creation of the project is triggered"
        createProject.createProject(projectApplication)

        then: "a project is successfully created"
        1 * output.projectCreated(project)
        0 * output.failNotification(_)
        0 * output.projectAlreadyExists(_,_)
    }

    def "An a project with duplicate project codes cannot be created"(){
        given: "a project application is provided"
        projectApplication = new ProjectApplication(offerId,"Title","objective","exp. design",projectManager,space, customer, projectCode)

        and: "the data source is able to create the project"
        dataSource.createProject(projectApplication) >> {throw new ProjectExistsException("The project already exists in the database")}

        when: "the creation of the project is triggered"
        createProject.createProject(projectApplication)

        then: "a project is successfully created"
        0 * output.projectCreated(_)
        0 * output.failNotification(_)
        1 * output.projectAlreadyExists(_,_)
    }

    def "Create sends a fail notification to the output if the database fails"(){
        given: "a project application is provided"
        projectApplication = new ProjectApplication(offerId,"Title","objective","exp. design",projectManager,space, customer, projectCode)

        and: "the data source is able to create the project"
        dataSource.createProject(projectApplication) >> {throw new DatabaseQueryException("An exception occurred.")}

        when: "the creation of the project is triggered"
        createProject.createProject(projectApplication)

        then: "a project is successfully created"
        0 * output.projectCreated(_)
        1 * output.failNotification("The project application for ${projectApplication.projectCode} was not successful. The project can not be stored in the database.")
        0 * output.projectAlreadyExists(_,_)
    }

    def "If a new space is provided it shall be created before the project is created"(){
        given: "a project application is provided"
        projectApplication = new ProjectApplication(offerId,"Title","objective","exp. design",projectManager,space, customer, projectCode)
        Project project = new Project(projectIdentifier,projectApplication.projectTitle,offerId)

        and: "the data source is able to create the project"
        dataSource.createProject(projectApplication) >> project
        spaceDataSource.createProjectSpace(space) >> {}

        when: "the creation of the project is triggered"
        createProject.createProjectWithSpace(projectApplication)

        then: "a space and project are successfully created"
        1 * output.projectCreated(project)
        0 * output.failNotification(_)
        0 * output.projectAlreadyExists(_,_)
    }
}
