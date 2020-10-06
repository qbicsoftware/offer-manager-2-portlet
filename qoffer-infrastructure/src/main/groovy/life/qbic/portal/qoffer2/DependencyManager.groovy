package life.qbic.portal.qoffer2

import groovy.util.logging.Log4j2
import life.qbic.datamodel.dtos.business.AcademicTitle
import life.qbic.portal.portlet.customers.CustomerDbGateway
import life.qbic.portal.portlet.customers.affiliation.create.CreateAffiliation
import life.qbic.portal.portlet.customers.create.CreateCustomer
import life.qbic.portal.qoffer2.customers.CustomerDatabaseQueries
import life.qbic.portal.qoffer2.customers.CustomerDbConnector
import life.qbic.portal.qoffer2.database.DatabaseSession
import life.qbic.portal.qoffer2.web.controllers.CreateAffiliationController
import life.qbic.portal.qoffer2.web.presenters.CreateAffiliationPresenter
import life.qbic.portal.qoffer2.web.presenters.CreateCustomerPresenter
import life.qbic.portal.qoffer2.web.viewmodel.CreateAffiliationViewModel
import life.qbic.portal.qoffer2.web.viewmodel.CreateCustomerViewModel
import life.qbic.portal.qoffer2.web.views.CreateAffiliationView
import life.qbic.portal.qoffer2.web.views.PortletView
import life.qbic.portal.qoffer2.web.presenters.Presenter
import life.qbic.portal.qoffer2.web.viewmodel.ViewModel
import life.qbic.portal.qoffer2.web.controllers.CreateCustomerController
import life.qbic.portal.qoffer2.web.views.CreateCustomerView
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
    private Presenter presenter
    private CreateCustomerPresenter createCustomerPresenter
    private CreateAffiliationPresenter createAffiliationPresenter

    private CustomerDbGateway customerDbGateway
    private CreateCustomer createCustomer
    private CreateAffiliation createAffiliation
    private CreateCustomerController createCustomerController
    private CreateAffiliationController createAffiliationController

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

    private void setupViewModels() {
        // setup view models
        try {
            this.viewModel = new ViewModel()
            viewModel.affiliations.addAll(customerDbGateway.allAffiliations)
            viewModel.academicTitles.addAll(AcademicTitle.values().collect {it.value})
        } catch (Exception e) {
            log.error("Unexpected excpetion during ${ViewModel.getSimpleName()} view model setup.", e)
            throw e
        }

        try {
            this.createCustomerViewModel = new CreateCustomerViewModel()
        } catch (Exception e) {
            log.error("Unexpected excpetion during ${CreateCustomerViewModel.getSimpleName()} view model setup.", e)
            throw e
        }

        try {
            this.createAffiliationViewModel = new CreateAffiliationViewModel()
        } catch (Exception e) {
            log.error("Unexpected excpetion during ${CreateAffiliationViewModel.getSimpleName()} view model setup.", e)
            throw e
        }
    }

    private void setupDbConnections() {
        try {

            String user = configurationManager.getMysqlUser()
            String password = configurationManager.getMysqlPass()
            String host = configurationManager.getMysqlHost()
            String port = configurationManager.getMysqlPort()
            String sqlDatabase = configurationManager.getMysqlDB()

            DatabaseSession.create(user, password, host, port, sqlDatabase)
            CustomerDatabaseQueries queries = new CustomerDatabaseQueries(DatabaseSession.INSTANCE)
            customerDbGateway = new CustomerDbConnector(queries)
        } catch (Exception e) {
            log.error("Unexpected exception during customer database connection.", e)
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
    }

    private void setupUseCaseInteractors() {
        this.createCustomer = new CreateCustomer(createCustomerPresenter, customerDbGateway)
        this.createAffiliation = new CreateAffiliation(createAffiliationPresenter, customerDbGateway)
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
    }

    private void setupViews() {
        CreateCustomerView createCustomerView
        try {
            createCustomerView = new CreateCustomerView(this.createCustomerController, this.viewModel, this.createCustomerViewModel)
        } catch (Exception e) {
            log.error("Could not create ${CreateCustomerView.getSimpleName()} view.", e)
            throw e
        }

        CreateAffiliationView createAffiliationView
        try {
            createAffiliationView = new CreateAffiliationView(this.viewModel, this.createAffiliationViewModel, this.createAffiliationController)
        } catch (Exception e) {
            log.error("Could not create ${CreateAffiliationView.getSimpleName()} view.", e)
            throw e
        }

        PortletView portletView
        try {
            portletView = new PortletView(this.viewModel, createCustomerView, createAffiliationView)
            this.portletView = portletView
        } catch (Exception e) {
            log.error("Could not create ${PortletView.getSimpleName()} view.", e)
            throw e
        }
    }


}
