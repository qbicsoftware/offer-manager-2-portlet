package life.qbic.portal.offermanager.components.offer.create

import groovy.beans.Bindable
import life.qbic.business.logging.Logger
import life.qbic.business.logging.Logging
import life.qbic.datamodel.dtos.business.*
import life.qbic.datamodel.dtos.business.services.*
import life.qbic.datamodel.dtos.general.Person
import life.qbic.portal.offermanager.communication.EventEmitter
import life.qbic.portal.offermanager.communication.Subscription
import life.qbic.portal.offermanager.dataresources.ResourcesService

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
 * @since 0.1.0
 */
class CreateOfferViewModel {

    List<Product> sequencingProducts = new ObservableList(new ArrayList<Product>())
    List<Product> primaryAnalysisProducts = new ObservableList(new ArrayList<Product>())
    List<Product> secondaryAnalysisProducts = new ObservableList(new ArrayList<Product>())
    List<Product> managementProducts = new ObservableList(new ArrayList<Product>())
    List<Product> storageProducts = new ObservableList(new ArrayList<Product>())
    List<Product> proteomicAnalysisProducts = new ObservableList(new ArrayList<Product>())
    List<Product> metabolomicAnalysisProduct = new ObservableList(new ArrayList<Product>())

    ObservableList productItems = new ObservableList(new ArrayList<ProductItemViewModel>())
    ObservableList foundCustomers = new ObservableList(new ArrayList<Customer>())
    ObservableList availableProjectManagers = new ObservableList(new ArrayList<ProjectManager>())

    @Bindable
    OfferId offerId
    @Bindable
    String projectTitle
    @Bindable
    String projectObjective
    @Bindable
    String experimentalDesign
    @Bindable
    Customer customer
    @Bindable
    Affiliation customerAffiliation
    @Bindable
    ProjectManager projectManager
    @Bindable
    double offerPrice

    @Bindable
    Boolean projectTitleValid = false
    @Bindable
    Boolean projectObjectiveValid = false
    @Bindable
    Boolean experimentalDesignValid = true

    @Bindable
    double netPrice = 0
    @Bindable
    double taxes = 0
    @Bindable
    double overheads = 0
    @Bindable
    double totalPrice = 0

    @Bindable
    Boolean sequencingGridSelected
    @Bindable
    Boolean primaryAnalysisGridSelected
    @Bindable
    Boolean secondaryAnalysisGridSelected
    @Bindable
    Boolean proteomicsAnalysisGridSelected
    @Bindable
    Boolean metabolomicsAnalysisGridSelected
    @Bindable
    Boolean projectManagementGridSelected
    @Bindable
    Boolean storageGridSelected

    @Bindable
    Boolean sequencingQuantityValid
    @Bindable
    Boolean primaryAnalysisQuantityValid
    @Bindable
    Boolean secondaryAnalysisQuantityValid
    @Bindable
    Boolean proteomicsAnalysisQuantityValid
    @Bindable
    Boolean metabolomicsAnalysisQuantityValid
    @Bindable
    Boolean projectManagementQuantityValid
    @Bindable
    Boolean storageQuantityValid

    @Bindable
    EventEmitter<String> resetViewRequired

    Optional<Offer> savedOffer = Optional.empty()

    private final ResourcesService<Customer> customerResourceService
    private final ResourcesService<Product> productsResourcesService
    private final ResourcesService<ProjectManager> managerResourceService
    // where to emit selection for updatable person to
    private final EventEmitter<Person> personUpdateEvent

    @Bindable
    Boolean offerCreatedSuccessfully

    private final Logging log = Logger.getLogger(this.class)

    CreateOfferViewModel(ResourcesService<Customer> customerResourceService,
                         ResourcesService<ProjectManager> managerResourceService,
                         ResourcesService<Product> productsResourcesService,
                         EventEmitter<Person> personUpdateEvent) {
        this.customerResourceService = customerResourceService
        this.productsResourcesService = productsResourcesService
        this.managerResourceService = managerResourceService
        this.personUpdateEvent = personUpdateEvent

        resetViewRequired = new EventEmitter<>()
        offerCreatedSuccessfully = false
        this.addPropertyChangeListener("offerCreatedSuccessfully", {
            if (it.newValue as Boolean)
                resetModel()
        })

        fetchPersonData()
        fetchProductData()
        subscribeToResources()
    }

    void addItem(ProductItemViewModel item) {
        // we don't do anything when the amount is equal or smaller zero
        if (item.quantity <= 0.0) {
            return
        }
        List<ProductItemViewModel> alreadyExistingItems =
                productItems.findAll { it.product.productId.equals(item.product.productId) } as List<ProductItemViewModel>
        double totalAmount = item.quantity
        for (ProductItemViewModel currentItem : alreadyExistingItems) {
            totalAmount = totalAmount + currentItem.quantity
        }
        productItems.add(new ProductItemViewModel(totalAmount, item.product))
        productItems.removeAll(alreadyExistingItems)
    }

    protected void resetModel() {
        offerCreatedSuccessfully = false

        offerId = null
        projectTitle = null
        projectObjective = null
        experimentalDesign = null
        customer = null
        customerAffiliation = null
        projectManager = null
        offerPrice = 0

        netPrice = 0
        taxes = 0
        overheads = 0
        totalPrice = 0

        productItems.clear()
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
    protected void refreshCustomers() {
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
    protected void refreshManagers() {
        List<ProjectManager> projectManagers = managerResourceService.iterator().toList()
        this.availableProjectManagers.clear()
        availableProjectManagers.addAll(projectManagers)
    }

    private void refreshProducts() {
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

            switch (product) {
                case Sequencing:
                    sequencingProducts.add(product)
                    break
                case ProjectManagement:
                    managementProducts.add(product)
                    break
                case PrimaryAnalysis:
                    primaryAnalysisProducts.add(product)
                    break
                case SecondaryAnalysis:
                    secondaryAnalysisProducts.add(product)
                    break
                case DataStorage:
                    storageProducts.add(product)
                    break
                case ProteomicAnalysis:
                    proteomicAnalysisProducts.add(product)
                    break
                case MetabolomicAnalysis:
                    metabolomicAnalysisProduct.add(product)
                    break
                default:
                    // this should not happen
                    throw new RuntimeException("Unknown product category '${product.getClass().getSimpleName()}'")
            }
        }
    }

    /**
     * Sets the customer for the offer. Whenever a change is detected an event is emitted.
     * @param customer
     */
    void setCustomer(Customer customer) {
        if (this.customer != customer) {
            personUpdateEvent.emit(customer)
        } else {
            log.debug("Overwrite of equal values. ${this.customer} to be overwritten with $customer. " +
                    "No event is fired.")
        }
        this.customer = customer
    }
}
