package life.qbic.portal.offermanager.dataresources.projects

import groovy.util.logging.Log4j2

import life.qbic.datamodel.dtos.general.Person
import life.qbic.portal.offermanager.dataresources.persons.PersonDbConnector

import life.qbic.datamodel.dtos.general.Person
import life.qbic.portal.offermanager.dataresources.persons.PersonDbConnector

import life.qbic.datamodel.dtos.business.*
import life.qbic.datamodel.dtos.projectmanagement.*
import life.qbic.business.projects.create.ProjectExistsException

import life.qbic.business.exceptions.DatabaseQueryException
import life.qbic.portal.offermanager.dataresources.database.ConnectionProvider

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
 *
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
   * Add a project to the user database to connect additional metadata that is not stored in openBIS
   * The project is uniquely recognizable by its openBIS project identifier, containing space and
   * project code
   * @param projectIdentifier a project identifier object denoting the openBIS identifier
   * @param projectApplication a project application object used to add additional metadata
   */
  public Project addProjectAndConnectPersonsInUserDB(projectIdentifier, projectApplication) {
    //collect infos needed for database
    String projectTitle = projectApplication.getProjectTitle()
    Customer customer = projectApplication.getCustomer()
    ProjectManager projectManager = projectApplication.getProjectManager()
    
    //fetch needed person ids from database
    int customerID = personDBConnector.getPersonId(customer)
    int managerID = personDBConnector.getPersonId(projectManager)
  
    Connection connection = connectionProvider.connect()
    connection.setAutoCommit(false)

    connection.withCloseable {it ->
      try {
        int projectID = addProjectToDB(it, projectIdentifier, projectTitle)
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
  return new Project(projectIdentifier, projectTitle, projectApplication.getLinkedOffer())
 }

  private boolean isProjectInDB(String projectIdentifier) {
    log.debug("Looking for project " + projectIdentifier + " in the DB");
    String sql = "SELECT * from projects WHERE openbis_project_identifier = ?";
    Connection connection = connectionProvider.connect()
    connection.withCloseable { it ->
      PreparedStatement statement = it.prepareStatement(sql);
      statement.setString(1, projectIdentifier);
      ResultSet rs = statement.executeQuery();
      if (rs.next()) {
        return true
      }
    }
    return false;
  }

  private int addProjectToDB(Connection connection, String projectIdentifier, String projectName) {
    if(isProjectInDB(projectIdentifier)) {
      throw new ProjectExistsException("Project "+projectIdentifier+" is already in the user database")
    }
    log.debug("Trying to add project " + projectIdentifier + " to the person DB");
    String sql = "INSERT INTO projects (openbis_project_identifier, short_title) VALUES(?, ?)";
    try (PreparedStatement statement =
    connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
      statement.setString(1, projectIdentifier);
      statement.setString(2, projectName);
      statement.execute();
      ResultSet rs = statement.getGeneratedKeys();
      if (rs.next()) {
        logout(conn);
        log.debug("Successful.");
        return rs.getInt(1);
      }
    }
    return -1
  }

  private void addPersonToProject(Connection connection, int projectID, int personID, String role) {
    if (!hasPersonRoleInProject(personID, projectID, role)) {
      log.debug("Trying to add person with role " + role + " to a project.");
      String sql =
      "INSERT INTO projects_persons (project_id, person_id, project_role) VALUES(?, ?, ?)";
      try (PreparedStatement statement =
      connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
        statement.setInt(1, projectID);
        statement.setInt(2, personID);
        statement.setString(3, role);
        statement.execute();
        log.debug("Successful.");
      } catch (Exception e) {
        log.error("SQL operation unsuccessful: " + e.getMessage());
        e.printStackTrace();
      }
    }
  }

  private boolean hasPersonRoleInProject(int personID, int projectID, String role) {
    logger.info("Checking if person already has this role in the project.");
    String sql =
        "SELECT * from projects_persons WHERE person_id = ? AND project_id = ? and project_role = ?";
    boolean res = false;
  Connection connection = connectionProvider.connect()
    try {
      PreparedStatement statement = connection.prepareStatement(sql);
      statement.setInt(1, personID);
      statement.setInt(2, projectID);
      statement.setString(3, role);
      ResultSet rs = statement.executeQuery();
      if (rs.next()) {
        res = true;
        logger.info("person already has this role!");
      }
    } catch (Exception e) {
      logger.error("SQL operation unsuccessful: " + e.getMessage());
      e.printStackTrace();
    }
    logout(conn);
    return res;
  }
}