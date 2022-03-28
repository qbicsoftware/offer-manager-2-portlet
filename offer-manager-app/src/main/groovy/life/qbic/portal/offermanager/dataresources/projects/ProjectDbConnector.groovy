package life.qbic.portal.offermanager.dataresources.projects


import groovy.util.logging.Log4j2
import life.qbic.business.exceptions.DatabaseQueryException
import life.qbic.business.projects.create.ProjectExistsException
import life.qbic.datamodel.dtos.business.Customer
import life.qbic.datamodel.dtos.business.ProjectApplication
import life.qbic.datamodel.dtos.business.ProjectManager
import life.qbic.datamodel.dtos.general.Person
import life.qbic.datamodel.dtos.projectmanagement.Project
import life.qbic.datamodel.dtos.projectmanagement.ProjectCode
import life.qbic.datamodel.dtos.projectmanagement.ProjectIdentifier
import life.qbic.datamodel.dtos.projectmanagement.ProjectSpace
import life.qbic.portal.offermanager.dataresources.database.ConnectionProvider
import life.qbic.portal.offermanager.dataresources.persons.PersonDbConnector

import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.ResultSet
import java.sql.Statement

/**
 * Provides operations on QBiC project data
 *
 * This class is responsible for transferring project data to the project/customer db
 *
 * @since 1.0.0
 */
@Log4j2
class ProjectDbConnector {

    /**
     * A connection to the project/customer database used to create queries.
     */
    private final ConnectionProvider connectionProvider
    /**
     * A connector to the customer database used to create queries.
     */
    private final PersonDbConnector personDbConnector

    /**
     * Constructor for a ProjectDbConnector
     * @param connectionProvider a connection provider to the project/customer db
     * @param personDbConnector db connector used to connect projects to customer and manager
     *
     */
    ProjectDbConnector(ConnectionProvider connectionProvider, PersonDbConnector personDbConnector) {
        this.connectionProvider = connectionProvider
        this.personDbConnector = personDbConnector
    }

    /**
     * parses existing projects from user database, might be needed later if more complex information is to be listed
     */
    List<ProjectIdentifier> fetchProjects() {
        List<ProjectIdentifier> projects = []
        String query = "SELECT openbis_project_identifier from projects"
        Connection connection = connectionProvider.connect()
        connection.withCloseable {
            def preparedStatement = it.prepareStatement(query)
            ResultSet resultSet = preparedStatement.executeQuery()
            while (resultSet.next()) {
                try {
                    String[] tokens = resultSet.getString('openbis_project_identifier').split("/")
                    ProjectSpace space = new ProjectSpace(tokens[1])
                    ProjectCode project = new ProjectCode(tokens[2])
                    projects.add(new ProjectIdentifier(space, project))
                } catch (Exception e) {
                    log.error(e)
                    throw new DatabaseQueryException("Could not parse existing projects from database.")
                }
            }
        }
        return projects
    }

    /**
     * Add a project to the user database to connect additional metadata that is not stored in openBIS
     * The project is uniquely recognizable by its openBIS project identifier, containing space and
     * project code
     * @param projectIdentifier a project identifier object denoting the openBIS identifier
     * @param projectApplication a project application object used to add additional metadata
     */
    Project addProjectAndConnectPersonsInUserDB(ProjectIdentifier projectIdentifier,
                                                ProjectApplication projectApplication) {
        //collect infos needed for database
        String projectTitle = projectApplication.getProjectTitle()
        Customer customer = projectApplication.getCustomer()
        ProjectManager projectManager = projectApplication.getProjectManager()

        //fetch needed person ids from database
        int customerID = getPersonDbId(customer)
        int managerID = getPersonDbId(projectManager)

        Connection connection = connectionProvider.connect()
        connection.setAutoCommit(false)

        connection.withCloseable { it ->
            try {
                int projectID = addProjectToDB(
                        it,
                        projectApplication)
                addPersonToProject(it, projectID, managerID, "Manager")
                addPersonToProject(it, projectID, customerID, "PI")

                it.commit()

            } catch (Exception e) {
                log.error(e.message)
                log.error(e.stackTrace.join("\n"))
                it.rollback()

                throw new DatabaseQueryException("Could not add person and project data to user database.")
            }
        }
        return new Project.Builder(projectIdentifier, projectTitle)
                .linkedOfferId(projectApplication.linkedOffer).build()
    }

    private int getPersonDbId(Person personDto) {
        return personDbConnector.listPersons().stream().filter(it -> matches(it, personDto))
                .map(it -> it.getId())
                .findFirst()
                .orElseThrow(() ->
                        new RuntimeException("No person matching customer found for linking in project"))
    }

    private static boolean matches(life.qbic.business.persons.Person person, Person personDto) {
        return person.firstName.equals(personDto.getFirstName())
                && person.lastName.equals(personDto.getLastName())
                && person.email.equals(personDto.emailAddress)
                && person.isActive
    }

    private boolean isProjectInDB(String projectIdentifier) {
        String sql = "SELECT * from projects WHERE openbis_project_identifier = ?"
        Connection connection = connectionProvider.connect()
        connection.withCloseable { it ->
            PreparedStatement statement = it.prepareStatement(sql)
            statement.setString(1, projectIdentifier)
            ResultSet rs = statement.executeQuery()
            return rs.next()
        }
        return false
    }

    private int addProjectToDB(Connection connection, ProjectApplication projectApplication) {
        String projectId = new ProjectIdentifier(
                projectApplication.projectSpace,
                projectApplication.projectCode).toString()

        if (isProjectInDB(projectId)) {
            throw new ProjectExistsException("Project " + projectId + " is already in the user database")
        }
        log.debug("Trying to add project " + projectId + " to the person DB")
        String sql = "INSERT INTO projects (openbis_project_identifier, short_title, long_description) VALUES(?, ?, ?)"
        try (PreparedStatement statement =
                connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, projectId)
            statement.setString(2, projectApplication.projectTitle)
            statement.setString(3, projectApplication.experimentalDesign)
            statement.execute()
            ResultSet rs = statement.getGeneratedKeys()
            if (rs.next()) {
                log.debug("Successful.")
                return rs.getInt(1)
            }
        }
        return -1
    }

    private void addPersonToProject(Connection connection, int projectID, int personID, String role) {
        if (!hasPersonRoleInProject(connection, personID, projectID, role)) {
            log.debug("Trying to add person with role " + role + " to a project.")
            String sql =
                    "INSERT INTO projects_persons (project_id, person_id, project_role) VALUES(?, ?, ?)"
            try (PreparedStatement statement =
                    connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                statement.setInt(1, projectID)
                statement.setInt(2, personID)
                statement.setString(3, role)
                statement.execute()
            } catch (Exception e) {
                log.error("SQL operation unsuccessful: " + e.getMessage())
                log.debug("SQL operation unsuccessful: " + e.getMessage(), e)
            }
        }
    }

    private static boolean hasPersonRoleInProject(Connection connection, int personID, int projectID, String role) {
        String sql =
                "SELECT * from projects_persons WHERE person_id = ? AND project_id = ? and project_role = ?"
        boolean res = false
        try {
            PreparedStatement statement = connection.prepareStatement(sql)
            statement.setInt(1, personID)
            statement.setInt(2, projectID)
            statement.setString(3, role)
            ResultSet rs = statement.executeQuery()
            if (rs.next()) {
                res = true
            }
        } catch (Exception e) {
            log.error("SQL operation unsuccessful: " + e.getMessage())
            log.debug("SQL operation unsuccessful: " + e.getMessage(), e)
        }
        return res
    }
}
