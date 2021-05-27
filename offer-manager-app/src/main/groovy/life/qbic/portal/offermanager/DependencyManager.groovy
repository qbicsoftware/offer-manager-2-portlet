package life.qbic.portal.offermanager

import groovy.util.logging.Log4j2
import life.qbic.business.offers.create.CreateOffer
import life.qbic.business.offers.create.CreateOfferDataSource
import life.qbic.business.offers.fetch.FetchOffer
import life.qbic.business.offers.fetch.FetchOfferDataSource
import life.qbic.business.persons.affiliation.create.CreateAffiliation
import life.qbic.business.persons.affiliation.create.CreateAffiliationDataSource
import life.qbic.business.persons.create.CreatePerson
import life.qbic.business.persons.create.CreatePersonDataSource
import life.qbic.business.products.archive.ArchiveProduct
import life.qbic.business.products.copy.CopyProduct
import life.qbic.business.products.create.CreateProduct
import life.qbic.business.projects.create.CreateProject
import life.qbic.business.projects.create.CreateProjectDataSource
import life.qbic.business.projects.spaces.CreateProjectSpaceDataSource
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
import life.qbic.portal.offermanager.dataresources.offers.OfferDbConnector
import life.qbic.portal.offermanager.dataresources.offers.OfferOverview
import life.qbic.portal.offermanager.dataresources.offers.OfferResourcesService
import life.qbic.portal.offermanager.dataresources.offers.OverviewService
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
 * @since: 1.0.0
 */

@Log4j2
class DependencyManager {

    private final Role userRole

    private AppViewModel viewModel
    private UpdatePersonViewModel updatePersonViewModel
    private CreateOfferViewModel updateOfferViewModel
    private SearchPersonViewModel searchPersonViewModel
    private MaintainProductsViewModel maintainProductsViewModel
    private MaintainProductsViewModel maintainProductsViewModelArchive
    private CreateProductViewModel createProductViewModel
    private CopyProductViewModel copyProductViewModel

    private AppPresenter presenter
    private CreatePersonPresenter updateCustomerPresenter
    private CreateOfferPresenter updateOfferPresenter
    private MaintainProductsPresenter createProductPresenter
    private MaintainProductsPresenter archiveProductPresenter
    private MaintainProductsPresenter copyProductPresenter

    private PersonDbConnector customerDbConnector
    private OfferDbConnector offerDbConnector
    private ProductsDbConnector productsDbConnector
    private ProjectMainConnector projectMainConnector
    private ProjectDbConnector projectDbConnector
    private OpenBisClient openbisClient

    private CreatePerson updateCustomer
    private CreateOffer updateOffer
    private FetchOffer fetchOfferUpdateOffer
    private CreateProduct createProduct
    private ArchiveProduct archiveProduct
    private CopyProduct copyProduct

    private CreatePersonController updateCustomerController
    private CreateOfferController updateOfferController
    private MaintainProductsController maintainProductController

    private CreatePersonView updatePersonView
    private AppView portletView
    private ConfigurationManager configurationManager

    private OverviewService overviewService
    private EventEmitter<Offer> offerUpdateEvent
    private CustomerResourceService customerResourceService
    private AffiliationResourcesService affiliationService
    private OfferResourcesService offerService
    private ProductsResourcesService productsResourcesService
    private ProjectManagerResourceService managerResourceService
    private PersonResourceService personResourceService
    private ProjectSpaceResourceService projectSpaceResourceService
    private ProjectResourceService projectResourceService
    private EventEmitter<Person> personUpdateEvent
    private EventEmitter<Project> projectCreatedEvent
    private EventEmitter<Product> productUpdateEvent
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
        setupServices()
        setupEventEmitter()
        setupViewModels()
        setupPresenters()
        setupUseCaseInteractors()
        setupControllers()
        setupViews()
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
            customerDbConnector = new PersonDbConnector(DatabaseSession.getInstance())
            productsDbConnector = new ProductsDbConnector(DatabaseSession.getInstance())
            offerDbConnector = new OfferDbConnector(DatabaseSession.getInstance(),
                    customerDbConnector, productsDbConnector)
            projectDbConnector = new ProjectDbConnector(DatabaseSession.getInstance(), customerDbConnector)


