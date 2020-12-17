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
import life.qbic.portal.qoffer2.customers.CustomerDatabaseQueries
import life.qbic.portal.qoffer2.customers.CustomerDbConnector
import life.qbic.portal.qoffer2.database.DatabaseSession
import life.qbic.portal.qoffer2.offers.OfferDbConnector
import life.qbic.portal.qoffer2.products.ProductsDbConnector
import life.qbic.portal.qoffer2.web.controllers.CreateAffiliationController
import life.qbic.portal.qoffer2.web.controllers.CreateOfferController
import life.qbic.portal.qoffer2.web.controllers.ListProductsController
import life.qbic.portal.qoffer2.web.controllers.SearchCustomerController
import life.qbic.portal.qoffer2.web.controllers.ListAffiliationsController
import life.qbic.portal.qoffer2.web.presenters.CreateAffiliationPresenter
import life.qbic.portal.qoffer2.web.presenters.CreateCustomerPresenter
import life.qbic.portal.qoffer2.web.presenters.CreateOfferPresenter
import life.qbic.portal.qoffer2.web.presenters.ListAffiliationsPresenter
import life.qbic.portal.qoffer2.web.presenters.SearchCustomerPresenter
import life.qbic.portal.qoffer2.web.viewmodel.CreateAffiliationViewModel
import life.qbic.portal.qoffer2.web.viewmodel.CreateCustomerViewModel
import life.qbic.portal.qoffer2.web.viewmodel.CreateOfferViewModel
import life.qbic.portal.qoffer2.web.viewmodel.SearchCustomerViewModel
import life.qbic.portal.qoffer2.web.views.CreateAffiliationView
import life.qbic.portal.qoffer2.web.views.CreateOfferView
import life.qbic.portal.qoffer2.web.views.PortletView
import life.qbic.portal.qoffer2.web.presenters.Presenter
import life.qbic.portal.qoffer2.web.viewmodel.ViewModel
import life.qbic.portal.qoffer2.web.controllers.CreateCustomerController
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

    private Presenter presenter
    private CreateCustomerPresenter createCustomerPresenter
    private CreateAffiliationPresenter createAffiliationPresenter
    private ListAffiliationsPresenter listAffiliationsPresenter
    private SearchCustomerPresenter searchCustomerPresenter
    private CreateOfferPresenter createOfferPresenter

    private CustomerDbConnector customerDbConnector
    private OfferDbConnector offerDbConnector
    private ProductsDbConnector productsDbConnector

    private CreateCustomer createCustomer
    private CreateAffiliation createAffiliation
    private ListAffiliations listAffiliations
    private SearchCustomer searchCustomer
    private CreateOffer createOffer
    private ListProducts listProducts

    private CreateCustomerController createCustomerController
    private CreateAffiliationController createAffiliationController
    private SearchCustomerController searchCustomerController
    private ListAffiliationsController listAffiliationsController
    private CreateOfferController createOfferController
    private ListProductsController listProductsController

    private CreateAffiliationView createAffiliationView
    private PortletView portletView
    private ConfigurationManager configurationManager

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
            //todo is there another DB to which we want to connect here?
            offerDbConnector = new OfferDbConnector(DatabaseSession.getInstance())
            productsDbConnector = new ProductsDbConnector(DatabaseSession.getInstance())
        } catch (Exception e) {
            log.error("Unexpected exception during customer database connection.", e)
            throw e
        }
    }

    private void setupViewModels() {
        // setup view models
        try {
            this.viewModel = new ViewModel()
        } catch (Exception e) {
            log.error("Unexpected excpetion during ${ViewModel.getSimpleName()} view model setup.", e)
            throw e
        }

        try {
            this.createCustomerViewModel = new CreateCustomerViewModel()
            createCustomerViewModel.academicTitles.addAll(AcademicTitle.values().collect {it.value})

        } catch (Exception e) {
            log.error("Unexpected excpetion during ${CreateCustomerViewModel.getSimpleName()} view model setup.", e)
            throw e
        }

        try {
            this.createAffiliationViewModel = new CreateAffiliationViewModel()
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
            this.createOfferViewModel = new CreateOfferViewModel()
            //todo add affiliations, customers and project managers to the model
        } catch (Exception e) {
            log.error("Unexpected excpetion during ${CreateOfferViewModel.getSimpleName()} view model setup.", e)
            throw e
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
            this.createOfferPresenter = new CreateOfferPresenter(this.viewModel, this.createOfferViewModel)
        } catch (Exception e) {
            log.error("Unexpected exception during ${CreateOfferViewModel.getSimpleName()} setup", e)
        }
    }

    private void setupUseCaseInteractors() {
        this.createCustomer = new CreateCustomer(createCustomerPresenter, customerDbConnector)
        this.createAffiliation = new CreateAffiliation(createAffiliationPresenter, customerDbConnector)
        this.listAffiliations = new ListAffiliations(listAffiliationsPresenter, customerDbConnector)
        this.createOffer = new CreateOffer(offerDbConnector, createOfferPresenter)
        this.listProducts = new ListProducts(productsDbConnector,createOfferPresenter)
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
            this.listProductsController = new ListProductsController(this.listProducts)
        } catch (Exception e) {
            log.error("Unexpected exception during ${ListProductsController.getSimpleName()} setup", e)
        }
    }

    private void setupViews() {

        CreateCustomerView createCustomerView
        try {
            createCustomerView = new CreateCustomerView(this.createCustomerController, this.viewModel, this.createCustomerViewModel)
            listAffiliationsController.listAffiliations()
        } catch (Exception e) {
            log.error("Could not create ${CreateCustomerView.getSimpleName()} view.", e)
            throw e
        }

        CreateAffiliationView createAffiliationView
        try {
            createAffiliationView = new CreateAffiliationView(this.viewModel, this.createAffiliationViewModel, this.createAffiliationController)
            this.createAffiliationView = createAffiliationView
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
            createOfferView = new CreateOfferView(this.viewModel, this.createOfferViewModel,this.createOfferController,this.listProductsController, createAffiliationView)
        } catch (Exception e) {
            log.error("Could not create ${CreateOfferView.getSimpleName()} view.", e)
            throw e
        }

        PortletView portletView
        try {
            portletView = new PortletView(this.viewModel, createCustomerView, createAffiliationView, searchCustomerView, createOfferView)
            this.portletView = portletView
        } catch (Exception e) {
            log.error("Could not create ${PortletView.getSimpleName()} view.", e)
            throw e
        }

        createCustomerView?.addAffiliationSelectionListener(portletView)
    }


}
