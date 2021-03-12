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

import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.ResultSet
import java.sql.Statement

import life.qbic.openbis.openbisclient.OpenBisClient

import ch.ethz.sis.openbis.generic.asapi.v3.dto.common.operation.IOperation;
import ch.ethz.sis.openbis.generic.asapi.v3.dto.project.create.CreateProjectsOperation;
import ch.ethz.sis.openbis.generic.asapi.v3.dto.project.create.ProjectCreation;
import ch.ethz.sis.openbis.generic.asapi.v3.dto.project.id.ProjectIdentifier;
import ch.ethz.sis.openbis.generic.asapi.v3.dto.space.create.CreateSpacesOperation;
import ch.ethz.sis.openbis.generic.asapi.v3.dto.project.Project;
import ch.ethz.sis.openbis.generic.asapi.v3.dto.space.create.SpaceCreation;
import ch.ethz.sis.openbis.generic.asapi.v3.dto.space.id.SpacePermId;
import ch.ethz.sis.openbis.generic.asapi.v3.IApplicationServerApi;

/**
 * Provides operations on QBiC project data
 *
 * This class implements the data sources of the different use cases and is responsible for transferring data to the customer db and openBIS
 *
 * @since: 1.0.0
 *
 */
@Log4j2
class ProjectMainConnector implements CreateProjectDataSource, CreateProjectSpaceDataSource {

  /**
   * A connection to the customer database used to create queries.
   */
  private final ProjectDbConnector projectDbConnector
  private final OpenBisClient openbisClient

  /**
   * Constructor for a ProjectOpenBisAndDBConnector
   * @param connection a connection to the customer db
   * 
   * @see Connection
   */
  ProjectMainConnector(ProjectDbConnector projectDbConnector, OpenBisClient openbisClient) {
    this.projectDbConnector = projectDbConnector
    this.openbisClient = openbisClient
  }
  
  public List<ProjectIdentifier> fetchProjects() {
    //projectDbConnector.fetchProjects() might be used at some point to fetch more metadata
    
    List<ProjectIdentifier> projects = []
    for(Project openbisProject : openbisClient.listProjects()) {
      String space = openbisProject.getSpace().getCode()
      String code = openbisProject.getCode()
      projects.add(new ProjectIdentifier(space, code))
    }
    return projects
  }

}