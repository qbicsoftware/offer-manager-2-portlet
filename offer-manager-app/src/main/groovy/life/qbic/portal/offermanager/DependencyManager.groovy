package life.qbic.portal.offermanager

import groovy.util.logging.Log4j2
import life.qbic.business.offers.create.CreateOffer
import life.qbic.business.offers.create.CreateOfferDataSource
import life.qbic.business.offers.fetch.FetchOffer
import life.qbic.business.offers.fetch.FetchOfferDataSource
import life.qbic.business.persons.affiliation.create.CreateAffiliation
import life.qbic.business.persons.affiliation.create.CreateAffiliationDataSource
import life.qbic.business.persons.affiliation.list.ListAffiliationsDataSource
import life.qbic.business.persons.create.CreatePerson
import life.qbic.business.persons.create.CreatePersonDataSource
import life.qbic.business.persons.list.ListPersonsDataSource
import life.qbic.business.persons.search.SearchPersonDataSource
import life.qbic.business.products.archive.ArchiveProduct
import life.qbic.business.products.archive.ArchiveProductDataSource
import life.qbic.business.products.copy.CopyProduct
import life.qbic.business.products.copy.CopyProductDataSource
import life.qbic.business.products.create.CreateProduct
import life.qbic.business.products.create.CreateProductDataSource
import life.qbic.business.products.list.ListProductsDataSource
import life.qbic.business.projects.create.CreateProject
import life.qbic.business.projects.create.CreateProjectDataSource
import life.qbic.business.projects.list.ListProjectsDataSource
import life.qbic.business.projects.spaces.create.CreateProjectSpaceDataSource
import life.qbic.business.projects.spaces.list.ListProjectSpacesDataSource
import life.qbic.datamodel.dtos.business.*
import life.qbic.datamodel.dtos.business.services.Product
import life.qbic.datamodel.dtos.general.Person
import life.qbic.datamodel.dtos.projectmanagement.Project
import life.qbic.datamodel.dtos.projectmanagement.ProjectIdentifier
import life.qbic.datamodel.dtos.projectmanagement.ProjectSpace
import life.qbic.openbis.openbisclient.OpenBisClient
import life.qbic.portal.offermanager.communication.EventEmitter
import life.qbic.portal.offermanager.components.AppPresenter
import life.qbic.portal.offermanager.components.AppView
import life.qbic.portal.offermanager.components.AppViewModel
import life.qbic.portal.offermanager.components.affiliation.create.CreateAffiliationController
import life.qbic.portal.offermanager.components.affiliation.create.CreateAffiliationPresenter
import life.qbic.portal.offermanager.components.affiliation.create.CreateAffiliationView
import life.qbic.portal.offermanager.components.affiliation.create.CreateAffiliationViewModel
import life.qbic.portal.offermanager.components.affiliation.search.SearchAffiliationView
import life.qbic.portal.offermanager.components.affiliation.search.SearchAffiliationViewModel
import life.qbic.portal.offermanager.components.offer.create.CreateOfferController
import life.qbic.portal.offermanager.components.offer.create.CreateOfferPresenter
import life.qbic.portal.offermanager.components.offer.create.CreateOfferView
import life.qbic.portal.offermanager.components.offer.create.CreateOfferViewModel
import life.qbic.portal.offermanager.components.offer.overview.OfferOverviewController
import life.qbic.portal.offermanager.components.offer.overview.OfferOverviewModel
import life.qbic.portal.offermanager.components.offer.overview.OfferOverviewPresenter
import life.qbic.portal.offermanager.components.offer.overview.OfferOverviewView
import life.qbic.portal.offermanager.components.offer.overview.projectcreation.CreateProjectController
import life.qbic.portal.offermanager.components.offer.overview.projectcreation.CreateProjectPresenter
import life.qbic.portal.offermanager.components.offer.overview.projectcreation.CreateProjectView
import life.qbic.portal.offermanager.components.offer.overview.projectcreation.CreateProjectViewModel
import life.qbic.portal.offermanager.components.offer.update.UpdateOfferViewModel
import life.qbic.portal.offermanager.components.person.create.CreatePersonController
import life.qbic.portal.offermanager.components.person.create.CreatePersonPresenter
import life.qbic.portal.offermanager.components.person.create.CreatePersonView
import life.qbic.portal.offermanager.components.person.create.CreatePersonViewModel
import life.qbic.portal.offermanager.components.person.search.SearchPersonView
import life.qbic.portal.offermanager.components.person.search.SearchPersonViewModel
import life.qbic.portal.offermanager.components.person.update.UpdatePersonView
import life.qbic.portal.offermanager.components.person.update.UpdatePersonViewModel
import life.qbic.portal.offermanager.components.product.MaintainProductsController
import life.qbic.portal.offermanager.components.product.MaintainProductsPresenter
import life.qbic.portal.offermanager.components.product.MaintainProductsView
import life.qbic.portal.offermanager.components.product.MaintainProductsViewModel
import life.qbic.portal.offermanager.components.product.copy.CopyProductView
import life.qbic.portal.offermanager.components.product.copy.CopyProductViewModel
import life.qbic.portal.offermanager.components.product.create.CreateProductView
import life.qbic.portal.offermanager.components.product.create.CreateProductViewModel
import life.qbic.portal.offermanager.dataresources.ResourcesService
import life.qbic.portal.offermanager.dataresources.database.DatabaseSession
import life.qbic.portal.offermanager.dataresources.offers.*
import life.qbic.portal.offermanager.dataresources.persons.*
import life.qbic.portal.offermanager.dataresources.products.ProductsDbConnector
import life.qbic.portal.offermanager.dataresources.products.ProductsResourcesService
import life.qbic.portal.offermanager.dataresources.projects.ProjectDbConnector
import life.qbic.portal.offermanager.dataresources.projects.ProjectMainConnector
import life.qbic.portal.offermanager.dataresources.projects.ProjectResourceService
import life.qbic.portal.offermanager.dataresources.projects.ProjectSpaceResourceService
import life.qbic.portal.offermanager.security.Role
import life.qbic.portal.utils.ConfigurationManager
import life.qbic.portal.utils.ConfigurationManagerFactory

