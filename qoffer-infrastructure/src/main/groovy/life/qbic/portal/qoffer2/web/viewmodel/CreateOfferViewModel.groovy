package life.qbic.portal.qoffer2.web.viewmodel

import groovy.beans.Bindable
import life.qbic.datamodel.dtos.business.Affiliation
import life.qbic.datamodel.dtos.business.Customer
import life.qbic.datamodel.dtos.business.ProjectManager
import life.qbic.datamodel.dtos.business.services.Product
import life.qbic.portal.qoffer2.events.Subscription
import life.qbic.portal.qoffer2.services.CustomerService

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

    List<Customer> foundCustomers = []
    List<ProjectManager> projectManagers = []


    @Bindable String projectTitle
    @Bindable String projectDescription
    @Bindable Customer customer
    @Bindable Affiliation customerAffiliation
    @Bindable ProjectManager projectManager
    @Bindable List<ProductItemViewModel> productItems
    @Bindable double offerPrice

    @Bindable double netPrice = 0
    @Bindable double taxes = 0
    @Bindable double overheads = 0
    @Bindable double totalPrice = 0

    final private CustomerService customerService

    CreateOfferViewModel(CustomerService customerService) {
        this.customerService = customerService
        this.customerService.eventEmitter.register( (List<Customer> customerList) -> {
            this.foundCustomers = customerList
        })
        this.customerService.reloadResources()
    }

    void refresh() {
        this.customerService.reloadResources()
    }
}
