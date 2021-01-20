package life.qbic.portal.qoffer2

import groovy.util.logging.Log4j2

import life.qbic.datamodel.dtos.business.AcademicTitle
import life.qbic.datamodel.dtos.business.AffiliationCategory
import life.qbic.portal.portlet.customers.affiliation.create.CreateAffiliation
import life.qbic.portal.portlet.customers.affiliation.list.ListAffiliations
import life.qbic.portal.portlet.customers.create.CreateCustomer
import life.qbic.portal.portlet.customers.search.SearchCustomer
import life.qbic.portal.portlet.offers.create.CreateOffer
import life.qbic.portal.portlet.products.ListProducts
import life.qbic.portal.qoffer2.offers.OfferDbConnector
import life.qbic.portal.qoffer2.customers.CustomerDbConnector
import life.qbic.portal.qoffer2.products.ProductsDbConnector
import life.qbic.portal.qoffer2.database.DatabaseSession

import life.qbic.portal.qoffer2.services.OfferUpdateService
import life.qbic.portal.qoffer2.services.OverviewService
import life.qbic.portal.qoffer2.customers.AffiliationResourcesService
import life.qbic.portal.qoffer2.offers.OfferResourcesService
import life.qbic.portal.qoffer2.customers.PersonResourcesService

import life.qbic.portal.qoffer2.web.controllers.CreateAffiliationController
import life.qbic.portal.qoffer2.web.controllers.CreateOfferController
import life.qbic.portal.qoffer2.web.controllers.ListProductsController
import life.qbic.portal.qoffer2.web.controllers.SearchCustomerController
import life.qbic.portal.qoffer2.web.controllers.ListAffiliationsController
import life.qbic.portal.qoffer2.web.controllers.CreateCustomerController

import life.qbic.portal.qoffer2.web.presenters.CreateAffiliationPresenter
import life.qbic.portal.qoffer2.web.presenters.CreateCustomerPresenter
import life.qbic.portal.qoffer2.web.presenters.CreateOfferPresenter
import life.qbic.portal.qoffer2.web.presenters.ListAffiliationsPresenter
import life.qbic.portal.qoffer2.web.presenters.SearchCustomerPresenter
import life.qbic.portal.qoffer2.web.presenters.Presenter

import life.qbic.portal.qoffer2.web.viewmodel.CreateAffiliationViewModel
import life.qbic.portal.qoffer2.web.viewmodel.CreateCustomerViewModel
import life.qbic.portal.qoffer2.web.viewmodel.CreateOfferViewModel
import life.qbic.portal.qoffer2.web.viewmodel.OfferOverviewModel
import life.qbic.portal.qoffer2.web.viewmodel.SearchCustomerViewModel
import life.qbic.portal.qoffer2.web.viewmodel.UpdateOfferViewModel
import life.qbic.portal.qoffer2.web.viewmodel.ViewModel