/**
 * Class that manages all the dependency injections and class instance creations
 *
 * This class has access to all classes that are instantiated at setup. It is responsible to construct
 * and provide every instance with it's dependencies injected. The class should only be accessed once upon
 * portlet creation and shall not be used later on in the control flow.
 *
 * @since 1.0.0
 */

@Log4j2
class DependencyManager {

    private final Role userRole
    private ConfigurationManager configurationManager

    private AppPresenter presenter
    private AppView portletView
    private AppViewModel viewModel

    private ResourcesService<Affiliation> affiliationService
    private ResourcesService<Customer> customerResourceService
    private ResourcesService<Offer> offerService
    private ResourcesService<OfferOverview> overviewService
    private ResourcesService<Person> personResourceService
    private ResourcesService<Product> productsResourcesService
    private ResourcesService<ProjectIdentifier> projectResourceService
    private ResourcesService<ProjectManager> managerResourceService
    private ResourcesService<ProjectSpace> projectSpaceResourceService

    private EventEmitter<Offer> offerUpdateEvent
    private EventEmitter<Person> personUpdateEvent
    private EventEmitter<Project> projectCreatedEvent


    // Implemented by life.qbic.portal.offermanager.dataresources.offers.OfferDbConnector
    private CreateOfferDataSource createOfferDataSource
    private FetchOfferDataSource fetchOfferDataSource
    private OfferOverviewDataSource offerOverviewDataSource
    private ProjectAssistant projectAssistant
    // Implemented by life.qbic.portal.offermanager.dataresources.persons.PersonDbConnector
    private CreateAffiliationDataSource createAffiliationDataSource
    private CreatePersonDataSource createPersonDataSource
    private ListAffiliationsDataSource listAffiliationsDataSource
    private ListPersonsDataSource listPersonsDataSource
    private SearchPersonDataSource searchPersonDataSource
    // Implemented by life.qbic.portal.offermanager.dataresources.products.ProductsDbConnector
    private ArchiveProductDataSource archiveProductDataSource
    private CopyProductDataSource copyProductDataSource
    private CreateProductDataSource createProductDataSource
    private ListProductsDataSource listProductsDataSource
    // Implemented by life.qbic.portal.offermanager.dataresources.projects.ProjectMainConnector
    private CreateProjectDataSource createProjectDataSource
    private CreateProjectSpaceDataSource createProjectSpaceDataSource
    private ListProjectSpacesDataSource listProjectSpacesDataSource
    private ListProjectsDataSource listProjectsDataSource

