package life.qbic.portal.qoffer2.web.views

import com.vaadin.ui.Component
import com.vaadin.ui.FormLayout
import life.qbic.datamodel.dtos.business.ProductItem
import life.qbic.portal.qoffer2.web.controllers.CreateOfferController
import life.qbic.portal.qoffer2.web.controllers.ListProductsController
import life.qbic.portal.qoffer2.web.viewmodel.CreateOfferViewModel
import life.qbic.portal.qoffer2.web.viewmodel.ProductItemViewModel
import life.qbic.portal.qoffer2.web.viewmodel.ViewModel

import life.qbic.portal.qoffer2.web.views.create.offer.CustomerSelectionView
import life.qbic.portal.qoffer2.web.views.create.offer.OfferOverviewView
import life.qbic.portal.qoffer2.web.views.create.offer.ProjectInformationView
import life.qbic.portal.qoffer2.web.views.create.offer.ProjectManagerSelectionView
import life.qbic.portal.qoffer2.web.views.create.offer.SelectItemsView

/**
 * This class generates a Layout in which the user
 * can input the necessary information for the creation of a new offer
 *
 * CreateOfferView will be integrated into the qOffer 2.0 Portlet and provides an User Interface
 * with the intention of enabling a user the creation of a new Offer in the QBiC Database
 *
 * @since: 0.1.0
 *
 */
class CreateOfferView extends FormLayout{

    final private ViewModel sharedViewModel
    final private CreateOfferViewModel view

    final private CreateOfferController controller
    final private ListProductsController listProductsController

    final private ProjectInformationView projectInformationView
    final private CustomerSelectionView customerSelectionView
    final private ProjectManagerSelectionView projectManagerSelectionView
    final private SelectItemsView selectItemsView
    final private OfferOverviewView overviewView

    final private CreateCustomerView createCustomerView
    private ButtonNavigationView navigationView
    final private CreateAffiliationView createAffiliationView

    final private ViewHistory viewHistory


    CreateOfferView(ViewModel sharedViewModel, CreateOfferViewModel createOfferViewModel, CreateOfferController controller, ListProductsController listProductsController, CreateCustomerView createCustomerView, CreateAffiliationView createAffiliationView) {
        super()
        this.sharedViewModel = sharedViewModel
        this.view = createOfferViewModel
        this.controller = controller
        this.listProductsController = listProductsController
        this.createCustomerView = createCustomerView
        this.createAffiliationView = createAffiliationView
        this.projectInformationView = new ProjectInformationView(view)
        this.customerSelectionView = new CustomerSelectionView(view)

        this.projectManagerSelectionView = new ProjectManagerSelectionView(view)
        this.selectItemsView = new SelectItemsView(view,sharedViewModel)
        this.overviewView = new OfferOverviewView(view)

        initLayout()
        registerListeners()
        fetchData()

        // add all step views to the main view
        addStepViewsToCurrent()
        // hide all views
        hideViews()

        // Init the view navigation history to be able to navigate back in history
        this.viewHistory = new ViewHistory(projectInformationView)
    }

    /**
     * Initializes the view with the ProjectInformationView, which is the first component to be shown
     */
    private void initLayout(){
        navigationView = new ButtonNavigationView()
                .addNavigationItem("1. Project Information")
                .addNavigationItem("2. Select Customer")
                .addNavigationItem("3. Assign Project Manager")
                .addNavigationItem("4. Add Product Items")
                .addNavigationItem("5. Offer Overview")

        navigationView.showNextStep()
        this.addComponent(navigationView)
        this.addComponent(projectInformationView)
        this.setSizeFull()
    }

    /**
     * Fetch data from database for observable lists
     */
    private void fetchData(){
        listProductsController.listProducts()
        //todo add the initalization of data e.g. customer and PM here
    }

