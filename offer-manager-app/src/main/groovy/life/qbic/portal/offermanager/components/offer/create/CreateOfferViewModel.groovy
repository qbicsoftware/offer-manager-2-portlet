package life.qbic.portal.offermanager.components.offer.create

import groovy.beans.Bindable
import groovy.transform.EqualsAndHashCode
import life.qbic.datamodel.dtos.business.Affiliation
import life.qbic.datamodel.dtos.business.Customer
import life.qbic.datamodel.dtos.business.Offer
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
    List<ProductItemViewModel> proteomicAnalysisProducts = new ObservableList(new ArrayList<ProductItemViewModel>())
    List<ProductItemViewModel> metabolomicAnalysisProduct = new ObservableList(new ArrayList<ProductItemViewModel>())

    ObservableList productItems = new ObservableList(new ArrayList<ProductItemViewModel>())
    ObservableList foundCustomers = new ObservableList(new ArrayList<Customer>())
    ObservableList availableProjectManagers = new ObservableList(new ArrayList<ProjectManager>())

    @Bindable OfferId offerId
    @Bindable String projectTitle
    @Bindable String projectObjective
    @Bindable Customer customer
    @Bindable Affiliation customerAffiliation
    @Bindable ProjectManager projectManager
    @Bindable double offerPrice

    @Bindable double netPrice = 0
    @Bindable double taxes = 0
    @Bindable double overheads = 0
    @Bindable double totalPrice = 0

    Optional<Offer> savedOffer = Optional.empty()

    private final CustomerResourceService customerResourceService
    private final ProductsResourcesService productsResourcesService
    private final ProjectManagerResourceService managerResourceService

    CreateOfferViewModel(CustomerResourceService customerResourceService,
                         ProjectManagerResourceService managerResourceService,
                         ProductsResourcesService productsResourcesService) {
        this.customerResourceService = customerResourceService
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
        this.foundCustomers.addAll(customerResourceService.iterator())
    }

    private void fetchProductData() {
        populateProductLists(productsResourcesService.iterator().toList())
    }

    private void subscribeToResources() {
        Subscription<Customer> customerSubscription = new Subscription<Customer>() {
            @Override
            void receive(Customer customer) {
                refreshCustomers()
            }
        }
        this.customerResourceService.subscribe(customerSubscription)

        Subscription<ProjectManager> managerSubscription = new Subscription<ProjectManager>() {
            @Override
            void receive(ProjectManager projectManager) {
                refreshManagers()
            }
        }
        this.managerResourceService.subscribe(managerSubscription)

        Subscription<Product> productSubscription = new Subscription<Product>() {
            @Override
            void receive(Product product) {
                refreshProducts()
            }
        }
        this.productsResourcesService.subscribe(productSubscription)
    }

    /**
     * This method replaces the foundCustomer list with the list provided by the customerResourceService
     *
     * This method will be triggered when a service event is triggered and is intended
     * to refresh the customers shown in the grid with the ones currently stored in tce service
     */
    protected void refreshCustomers(){
        List<Customer> customers = customerResourceService.iterator().toList()
        this.foundCustomers.clear()
        foundCustomers.addAll(customers)
    }

    /**
     * This method replaces the availableProjectManager list with the list provided by the managerResourceService
     *
     * This method will be triggered when a service event is triggered and is intended
     * to refresh the project managers shown in the grid with the ones currently stored in the service
     */
    protected void refreshManagers(){
        List<ProjectManager> projectManagers = managerResourceService.iterator().toList()
        this.availableProjectManagers.clear()
        availableProjectManagers.addAll(projectManagers)
    }

    private void refreshProducts(){
        List<Product> products = productsResourcesService.iterator().toList()
        populateProductLists(products)
    }

    private void populateProductLists(List<Product> products) {
        this.sequencingProducts.clear()
        this.managementProducts.clear()
        this.primaryAnalysisProducts.clear()
        this.secondaryAnalysisProducts.clear()
        this.storageProducts.clear()
        this.proteomicAnalysisProducts.clear()
        this.metabolomicAnalysisProduct.clear()

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
                case ProteomicAnalysis:
                    proteomicAnalysisProducts.add(productItem)
                    break
                case MetabolomicAnalysis:
                    metabolomicAnalysisProduct.add(productItem)
                    break
                default:
                    // this should not happen
                    throw new RuntimeException("Unknown product category '${product.getClass().getSimpleName()}'")
            }
        }
    }
}