    /**
     * Public constructor.
     *
     * This constructor creates a dependency manager with all the instances of required classes.
     * It ensures that the {@link #portletView} field is set.
     */
    DependencyManager(Role userRole) {
        configurationManager = ConfigurationManagerFactory.getInstance()
        this.userRole = userRole
        initializeDependencies()
    }

    private void initializeDependencies() {
        // The ORDER in which the methods below are called MUST NOT CHANGE
        setupDbConnections()
        setupEventEmitter()
        setupServices()
        viewModel = new AppViewModel(this.userRole)
        presenter = new AppPresenter(this.viewModel)
        portletView = setupAppView()
    }

    protected AppView getPortletView() {
        return this.portletView
    }

    private void setupDbConnections() {
        try {
            String user = Objects.requireNonNull(configurationManager.getMysqlUser(), "Mysql user missing.")
            String password = Objects.requireNonNull(configurationManager.getMysqlPass(), "Mysql password missing.")
            String host = Objects.requireNonNull(configurationManager.getMysqlHost(), "Mysql host missing.")
            String port = Objects.requireNonNull(configurationManager.getMysqlPort(), "Mysql port missing.")
            String sqlDatabase = Objects.requireNonNull(configurationManager.getMysqlDB(), "Mysql database name missing.")

            DatabaseSession.init(user, password, host, port, sqlDatabase)
            PersonDbConnector personDbConnector = new PersonDbConnector(DatabaseSession.getInstance())
            createPersonDataSource = personDbConnector
            searchPersonDataSource = personDbConnector
            createAffiliationDataSource = personDbConnector
            listAffiliationsDataSource = personDbConnector
            listPersonsDataSource = personDbConnector

            ProductsDbConnector productsDbConnector = new ProductsDbConnector(DatabaseSession.getInstance())
            archiveProductDataSource = productsDbConnector
            createProductDataSource = productsDbConnector
            copyProductDataSource = productsDbConnector
            listProductsDataSource = productsDbConnector

            /* Currently life.qbic.portal.offermanager.dataresources.offers.OfferDbConnector
             *  cannot be decoupled by interfaces from
             *  life.qbic.portal.offermanager.dataresources.persons.PersonDbConnector nor
             *  life.qbic.portal.offermanager.dataresources.products.ProductsDbConnector
            */
            OfferDbConnector offerDbConnector = new OfferDbConnector(DatabaseSession.getInstance(),
                    personDbConnector, productsDbConnector)
            createOfferDataSource = offerDbConnector
            fetchOfferDataSource = offerDbConnector
            projectAssistant = offerDbConnector
            offerOverviewDataSource = offerDbConnector

            /* Currently life.qbic.portal.offermanager.dataresources.projects.ProjectDbConnector
             *  cannot be decoupled by interfaces from
             *  life.qbic.portal.offermanager.dataresources.persons.PersonDbConnector
             */
            ProjectDbConnector projectDbConnector = new ProjectDbConnector(DatabaseSession.getInstance(), personDbConnector)

            final String openbisURL = configurationManager.getDataSourceUrl() + "/openbis/openbis"
            OpenBisClient openbisClient = new OpenBisClient(configurationManager.getDataSourceUser(), configurationManager.getDataSourcePassword(), openbisURL)
            openbisClient.login()
            ProjectMainConnector projectMainConnector = new ProjectMainConnector(
                    projectDbConnector,
                    openbisClient,
                    projectAssistant)
            createProjectDataSource = projectMainConnector
            createProjectSpaceDataSource = projectMainConnector
            listProjectsDataSource = projectMainConnector
            listProjectSpacesDataSource = projectMainConnector

        } catch (Exception e) {
            log.error("Unexpected exception during customer database connection.", e)
            throw e
        }
    }

