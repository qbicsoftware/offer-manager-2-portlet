package life.qbic.portal.offermanager.dataresources.projects

import ch.ethz.sis.openbis.generic.asapi.v3.IApplicationServerApi
import ch.ethz.sis.openbis.generic.asapi.v3.dto.common.operation.IOperation
import ch.ethz.sis.openbis.generic.asapi.v3.dto.entitytype.id.EntityTypePermId
import ch.ethz.sis.openbis.generic.asapi.v3.dto.experiment.create.CreateExperimentsOperation
import ch.ethz.sis.openbis.generic.asapi.v3.dto.experiment.create.ExperimentCreation
import ch.ethz.sis.openbis.generic.asapi.v3.dto.experiment.id.ExperimentIdentifier
import ch.ethz.sis.openbis.generic.asapi.v3.dto.operation.SynchronousOperationExecutionOptions
import ch.ethz.sis.openbis.generic.asapi.v3.dto.project.create.CreateProjectsOperation
import ch.ethz.sis.openbis.generic.asapi.v3.dto.project.create.ProjectCreation
import ch.ethz.sis.openbis.generic.asapi.v3.dto.sample.create.CreateSamplesOperation
import ch.ethz.sis.openbis.generic.asapi.v3.dto.sample.create.SampleCreation
import ch.ethz.sis.openbis.generic.asapi.v3.dto.space.create.CreateSpacesOperation
import ch.ethz.sis.openbis.generic.asapi.v3.dto.space.create.SpaceCreation
import ch.ethz.sis.openbis.generic.asapi.v3.dto.space.id.SpacePermId
import groovy.transform.CompileStatic
import groovy.util.logging.Log4j2
import life.qbic.business.exceptions.DatabaseQueryException
import life.qbic.business.projects.create.CreateProjectDataSource
import life.qbic.business.projects.create.ProjectExistsException
import life.qbic.business.projects.create.SpaceNonExistingException
import life.qbic.business.projects.list.ListProjectsDataSource
import life.qbic.business.projects.spaces.create.CreateProjectSpaceDataSource
import life.qbic.business.projects.spaces.create.ProjectSpaceExistsException
import life.qbic.business.projects.spaces.list.ListProjectSpacesDataSource
import life.qbic.datamodel.dtos.business.ProjectApplication
import life.qbic.datamodel.dtos.projectmanagement.Project
import life.qbic.datamodel.dtos.projectmanagement.ProjectCode
import life.qbic.datamodel.dtos.projectmanagement.ProjectIdentifier
import life.qbic.datamodel.dtos.projectmanagement.ProjectSpace
import life.qbic.datamodel.identifiers.ExperimentCodeFunctions
import life.qbic.openbis.openbisclient.OpenBisClient
import life.qbic.portal.offermanager.dataresources.offers.ProjectAssistant
import life.qbic.xml.manager.StudyXMLParser
import life.qbic.xml.study.Qexperiment

import javax.xml.bind.JAXBElement
import javax.xml.bind.JAXBException

/**
 * Provides operations on QBiC project data
 *
 * This class implements the data sources of the different use cases and is responsible for
 * transferring data to the project/customer db and openBIS
 *
 * @since 1.0.0
 */
@Log4j2
@CompileStatic
class ProjectMainConnector implements CreateProjectDataSource, CreateProjectSpaceDataSource, ListProjectsDataSource, ListProjectSpacesDataSource {

    /**
     * A connection to the project (and customer) database used to create queries.
     */
    private final ProjectDbConnector projectDbConnector
    private final OpenBisClient openbisClient
    private final ProjectAssistant projectAssistant
    private List<ProjectSpace> openbisSpaces
    private List<ProjectIdentifier> openbisProjects

    /**
     * Constructor for a ProjectMainConnector
     * @param projectDbConnector a connector enabling interaction with the project database
     * @param openbisClient an openBIS client API object
     */
    ProjectMainConnector(ProjectDbConnector projectDbConnector,
                         OpenBisClient openbisClient,
                         ProjectAssistant projectAssistant) {
        this.projectDbConnector = projectDbConnector
        this.openbisClient = openbisClient
        this.projectAssistant = projectAssistant
        fetchExistingSpaces()
        fetchExistingProjects()
    }

    private void fetchExistingSpaces() {
        this.openbisSpaces = new ArrayList<>()
        for (String spaceName : openbisClient.listSpaces()) {
            this.openbisSpaces.add(new ProjectSpace(spaceName))
        }
    }

    /**
     * Returns a copy of the list of available project spaces that has been fetched from openBIS
     * upon creation of this class instance
     */
    @Override
    List<ProjectSpace> listSpaces() {
        return new ArrayList<ProjectSpace>(openbisSpaces)
    }

    private void fetchExistingProjects() {
        //projectDbConnector.fetchProjects() might be used at some point to fetch more metadata

        openbisProjects = []
        for (ch.ethz.sis.openbis.generic.asapi.v3.dto.project.Project openbisProject : openbisClient.listProjects()) {
            try {
                ProjectSpace space = new ProjectSpace(openbisProject.getSpace().getCode())
                ProjectCode code = new ProjectCode(openbisProject.getCode())
                openbisProjects.add(new ProjectIdentifier(space, code))
            } catch (Exception e) {
                log.error(e.message)
            }
        }
    }