import life.qbic.portal.qoffer2.web.views.CreateAffiliationView
import life.qbic.portal.qoffer2.web.views.CreateOfferView
import life.qbic.portal.qoffer2.web.views.OverviewView
import life.qbic.portal.qoffer2.web.views.PortletView
import life.qbic.portal.qoffer2.web.views.CreateCustomerView
import life.qbic.portal.qoffer2.web.views.SearchCustomerView

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

    private ViewModel viewModel
    private CreateCustomerViewModel createCustomerViewModel
    private CreateAffiliationViewModel createAffiliationViewModel
    private SearchCustomerViewModel searchCustomerViewModel
    private CreateOfferViewModel createOfferViewModel
    private CreateOfferViewModel updateOfferViewModel
    private OfferOverviewModel offerOverviewModel

    private Presenter presenter
    private CreateCustomerPresenter createCustomerPresenter
    private CreateAffiliationPresenter createAffiliationPresenter
    private ListAffiliationsPresenter listAffiliationsPresenter
    private SearchCustomerPresenter searchCustomerPresenter
    private CreateOfferPresenter createOfferPresenter
    private CreateOfferPresenter updateOfferPresenter

    private CustomerDbConnector customerDbConnector
    private OfferDbConnector offerDbConnector
    private ProductsDbConnector productsDbConnector

    private CreateCustomer createCustomer
    private CreateAffiliation createAffiliation
    private ListAffiliations listAffiliations
    private SearchCustomer searchCustomer
    private CreateOffer createOffer
    private CreateOffer updateOffer
    private ListProducts listProducts

    private CreateCustomerController createCustomerController
    private CreateAffiliationController createAffiliationController
    private SearchCustomerController searchCustomerController
    private ListAffiliationsController listAffiliationsController
    private CreateOfferController createOfferController
    private CreateOfferController updateOfferController
    private ListProductsController listProductsController

    private CreateCustomerView createCustomerView
    private CreateCustomerView createCustomerViewNewOffer
    private CreateAffiliationView createAffiliationView
    private PortletView portletView
    private ConfigurationManager configurationManager

    private OverviewService overviewService
    private OfferUpdateService offerUpdateService
    private PersonResourcesService customerService
    private AffiliationResourcesService affiliationService
    private OfferResourcesService offerService

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

    protected PortletView getPortletView() {
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
            offerDbConnector = new OfferDbConnector(DatabaseSession.getInstance(), customerDbConnector, productsDbConnector)

        } catch (Exception e) {
            log.error("Unexpected exception during customer database connection.", e)
            throw e
        }
    }

    private void setupServices() {
        this.offerService = new OfferResourcesService()
        this.overviewService = new OverviewService(offerDbConnector, offerService)
        this.offerUpdateService = new OfferUpdateService()
        this.customerService = new PersonResourcesService(customerDbConnector)
        this.affiliationService = new AffiliationResourcesService(customerDbConnector)
    }

    private void setupViewModels() {
        // setup view models
        try {
            this.viewModel = new ViewModel(affiliationService)
        } catch (Exception e) {
            log.error("Unexpected excpetion during ${ViewModel.getSimpleName()} view model setup.", e)
            throw e
        }

        try {
            this.createCustomerViewModel = new CreateCustomerViewModel(customerService)
            createCustomerViewModel.academicTitles.addAll(AcademicTitle.values().collect {it.value})

        } catch (Exception e) {
            log.error("Unexpected excpetion during ${CreateCustomerViewModel.getSimpleName()} view model setup.", e)
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
            this.searchCustomerViewModel = new SearchCustomerViewModel()

        } catch (Exception e) {
            log.error("Unexpected excpetion during ${SearchCustomerViewModel.getSimpleName()} view model setup.", e)
            throw e
        }

        try {
            this.createOfferViewModel = new CreateOfferViewModel(customerService)
            //todo add affiliations, customers and project managers to the model
        } catch (Exception e) {
            log.error("Unexpected exception during ${CreateOfferViewModel.getSimpleName()} view model setup.", e)
            throw e
        }

        try {
            this.updateOfferViewModel = new UpdateOfferViewModel(customerService,
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
    }

    private void setupPresenters() {
        try {
            this.presenter = new Presenter(this.viewModel)
        } catch (Exception e) {
            log.error("Unexpected exception during ${Presenter.getSimpleName()} setup." , e)
            throw e
        }

        try {
            this.createCustomerPresenter = new CreateCustomerPresenter(this.viewModel, this.createCustomerViewModel)
        } catch (Exception e) {
            log.error("Unexpected exception during ${CreateCustomerPresenter.getSimpleName()} setup." , e)
            throw e
        }

        try {
            this.createAffiliationPresenter = new CreateAffiliationPresenter(this.viewModel, this.createAffiliationViewModel)
        } catch (Exception e) {
            log.error("Unexpected exception during ${CreateAffiliationPresenter.getSimpleName()} setup." , e)
            throw e
        }

        try {
            this.listAffiliationsPresenter = new ListAffiliationsPresenter(this.viewModel)
        } catch (Exception e) {
            log.error("Unexpected exception during ${ListAffiliationsPresenter.getSimpleName()} setup", e)
        }

        try {
            this.searchCustomerPresenter = new SearchCustomerPresenter(this.viewModel, this.searchCustomerViewModel)
        } catch (Exception e) {
            log.error("Unexpected exception during ${SearchCustomerPresenter.getSimpleName()} setup", e)
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
        this.listAffiliations = new ListAffiliations(listAffiliationsPresenter, customerDbConnector)
        this.createOffer = new CreateOffer(offerDbConnector, createOfferPresenter)
        this.updateOffer = new CreateOffer(offerDbConnector, updateOfferPresenter)
        this.listProducts = new ListProducts(productsDbConnector,[createOfferPresenter,updateOfferPresenter])
        this.searchCustomer = new SearchCustomer(searchCustomerPresenter, customerDbConnector)
    }

    private void setupControllers() {
        try {
            this.createCustomerController = new CreateCustomerController(this.createCustomer)
        } catch (Exception e) {
            log.error("Unexpected exception during ${CreateCustomerController.getSimpleName()} setup.", e)
            throw e
        }
        try {
            this.createAffiliationController = new CreateAffiliationController(this.createAffiliation)
        } catch (Exception e) {
            log.error("Unexpected exception during ${CreateCustomerController.getSimpleName()} setup.", e)
            throw e
        }
        try {
            this.searchCustomerController = new SearchCustomerController(this.searchCustomer)
        } catch (Exception e) {
            log.error("Unexpected exception during ${SearchCustomerController.getSimpleName()} setup.", e)
            throw e
        }
        try {
            this.listAffiliationsController = new ListAffiliationsController(this.listAffiliations)
        } catch (Exception e) {
            log.error("Unexpected exception during ${ListAffiliationsController.getSimpleName()} setup", e)
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
        try {
            this.listProductsController = new ListProductsController(this.listProducts)
        } catch (Exception e) {
            log.error("Unexpected exception during ${ListProductsController.getSimpleName()} setup", e)
        }
    }

    private void setupViews() {

        try {
            this.createCustomerView = new CreateCustomerView(this.createCustomerController, this.viewModel, this.createCustomerViewModel)
            listAffiliationsController.listAffiliations()
        } catch (Exception e) {
            log.error("Could not create ${CreateCustomerView.getSimpleName()} view.", e)
            throw e
        }

        try {
            this.createCustomerViewNewOffer = new CreateCustomerView(this.createCustomerController, this.viewModel, this.createCustomerViewModel)
            listAffiliationsController.listAffiliations()
        } catch (Exception e) {
            log.error("Could not create ${CreateCustomerView.getSimpleName()} view.", e)
            throw e
        }

        try {
            this.createAffiliationView = new CreateAffiliationView(this.viewModel, this.createAffiliationViewModel, this.createAffiliationController)
        } catch (Exception e) {
            log.error("Could not create ${CreateAffiliationView.getSimpleName()} view.", e)
            throw e
        }

        SearchCustomerView searchCustomerView

        try {
            searchCustomerView = new SearchCustomerView(this.searchCustomerController, this.viewModel, this.searchCustomerViewModel)
        } catch (Exception e) {
            log.error("Could not create ${SearchCustomerView.getSimpleName()} view.", e)
            throw e
        }

        CreateOfferView createOfferView
        try {
            createOfferView = new CreateOfferView(
                    this.viewModel,
                    this.createOfferViewModel,
                    this.createOfferController,
                    this.listProductsController,
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
                    this.listProductsController,
                    this.createCustomerView,
                    this.createAffiliationView,
                    this.offerService)
        } catch (Exception e) {
            log.error("Could not create ${CreateOfferView.getSimpleName()} view.", e)
            throw e
        }

        OverviewView overviewView
        try {
            overviewView = new OverviewView(offerOverviewModel, offerUpdateService)
        } catch (Exception e) {
            log.error("Could not create ${OverviewView.getSimpleName()} view.", e)
            throw e
        }

        PortletView portletView
        try {
            CreateCustomerView createCustomerView2 = new CreateCustomerView(createCustomerController, this
                    .viewModel, createCustomerViewModel)
            CreateAffiliationView createAffiliationView2 = new CreateAffiliationView(this.viewModel,
                    createAffiliationViewModel, createAffiliationController)
            portletView = new PortletView(this.viewModel, createCustomerView2,
                    createAffiliationView2,
                    searchCustomerView,
                    createOfferView,
                    overviewView,
                    updateOfferView
            )
            this.portletView = portletView
        } catch (Exception e) {
            log.error("Could not create ${PortletView.getSimpleName()} view.", e)
            throw e
        }

        createCustomerView?.addAffiliationSelectionListener(portletView)
    }


}
