package life.qbic.portal.offermanager

import groovy.util.logging.Log4j2
import life.qbic.datamodel.dtos.business.AcademicTitle
import life.qbic.datamodel.dtos.business.AffiliationCategory
import life.qbic.business.customers.affiliation.create.CreateAffiliation
import life.qbic.business.customers.create.CreateCustomer
import life.qbic.business.offers.create.CreateOffer
import life.qbic.portal.offermanager.components.person.search.SearchPersonView
import life.qbic.portal.offermanager.components.person.search.SearchPersonViewModel
import life.qbic.portal.offermanager.components.person.update.UpdatePersonViewModel
import life.qbic.portal.offermanager.dataresources.persons.AffiliationResourcesService
import life.qbic.portal.offermanager.dataresources.persons.CustomerDbConnector
import life.qbic.portal.offermanager.dataresources.persons.CustomerResourceService

import life.qbic.portal.offermanager.dataresources.database.DatabaseSession
import life.qbic.portal.offermanager.dataresources.offers.OfferDbConnector
import life.qbic.portal.offermanager.dataresources.offers.OfferResourcesService
import life.qbic.portal.offermanager.dataresources.persons.PersonUpdateService
import life.qbic.portal.offermanager.dataresources.persons.ProjectManagerResourceService
import life.qbic.portal.offermanager.dataresources.products.ProductsDbConnector
import life.qbic.portal.offermanager.dataresources.products.ProductsResourcesService
import life.qbic.portal.offermanager.dataresources.offers.OfferUpdateService
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

    private AppViewModel viewModel
    private CreatePersonViewModel createCustomerViewModel
    private UpdatePersonViewModel updatePersonViewModel
    private CreateAffiliationViewModel createAffiliationViewModel
    private CreateOfferViewModel createOfferViewModel
    private CreateOfferViewModel updateOfferViewModel
    private OfferOverviewModel offerOverviewModel
    private SearchPersonViewModel searchPersonViewModel

    private AppPresenter presenter
    private CreatePersonPresenter createCustomerPresenter
    private CreatePersonPresenter updateCustomerPresenter
    private CreateAffiliationPresenter createAffiliationPresenter
    private CreateOfferPresenter createOfferPresenter
    private CreateOfferPresenter updateOfferPresenter

    private CustomerDbConnector customerDbConnector
    private OfferDbConnector offerDbConnector
    private ProductsDbConnector productsDbConnector

    private CreateCustomer createCustomer
    private CreateCustomer updateCustomer
    private CreateAffiliation createAffiliation
    private CreateOffer createOffer
    private CreateOffer updateOffer

    private CreatePersonController createCustomerController
    private CreatePersonController updateCustomerController
    private CreateAffiliationController createAffiliationController
    private CreateOfferController createOfferController
    private CreateOfferController updateOfferController

    private CreatePersonView createCustomerView
    private CreatePersonView updatePersonView
    private CreatePersonView createCustomerViewNewOffer
    private CreateAffiliationView createAffiliationView
    private SearchPersonView searchPersonView
    private AppView portletView
    private ConfigurationManager configurationManager

    private OverviewService overviewService
    private OfferUpdateService offerUpdateService
    private CustomerResourceService customerResourceService
    private AffiliationResourcesService affiliationService
    private OfferResourcesService offerService
    private ProductsResourcesService productsResourcesService
    private ProjectManagerResourceService managerResourceService
    private PersonUpdateService personUpdateService

    /**
     * Public constructor.
     *
     * This constructor creates a dependency manager with all the instances of required classes.
     * It ensures that the {@link #portletView} field is set.
     */
    DependencyManager() {
        configurationManager = ConfigurationManagerFactory.getInstance()
        initializeDependencies()
    }

    private void initializeDependencies() {
        // The ORDER in which the methods below are called MUST NOT CHANGE
        setupDbConnections()
        setupServices()
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
            customerDbConnector = new CustomerDbConnector(DatabaseSession.getInstance())
            productsDbConnector = new ProductsDbConnector(DatabaseSession.getInstance())
            offerDbConnector = new OfferDbConnector(DatabaseSession.getInstance(),
                    customerDbConnector, productsDbConnector)

        } catch (Exception e) {
            log.error("Unexpected exception during customer database connection.", e)
            throw e
        }
    }

    private void setupServices() {
        this.offerService = new OfferResourcesService()
        this.overviewService = new OverviewService(offerDbConnector, offerService)
        this.offerUpdateService = new OfferUpdateService()
        this.managerResourceService = new ProjectManagerResourceService(customerDbConnector)
        this.productsResourcesService = new ProductsResourcesService(productsDbConnector)
        this.affiliationService = new AffiliationResourcesService(customerDbConnector)
        this.customerResourceService = new CustomerResourceService(customerDbConnector)
        this.personUpdateService = new PersonUpdateService()
    }

    private void setupViewModels() {
        // setup view models
        try {
            this.viewModel = new AppViewModel(affiliationService)
        } catch (Exception e) {
            log.error("Unexpected excpetion during ${AppViewModel.getSimpleName()} view model setup.", e)
            throw e
        }

        try {
            this.createCustomerViewModel = new CreatePersonViewModel(
                    customerResourceService,
                    managerResourceService,
                    affiliationService)
            createCustomerViewModel.academicTitles.addAll(AcademicTitle.values().collect {it.value})

        } catch (Exception e) {
            log.error("Unexpected excpetion during ${CreatePersonViewModel.getSimpleName()} view model setup.", e)
            throw e
        }

        try {
            this.updatePersonViewModel = new UpdatePersonViewModel(
                    customerResourceService,
                    managerResourceService,
                    affiliationService,
                    personUpdateService)
            updatePersonViewModel.academicTitles.addAll(AcademicTitle.values().collect {it.value})

        } catch (Exception e) {
            log.error("Unexpected excpetion during ${UpdatePersonViewModel.getSimpleName()} view model setup.", e)
            throw e
        }

        try {
            this.createAffiliationViewModel = new CreateAffiliationViewModel(affiliationService)
            createAffiliationViewModel.affiliationCategories.addAll(AffiliationCategory.values().collect{it.value})
        } catch (Exception e) {
            log.error("Unexpected excpetion during ${CreateAffiliationViewModel.getSimpleName()} view model setup.", e)
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
                    offerUpdateService)
        } catch (Exception e) {
            log.error("Unexpected excpetion during ${CreateOfferViewModel.getSimpleName()} view model setup.", e)
            throw e
        }

        try {
            this.offerOverviewModel = new OfferOverviewModel(overviewService, offerDbConnector,
                    viewModel)
        } catch (Exception e) {
            log.error("Unexpected excpetion during ${OfferOverviewModel.getSimpleName()} view model setup.", e)
        }

        try{
            this.searchPersonViewModel = new SearchPersonViewModel(customerResourceService)
        }catch (Exception e) {
            log.error("Unexpected excpetion during ${SearchPersonViewModel.getSimpleName()} view model setup.", e)
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
    }

    private void setupUseCaseInteractors() {
        this.createCustomer = new CreateCustomer(createCustomerPresenter, customerDbConnector)
        this.createAffiliation = new CreateAffiliation(createAffiliationPresenter, customerDbConnector)
        this.createOffer = new CreateOffer(offerDbConnector, createOfferPresenter)
        this.updateOffer = new CreateOffer(offerDbConnector, updateOfferPresenter)
        this.updateCustomer = new CreateCustomer(updateCustomerPresenter, customerDbConnector)
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
            this.createAffiliationController = new CreateAffiliationController(this.createAffiliation)
        } catch (Exception e) {
            log.error("Unexpected exception during ${CreateAffiliationController.getSimpleName()} setup.", e)
            throw e
        }
        try {
            this.createOfferController = new CreateOfferController(this.createOffer,this.createOffer)
        } catch (Exception e) {
            log.error("Unexpected exception during ${CreateOfferController.getSimpleName()} setup", e)
        }
        try {
            this.updateOfferController = new CreateOfferController(this.updateOffer,this.updateOffer)
        } catch (Exception e) {
            log.error("Unexpected exception during ${CreateOfferController.getSimpleName()} setup", e)
        }
    }

    private void setupViews() {

        try {
            this.createCustomerView = new CreatePersonView(this.createCustomerController, this.viewModel, this.createCustomerViewModel)
        } catch (Exception e) {
            log.error("Could not create ${CreatePersonView.getSimpleName()} view.", e)
            throw e
        }

        try {
            this.updatePersonView = new CreatePersonView(this.updateCustomerController, this.viewModel, this.updatePersonViewModel)
        } catch (Exception e) {
            log.error("Could not create ${CreatePersonView.getSimpleName()} view.", e)
            throw e
        }

        try {
            this.createCustomerViewNewOffer = new CreatePersonView(this.createCustomerController, this.viewModel, this.createCustomerViewModel)
        } catch (Exception e) {
            log.error("Could not create ${CreatePersonView.getSimpleName()} view.", e)
            throw e
        }

        try {
            this.createAffiliationView = new CreateAffiliationView(this.viewModel, this.createAffiliationViewModel, this.createAffiliationController)
        } catch (Exception e) {
            log.error("Could not create ${CreateAffiliationView.getSimpleName()} view.", e)
            throw e
        }

        CreateOfferView createOfferView
        try {
            createOfferView = new CreateOfferView(
                    this.viewModel,
                    this.createOfferViewModel,
                    this.createOfferController,
                    this.createCustomerViewNewOffer,
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
                    this.createCustomerView,
                    this.createAffiliationView,
                    this.offerService)
        } catch (Exception e) {
            log.error("Could not create ${CreateOfferView.getSimpleName()} view.", e)
            throw e
        }

        OfferOverviewView overviewView
        try {
            overviewView = new OfferOverviewView(offerOverviewModel, offerUpdateService)
        } catch (Exception e) {
            log.error("Could not create ${OfferOverviewView.getSimpleName()} view.", e)
            throw e
        }

        SearchPersonView searchPersonView
        try{
            searchPersonView = new SearchPersonView(searchPersonViewModel, personUpdateService, updatePersonView)
        } catch (Exception e) {
            log.error("Could not create ${SearchPersonView.getSimpleName()} view.", e)
            throw e
        }

        AppView portletView
        try {
            CreatePersonView createCustomerView2 = new CreatePersonView(createCustomerController, this
                    .viewModel, createCustomerViewModel)
            CreateAffiliationView createAffiliationView2 = new CreateAffiliationView(this.viewModel,
                    createAffiliationViewModel, createAffiliationController)

            portletView = new AppView(this.viewModel, createCustomerView2,
                    createAffiliationView2,
                    createOfferView,
                    overviewView,
                    updateOfferView,
                    searchPersonView
            )
            this.portletView = portletView
        } catch (Exception e) {
            log.error("Could not create ${AppView.getSimpleName()} view.", e)
            throw e
        }
    }


}