    /**
     * Registers all listeners for the buttons that enable switching between the different subviews of CreateOfferView and saving the offer
     */
    private void registerListeners() {

        this.projectInformationView.next.addClickListener({ event ->
            viewHistory.loadNewView(customerSelectionView)
            navigationView.showNextStep()
        })
        this.customerSelectionView.next.addClickListener({
            viewHistory.loadNewView(projectManagerSelectionView)
            navigationView.showNextStep()
        })
        this.customerSelectionView.previous.addClickListener({
            viewHistory.loadNewView(projectInformationView)
            navigationView.showPreviousStep()
        })
        this.customerSelectionView.createCustomerButton.addClickListener({
            viewHistory.loadNewView(createCustomerView)
        })
        this.createCustomerView.abortButton.addClickListener({
            viewHistory.showPrevious()
        })
        this.createCustomerView.submitButton.addClickListener({
            view.refresh()
            viewHistory.showPrevious()
        })
        this.customerSelectionView.createAffiliationButton.addClickListener({
            viewHistory.loadNewView(createAffiliationView)
        })
        this.createAffiliationView.abortButton.addClickListener({
            viewHistory.showPrevious()
        })
        this.createAffiliationView.submitButton.addClickListener({
            viewHistory.showPrevious()
        })
        this.projectManagerSelectionView.next.addClickListener({
            viewHistory.loadNewView(selectItemsView)
            navigationView.showNextStep()
        })
        this.projectManagerSelectionView.previous.addClickListener({
            viewHistory.loadNewView(customerSelectionView)
            navigationView.showPreviousStep()
        })
        this.selectItemsView.next.addClickListener({
            controller.calculatePriceForItems(getProductItems(view.productItems),view.customerAffiliation)
            overviewView.fillPanel()
            viewHistory.loadNewView(overviewView)
            navigationView.showNextStep()
        })
        this.selectItemsView.previous.addClickListener({
            viewHistory.loadNewView(projectManagerSelectionView)
            navigationView.showPreviousStep()
        })
        this.overviewView.previous.addClickListener({
            viewHistory.loadNewView(selectItemsView)
            navigationView.showPreviousStep()
        })
        this.overviewView.save.addClickListener({
            controller.createOffer(
                    view.projectTitle,
                    view.projectDescription,
                    view.customer,
                    view.projectManager,
                    getProductItems(view.productItems),
                    view.customerAffiliation)
        })
        this.createCustomerView.createAffiliationButton.addClickListener({
            viewHistory.loadNewView(createAffiliationView)
        })
    }

    /**
     * Translates the ProductItemViewModel into the ProductItem DTO
     * @param items The items of type ProductItemViewModel which are used from the view
     * @return list of items of type ProductItem
     */
    private static List<ProductItem> getProductItems(List<ProductItemViewModel> items){
        List<ProductItem> productItems = []
        items.each {
            productItems.add(new ProductItem(it.quantity,it.product))
        }
        return productItems
    }

    private void hideViews() {
        this.projectInformationView.setVisible(false)
        this.customerSelectionView.setVisible(false)
        this.createCustomerView.setVisible(false)
        this.createAffiliationView.setVisible(false)
        this.projectManagerSelectionView.setVisible(false)
        this.selectItemsView.setVisible(false)
        this.overviewView.setVisible(false)
    }

    private void addStepViewsToCurrent() {
        this.addComponents(
                this.projectInformationView,
                this.customerSelectionView,
                this.createCustomerView,
                this.createAffiliationView,
                this.projectManagerSelectionView,
                this.selectItemsView,
                this.overviewView)
    }

    /*
     * Small helper class that assists us keeping track of the view components
     * history.
     */
    private class ViewHistory {

        private List<Component> history

        private int currentPosition

        private Component currentView

        ViewHistory(Component c) {
            history = new LinkedList<>()
            currentPosition = 0
            currentView = c
            history.add(c)
            currentView.setVisible(true)
        }

        def loadNewView(Component view) {
            history = history.size() > 1 ? history.subList(0, currentPosition+1) : history
            history.add(view)
            currentView.setVisible(false)
            currentView = view
            currentView.setVisible(true)
            currentPosition++
        }

        def showNext() {
            if(currentPosition == history.size()-1) {
                // the current view is the latest view in history
            } else {
                currentPosition++
                currentView.setVisible(false)
                currentView = history.get(currentPosition)
                currentView.setVisible(true)
            }
        }

        def showPrevious() {
            if(currentPosition == 0) {
                // the current view is the oldest view in history
            } else {
                currentPosition -= 1
                currentView.setVisible(false)
                currentView = history.get(currentPosition)
                currentView.setVisible(true)
            }
        }
    }
}
