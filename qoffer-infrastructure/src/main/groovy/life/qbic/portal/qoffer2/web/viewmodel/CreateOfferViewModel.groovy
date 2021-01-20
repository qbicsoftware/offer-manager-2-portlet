package life.qbic.portal.qoffer2.web.viewmodel

import groovy.beans.Bindable
import life.qbic.datamodel.dtos.business.Affiliation
import life.qbic.datamodel.dtos.business.Customer
import life.qbic.datamodel.dtos.business.OfferId
import life.qbic.datamodel.dtos.business.ProjectManager
import life.qbic.datamodel.dtos.business.services.*
import life.qbic.portal.qoffer2.customers.PersonResourcesService
import life.qbic.portal.qoffer2.products.ProductsResourcesService

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

    List<ProductItemViewModel> sequencingProducts =  new ObservableList(new ArrayList<ProductItemViewModel>())
    List<ProductItemViewModel> primaryAnalysisProducts =  new ObservableList(new ArrayList<ProductItemViewModel>())
    List<ProductItemViewModel> secondaryAnalysisProducts =  new ObservableList(new ArrayList<ProductItemViewModel>())
    List<ProductItemViewModel> managementProducts =  new ObservableList(new ArrayList<ProductItemViewModel>())
    List<ProductItemViewModel> storageProducts =  new ObservableList(new ArrayList<ProductItemViewModel>())

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

    private final PersonResourcesService personService
    private final ProductsResourcesService productsResourcesService

    CreateOfferViewModel(PersonResourcesService personService, ProductsResourcesService productsResourcesService) {
        this.personService = personService
        this.productsResourcesService = productsResourcesService
        this.availableProjectManagers = personService.getProjectManagers()
        this.foundCustomers = personService.getCustomers()
        populateProductLists(productsResourcesService.getProducts())
    }

    private void subscribeToResources() {
        this.personService.customerEvent.register((List<Customer> customerList) -> {
            this.foundCustomers.clear()
            this.foundCustomers.addAll(customerList)
        })
        this.personService.projectManagerEvent.register((List<ProjectManager> managerList) -> {
            this.availableProjectManagers.clear()
            this.availableProjectManagers.addAll(managerList)
        })
        this.productsResourcesService.productEventEmitter.register((List<Product> products) -> {
            populateProductLists(products)
        })
    }

    private void populateProductLists(List<Product> products) {
        this.sequencingProducts.clear()
        this.managementProducts.clear()
        this.primaryAnalysisProducts.clear()
        this.secondaryAnalysisProducts.clear()
        this.storageProducts.clear()

        products.each { product ->
            ProductItemViewModel productItem = new ProductItemViewModel(0, product)

            if (product instanceof Sequencing) {
                this.sequencingProducts.add(productItem)
            } else if (product instanceof ProjectManagement) {
                this.managementProducts.add(productItem)
            } else if (product instanceof PrimaryAnalysis) {
                this.primaryAnalysisProducts.add(productItem)
            } else if (product instanceof SecondaryAnalysis) {
                this.secondaryAnalysisProducts.add(productItem)
            } else if (product instanceof DataStorage) {
                this.storageProducts.add(productItem)
            }
        }
    }

    void refresh() {
        refreshPersons()
        refreshProducts()
    }

    void refreshPersons() {
        this.personService.reloadResources()
    }

    void refreshProducts() {
        this.productsResourcesService.reloadResources()
    }
}
