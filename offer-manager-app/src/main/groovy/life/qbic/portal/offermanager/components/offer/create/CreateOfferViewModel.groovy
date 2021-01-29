package life.qbic.portal.offermanager.components.offer.create

import groovy.beans.Bindable
import life.qbic.datamodel.dtos.business.Affiliation
import life.qbic.datamodel.dtos.business.Customer
import life.qbic.datamodel.dtos.business.OfferId
import life.qbic.datamodel.dtos.business.ProjectManager
import life.qbic.datamodel.dtos.business.services.*
import life.qbic.portal.offermanager.dataresources.persons.CustomerResourceService
import life.qbic.portal.offermanager.dataresources.persons.ProjectManagerResourceService
import life.qbic.portal.offermanager.dataresources.products.ProductsResourcesService
import life.qbic.portal.offermanager.communication.Subscription

/**
 * A ViewModel holding data that is presented in a
 * life.qbic.portal.qoffer2.web.viewmodel.CreateOfferViewModel
 *
 * This class holds all specific fields that are mutable in the view
 * Whenever values change it should be reflected in the corresponding view. This class can be used
 * for UI unit testing purposes.
 *
 * This class can contain JavaBean objects to enable views to listen to changes in the values.
 *
 * @since: 0.1.0
 *
 */
class CreateOfferViewModel {

    List<ProductItemViewModel> sequencingProducts = new ObservableList(new ArrayList<ProductItemViewModel>())
    List<ProductItemViewModel> primaryAnalysisProducts = new ObservableList(new ArrayList<ProductItemViewModel>())
    List<ProductItemViewModel> secondaryAnalysisProducts = new ObservableList(new ArrayList<ProductItemViewModel>())
    List<ProductItemViewModel> managementProducts = new ObservableList(new ArrayList<ProductItemViewModel>())
    List<ProductItemViewModel> storageProducts = new ObservableList(new ArrayList<ProductItemViewModel>())

    ObservableList productItems = new ObservableList(new ArrayList<ProductItemViewModel>())
    ObservableList foundCustomers = new ObservableList(new ArrayList<Customer>())
    ObservableList availableProjectManagers = new ObservableList(new ArrayList<ProjectManager>())

    @Bindable OfferId offerId
    @Bindable String projectTitle
    @Bindable String projectDescription
    @Bindable Customer customer
    @Bindable Affiliation customerAffiliation
    @Bindable ProjectManager projectManager
    @Bindable double offerPrice

    @Bindable double netPrice = 0
    @Bindable double taxes = 0
    @Bindable double overheads = 0
    @Bindable
    double totalPrice = 0

    private final CustomerResourceService customerService
    private final ProductsResourcesService productsResourcesService
    private final ProjectManagerResourceService managerResourceService

    CreateOfferViewModel(CustomerResourceService customerService,
                         ProjectManagerResourceService managerResourceService,
                         ProductsResourcesService productsResourcesService) {
        this.customerService = customerService
        this.productsResourcesService = productsResourcesService
        this.managerResourceService = managerResourceService
        fetchPersonData()
        fetchProductData()
        subscribeToResources()
    }

    private void fetchPersonData() {
        this.availableProjectManagers.clear()
        this.availableProjectManagers.addAll(managerResourceService.iterator())
        this.foundCustomers.clear()
        this.foundCustomers.addAll(customerService.iterator())
    }

    private void fetchProductData() {
        populateProductLists(productsResourcesService.getProducts())
    }

    private void subscribeToResources() {
        this.customerService.subscribe((Customer customer) -> {
            this.foundCustomers.clear()
            this.foundCustomers.addAll(customerService.iterator())
        })
        this.managerResourceService.subscribe((List<ProjectManager> managerList) -> {
            this.availableProjectManagers.clear()
            this.availableProjectManagers.addAll(managerResourceService.iterator())
        })

        Subscription<List<Product>> productSubscription = new Subscription<List<Product>>() {
            @Override
            void receive(List<Product> products) {
                populateProductLists(products)
            }
        }
        this.productsResourcesService.productEventEmitter.register(productSubscription)
    }

    private void populateProductLists(List<Product> products) {
        this.sequencingProducts.clear()
        this.managementProducts.clear()
        this.primaryAnalysisProducts.clear()
        this.secondaryAnalysisProducts.clear()
        this.storageProducts.clear()

        products.each { product ->
            ProductItemViewModel productItem = new ProductItemViewModel(0, product)

            switch (product) {
                case Sequencing:
                    sequencingProducts.add(productItem)
                    break
                case ProjectManagement:
                    managementProducts.add(productItem)
                    break
                case PrimaryAnalysis:
                    primaryAnalysisProducts.add(productItem)
                    break
                case SecondaryAnalysis:
                    secondaryAnalysisProducts.add(productItem)
                    break
                case DataStorage:
                    storageProducts.add(productItem)
                    break
                default:
                    // this should not happen
                    throw new RuntimeException("Unknown product category '${product.getClass().getSimpleName()}'")
            }
        }
    }

    /**
     * This method refreshes the data underlying the current view model
     */
    void refresh() {
        //TODO where and how to catch DatabaseQueryException ?
        refreshPersons()
        refreshProducts()
    }

    /**
     * This method triggers a refresh for all Person resources
     * @see life.qbic.datamodel.dtos.general.Person
     */
    void refreshPersons() {
        this.customerService.reloadResources()
    }

    /**
     * Calling this method triggers a refresh of all available Product resources.
     * @see Product
     */
    void refreshProducts() {
        this.productsResourcesService.reloadResources()
    }
}