    /**
     * This method needs the following field to be instantiated
     * <ul>
     *     <li>{@link #listAffiliationsDataSource}</li>
     *     <li>{@link }</li>
     * </ul>
     */
    private void setupServices() {
        this.affiliationService = new AffiliationResourcesService(listAffiliationsDataSource)
        this.customerResourceService = new CustomerResourceService(listPersonsDataSource)
        this.managerResourceService = new ProjectManagerResourceService(listPersonsDataSource)
        this.offerService = new OfferResourcesService()
        this.overviewService = new OverviewService(offerOverviewDataSource, offerService, projectCreatedEvent)
        this.personResourceService = new PersonResourceService(listPersonsDataSource)
        this.productsResourcesService = new ProductsResourcesService(listProductsDataSource)
        this.projectResourceService = new ProjectResourceService(listProjectsDataSource)
        this.projectSpaceResourceService = new ProjectSpaceResourceService(listProjectSpacesDataSource)
    }

    private void setupEventEmitter() {
        this.offerUpdateEvent = new EventEmitter<Offer>()
        this.personUpdateEvent = new EventEmitter<Person>()
        this.projectCreatedEvent = new EventEmitter<Project>()
    }

    private AppView setupAppView() {
        CreateAffiliationView createAffiliationView = createCreateAffiliationView(
                viewModel, 
                affiliationService, 
                createAffiliationDataSource)
        CreateOfferView createOfferView = createCreateOfferView(
                affiliationService,
                customerResourceService,
                personResourceService,
                managerResourceService,
                productsResourcesService,
                offerService,
                viewModel,
                createAffiliationDataSource,
                createPersonDataSource,
                createOfferDataSource,
                fetchOfferDataSource)
        CreatePersonView createPersonView = createCreatePersonView(
                viewModel,
                affiliationService,
                customerResourceService,
                personResourceService,
                managerResourceService,
                createAffiliationDataSource,
                createPersonDataSource)
        CreateOfferView updateOfferView = createUpdateOfferView(
                viewModel,
                affiliationService,
                customerResourceService,
                offerService,
                personResourceService,
                managerResourceService,
                productsResourcesService,
                offerUpdateEvent,
                createAffiliationDataSource,
                createOfferDataSource,
                createPersonDataSource,
                fetchOfferDataSource)
        MaintainProductsView maintainProductsView = createMaintainProductsView(
                viewModel,
                productsResourcesService,
                archiveProductDataSource,
                createProductDataSource,
                copyProductDataSource)
        OfferOverviewView overviewView = createOfferOverviewView(
                viewModel,
                overviewService,
                projectResourceService,
                projectSpaceResourceService,
                offerUpdateEvent,
                projectCreatedEvent,
                createProjectDataSource,
                createProjectSpaceDataSource,
                fetchOfferDataSource)
        SearchAffiliationView searchAffiliationView = createSearchAffiliationView(affiliationService)
        SearchPersonView searchPersonView = createSearchPersonView(
                viewModel,
                affiliationService,
                customerResourceService,
                managerResourceService,
                personResourceService,
                createAffiliationDataSource,
                createPersonDataSource)

        AppView portletView = new AppView(this.viewModel,
                createPersonView,
                createAffiliationView,
                searchAffiliationView,
                createOfferView,
                overviewView,
                updateOfferView,
                searchPersonView,
                maintainProductsView
        )
        return portletView
    }

