package life.qbic.portal.qoffer2.web.views

import com.vaadin.ui.FormLayout
import life.qbic.datamodel.dtos.business.ProductItem
import life.qbic.portal.portlet.customers.create.CreateCustomer
import life.qbic.portal.qoffer2.DependencyManager
import life.qbic.portal.qoffer2.web.controllers.CreateCustomerController
import life.qbic.portal.qoffer2.web.controllers.CreateOfferController
import life.qbic.portal.qoffer2.web.controllers.ListProductsController
import life.qbic.portal.qoffer2.web.viewmodel.CreateCustomerViewModel
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


    CreateOfferView(ViewModel sharedViewModel, CreateOfferViewModel createOfferViewModel, CreateOfferController controller, ListProductsController listProductsController, CreateCustomerView createCustomerView, CreateAffiliationView createAffiliationView) {
        super()
        this.sharedViewModel = sharedViewModel
        this.view = createOfferViewModel
        this.controller = controller
        this.listProductsController = listProductsController
        this.createCustomerView = createCustomerView
        this.createAffiliationView = createAffiliationView

        projectInformationView = new ProjectInformationView(view)
        customerSelectionView = new CustomerSelectionView(view)

        projectManagerSelectionView = new ProjectManagerSelectionView(view)
        selectItemsView = new SelectItemsView(view,sharedViewModel)
        overviewView = new OfferOverviewView(view)

        this.setSizeFull()

        initLayout()
        registerListeners()
        fetchData()
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
            this.removeComponent(projectInformationView)
            this.addComponent(customerSelectionView)
            navigationView.showNextStep()
        })
        this.customerSelectionView.next.addClickListener({
            this.removeComponent(customerSelectionView)
            this.addComponent(projectManagerSelectionView)
            navigationView.showNextStep()
        })
        this.customerSelectionView.previous.addClickListener({
            this.removeComponent(customerSelectionView)
            this.addComponent(projectInformationView)
            navigationView.showPreviousStep()
        })
        this.customerSelectionView.createCustomerButton.addClickListener({
            this.removeComponent(customerSelectionView)
            this.addComponent(createCustomerView)
        })
        this.createCustomerView.abortButton.addClickListener({
            this.removeComponent(createCustomerView)
            this.addComponent(customerSelectionView)
        })
        this.createCustomerView.submitButton.addClickListener({
            this.removeComponent(createCustomerView)
            this.addComponent(customerSelectionView)
        })
        this.customerSelectionView.createAffiliationButton.addClickListener({
            this.removeComponent(customerSelectionView)
            this.addComponent(createAffiliationView)
        })
        this.createAffiliationView.abortButton.addClickListener({
            this.removeComponent(createAffiliationView)
            this.addComponent(customerSelectionView)
        })
        this.createAffiliationView.submitButton.addClickListener({
            this.removeComponent(createAffiliationView)
            this.addComponent(customerSelectionView)
        })
        this.projectManagerSelectionView.next.addClickListener({
            this.removeComponent(projectManagerSelectionView)
            this.addComponent(selectItemsView)
            navigationView.showNextStep()
        })
        this.projectManagerSelectionView.previous.addClickListener({
            this.removeComponent(projectManagerSelectionView)
            this.addComponent(customerSelectionView)
            navigationView.showPreviousStep()
        })
        this.selectItemsView.next.addClickListener({
            this.removeComponent(selectItemsView)
            controller.calculatePriceForItems(getProductItems(view.productItems),view.customerAffiliation.category)
            overviewView.fillPanel()
            this.addComponent(overviewView)
            navigationView.showNextStep()
        })
        this.selectItemsView.previous.addClickListener({
            this.removeComponent(selectItemsView)
            this.addComponent(projectManagerSelectionView)
            navigationView.showPreviousStep()
        })
        this.overviewView.previous.addClickListener({
            this.removeComponent(overviewView)
            this.addComponent(selectItemsView)
            navigationView.showPreviousStep()
        })
        this.overviewView.save.addClickListener({
            controller.createOffer(view.projectTitle, view.projectDescription,view.customer,view.projectManager,
                    getProductItems(view.productItems),view.offerPrice,view.customerAffiliation)
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
}
