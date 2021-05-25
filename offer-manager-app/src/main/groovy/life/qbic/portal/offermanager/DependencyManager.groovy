package life.qbic.portal.offermanager

import groovy.util.logging.Log4j2
import life.qbic.business.offers.fetch.FetchOffer
import life.qbic.business.products.archive.ArchiveProduct
import life.qbic.business.products.copy.CopyProduct
import life.qbic.business.products.create.CreateProduct
import life.qbic.business.projects.create.CreateProject
import life.qbic.datamodel.dtos.business.AcademicTitle
import life.qbic.datamodel.dtos.business.AffiliationCategory
import life.qbic.business.persons.affiliation.create.CreateAffiliation
import life.qbic.business.persons.create.CreatePerson
import life.qbic.business.offers.create.CreateOffer
import life.qbic.datamodel.dtos.business.Offer
import life.qbic.datamodel.dtos.business.services.Product
import life.qbic.datamodel.dtos.general.Person
import life.qbic.datamodel.dtos.projectmanagement.Project
import life.qbic.portal.offermanager.communication.EventEmitter
import life.qbic.portal.offermanager.components.affiliation.search.SearchAffiliationView
import life.qbic.portal.offermanager.components.affiliation.search.SearchAffiliationViewModel
import life.qbic.portal.offermanager.components.offer.overview.OfferOverviewController
import life.qbic.portal.offermanager.components.offer.overview.OfferOverviewPresenter
import life.qbic.portal.offermanager.components.offer.overview.projectcreation.CreateProjectController
import life.qbic.portal.offermanager.components.offer.overview.projectcreation.CreateProjectPresenter
import life.qbic.portal.offermanager.components.offer.overview.projectcreation.CreateProjectView
import life.qbic.portal.offermanager.components.offer.overview.projectcreation.CreateProjectViewModel
import life.qbic.portal.offermanager.components.person.search.SearchPersonView
import life.qbic.portal.offermanager.components.person.search.SearchPersonViewModel
import life.qbic.portal.offermanager.components.person.update.UpdatePersonView
import life.qbic.portal.offermanager.components.person.update.UpdatePersonViewModel
import life.qbic.portal.offermanager.components.product.MaintainProductsPresenter
import life.qbic.portal.offermanager.components.product.MaintainProductsView
import life.qbic.portal.offermanager.components.product.MaintainProductsViewModel
import life.qbic.portal.offermanager.components.product.copy.CopyProductView
import life.qbic.portal.offermanager.components.product.copy.CopyProductViewModel
import life.qbic.portal.offermanager.components.product.create.CreateProductView
import life.qbic.portal.offermanager.components.product.create.CreateProductViewModel
import life.qbic.portal.offermanager.components.product.MaintainProductsController
import life.qbic.portal.offermanager.dataresources.persons.AffiliationResourcesService
import life.qbic.portal.offermanager.dataresources.persons.PersonDbConnector
import life.qbic.portal.offermanager.dataresources.persons.CustomerResourceService
import life.qbic.portal.offermanager.dataresources.projects.ProjectMainConnector
import life.qbic.portal.offermanager.dataresources.projects.ProjectDbConnector

import life.qbic.openbis.openbisclient.OpenBisClient

import life.qbic.portal.offermanager.dataresources.database.DatabaseSession
import life.qbic.portal.offermanager.dataresources.offers.OfferDbConnector
import life.qbic.portal.offermanager.dataresources.offers.OfferResourcesService

import life.qbic.portal.offermanager.dataresources.persons.PersonResourceService

import life.qbic.portal.offermanager.dataresources.persons.ProjectManagerResourceService
import life.qbic.portal.offermanager.dataresources.products.ProductsDbConnector
import life.qbic.portal.offermanager.dataresources.products.ProductsResourcesService

import life.qbic.portal.offermanager.dataresources.offers.OverviewService
import life.qbic.portal.offermanager.components.affiliation.create.CreateAffiliationController
import life.qbic.portal.offermanager.components.person.create.CreatePersonController
import life.qbic.portal.offermanager.components.offer.create.CreateOfferController