    /**
     *
     * @param sharedViewModel
     * @param affiliationResourcesService
     * @param dataSource
     * @return a new CreateAffiliationView using the provided resources
     */
    private static CreateAffiliationView createCreateAffiliationView(AppViewModel sharedViewModel,
                                                                     ResourcesService<Affiliation> affiliationResourcesService,
                                                                     CreateAffiliationDataSource dataSource) {
        CreateAffiliationViewModel createAffiliationViewModel = new CreateAffiliationViewModel(affiliationResourcesService)
        createAffiliationViewModel.affiliationCategories.addAll(AffiliationCategory.values().collect { it.value })

        CreateAffiliationPresenter createAffiliationPresenter = new CreateAffiliationPresenter(sharedViewModel, createAffiliationViewModel)
        CreateAffiliation createAffiliation = new CreateAffiliation(createAffiliationPresenter, dataSource)
        CreateAffiliationController createAffiliationController = new CreateAffiliationController(createAffiliation)
        return new CreateAffiliationView(sharedViewModel, createAffiliationViewModel, createAffiliationController)
    }


    /**
     *
     * @param affiliationResourcesService
     * @return a new SearchAffiliationView using the provided resources
     */
    private static SearchAffiliationView createSearchAffiliationView(ResourcesService<Affiliation> affiliationResourcesService) {
        SearchAffiliationViewModel searchAffiliationViewModel = new SearchAffiliationViewModel(affiliationResourcesService)
        SearchAffiliationView searchAffiliationView = new SearchAffiliationView(searchAffiliationViewModel)
        return searchAffiliationView
    }

    /**
     *
     * @param sharedViewModel
     * @param affiliationResourcesService
     * @param customerResourcesService
     * @param personResourcesService
     * @param projectManagerResourcesService
     * @param createAffiliationDataSource
     * @param createPersonDataSource
     * @return a new CreatePersonView instance
     */
    private static CreatePersonView createCreatePersonView(AppViewModel sharedViewModel,
                                                           ResourcesService<Affiliation> affiliationResourcesService,
                                                           ResourcesService<Customer> customerResourcesService,
                                                           ResourcesService<Person> personResourcesService,
                                                           ResourcesService<ProjectManager> projectManagerResourcesService,
                                                           CreateAffiliationDataSource createAffiliationDataSource,
                                                           CreatePersonDataSource createPersonDataSource) {

        CreatePersonViewModel createPersonViewModel = new CreatePersonViewModel(
                customerResourcesService,
                projectManagerResourcesService,
                affiliationResourcesService,
                personResourcesService
        )
        createPersonViewModel.academicTitles.addAll(AcademicTitle.values().collect { it.value })

        CreatePersonPresenter createPersonPresenter = new CreatePersonPresenter(sharedViewModel, createPersonViewModel)
        CreatePerson createPerson = new CreatePerson(createPersonPresenter, createPersonDataSource)
        CreatePersonController createPersonController = new CreatePersonController(createPerson)

        CreateAffiliationView createAffiliationView = createCreateAffiliationView(sharedViewModel, affiliationResourcesService, createAffiliationDataSource)

        CreatePersonView createPersonView = new CreatePersonView(createPersonController, sharedViewModel, createPersonViewModel, createAffiliationView)
        return createPersonView
    }