            final String openbisURL = configurationManager.getDataSourceUrl() + "/openbis/openbis"
            openbisClient = new OpenBisClient(configurationManager.getDataSourceUser(), configurationManager.getDataSourcePassword(), openbisURL)
            openbisClient.login()

            projectMainConnector = new ProjectMainConnector(
                    projectDbConnector,
                    openbisClient,
                    offerDbConnector)

        } catch (Exception e) {
            log.error("Unexpected exception during customer database connection.", e)
            throw e
        }
    }

    private void setupServices() {
        this.offerService = new OfferResourcesService()
        this.projectCreatedEvent = new EventEmitter<>()
        this.overviewService = new OverviewService(offerDbConnector, offerService, projectCreatedEvent)
        this.managerResourceService = new ProjectManagerResourceService(customerDbConnector)
        this.productsResourcesService = new ProductsResourcesService(productsDbConnector)
        this.affiliationService = new AffiliationResourcesService(customerDbConnector)
        this.customerResourceService = new CustomerResourceService(customerDbConnector)
        this.personResourceService = new PersonResourceService(customerDbConnector)
        this.projectSpaceResourceService = new ProjectSpaceResourceService(projectMainConnector)
        this.projectResourceService = new ProjectResourceService(projectMainConnector)
    }

    private void setupEventEmitter() {
        this.offerUpdateEvent = new EventEmitter<Offer>()
        this.personUpdateEvent = new EventEmitter<Person>()
        this.productUpdateEvent = new EventEmitter<Product>()
    }

    private void setupViewModels() {
        // setup view models
        try {
            this.viewModel = new AppViewModel(affiliationService, this.userRole)
        } catch (Exception e) {
            log.error("Unexpected exception during ${AppViewModel.getSimpleName()} view model setup.", e)
            throw e
        }

        try {
            this.updatePersonViewModel = new UpdatePersonViewModel(
                    customerResourceService,
                    managerResourceService,
                    affiliationService,
                    personUpdateEvent,
                    personResourceService)
            updatePersonViewModel.academicTitles.addAll(AcademicTitle.values().collect { it.value })

        } catch (Exception e) {
            log.error("Unexpected exception during ${UpdatePersonViewModel.getSimpleName()} view model setup.", e)
            throw e
        }
        try {
            this.updateOfferViewModel = new UpdateOfferViewModel(
                    customerResourceService,
                    managerResourceService,
                    productsResourcesService,
                    offerUpdateEvent)
        } catch (Exception e) {
            log.error("Unexpected exception during ${CreateOfferViewModel.getSimpleName()} view model setup.", e)
            throw e
        }

        try {
            this.searchPersonViewModel = new SearchPersonViewModel(personResourceService, personUpdateEvent)
        } catch (Exception e) {
            log.error("Unexpected exception during ${SearchPersonViewModel.getSimpleName()} view model setup.", e)
        }

        try {
            this.maintainProductsViewModel = new MaintainProductsViewModel(productsResourcesService, productUpdateEvent)
        } catch (Exception e) {
            log.error("Unexpected exception during ${MaintainProductsViewModel.getSimpleName()} view model setup.", e)
        }

        try {
            this.maintainProductsViewModelArchive = new MaintainProductsViewModel(productsResourcesService, productUpdateEvent)
        } catch (Exception e) {
            log.error("Unexpected exception during ${MaintainProductsViewModel.getSimpleName()} view model setup.", e)
        }

        try {
            this.createProductViewModel = new CreateProductViewModel()
        } catch (Exception e) {
            log.error("Unexpected exception during ${CreateProductViewModel.getSimpleName()} view model setup.", e)
        }

        try {
            this.copyProductViewModel = new CopyProductViewModel(productUpdateEvent)
        } catch (Exception e) {
            log.error("Unexpected exception during ${CopyProductViewModel.getSimpleName()} view model setup.", e)
        }
    }

    private void setupPresenters() {
        try {
            this.presenter = new AppPresenter(this.viewModel)
        } catch (Exception e) {
            log.error("Unexpected exception during ${AppPresenter.getSimpleName()} setup.", e)
            throw e
        }

        try {
            this.updateCustomerPresenter = new CreatePersonPresenter(this.viewModel, this.updatePersonViewModel)
        } catch (Exception e) {
            log.error("Unexpected exception during ${CreatePersonPresenter.getSimpleName()} setup.", e)
            throw e
        }

        try {
            this.updateOfferPresenter = new CreateOfferPresenter(this.viewModel,
                    this.updateOfferViewModel, this.offerService)
        } catch (Exception e) {
            log.error("Unexpected exception during ${CreateOfferViewModel.getSimpleName()} setup", e)
        }

        try {
            this.createProductPresenter = new MaintainProductsPresenter(this.maintainProductsViewModel, this.viewModel)
        } catch (Exception e) {
            log.error("Unexpected exception during ${MaintainProductsPresenter.getSimpleName()} setup", e)
        }
        try {
            this.archiveProductPresenter = new MaintainProductsPresenter(this.maintainProductsViewModelArchive, this.viewModel)
        } catch (Exception e) {
            log.error("Unexpected exception during ${MaintainProductsPresenter.getSimpleName()} setup", e)
        }
        try {
            this.copyProductPresenter = new MaintainProductsPresenter(this.maintainProductsViewModel, this.viewModel)
        } catch (Exception e) {
            log.error("Unexpected exception during ${MaintainProductsPresenter.getSimpleName()} setup", e)
        }
    }

    private void setupUseCaseInteractors() {

        this.updateOffer = new CreateOffer(offerDbConnector, updateOfferPresenter)
        this.updateCustomer = new CreatePerson(updateCustomerPresenter, customerDbConnector)

        this.fetchOfferUpdateOffer = new FetchOffer(offerDbConnector, updateOfferPresenter)

        this.createProduct = new CreateProduct(productsDbConnector, createProductPresenter)
        this.archiveProduct = new ArchiveProduct(productsDbConnector, archiveProductPresenter)
        this.copyProduct = new CopyProduct(productsDbConnector, copyProductPresenter, productsDbConnector)
    }

    private void setupControllers() {
        try {
            this.updateCustomerController = new CreatePersonController(this.updateCustomer)
        } catch (Exception e) {
            log.error("Unexpected exception during ${CreatePersonController.getSimpleName()} setup.", e)
            throw e
        }
        try {
            this.updateOfferController = new CreateOfferController(this.updateOffer, this.fetchOfferUpdateOffer, this.updateOffer)
        } catch (Exception e) {
            log.error("Unexpected exception during ${CreateOfferController.getSimpleName()} setup", e)
        }

        try {
            this.maintainProductController = new MaintainProductsController(this.createProduct, this.archiveProduct, this.copyProduct)
        } catch (Exception e) {
            log.error("Unexpected exception during ${MaintainProductsController.getSimpleName()} setup", e)
        }
    }

    private void setupViews() {

        try {
            CreateAffiliationView createAffiliationView = createCreateAffiliationView(viewModel, affiliationService, customerDbConnector)
            this.updatePersonView = new UpdatePersonView(this.updateCustomerController, this.viewModel, this.updatePersonViewModel, createAffiliationView)
        } catch (Exception e) {
            log.error("Could not create ${UpdatePersonView.getSimpleName()} view.", e)
            throw e
        }

        CreateOfferView updateOfferView
        try {
            CreateAffiliationView createAffiliationView = createCreateAffiliationView(viewModel, affiliationService, customerDbConnector)
            CreatePersonView createCustomerView = createCreatePersonView(viewModel,
                    affiliationService,
                    customerResourceService,
                    personResourceService,
                    managerResourceService,
                    customerDbConnector,
                    customerDbConnector)
            updateOfferView = new CreateOfferView(
                    this.viewModel,
                    this.updateOfferViewModel,
                    this.updateOfferController,
                    createCustomerView,
                    createAffiliationView,
                    this.offerService)
        } catch (Exception e) {
            log.error("Could not create ${CreateOfferView.getSimpleName()} view.", e)
            throw e
        }

        SearchPersonView searchPersonView
        try {
            searchPersonView = new SearchPersonView(searchPersonViewModel, updatePersonView)
        } catch (Exception e) {
            log.error("Could not create ${SearchPersonView.getSimpleName()} view.", e)
            throw e
        }

        CreateProductView createProductView
        try {
            createProductView = new CreateProductView(createProductViewModel, maintainProductController)
        } catch (Exception e) {
            log.error("Could not create ${CreateProductView.getSimpleName()} view.", e)
            throw e
        }

        CopyProductView copyProductView
        try {
            copyProductView = new CopyProductView(copyProductViewModel, maintainProductController)
        } catch (Exception e) {
            log.error("Could not create ${CopyProductView.getSimpleName()} view.", e)
            throw e
        }

        MaintainProductsView maintainProductsView
        try {
            maintainProductsView = new MaintainProductsView(maintainProductsViewModel, createProductView, copyProductView, maintainProductController)
        } catch (Exception e) {
            log.error("Could not create ${MaintainProductsView.getSimpleName()} view.", e)
            throw e
        }

        AppView portletView
        try {
            CreatePersonView createPersonView = createCreatePersonView(viewModel,
                    affiliationService,
                    customerResourceService,
                    personResourceService,
                    managerResourceService,
                    customerDbConnector,
                    customerDbConnector)
            CreateAffiliationView createAffiliationView = createCreateAffiliationView(viewModel, affiliationService, customerDbConnector)
            SearchAffiliationView searchAffiliationView = createSearchAffiliationView(affiliationService)
            CreateOfferView createOfferView = createCreateOfferView(
                    affiliationService,
                    customerResourceService,
                    personResourceService,
                    managerResourceService,
                    productsResourcesService,
                    offerService,
                    viewModel,
                    customerDbConnector,
                    customerDbConnector,
                    offerDbConnector,
                    offerDbConnector
            )
            OfferOverviewView overviewView = createOfferOverviewView(
                    viewModel,
                    overviewService,
                    projectResourceService,
                    projectSpaceResourceService,
                    offerUpdateEvent,
                    projectCreatedEvent,
                    projectMainConnector,
                    projectMainConnector,
                    offerDbConnector)

            portletView = new AppView(this.viewModel,
                    createPersonView,
                    createAffiliationView,
                    searchAffiliationView,
                    createOfferView,
                    overviewView,
                    updateOfferView,
                    searchPersonView,
                    maintainProductsView,
            )
            this.portletView = portletView
        } catch (Exception e) {
            log.error("Could not create ${AppView.getSimpleName()} view.", e)
            throw e
        }
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
                createAffiliationView,
                offerResourcesService)

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

    private static CreateOfferView createUpdateOffer(ResourcesService<Customer> customerResourcesService,
                                                     ResourcesService<ProjectManager> projectManagerResourcesService,
                                                     ResourcesService<Product> productResourcesService,
                                                     EventEmitter<Offer> offerUpdateEvent) {
        UpdateOfferViewModel updateOfferViewModel = new UpdateOfferViewModel(
                customerResourcesService,
                projectManagerResourcesService,
                productResourcesService,
                offerUpdateEvent)
        //TODO implement
        throw new RuntimeException("Not Implemented.")
    }

    private static SearchPersonView createSearchPerson() {
        //TODO implement
        throw new RuntimeException("Not Implemented.")
    }

    private static MaintainProductsView createMaintainProducts() {
        //TODO implement
        throw new RuntimeException("Not Implemented.")
    }

    private static UpdatePersonView createUpdatePersonView() {
        //TODO implement
        throw new RuntimeException("Not Implemented.")
    }


}