import life.qbic.portal.offermanager.components.affiliation.create.CreateAffiliationPresenter
import life.qbic.portal.offermanager.components.person.create.CreatePersonPresenter
import life.qbic.portal.offermanager.components.offer.create.CreateOfferPresenter
import life.qbic.portal.offermanager.components.AppPresenter
import life.qbic.portal.offermanager.components.affiliation.create.CreateAffiliationViewModel
import life.qbic.portal.offermanager.components.person.create.CreatePersonViewModel
import life.qbic.portal.offermanager.components.offer.create.CreateOfferViewModel
import life.qbic.portal.offermanager.components.offer.overview.OfferOverviewModel
import life.qbic.portal.offermanager.components.offer.update.UpdateOfferViewModel
import life.qbic.portal.offermanager.components.AppViewModel
import life.qbic.portal.offermanager.components.affiliation.create.CreateAffiliationView
import life.qbic.portal.offermanager.components.person.create.CreatePersonView
import life.qbic.portal.offermanager.components.offer.create.CreateOfferView
import life.qbic.portal.offermanager.components.offer.overview.OfferOverviewView
import life.qbic.portal.offermanager.components.AppView
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
    private CreatePersonViewModel createCustomerViewModel
    private UpdatePersonViewModel updatePersonViewModel
    private CreateAffiliationViewModel createAffiliationViewModel
    private SearchAffiliationViewModel searchAffiliationViewModel
    private CreateOfferViewModel createOfferViewModel
    private CreateOfferViewModel updateOfferViewModel
    private OfferOverviewModel offerOverviewModel
    private SearchPersonViewModel searchPersonViewModel
    private CreatePersonViewModel createCustomerViewModelNewOffer
    private MaintainProductsViewModel maintainProductsViewModel
    private MaintainProductsViewModel maintainProductsViewModelArchive
    private CreateProductViewModel createProductViewModel
    private CopyProductViewModel copyProductViewModel
    private CreateProjectViewModel createProjectModel

    private AppPresenter presenter
    private CreatePersonPresenter createCustomerPresenter
    private CreatePersonPresenter updateCustomerPresenter
    private CreatePersonPresenter createCustomerPresenterNewOffer
    private CreateAffiliationPresenter createAffiliationPresenter
    private CreateOfferPresenter createOfferPresenter
    private CreateOfferPresenter updateOfferPresenter
    private OfferOverviewPresenter offerOverviewPresenter
    private MaintainProductsPresenter createProductPresenter
    private MaintainProductsPresenter archiveProductPresenter
    private MaintainProductsPresenter copyProductPresenter
    private CreateProjectPresenter createProjectPresenter

    private PersonDbConnector customerDbConnector
    private OfferDbConnector offerDbConnector
    private ProductsDbConnector productsDbConnector
    private ProjectMainConnector projectMainConnector
    private ProjectDbConnector projectDbConnector
    private OpenBisClient openbisClient

    private CreatePerson createCustomer
    private CreatePerson createCustomerNewOffer
    private CreatePerson updateCustomer
    private CreateAffiliation createAffiliation
    private CreateOffer createOffer
    private CreateOffer updateOffer
    private CreateProject createProject
    private FetchOffer fetchOfferOfferOverview
    private FetchOffer fetchOfferCreateOffer
    private FetchOffer fetchOfferUpdateOffer
    private CreateProduct createProduct
    private ArchiveProduct archiveProduct
    private CopyProduct copyProduct

    private CreatePersonController createCustomerController
    private CreatePersonController updateCustomerController
    private CreatePersonController createCustomerControllerNewOffer
    private CreateAffiliationController createAffiliationController
    private CreateOfferController createOfferController
    private CreateOfferController updateOfferController
    private OfferOverviewController offerOverviewController
    private MaintainProductsController maintainProductController
    private CreateProjectController createProjectController

    private CreatePersonView createPersonView
    private CreatePersonView updatePersonView
    private UpdatePersonView updatePersonViewNewOffer
    private CreatePersonView createCustomerViewNewOffer
    private CreateAffiliationView createAffiliationView
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

    private void setupEventEmitter(){
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
            this.createCustomerViewModel = new CreatePersonViewModel(
                    customerResourceService,
                    managerResourceService,
                    affiliationService, personResourceService)
            createCustomerViewModel.academicTitles.addAll(AcademicTitle.values().collect {it.value})

        } catch (Exception e) {
            log.error("Unexpected exception during ${CreatePersonViewModel.getSimpleName()} view model setup.", e)
            throw e
        }

        try {
            this.createCustomerViewModelNewOffer = new CreatePersonViewModel(
                    customerResourceService,
                    managerResourceService,
                    affiliationService,
                    personResourceService)
            createCustomerViewModelNewOffer.academicTitles.addAll(AcademicTitle.values().collect {it.value})

        } catch (Exception e) {
            log.error("Unexpected exception during ${CreatePersonViewModel.getSimpleName()} view model setup.", e)
            throw e
        }

        try {
            this.updatePersonViewModel = new UpdatePersonViewModel(
                    customerResourceService,
                    managerResourceService,
                    affiliationService,
                    personUpdateEvent,
                    personResourceService)
            updatePersonViewModel.academicTitles.addAll(AcademicTitle.values().collect {it.value})

        } catch (Exception e) {
            log.error("Unexpected exception during ${UpdatePersonViewModel.getSimpleName()} view model setup.", e)
            throw e
        }

        try {
            this.createAffiliationViewModel = new CreateAffiliationViewModel(affiliationService)
            createAffiliationViewModel.affiliationCategories.addAll(AffiliationCategory.values().collect{it.value})
        } catch (Exception e) {
            log.error("Unexpected exception during ${CreateAffiliationViewModel.getSimpleName()} view model setup.", e)
            throw e
        }

        try {
            this.createOfferViewModel = new CreateOfferViewModel(
                    customerResourceService,
                    managerResourceService,
                    productsResourcesService)
            //todo add affiliations, persons and project managers to the model
        } catch (Exception e) {
            log.error("Unexpected exception during ${CreateOfferViewModel.getSimpleName()} view model setup.", e)
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
            this.offerOverviewModel = new OfferOverviewModel(overviewService, viewModel, offerUpdateEvent)
        } catch (Exception e) {
            log.error("Unexpected exception during ${OfferOverviewModel.getSimpleName()} view model setup.", e)
        }

        try {
            this.searchPersonViewModel = new SearchPersonViewModel(personResourceService, personUpdateEvent)
        }catch (Exception e) {
            log.error("Unexpected exception during ${SearchPersonViewModel.getSimpleName()} view model setup.", e)
        }

        try {
            this.createProjectModel = new CreateProjectViewModel(projectSpaceResourceService, projectResourceService)
        }catch (Exception e) {
            log.error("Unexpected exception during ${CreateProjectViewModel.getSimpleName()} view model" +
                    " setup.", e)
        }

        try {
            this.maintainProductsViewModel = new MaintainProductsViewModel(productsResourcesService, productUpdateEvent)
        }catch (Exception e) {
            log.error("Unexpected exception during ${MaintainProductsViewModel.getSimpleName()} view model setup.", e)
        }

        try {
            this.maintainProductsViewModelArchive = new MaintainProductsViewModel(productsResourcesService, productUpdateEvent)
        }catch (Exception e) {
            log.error("Unexpected exception during ${MaintainProductsViewModel.getSimpleName()} view model setup.", e)
        }

        try {
            this.createProductViewModel = new CreateProductViewModel()
        }catch (Exception e) {
            log.error("Unexpected exception during ${CreateProductViewModel.getSimpleName()} view model setup.", e)
        }

        try {
            this.copyProductViewModel = new CopyProductViewModel(productUpdateEvent)
        }catch (Exception e) {
            log.error("Unexpected exception during ${CopyProductViewModel.getSimpleName()} view model setup.", e)
        }

        try {
            this.searchAffiliationViewModel = new SearchAffiliationViewModel(affiliationService)
        } catch (Exception e) {
            log.error("Unexpected exception during ${SearchAffiliationViewModel.getSimpleName()} creation.")
            log.debug("Unexpected exception during ${SearchAffiliationViewModel.getSimpleName()} creation.", e)
        }
    }

    private void setupPresenters() {
        try {
            this.presenter = new AppPresenter(this.viewModel)
        } catch (Exception e) {
            log.error("Unexpected exception during ${AppPresenter.getSimpleName()} setup." , e)
            throw e
        }

        try {
            this.createCustomerPresenter = new CreatePersonPresenter(this.viewModel, this.createCustomerViewModel)
        } catch (Exception e) {
            log.error("Unexpected exception during ${CreatePersonPresenter.getSimpleName()} setup." , e)
            throw e
        }

        try {
            this.createCustomerPresenterNewOffer = new CreatePersonPresenter(this.viewModel, this.createCustomerViewModelNewOffer)
        } catch (Exception e) {
            log.error("Unexpected exception during ${CreatePersonPresenter.getSimpleName()} setup." , e)
            throw e
        }

        try {
            this.updateCustomerPresenter = new CreatePersonPresenter(this.viewModel, this.updatePersonViewModel)
        } catch (Exception e) {
            log.error("Unexpected exception during ${CreatePersonPresenter.getSimpleName()} setup." , e)
            throw e
        }

        try {
            this.createAffiliationPresenter = new CreateAffiliationPresenter(this.viewModel, this.createAffiliationViewModel)
        } catch (Exception e) {
            log.error("Unexpected exception during ${CreateAffiliationPresenter.getSimpleName()} setup." , e)
            throw e
        }

        try {
            this.createOfferPresenter = new CreateOfferPresenter(this.viewModel,
                    this.createOfferViewModel, this.offerService)
        } catch (Exception e) {
            log.error("Unexpected exception during ${CreateOfferViewModel.getSimpleName()} setup", e)
        }

        try {
            this.updateOfferPresenter = new CreateOfferPresenter(this.viewModel,
                    this.updateOfferViewModel, this.offerService)
        } catch (Exception e) {
            log.error("Unexpected exception during ${CreateOfferViewModel.getSimpleName()} setup", e)
        }
        try {
            this.offerOverviewPresenter = new OfferOverviewPresenter(this.viewModel, this.offerOverviewModel)
        } catch (Exception e) {
            log.error("Unexpected exception during ${OfferOverviewPresenter.getSimpleName()} setup", e)
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
        try {
            this.createProjectPresenter = new CreateProjectPresenter(createProjectModel, viewModel, projectCreatedEvent)
        } catch (Exception e) {
            log.error("Unexpected exception during ${CreateProjectPresenter.getSimpleName()} setup", e)
        }
    }

    private void setupUseCaseInteractors() {
        this.createCustomer = new CreatePerson(createCustomerPresenter, customerDbConnector)
        this.createCustomerNewOffer = new CreatePerson(createCustomerPresenterNewOffer, customerDbConnector)

        this.createAffiliation = new CreateAffiliation(createAffiliationPresenter, customerDbConnector)

        this.createOffer = new CreateOffer(offerDbConnector, createOfferPresenter)
        this.updateOffer = new CreateOffer(offerDbConnector, updateOfferPresenter)
        this.updateCustomer = new CreatePerson(updateCustomerPresenter, customerDbConnector)

        this.fetchOfferOfferOverview = new FetchOffer(offerDbConnector, offerOverviewPresenter)
        this.fetchOfferCreateOffer = new FetchOffer(offerDbConnector, createOfferPresenter)
        this.fetchOfferUpdateOffer = new FetchOffer(offerDbConnector, updateOfferPresenter)

        this.createProduct = new CreateProduct(productsDbConnector,createProductPresenter)
        this.archiveProduct = new ArchiveProduct(productsDbConnector,archiveProductPresenter)
        this.copyProduct = new CopyProduct(productsDbConnector, copyProductPresenter, productsDbConnector)
        this.createProject = new CreateProject(createProjectPresenter, projectMainConnector, projectMainConnector)
    }

    private void setupControllers() {
        try {
            this.createCustomerController = new CreatePersonController(this.createCustomer)
        } catch (Exception e) {
            log.error("Unexpected exception during ${CreatePersonController.getSimpleName()} setup.", e)
            throw e
        }
        try {
            this.updateCustomerController = new CreatePersonController(this.updateCustomer)
        } catch (Exception e) {
            log.error("Unexpected exception during ${CreatePersonController.getSimpleName()} setup.", e)
            throw e
        }
        try {
            this.createCustomerControllerNewOffer = new CreatePersonController(this.createCustomerNewOffer)
        } catch (Exception e) {
            log.error("Unexpected exception during ${CreatePersonController.getSimpleName()} setup.", e)
            throw e
        }
        try {
            this.createAffiliationController = new CreateAffiliationController(this.createAffiliation)
        } catch (Exception e) {
            log.error("Unexpected exception during ${CreateAffiliationController.getSimpleName()} setup.", e)
            throw e
        }
        try {
            this.createOfferController = new CreateOfferController(this.createOffer, this.fetchOfferCreateOffer, this.createOffer)
        } catch (Exception e) {
            log.error("Unexpected exception during ${CreateOfferController.getSimpleName()} setup", e)
        }
        try {
            this.updateOfferController = new CreateOfferController(this.updateOffer, this.fetchOfferUpdateOffer, this.updateOffer)
        } catch (Exception e) {
            log.error("Unexpected exception during ${CreateOfferController.getSimpleName()} setup", e)
        }
        try {
            this.offerOverviewController = new OfferOverviewController(this.fetchOfferOfferOverview)
        } catch (Exception e) {
            log.error("Unexpected exception during ${OfferOverviewController.getSimpleName()} setup", e)
        }

        try {
            this.maintainProductController = new MaintainProductsController(this.createProduct, this.archiveProduct, this.copyProduct)
        } catch (Exception e) {
            log.error("Unexpected exception during ${MaintainProductsController.getSimpleName()} setup", e)
        }

        try{
            this.createProjectController = new CreateProjectController(this.createProject)
        } catch (Exception e) {
            log.error("Unexpected exception during ${CreateProjectController.getSimpleName()} setup", e)
        }
    }

    private void setupViews() {


        try {
            this.createAffiliationView = new CreateAffiliationView(this.viewModel, this.createAffiliationViewModel, this.createAffiliationController)
        } catch (Exception e) {
            log.error("Could not create ${CreateAffiliationView.getSimpleName()} view.", e)
            throw e
        }

        try {
            CreateAffiliationView createAffiliationView = new CreateAffiliationView(this.viewModel, this.createAffiliationViewModel, this.createAffiliationController)
            this.createPersonView = new CreatePersonView(this.createCustomerController, this.viewModel, this.createCustomerViewModel, createAffiliationView)
        } catch (Exception e) {
            log.error("Could not create ${CreatePersonView.getSimpleName()} view.", e)
            throw e
        }

        try {
            CreateAffiliationView createAffiliationView = new CreateAffiliationView(this.viewModel, this.createAffiliationViewModel, this.createAffiliationController)
            this.updatePersonView = new UpdatePersonView(this.updateCustomerController, this.viewModel, this.updatePersonViewModel, createAffiliationView)
        } catch (Exception e) {
            log.error("Could not create ${UpdatePersonView.getSimpleName()} view.", e)
            throw e
        }

        try {
            this.createCustomerViewNewOffer = new CreatePersonView(this.createCustomerControllerNewOffer, this.viewModel, this.createCustomerViewModelNewOffer, createAffiliationView)
        } catch (Exception e) {
            log.error("Could not create ${CreatePersonView.getSimpleName()} view.", e)
            throw e
        }

        try {
            this.updatePersonViewNewOffer = new UpdatePersonView(updateCustomerController, viewModel, updatePersonViewModel, createAffiliationView)
        } catch (Exception e) {
            log.error("Could not create ${CreatePersonView.getSimpleName()} view.", e)
            throw e
        }

        CreateOfferView createOfferView
        try {
            createOfferView = new CreateOfferView(
                    this.viewModel,
                    this.createOfferViewModel,
                    this.createOfferController,
                    this.createCustomerViewNewOffer,
                    this.updatePersonViewNewOffer,
                    this.createAffiliationView,
                    this.offerService)
        } catch (Exception e) {
            log.error("Could not create ${CreateOfferView.getSimpleName()} view.", e)
            throw e
        }

        CreateOfferView updateOfferView
        try {
            updateOfferView = new CreateOfferView(
                    this.viewModel,
                    this.updateOfferViewModel,
                    this.updateOfferController,
                    this.createPersonView,
                    this.updatePersonViewNewOffer,
                    this.createAffiliationView,
                    this.offerService)
        } catch (Exception e) {
            log.error("Could not create ${CreateOfferView.getSimpleName()} view.", e)
            throw e
        }

        CreateProjectView createProjectView
        try{
            createProjectView = new CreateProjectView(createProjectModel, createProjectController)
        } catch (Exception e) {
            log.error("Could not create ${CreateProjectView.getSimpleName()} view.", e)
            throw e
        }

        OfferOverviewView overviewView
        try {
            overviewView = new OfferOverviewView(offerOverviewModel, offerOverviewController, createProjectView)
        } catch (Exception e) {
            log.error("Could not create ${OfferOverviewView.getSimpleName()} view.", e)
            throw e
        }

        SearchPersonView searchPersonView
        try{
            searchPersonView = new SearchPersonView(searchPersonViewModel, updatePersonView)
        } catch (Exception e) {
            log.error("Could not create ${SearchPersonView.getSimpleName()} view.", e)
            throw e
        }

        CreateProductView createProductView
        try{
            createProductView = new CreateProductView(createProductViewModel,maintainProductController)
        }catch(Exception e){
            log.error("Could not create ${CreateProductView.getSimpleName()} view.", e)
            throw e
        }

        CopyProductView copyProductView
        try{
            copyProductView = new CopyProductView(copyProductViewModel, maintainProductController)
        }catch(Exception e){
            log.error("Could not create ${CopyProductView.getSimpleName()} view.", e)
            throw e
        }

        MaintainProductsView maintainProductsView
        try{
            maintainProductsView = new MaintainProductsView(maintainProductsViewModel, createProductView, copyProductView, maintainProductController)
        }catch (Exception e) {
            log.error("Could not create ${MaintainProductsView.getSimpleName()} view.", e)
            throw e
        }

        AppView portletView
        try {
            CreatePersonView createCustomerView2 = new CreatePersonView(createCustomerController, this
                    .viewModel, createCustomerViewModel, createAffiliationView)
            CreateAffiliationView createAffiliationView2 = new CreateAffiliationView(this.viewModel,
                    createAffiliationViewModel, createAffiliationController)
            SearchAffiliationView searchAffiliationView = new SearchAffiliationView(this.searchAffiliationViewModel)
            portletView = new AppView(this.viewModel, createCustomerView2,
                    createAffiliationView2,
                    searchAffiliationView,
                    createOfferView,
                    overviewView,
                    updateOfferView,
                    searchPersonView,
                    maintainProductsView,
                    createProjectView
            )
            this.portletView = portletView
        } catch (Exception e) {
            log.error("Could not create ${AppView.getSimpleName()} view.", e)
            throw e
        }
    }


}