    /**
     *
     * @param affiliationResourcesService
     * @param customerResourcesService
     * @param projectManagerResourcesService
     * @param productResourcesService
     * @param offerResourcesService
     * @param sharedViewModel
     * @param createAffiliationDataSource
     * @param createOfferDataSource
     * @param fetchOfferDataSource
     * @return
     */
    private static CreateOfferView createCreateOfferView(ResourcesService<Affiliation> affiliationResourcesService,
                                                         ResourcesService<Customer> customerResourcesService,
                                                         ResourcesService<Person> personResourcesService,
                                                         ResourcesService<ProjectManager> projectManagerResourcesService,
                                                         ResourcesService<Product> productResourcesService,
                                                         ResourcesService<Offer> offerResourcesService,
                                                         AppViewModel sharedViewModel,
                                                         CreateAffiliationDataSource createAffiliationDataSource,
                                                         CreatePersonDataSource createPersonDataSource,
                                                         CreateOfferDataSource createOfferDataSource,
                                                         FetchOfferDataSource fetchOfferDataSource) {
        CreateOfferViewModel createOfferViewModel = new CreateOfferViewModel(
                customerResourcesService,
                projectManagerResourcesService,
                productResourcesService
        )

        CreateOfferPresenter createOfferPresenter = new CreateOfferPresenter(
                sharedViewModel,
                createOfferViewModel,
                offerResourcesService
        )

        CreateOffer createOffer = new CreateOffer(createOfferDataSource, createOfferPresenter)
        FetchOffer fetchOffer = new FetchOffer(fetchOfferDataSource, createOfferPresenter)
        CreateOfferController createOfferController = new CreateOfferController(createOffer, fetchOffer, createOffer)

        CreatePersonView createPersonView = createCreatePersonView(
                sharedViewModel,
                affiliationResourcesService,
                customerResourcesService,
                personResourcesService,
                projectManagerResourcesService,
                createAffiliationDataSource,
                createPersonDataSource
        )

        CreateAffiliationView createAffiliationView = createCreateAffiliationView(
                sharedViewModel,
                affiliationResourcesService,
                createAffiliationDataSource
        )

        CreateOfferView createOfferView = new CreateOfferView(
                sharedViewModel,
                createOfferViewModel,
                createOfferController,
                createPersonView,
                createAffiliationView)

        return createOfferView
    }

    /**
     *
     * @param sharedViewModel
     * @param offerOverviewResourcesService a service with offerOverviews should listen to the events emitted in the projectCreatedEvent
     * @param projectResourcesService
     * @param projectSpaceResourcesService
     * @param offerSelectedEvent
     * @param projectCreatedEvent the event emitter where a project event should be emitted to
     * @param createProjectDataSource
     * @param createProjectSpaceDataSource
     * @param fetchOfferDataSource
     * @return
     */
    private static OfferOverviewView createOfferOverviewView(AppViewModel sharedViewModel,
                                                             ResourcesService<OfferOverview> offerOverviewResourcesService,
                                                             ResourcesService<ProjectIdentifier> projectResourcesService,
                                                             ResourcesService<ProjectSpace> projectSpaceResourcesService,
                                                             EventEmitter<Offer> offerSelectedEvent,
                                                             EventEmitter<Project> projectCreatedEvent,
                                                             CreateProjectDataSource createProjectDataSource,
                                                             CreateProjectSpaceDataSource createProjectSpaceDataSource,
                                                             FetchOfferDataSource fetchOfferDataSource) {

        OfferOverviewModel offerOverviewViewModel = new OfferOverviewModel(offerOverviewResourcesService, sharedViewModel, offerSelectedEvent)
        OfferOverviewPresenter offerOverviewPresenter = new OfferOverviewPresenter(sharedViewModel, offerOverviewViewModel)
        FetchOffer fetchOffer = new FetchOffer(fetchOfferDataSource, offerOverviewPresenter)
        OfferOverviewController offerOverviewController = new OfferOverviewController(fetchOffer)

        CreateProjectViewModel createProjectViewModel = new CreateProjectViewModel(projectSpaceResourcesService, projectResourcesService)
        CreateProjectPresenter createProjectPresenter = new CreateProjectPresenter(createProjectViewModel, sharedViewModel, projectCreatedEvent)
        CreateProject createProject = new CreateProject(createProjectPresenter, createProjectDataSource, createProjectSpaceDataSource)
        CreateProjectController createProjectController = new CreateProjectController(createProject)
        CreateProjectView createProjectView = new CreateProjectView(createProjectViewModel, createProjectController)

        OfferOverviewView offerOverviewView = new OfferOverviewView(offerOverviewViewModel, offerOverviewController, createProjectView)
        return offerOverviewView
    }

