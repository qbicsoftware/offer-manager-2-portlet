package life.qbic.portal.offermanager.dataresources.projects

import groovy.util.logging.Log4j2
import life.qbic.datamodel.dtos.business.Customer
import life.qbic.datamodel.dtos.business.ProjectManager
import life.qbic.datamodel.dtos.general.Person
import life.qbic.business.projects.spaces.CreateProjectSpaceDataSource
import life.qbic.business.projects.spaces.ProjectSpaceExistsException
import life.qbic.business.projects.create.CreateProjectDataSource
import life.qbic.business.projects.create.ProjectExistsException

import life.qbic.datamodel.dtos.projectmanagement.*

import life.qbic.business.exceptions.DatabaseQueryException
import life.qbic.portal.offermanager.dataresources.database.ConnectionProvider

import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.ResultSet
import java.sql.Statement

import life.qbic.openbis.openbisclient.OpenBisClient

/**
 * Provides operations on QBiC project data
 *
 * This class implements the data sources of the different use cases and is responsible for transferring data to the customer db and openBIS
 *
 * @since: 1.0.0
 *
 */
@Log4j2
class ProjectOpenBisAndDBConnector implements CreateProjectDataSource, CreateProjectSpaceDataSource {

  /**
   * A connection to the customer database used to create queries.
   */
  private final ConnectionProvider connectionProvider
  private final OpenBisClient openbisClient

  /**
   * Constructor for a ProjectOpenBisAndDBConnector
   * @param connection a connection to the customer db
   * 
   * @see Connection
   */
  ProjectOpenBisAndDBConnector(ConnectionProvider connectionProvider, OpenBisClient openbisClient) {
    this.connectionProvider = connectionProvider
    this.openBisClient = openbisClient
  }

  @Override
  void createProjectSpace(ProjectSpace projectSpace) throws ProjectSpaceExistsException, DatabaseQueryException {
    String spaceName = projectSpace.getName()
    if(openbisClient.spaceExists(spaceName)) {
      throw new ProjectSpaceExistsException("Project space "+spaceName+" could not be created, as it exists in openBIS already!")
    }
    try {
      //we don't provide a description in our data model for now, but it's optional anyway
      createOpenbisSpace(spaceName, "")

    } catch (Exception e) {
      log.error(e.message)
      log.error(e.stackTrace.join("\n"))
      throw new DatabaseQueryException("Could not create project space.")
    }
  }

  private void createOpenbisSpace(String spaceName, String description) {
    // TODO this is not how the api should be used, I reckon
    IApplicationServerApi api = openbisClient.getV3()
    SpaceCreation space = new SpaceCreation()
    space.setCode(spaceName)

    space.setDescription(description);

    IOperation operation = new CreateSpacesOperation(space)
    api.handleOperations(operation)
  }

  private void createOpenbisProject(ProjectSpace space, ProjectCode projectCode, String description) {
    // TODO this is not how the api should be used, I reckon
    IApplicationServerApi api = openbisClient.getV3()

    ProjectCreation project = new ProjectCreation();
    project.setCode(projectCode.toString());
    project.setSpaceId(new SpacePermId(space.toString()));
    project.setDescription(description);

    IOperation operation = new CreateProjectsOperation(project);
    api.handleOperations(operation);
  }

