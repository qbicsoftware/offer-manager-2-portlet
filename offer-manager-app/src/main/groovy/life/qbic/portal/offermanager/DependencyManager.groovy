package life.qbic.portal.offermanager

import groovy.util.logging.Log4j2
import life.qbic.business.offers.content.CreateOfferContent
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
import life.qbic.portal.offermanager.dataresources.database.DatabaseSessionV2
import life.qbic.portal.offermanager.dataresources.database.SessionProvider
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
import org.hibernate.Session
import org.hibernate.query.Query

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

    /**
     * Projects are emitted after creation and consumed by a service
     */
    private EventEmitter<Project> projectCreatedEvent

    // Hibernate session management
    private SessionProvider sessionProvider

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
        // the database connections are needed first
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

            // Setup Hibernate session
            String dbFullUrl = "jdbc:mysql://" + host + ":" + port + "/" + sqlDatabase
            sessionProvider = new DatabaseSessionV2(dbFullUrl, user, password, "com.mysql.cj.jdbc.Driver", "org.hibernate.dialect.MariaDBDialect")
            trySessionProvider(sessionProvider)

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
     *     <li>{@link #listPersonsDataSource}</li>
     *     <li>{@link #listProductsDataSource}</li>
     *     <li>{@link #listProjectSpacesDataSource}</li>
     *     <li>{@link #listProjectsDataSource}</li>
     *     <li>{@link #offerOverviewDataSource}</li>
     *     <li>{@link #projectCreatedEvent}</li>
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
        this.projectCreatedEvent = new EventEmitter<Project>()
    }

    private AppView setupAppView() {

        CreateAffiliationView createAffiliationView = createCreateAffiliationView()
        CreateOfferView createOfferView = createCreateOfferView()
        CreatePersonView createPersonView = createCreatePersonView()
        MaintainProductsView maintainProductsView = createMaintainProductsView()
        SearchAffiliationView searchAffiliationView = createSearchAffiliationView()
        SearchPersonView searchPersonView = createSearchPersonView()

        // Used to emit offers that shall be updated
        EventEmitter<Offer> offerUpdateEvent = new EventEmitter<Offer>()

        CreateOfferView updateOfferView = createUpdateOfferView(offerUpdateEvent)
        OfferOverviewView overviewView = createOfferOverviewView(offerUpdateEvent, projectCreatedEvent)


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
     * Creates a new CreateAffiliationView using
     * <ul>
     *     <li>{@link #affiliationService}</li>
     *     <li>{@link #createAffiliationDataSource}</li>
     *     <li>{@link #viewModel}</li>
     * </ul>
     * @return a new CreateAffiliationView
     */
    private CreateAffiliationView createCreateAffiliationView() {
        AppViewModel sharedViewModel = this.viewModel
        ResourcesService<Affiliation> affiliationResourcesService = this.affiliationService
        CreateAffiliationDataSource dataSource = this.createAffiliationDataSource

        CreateAffiliationViewModel createAffiliationViewModel = new CreateAffiliationViewModel(affiliationResourcesService)
        createAffiliationViewModel.affiliationCategories.addAll(AffiliationCategory.values().collect { it.value })

        CreateAffiliationPresenter createAffiliationPresenter = new CreateAffiliationPresenter(sharedViewModel, createAffiliationViewModel)
        CreateAffiliation createAffiliation = new CreateAffiliation(createAffiliationPresenter, dataSource)
        CreateAffiliationController createAffiliationController = new CreateAffiliationController(createAffiliation)
        return new CreateAffiliationView(sharedViewModel, createAffiliationViewModel, createAffiliationController)
    }


    /**
     * Creates a new SearchAffiliationView using
     * <ul>
     *     <li>{@link #affiliationService}</li>
     * </ul>
     * @return a new SearchAffiliationView
     */
    private SearchAffiliationView createSearchAffiliationView() {
        ResourcesService<Affiliation> affiliationResourcesService = this.affiliationService
        SearchAffiliationViewModel searchAffiliationViewModel = new SearchAffiliationViewModel(affiliationResourcesService)
        SearchAffiliationView searchAffiliationView = new SearchAffiliationView(searchAffiliationViewModel)
        return searchAffiliationView
    }

    /**
     * Creates a new CreatePersonView using fields
     * <ul>
     *     <li>{@link #affiliationService}</li>
     *     <li>{@link #createPersonDataSource}</li>
     *     <li>{@link #customerResourceService}</li>
     *     <li>{@link #managerResourceService}</li>
     *     <li>{@link #personResourceService}</li>
     *     <li>{@link #viewModel}</li>
     * </ul>
     * and additional fields from
     * <ul>
     *     <li>{@link #createCreateAffiliationView()}</li>
     * </ul>
     * @return
     */
    private CreatePersonView createCreatePersonView() {

        AppViewModel sharedViewModel = this.viewModel
        ResourcesService<Affiliation> affiliationResourcesService = this.affiliationService
        ResourcesService<Customer> customerResourcesService = this.customerResourceService
        ResourcesService<Person> personResourcesService = this.personResourceService
        ResourcesService<ProjectManager> projectManagerResourcesService = this.managerResourceService
        CreatePersonDataSource createPersonDataSource = this.createPersonDataSource

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

        CreateAffiliationView createAffiliationView = createCreateAffiliationView()

        CreatePersonView createPersonView = new CreatePersonView(createPersonController, sharedViewModel, createPersonViewModel, createAffiliationView)
        return createPersonView
    }


    /**
     * Creates a new CreateOfferView using the following fields
     * <ul>
     *     <li>{@link #createOfferDataSource}</li>
     *     <li>{@link #customerResourceService}</li>
     *     <li>{@link #fetchOfferDataSource}</li>
     *     <li>{@link #managerResourceService}</li>
     *     <li>{@link #offerService}</li>
     *     <li>{@link #productsResourcesService}</li>
     *     <li>{@link #viewModel}</li>
     * </ul>
     * and methods
     * <ul>
     *     <li>{@link #createCreateAffiliationView()}</li>
     *     <li>{@link #createCreatePersonView()}</li>
     * </ul>
     *
     * @return a new CreateOfferView
     */
    private CreateOfferView createCreateOfferView() {

        AppViewModel sharedViewModel = this.viewModel
        CreateOfferDataSource createOfferDataSource = this.createOfferDataSource
        FetchOfferDataSource fetchOfferDataSource = this.fetchOfferDataSource
        ResourcesService<Customer> customerResourcesService = this.customerResourceService
        ResourcesService<Offer> offerResourcesService = this.offerService
        ResourcesService<Product> productResourcesService = this.productsResourcesService
        ResourcesService<ProjectManager> projectManagerResourcesService = this.managerResourceService

        EventEmitter<Person> personUpdateEvent = new EventEmitter<>()

        CreateOfferViewModel createOfferViewModel = new CreateOfferViewModel(
                customerResourcesService,
                projectManagerResourcesService,
                productResourcesService,
                personUpdateEvent
        )

        CreateOfferPresenter createOfferPresenter = new CreateOfferPresenter(
                sharedViewModel,
                createOfferViewModel,
                offerResourcesService
        )

        CreateOffer createOffer = new CreateOffer(createOfferDataSource, createOfferPresenter)
        FetchOffer fetchOffer = new FetchOffer(fetchOfferDataSource, createOfferPresenter)
        CreateOfferController createOfferController = new CreateOfferController(createOffer, fetchOffer, createOffer)

        CreatePersonView createPersonView = createCreatePersonView()
        UpdatePersonView updatePersonView = createUpdatePersonView(personUpdateEvent)

        CreateAffiliationView createAffiliationView = createCreateAffiliationView()

        CreateOfferView createOfferView = new CreateOfferView(
                sharedViewModel,
                createOfferViewModel,
                createOfferController,
                createPersonView,
                createAffiliationView,
                updatePersonView
        )

        return createOfferView
    }

    /**
     * Creates a new OfferOverviewView using fields
     * <ul>
     *     <li>{@link #createProjectDataSource}</li>
     *     <li>{@link #createProjectSpaceDataSource}</li>
     *     <li>{@link #fetchOfferDataSource}</li>
     *     <li>{@link #overviewService}</li>
     *     <li>{@link #projectResourceService}</li>
     *     <li>{@link #projectSpaceResourceService}</li>
     *     <li>{@link #viewModel}</li>
     * </ul>
     * @param offerSelectedEvent used to broadcast an offer being selected for update
     * @param projectCreatedEvent emits projects that are created successfully
     * @return a new OfferOverviewView
     */
    private OfferOverviewView createOfferOverviewView(EventEmitter<Offer> offerSelectedEvent,
                                                      EventEmitter<Project> projectCreatedEvent) {

        AppViewModel sharedViewModel = this.viewModel
        CreateProjectDataSource createProjectDataSource = this.createProjectDataSource
        CreateProjectSpaceDataSource createProjectSpaceDataSource = this.createProjectSpaceDataSource
        FetchOfferDataSource fetchOfferDataSource = this.fetchOfferDataSource
        ResourcesService<OfferOverview> offerOverviewResourcesService = this.overviewService
        ResourcesService<ProjectIdentifier> projectResourcesService = this.projectResourceService
        ResourcesService<ProjectSpace> projectSpaceResourcesService = this.projectSpaceResourceService

        OfferOverviewModel offerOverviewViewModel = new OfferOverviewModel(offerOverviewResourcesService, sharedViewModel, offerSelectedEvent)
        OfferOverviewPresenter offerOverviewPresenter = new OfferOverviewPresenter(sharedViewModel, offerOverviewViewModel)
        FetchOffer fetchOffer = new FetchOffer(fetchOfferDataSource, offerOverviewPresenter)
        CreateOfferContent createOfferContent = new CreateOfferContent(offerOverviewPresenter, fetchOfferDataSource)
        OfferOverviewController offerOverviewController = new OfferOverviewController(fetchOffer, createOfferContent)

        CreateProjectViewModel createProjectViewModel = new CreateProjectViewModel(projectSpaceResourcesService, projectResourcesService)
        CreateProjectPresenter createProjectPresenter = new CreateProjectPresenter(createProjectViewModel, sharedViewModel, projectCreatedEvent)
        CreateProject createProject = new CreateProject(createProjectPresenter, createProjectDataSource, createProjectSpaceDataSource)
        CreateProjectController createProjectController = new CreateProjectController(createProject)
        CreateProjectView createProjectView = new CreateProjectView(createProjectViewModel, createProjectController)

        OfferOverviewView offerOverviewView = new OfferOverviewView(offerOverviewViewModel, offerOverviewController, createProjectView)
        return offerOverviewView
    }

    /**
     * Creates a new update offer view using fields
     *     <li>{@link #createOfferDataSource}</li>
     *     <li>{@link #customerResourceService}</li>
     *     <li>{@link #fetchOfferDataSource}</li>
     *     <li>{@link #managerResourceService}</li>
     *     <li>{@link #offerService}</li>
     *     <li>{@link #productsResourcesService}</li>
     *     <li>{@link #viewModel}</li>
     * </ul>
     * and methods
     * <ul>
     *     <li>{@link #createCreatePersonView()}</li>
     *     <li>{@link #createCreateAffiliationView()}</li>
     * </ul>
     * @param offerUpdateEvent emits the offer to be updated
     * @return a new CreateOfferView to be used as update offer view
     */
    private CreateOfferView createUpdateOfferView(EventEmitter<Offer> offerUpdateEvent) {

        AppViewModel sharedViewModel = this.viewModel
        ResourcesService<Customer> customerResourcesService = this.customerResourceService
        ResourcesService<Offer> offerResourcesService = this.offerService
        ResourcesService<ProjectManager> projectManagerResourcesService = this.managerResourceService
        ResourcesService<Product> productResourcesService = this.productsResourcesService
        CreateOfferDataSource createOfferDataSource = this.createOfferDataSource
        FetchOfferDataSource fetchOfferDataSource = this.fetchOfferDataSource

        EventEmitter<Person> updatePersonEvent = new EventEmitter<Person>()

        UpdateOfferViewModel updateOfferViewModel = new UpdateOfferViewModel(
                customerResourcesService,
                projectManagerResourcesService,
                productResourcesService,
                updatePersonEvent,
                offerUpdateEvent
        )
        CreateOfferPresenter updateOfferPresenter = new CreateOfferPresenter(sharedViewModel, updateOfferViewModel, offerResourcesService)
        CreateOffer updateOffer = new CreateOffer(createOfferDataSource, updateOfferPresenter)

        FetchOffer fetchOffer = new FetchOffer(fetchOfferDataSource, updateOfferPresenter)
        CreatePersonView createPersonView = createCreatePersonView()
        UpdatePersonView updatePersonView = createUpdatePersonView(updatePersonEvent)
        CreateAffiliationView createAffiliationView = createCreateAffiliationView()

        CreateOfferController updateOfferController = new CreateOfferController(updateOffer, fetchOffer, updateOffer)
        CreateOfferView updateOfferView = new CreateOfferView(
                sharedViewModel,
                updateOfferViewModel,
                updateOfferController,
                createPersonView,
                createAffiliationView,
                updatePersonView)

        return updateOfferView
    }

    /**
     * Creates a new SearchPersonView using fields
     * <ul>
     *     <li>{@link #personResourceService}</li>
     * </ul>
     * and methods
     * <ul>
     *     <li>{@link #createUpdatePersonView()}</li>
     * </ul>
     * @return
     */
    private SearchPersonView createSearchPersonView() {

        ResourcesService<Person> personResourcesService = this.personResourceService

        // this event emitter is used to communicate between the search person view and the
        // update person view. The SearchPersonView emits persons to be updated. They are consumed
        // by the UpdatePersonView
        EventEmitter<Person> personSelectEvent = new EventEmitter<Person>()

        SearchPersonViewModel searchPersonViewModel = new SearchPersonViewModel(personResourcesService, personSelectEvent)
        UpdatePersonView updatePersonView = createUpdatePersonView(personSelectEvent)
        SearchPersonView searchPersonView = new SearchPersonView(searchPersonViewModel, updatePersonView)
        return searchPersonView
    }

    /**
     * Creates a new update person view using fields
     * <ul>
     *     <li>{@link #affiliationService}</li>
     *     <li>{@link #createPersonDataSource}</li>
     *     <li>{@link #customerResourceService}</li>
     *     <li>{@link #managerResourceService}</li>
     *     <li>{@link #personResourceService}</li>
     *     <li>{@link #viewModel}</li>
     * </ul>
     * and methods
     * <ul>
     *     <li>{@link #createCreateAffiliationView()}</li>
     * </ul>
     * @param personSelectEvent emits the person to be updated
     * @return a new UpdatePersonView
     */
    private UpdatePersonView createUpdatePersonView(EventEmitter<Person> personSelectEvent) {

        AppViewModel sharedViewModel = this.viewModel
        ResourcesService<Affiliation> affiliationResourcesService = this.affiliationService
        ResourcesService<Customer> customerResourcesService = this.customerResourceService
        ResourcesService<ProjectManager> projectManagerResourcesService = this.managerResourceService
        ResourcesService<Person> personResourcesService = this.personResourceService
        CreatePersonDataSource createPersonDataSource = this.createPersonDataSource

        CreateAffiliationView createAffiliationView = createCreateAffiliationView()
        UpdatePersonViewModel updatePersonViewModel = new UpdatePersonViewModel(
                customerResourcesService,
                projectManagerResourcesService,
                affiliationResourcesService,
                personSelectEvent,
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
     * Creates a new MaintainProductsView using fields
     * <ul>
     *     <li>{@link #archiveProductDataSource}</li>
     *     <li>{@link #createProductDataSource}</li>
     *     <li>{@link #productsResourcesService}</li>
     *     <li>{@link #viewModel}</li>
     * </ul>
     * @return
     */
    private MaintainProductsView createMaintainProductsView() {

        AppViewModel sharedViewModel = this.viewModel
        ResourcesService<Product> productResourcesService = this.productsResourcesService
        ArchiveProductDataSource archiveProductDataSource = this.archiveProductDataSource
        CreateProductDataSource createProductDataSource = this.createProductDataSource

        // used to communicate products to be copied from the MaintainProducts
        EventEmitter<Product> productSelectEvent = new EventEmitter<Product>()

        MaintainProductsViewModel maintainProductsViewModel = new MaintainProductsViewModel(productResourcesService, productSelectEvent)
        MaintainProductsPresenter maintainProductsPresenter = new MaintainProductsPresenter(maintainProductsViewModel, sharedViewModel)

        ArchiveProduct archiveProduct = new ArchiveProduct(archiveProductDataSource, maintainProductsPresenter)
        CreateProduct createProduct = new CreateProduct(createProductDataSource, maintainProductsPresenter)

        MaintainProductsController maintainProductsController = new MaintainProductsController(createProduct, archiveProduct)

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

    private static void trySessionProvider(DatabaseSessionV2 databaseSessionV2) {
        try(Session session = databaseSessionV2.getCurrentSession()) {
            session.beginTransaction()
            Query<Person> query = session.createQuery("FROM Person ")
            // Print entities
            List<life.qbic.business.persons.Person> persons = query.list() as List<life.qbic.business.persons.Person>
            for (life.qbic.business.persons.Person person : persons) {
                log.info(person)
            }
        }
    }
}
