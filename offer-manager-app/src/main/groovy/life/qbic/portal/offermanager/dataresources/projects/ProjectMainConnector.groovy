package life.qbic.portal.offermanager.dataresources.projects

import ch.ethz.sis.openbis.generic.asapi.v3.IApplicationServerApi
import groovy.transform.CompileStatic
import groovy.util.logging.Log4j2

import life.qbic.business.projects.spaces.CreateProjectSpaceDataSource
import life.qbic.business.projects.spaces.ProjectSpaceExistsException
import life.qbic.business.projects.create.CreateProjectDataSource
import life.qbic.business.projects.create.ProjectExistsException

import life.qbic.datamodel.dtos.business.*
import life.qbic.datamodel.dtos.projectmanagement.*

import life.qbic.business.exceptions.DatabaseQueryException


import life.qbic.openbis.openbisclient.OpenBisClient

import ch.ethz.sis.openbis.generic.asapi.v3.dto.operation.SynchronousOperationExecutionOptions
import ch.ethz.sis.openbis.generic.asapi.v3.dto.common.operation.IOperation
import ch.ethz.sis.openbis.generic.asapi.v3.dto.project.create.CreateProjectsOperation
import ch.ethz.sis.openbis.generic.asapi.v3.dto.project.create.ProjectCreation
import ch.ethz.sis.openbis.generic.asapi.v3.dto.space.create.CreateSpacesOperation
import ch.ethz.sis.openbis.generic.asapi.v3.dto.space.create.SpaceCreation
import ch.ethz.sis.openbis.generic.asapi.v3.dto.space.id.SpacePermId


/**
 * Provides operations on QBiC project data
 *
 * This class implements the data sources of the different use cases and is responsible for transferring data to the project/customer db and openBIS
 *
 * @since 1.0.0
 *
 */
@Log4j2
@CompileStatic
class ProjectMainConnector implements CreateProjectDataSource, CreateProjectSpaceDataSource {

  /**
   * A connection to the project (and customer) database used to create queries.
   */
  private final ProjectDbConnector projectDbConnector
  private final OpenBisClient openbisClient
  private List<ProjectSpace> openbisSpaces

  /**

   * Constructor for a ProjectMainConnector
   * @param projectDbConnector a connector enabling interaction with the project database
   * @param openbisClient an openBIS client API object
   */
  ProjectMainConnector(ProjectDbConnector projectDbConnector, OpenBisClient openbisClient) {
    this.projectDbConnector = projectDbConnector
    this.openbisClient = openbisClient
    fetchExistingSpaces()
  }
  
  private void fetchExistingSpaces() {
    this.openbisSpaces = new ArrayList<>()
    for(String spaceName : openbisClient.listSpaces()) {
      this.openbisSpaces.add(new ProjectSpace(spaceName))
    }
  }
  
  /**
   * Returns a copy of the list of available project spaces that has been fetched from openBIS upon creation of this class instance
   */
  public List<ProjectSpace> listSpaces() {
    return new ArrayList<ProjectSpace>(openbisSpaces);
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
    SpaceCreation space = new SpaceCreation()
    space.setCode(spaceName)

    space.setDescription(description);

    IOperation operation = new CreateSpacesOperation(space)
    handleOperations(operation)
  }

  private void createOpenbisProject(ProjectSpace space, ProjectCode projectCode, String description) {
    ProjectCreation project = new ProjectCreation();
    project.setCode(projectCode.toString());
    project.setSpaceId(new SpacePermId(space.toString()));
    project.setDescription(description);

    IOperation operation = new CreateProjectsOperation(project);
    handleOperations(operation);
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
    
    //if the space does not exist, an error shall be thrown
    if (!openbisClient.spaceExists(space.toString())) {
      throw new DatabaseQueryException("Could not create project because of non-existent space: "+space.toString())
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

    return projectDbConnector.addProjectAndConnectPersonsInUserDB(projectIdentifier, projectApplication)
  }
  
  private void handleOperations(IOperation operation) {
    IApplicationServerApi api = openbisClient.getV3()
    
    SynchronousOperationExecutionOptions options = new SynchronousOperationExecutionOptions()
    List<IOperation> ops = Arrays.asList(operation)
    try {
      api.executeOperations(adminToken, ops, options)
    } catch (Exception e) {
        log.error("Unexpected exception during openBIS operation.", e)
        throw e
    }
  }
}