  @Override
  Project createProject(ProjectApplication projectApplication) throws ProjectExistsException, DatabaseQueryException {
    //collect infos needed for openBIS
    ProjectSpace space = projectApplication.getProjectSpace()
    ProjectCode projectCode = projectApplication.getProjectCode()
    String description = projectApplication.getProjectObjective()

    ProjectIdentifier projectIdentifier = new ProjectIdentifier(space, projectCode)

    //collect infos needed for database
    String projectTitle = projectApplication.getProjectTitle()
    Customer customer = projectApplication.getCustomer()
    ProjectManager projectManager = projectApplication.getProjectManager()
    //TODO if the space does not exist, should it be created in this use case or fail?
    if (!openbisClient.spaceExists(space)) {
      createProjectSpace(projectSpace)
    }
    if (openbisClient.projectExists(space.toString(), projectCode.toString())) {
      throw new ProjectExistsException("Project "+projectIdentifier.toString()+" could not be created, as it exists in openBIS already!")
    }
    try {
      createOpenbisProject(space, projectCode, description)
    } catch (Exception e) {
      log.error(e.message)
      log.error(e.stackTrace.join("\n"))
      throw new DatabaseQueryException("Could not create project.")
    }

    addProjectAndConnectPersonsInUserDB(projectIdentifier.toString(), projectTitle, customer, projectManager)

    return new Project(projectIdentifier, projectTitle, projectApplication.getLinkedOffer())
  }
  
  private void addProjectAndConnectPersonsInUserDB(projectIdentifier, projectTitle, customer, projectManager) {
  //fetch needed person ids from database TODO
  int customerID = getPersonId(customer)
  int managerID = getPersonId(projectManager)
  
  Connection connection = connectionProvider.connect()
  connection.setAutoCommit(false)

  connection.withCloseable {it ->
    try {
      int projectID = addProjectToDB(connection, projectIdentifier, projectTitle)
      addPersonToProject(connection, projectID, managerID, "Manager")
      addPersonToProject(connection, projectID, customerID, "PI")
      
      connection.commit()

    } catch (Exception e) {
      log.error(e.message)
      log.error(e.stackTrace.join("\n"))
      connection.rollback()
      connection.close()
      throw new DatabaseQueryException("Could not add person and project data to user database.")
    }
  }
  }

  private int isProjectInDB(String projectIdentifier) {
    log.info("Looking for project " + projectIdentifier + " in the DB");
    String sql = "SELECT * from projects WHERE openbis_project_identifier = ?";
    int res = -1;
    Connection connection = connectionProvider.connect()
    connection.withCloseable {
      PreparedStatement statement = it.prepareStatement(sql);
      statement.setString(1, projectIdentifier);
      ResultSet rs = statement.executeQuery();
      if (rs.next()) {
        res = rs.getInt("id");
      }
    }
    return res;
  }

  public int addProjectToDB(Connection connection, String projectIdentifier, String projectName) {
    int exists = isProjectInDB(projectIdentifier);
    if (exists < 0) {
      log.info("Trying to add project " + projectIdentifier + " to the person DB");
      String sql = "INSERT INTO projects (openbis_project_identifier, short_title) VALUES(?, ?)";
      try (PreparedStatement statement =
      connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
        statement.setString(1, projectIdentifier);
        statement.setString(2, projectName);
        statement.execute();
        ResultSet rs = statement.getGeneratedKeys();
        if (rs.next()) {
          logout(conn);
          log.info("Successful.");
          return rs.getInt(1);
        }
      } catch (SQLException e) {
        log.error("SQL operation unsuccessful: " + e.getMessage());
        e.printStackTrace();
      }
      return -1;
    }
    return exists;
  }

  public void addPersonToProject(Connection connection, int projectID, int personID, String role) {
    if (!hasPersonRoleInProject(personID, projectID, role)) {
      log.info("Trying to add person with role " + role + " to a project.");
      String sql =
      "INSERT INTO projects_persons (project_id, person_id, project_role) VALUES(?, ?, ?)";
      try (PreparedStatement statement =
      connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
        statement.setInt(1, projectID);
        statement.setInt(2, personID);
        statement.setString(3, role);
        statement.execute();
        log.info("Successful.");
      } catch (SQLException e) {
        log.error("SQL operation unsuccessful: " + e.getMessage());
        e.printStackTrace();
      }
    }
  }

  public boolean hasPersonRoleInProject(int personID, int projectID, String role) {
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
    } catch (SQLException e) {
      logger.error("SQL operation unsuccessful: " + e.getMessage());
      e.printStackTrace();
    }
    logout(conn);
    return res;
  }
}
