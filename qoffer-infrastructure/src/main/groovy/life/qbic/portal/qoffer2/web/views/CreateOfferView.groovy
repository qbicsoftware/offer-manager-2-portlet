package life.qbic.portal.qoffer2.web.views

import com.vaadin.ui.FormLayout
import life.qbic.datamodel.dtos.business.ProductItem
import life.qbic.portal.qoffer2.web.controllers.CreateOfferController
import life.qbic.portal.qoffer2.web.controllers.ListProductsController

import life.qbic.portal.qoffer2.web.viewmodel.CreateOfferViewModel
import life.qbic.portal.qoffer2.web.viewmodel.ProductItemViewModel
import life.qbic.portal.qoffer2.web.viewmodel.ViewModel
import life.qbic.portal.qoffer2.web.viewmodel.create.offer.OfferOverviewViewModel
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
    final private CreateOfferViewModel viewModel
    final private OfferOverviewViewModel offerOverviewViewModel
    final private CreateOfferController controller
    final private ListProductsController listProductsController

    final private ProjectInformationView projectInformationView
    final private CustomerSelectionView customerSelectionView
    final private ProjectManagerSelectionView projectManagerSelectionView
    final private SelectItemsView selectItemsView
    final private OfferOverviewView overviewView


    CreateOfferView(ViewModel sharedViewModel, CreateOfferViewModel createOfferViewModel, CreateOfferController controller, ListProductsController listProductsController,
    ProjectInformationView projectInformationView, CustomerSelectionView customerSelectionView, ProjectManagerSelectionView projectManagerSelectionView,
    SelectItemsView selectItemsView, OfferOverviewView offerOverviewView, OfferOverviewViewModel offerOverviewViewModel) {
        super()
        this.sharedViewModel = sharedViewModel
        this.viewModel = createOfferViewModel
        this.offerOverviewViewModel = offerOverviewViewModel
        this.controller = controller
        this.listProductsController = listProductsController

        this.projectInformationView = projectInformationView
        this.customerSelectionView = customerSelectionView
        this.projectManagerSelectionView = projectManagerSelectionView
        this.selectItemsView = selectItemsView
        this.overviewView = offerOverviewView

        initLayout()
        registerListeners()
    }

    /**
     * Initializes the view with the ProjectInformationView, which is the first component to be shown
     */
    private void initLayout(){
        this.addComponent(projectInformationView)
    }

    /**
     * Registers all listeners for the buttons that enable switching between the different subviews of CreateOfferView and saving the offer
     */
    private void registerListeners() {
        this.projectInformationView.next.addClickListener({ event ->
            this.removeComponent(projectInformationView)
            this.addComponent(customerSelectionView)
        })
        this.customerSelectionView.next.addClickListener({
            this.removeComponent(customerSelectionView)
            this.addComponent(projectManagerSelectionView)
        })
        this.customerSelectionView.previous.addClickListener({
            this.removeComponent(customerSelectionView)
            this.addComponent(projectInformationView)
        })
        this.projectManagerSelectionView.next.addClickListener({
            this.removeComponent(projectManagerSelectionView)
            listProductsController.listProducts()
            this.addComponent(selectItemsView)
        })
        this.projectManagerSelectionView.previous.addClickListener({
            this.removeComponent(projectManagerSelectionView)
            this.addComponent(customerSelectionView)
        })
        this.selectItemsView.next.addClickListener({
            this.removeComponent(selectItemsView)
            controller.calculatePriceForItems(getProductItems(viewModel.selectedProductItems),viewModel.customerAffiliation.category)
            overviewView.fillPanel()
            this.addComponent(overviewView)
        })
        this.selectItemsView.previous.addClickListener({
            this.removeComponent(selectItemsView)
            this.addComponent(projectManagerSelectionView)
        })
        this.overviewView.previous.addClickListener({
            this.removeComponent(overviewView)
            this.addComponent(selectItemsView)
        })
        this.overviewView.save.addClickListener({
            controller.createOffer(viewModel.projectTitle,
                    viewModel.projectDescription,
                    viewModel.selectedCustomer,
                    viewModel.selectedProjectManager,
                    getProductItems(viewModel.selectedProductItems),
                    viewModel.offerPrice,
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
}