    /**
     *
     * @param sharedViewModel
     * @param affiliationResourcesService
     * @param customerResourcesService
     * @param offerResourcesService
     * @param personResourcesService
     * @param projectManagerResourcesService
     * @param productResourcesService
     * @param offerUpdateEvent
     * @param createAffiliationDataSource
     * @param createOfferDataSource
     * @param createPersonDataSource
     * @param fetchOfferDataSource
     * @return
     */
    private static CreateOfferView createUpdateOfferView(AppViewModel sharedViewModel,
                                                         ResourcesService<Affiliation> affiliationResourcesService,
                                                         ResourcesService<Customer> customerResourcesService,
                                                         ResourcesService<Offer> offerResourcesService,
                                                         ResourcesService<Person> personResourcesService,
                                                         ResourcesService<ProjectManager> projectManagerResourcesService,
                                                         ResourcesService<Product> productResourcesService,
                                                         EventEmitter<Offer> offerUpdateEvent,
                                                         CreateAffiliationDataSource createAffiliationDataSource,
                                                         CreateOfferDataSource createOfferDataSource,
                                                         CreatePersonDataSource createPersonDataSource,
                                                         FetchOfferDataSource fetchOfferDataSource) {
        UpdateOfferViewModel updateOfferViewModel = new UpdateOfferViewModel(
                customerResourcesService,
                projectManagerResourcesService,
                productResourcesService,
                offerUpdateEvent)
        CreateOfferPresenter updateOfferPresenter = new CreateOfferPresenter(sharedViewModel, updateOfferViewModel, offerResourcesService)
        CreateOffer updateOffer = new CreateOffer(createOfferDataSource, updateOfferPresenter)

        FetchOffer fetchOffer = new FetchOffer(fetchOfferDataSource, updateOfferPresenter)
        CreatePersonView createPersonView = createCreatePersonView(
                sharedViewModel,
                affiliationResourcesService,
                customerResourcesService,
                personResourcesService,
                projectManagerResourcesService,
                createAffiliationDataSource,
                createPersonDataSource)
        CreateAffiliationView createAffiliationView = createCreateAffiliationView(
                sharedViewModel,
                affiliationResourcesService,
                createAffiliationDataSource)

        CreateOfferController updateOfferController = new CreateOfferController(updateOffer, fetchOffer, updateOffer)
        CreateOfferView updateOfferView = new CreateOfferView(
                sharedViewModel,
                updateOfferViewModel,
                updateOfferController,
                createPersonView,
                createAffiliationView)

        return updateOfferView
    }

    /**
     *
     * @param sharedViewModel
     * @param affiliationResourcesService
     * @param customerResourcesService
     * @param projectManagerResourcesService
     * @param personResourcesService
     * @param createAffiliationDataSource
     * @param createPersonDataSource
     * @return
     */
    private static SearchPersonView createSearchPersonView(AppViewModel sharedViewModel,
                                                           ResourcesService<Affiliation> affiliationResourcesService,
                                                           ResourcesService<Customer> customerResourcesService,
                                                           ResourcesService<ProjectManager> projectManagerResourcesService,
                                                           ResourcesService<Person> personResourcesService,
                                                           CreateAffiliationDataSource createAffiliationDataSource,
                                                           CreatePersonDataSource createPersonDataSource) {
        // this event emitter is used to communicate between the search person view and the
        // update person view
        EventEmitter<Person> personSelectEvent = new EventEmitter<Person>()

        SearchPersonViewModel searchPersonViewModel = new SearchPersonViewModel(personResourcesService, personSelectEvent)
        UpdatePersonView updatePersonView = createUpdatePersonView(
                sharedViewModel,
                affiliationResourcesService,
                customerResourcesService,
                projectManagerResourcesService,
                personResourcesService,
                personSelectEvent,
                createAffiliationDataSource,
                createPersonDataSource
        )
        SearchPersonView searchPersonView = new SearchPersonView(searchPersonViewModel, updatePersonView)
        return searchPersonView
    }

