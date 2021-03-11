package life.qbic.portal.portlet.projects

import life.qbic.business.Constants
import life.qbic.business.projects.spaces.CreateProjectSpace
import life.qbic.business.projects.spaces.CreateProjectSpaceDataSource
import life.qbic.business.projects.spaces.CreateProjectSpaceOutput
import life.qbic.business.projects.spaces.ProjectSpaceExistsException
import life.qbic.datamodel.dtos.projectmanagement.ProjectSpace
import spock.lang.Shared
import spock.lang.Specification

/**
 * <h1>Test the behaviour of {@link life.qbic.business.projects.spaces.CreateProjectSpace}</h1>
 *
 * @since 1.0.0
 */
class CreateProjectSpaceSpec extends Specification{

    @Shared CreateProjectSpace createProjectSpace
    @Shared CreateProjectSpaceOutput output
    @Shared CreateProjectSpaceDataSource dataSource

    @Shared ProjectSpace space

    def setup(){
        output = Mock(CreateProjectSpaceOutput)
        dataSource = Stub(CreateProjectSpaceDataSource)
        createProjectSpace = new CreateProjectSpace(output,dataSource)

        space = new ProjectSpace("my-new-space")
    }

    def "A a successful project space creation throws no errors"(){
        given: "The database successfully creates the new space"
        dataSource.createProjectSpace(space) >> {}

        when: "a new project space needs to be created"
        createProjectSpace.createProjectSpace(space)

        then: "no error is thrown"
        1 * output.projectSpaceCreated(space)
        0 * output.failNotification(_)
        0 * output.projectSpaceAlreadyExists(_)
    }

    def "No duplicate project spaces can be created"(){
        given: "The database successfully creates the new space"
        dataSource.createProjectSpace(space) >> {throw new ProjectSpaceExistsException("Project space already exists")}

        when: "a new project space needs to be created"
        createProjectSpace.createProjectSpace(space)

        then: "no error is thrown"
        0 * output.projectSpaceCreated(space)
        0 * output.failNotification(_)
        1 * output.projectSpaceAlreadyExists(space)
    }

    def "Unexpected errors are caught"(){
        given: "The database successfully creates the new space"
        dataSource.createProjectSpace(space) >> {throw new Exception("Project space already exists")}

        when: "a new project space needs to be created"
        createProjectSpace.createProjectSpace(space)

        then: "no error is thrown"
        0 * output.projectSpaceCreated(space)
        1 * output.failNotification("An unexpected during the project creation occurred. " +
                "Please contact ${Constants.QBIC_HELPDESK_EMAIL}.")
        0 * output.projectSpaceAlreadyExists(_)
    }

}