    private void createOpenbisSpace(String spaceName, String description) {
        SpaceCreation space = new SpaceCreation()
        space.setCode(spaceName)

        space.setDescription(description)

        IOperation operation = new CreateSpacesOperation(space)
        handleOperations(operation)
    }

    private void createOpenbisProject(ProjectSpace space, ProjectCode projectCode, String description) {
        ProjectCreation project = new ProjectCreation()
        project.setCode(projectCode.toString())
        project.setSpaceId(new SpacePermId(space.toString()))
        project.setDescription(description)

        IOperation operation = new CreateProjectsOperation(project)
        handleOperations(operation)
    }

    private void setupEmptyExperimentalDesign(ProjectSpace space, ProjectCode projectCodeObj)
            throws JAXBException {
        StudyXMLParser xmlParser = new StudyXMLParser()
        JAXBElement<Qexperiment> res =
                xmlParser.createNewDesign(new HashSet<>(), new ArrayList<>(), new HashMap<>(), new HashMap<>())
        String emptyStudyXML = xmlParser.toString(res)

        String spaceCode = space.toString()
        String projectCode = projectCodeObj.toString()

        String experimentCode = projectCode + "_INFO"
        String sampleCode = projectCode + "000"
        String experimentIdentifier = ExperimentCodeFunctions.getInfoExperimentID(spaceCode, projectCode)

        Map<String, String> properties = new HashMap<>()
        properties.put("Q_EXPERIMENTAL_SETUP", emptyStudyXML)

        createOpenbisExperiment(spaceCode, projectCode, experimentCode, "Q_PROJECT_DETAILS", properties)

        createOpenbisSample(spaceCode, experimentIdentifier, sampleCode, "Q_ATTACHMENT_SAMPLE", new HashMap<>())
    }

    private void createOpenbisExperiment(String spaceCode, String projectCode, String experimentCode, String experimentType, Map<String, String> properties) {
        ExperimentCreation experiment = new ExperimentCreation()
        experiment.setCode(experimentCode)
        experiment.setProjectId(new ch.ethz.sis.openbis.generic.asapi.v3.dto.project.id.ProjectIdentifier(spaceCode, projectCode))
        experiment.setTypeId(new EntityTypePermId(experimentType))
        experiment.setProperties(properties)

        IOperation operation = new CreateExperimentsOperation(experiment)
        handleOperations(operation)
    }

    private void createOpenbisSample(String spaceCode, String experimentIdentifier, String sampleCode, String sampleType, Map<String, String> properties) {
        SampleCreation sampleCreation = new SampleCreation()
        sampleCreation.setTypeId(new EntityTypePermId(sampleType))
        sampleCreation.setSpaceId(new SpacePermId(spaceCode))

        sampleCreation.setExperimentId(new ExperimentIdentifier(experimentIdentifier))
        sampleCreation.setCode(sampleCode)

        sampleCreation.setProperties(properties)

        IOperation operation = new CreateSamplesOperation(sampleCreation)
        handleOperations(operation)
    }

    /**
     * Returns a copied list of existing projects fetched upon creation of this class
     * @inheritDoc
     */
    @Override
    List<ProjectIdentifier> listProjects() {
        return new ArrayList<ProjectIdentifier>(openbisProjects)
    }

    @Override
    void createProjectSpace(ProjectSpace projectSpace) throws ProjectSpaceExistsException, DatabaseQueryException {
        String spaceName = projectSpace.getName()
        if (openbisClient.spaceExists(spaceName)) {
            throw new ProjectSpaceExistsException("Project space " + spaceName + " could not be created, as it exists in openBIS already!")
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

    @Override
    Project createProject(ProjectApplication projectApplication) throws ProjectExistsException, DatabaseQueryException {
        //collect infos needed for openBIS
        ProjectSpace space = projectApplication.getProjectSpace()
        ProjectCode projectCode = projectApplication.getProjectCode()
        String description = projectApplication.getProjectObjective()

        ProjectIdentifier projectIdentifier = new ProjectIdentifier(space, projectCode)

        //if the space does not exist, an error shall be thrown
        if (!openbisClient.spaceExists(space.toString())) {
            throw new SpaceNonExistingException("Could not create project because of non-existent space: " + space.toString())
        }
        if (openbisClient.projectExists(space.toString(), projectCode.toString())) {
            throw new ProjectExistsException("Project " + projectIdentifier.toString() + " could not be created, as it exists in openBIS already!")
        }
        try {
            createOpenbisProject(space, projectCode, description)
            setupEmptyExperimentalDesign(space, projectCode)
            projectAssistant.linkOfferWithProject(projectApplication.linkedOffer, projectIdentifier)
        } catch (Exception e) {
            log.error(e.message)
            log.error(e.stackTrace.join("\n"))
            throw new DatabaseQueryException("Could not create project.")
        }

        return projectDbConnector.addProjectAndConnectPersonsInUserDB(projectIdentifier, projectApplication)
    }

    private void handleOperations(IOperation operation) {
        IApplicationServerApi api = openbisClient.getV3()

        SynchronousOperationExecutionOptions executionOptions = new SynchronousOperationExecutionOptions()
        List<IOperation> operationOptions = Arrays.asList(operation)
        try {
            api.executeOperations(openbisClient.getSessionToken(), operationOptions, executionOptions)
        } catch (Exception e) {
            log.error("Unexpected exception during openBIS operation.", e)
            throw e
        }
    }
}