    /**
     *
     * @param sharedViewModel
     * @param affiliationResourcesService
     * @param customerResourcesService
     * @param projectManagerResourcesService
     * @param personResourcesService
     * @param personUpdateEvent
     * @param createAffiliationDataSource
     * @param createPersonDataSource
     * @return
     */
    private static UpdatePersonView createUpdatePersonView(AppViewModel sharedViewModel,
                                                           ResourcesService<Affiliation> affiliationResourcesService,
                                                           ResourcesService<Customer> customerResourcesService,
                                                           ResourcesService<ProjectManager> projectManagerResourcesService,
                                                           ResourcesService<Person> personResourcesService,
                                                           EventEmitter<Person> personUpdateEvent,
                                                           CreateAffiliationDataSource createAffiliationDataSource,
                                                           CreatePersonDataSource createPersonDataSource) {

        CreateAffiliationView createAffiliationView = createCreateAffiliationView(
                sharedViewModel,
                affiliationResourcesService,
                createAffiliationDataSource
        )
        UpdatePersonViewModel updatePersonViewModel = new UpdatePersonViewModel(
                customerResourcesService,
                projectManagerResourcesService,
                affiliationResourcesService,
                personUpdateEvent,
                personResourcesService
        )
        updatePersonViewModel.academicTitles.addAll(AcademicTitle.values().collect { it.value })

        CreatePersonPresenter updatePersonPresenter = new CreatePersonPresenter(sharedViewModel, updatePersonViewModel)
        CreatePerson updatePerson = new CreatePerson(updatePersonPresenter, createPersonDataSource)
        CreatePersonController updatePersonController = new CreatePersonController(updatePerson)

        UpdatePersonView updatePersonView = new UpdatePersonView(
                updatePersonController,
                sharedViewModel,
                updatePersonViewModel,
                createAffiliationView
        )

        return updatePersonView
    }

    /**
     *
     * @param sharedViewModel
     * @param productResourcesService
     * @param archiveProductDataSource
     * @param createProductDataSource
     * @param copyProductDataSource
     * @return
     */
    private static MaintainProductsView createMaintainProductsView(AppViewModel sharedViewModel,
                                                                   ResourcesService<Product> productResourcesService,
                                                                   ArchiveProductDataSource archiveProductDataSource,
                                                                   CreateProductDataSource createProductDataSource,
                                                                   CopyProductDataSource copyProductDataSource) {
        // used to communicate selection events from the MaintainProducts to CopyProduct
        EventEmitter<Product> productSelectEvent = new EventEmitter<Product>()

        MaintainProductsViewModel maintainProductsViewModel = new MaintainProductsViewModel(productResourcesService, productSelectEvent)
        MaintainProductsPresenter maintainProductsPresenter = new MaintainProductsPresenter(maintainProductsViewModel, sharedViewModel)

        ArchiveProduct archiveProduct = new ArchiveProduct(archiveProductDataSource, maintainProductsPresenter)
        CreateProduct createProduct = new CreateProduct(createProductDataSource, maintainProductsPresenter)
        CopyProduct copyProduct = new CopyProduct(copyProductDataSource, maintainProductsPresenter, createProductDataSource)

        MaintainProductsController maintainProductsController = new MaintainProductsController(createProduct, archiveProduct, copyProduct)

        CreateProductViewModel createProductViewModel = new CreateProductViewModel()
        CreateProductView createProductView = new CreateProductView(createProductViewModel, maintainProductsController)
        CopyProductViewModel copyProductViewModel = new CopyProductViewModel(productSelectEvent)
        CopyProductView copyProductView = new CopyProductView(copyProductViewModel, maintainProductsController)


        MaintainProductsView maintainProductsView = new MaintainProductsView(
                maintainProductsViewModel,
                createProductView,
                copyProductView,
                maintainProductsController)

        return maintainProductsView
    }

}
