package life.qbic.portal.offermanager.components.createoffer

import com.vaadin.ui.Component
import com.vaadin.ui.FormLayout
import life.qbic.datamodel.dtos.business.ProductItem
import life.qbic.portal.offermanager.components.createaffiliation.CreateAffiliationView
import life.qbic.portal.offermanager.components.createoffer.customerselection.CustomerSelectionView
import life.qbic.portal.offermanager.components.createoffer.overview.OfferOverviewView
import life.qbic.portal.offermanager.components.createperson.CreatePersonView
import life.qbic.portal.offermanager.dataresources.offers.OfferResourcesService
import life.qbic.portal.offermanager.web.viewmodel.ProductItemViewModel
import life.qbic.portal.offermanager.components.AppViewModel
import life.qbic.portal.offermanager.components.createoffer.projectinformation.ProjectInformationView
import life.qbic.portal.offermanager.components.createoffer.managerselection.ProjectManagerSelectionView
import life.qbic.portal.offermanager.components.createoffer.productitemselection.SelectItemsView

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

    final private AppViewModel sharedViewModel
    final private CreateOfferViewModel viewModel

    private final CreateOfferController controller

    private final ProjectInformationView projectInformationView
    private final CustomerSelectionView customerSelectionView
    private final ProjectManagerSelectionView projectManagerSelectionView
    private final SelectItemsView selectItemsView
    private final OfferOverviewView overviewView

    private final CreatePersonView createCustomerView
    private ButtonNavigationView navigationView
    private final CreateAffiliationView createAffiliationView

    private final ViewHistory viewHistory


    CreateOfferView(AppViewModel sharedViewModel,
                    CreateOfferViewModel createOfferViewModel,
                    CreateOfferController controller,
                    CreatePersonView createCustomerView,
                    CreateAffiliationView createAffiliationView,
                    OfferResourcesService offerProviderService
    ) {
        super()
        this.sharedViewModel = sharedViewModel
        this.viewModel = createOfferViewModel
        this.controller = controller
        this.createCustomerView = createCustomerView
        this.createAffiliationView = createAffiliationView
        this.projectInformationView = new ProjectInformationView(viewModel)
        this.customerSelectionView = new CustomerSelectionView(viewModel)

        this.projectManagerSelectionView = new ProjectManagerSelectionView(viewModel)
        this.selectItemsView = new SelectItemsView(viewModel,sharedViewModel)
        this.overviewView = new OfferOverviewView(viewModel, offerProviderService)

        initLayout()
        registerListeners()

        // add all step views to the main view
        addStepViewsToCurrent()
        // hide all views
        hideViews()

        // Init the view navigation history to be able to navigate back in history
        this.viewHistory = new ViewHistory(projectInformationView)

        viewModel.refresh()
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
        this.addComponents(
                projectInformationView,
                customerSelectionView,
                createCustomerView,
                projectManagerSelectionView,
                selectItemsView,
                overviewView
        )
        this.setSizeFull()
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
            viewModel.refreshPersons()
            viewHistory.showPrevious()
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
            controller.calculatePriceForItems(getProductItems(viewModel.productItems),
                    viewModel.customerAffiliation)
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
                    viewModel.offerId,
                    viewModel.projectTitle,
                    viewModel.projectDescription,
                    viewModel.customer,
                    viewModel.projectManager,
                    getProductItems(viewModel.productItems),
                    viewModel.customerAffiliation)
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
