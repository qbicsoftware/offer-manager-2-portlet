package life.qbic.portal.qoffer2.web.viewmodel

import groovy.beans.Bindable
import life.qbic.datamodel.dtos.business.Affiliation
import life.qbic.datamodel.dtos.business.Customer
import life.qbic.datamodel.dtos.business.ProjectManager
import life.qbic.datamodel.dtos.business.services.Product

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

    List<ProductItemViewModel> sequencingProducts = [] as ObservableList
    List<ProductItemViewModel> primaryAnalysisProducts = [] as ObservableList
    List<ProductItemViewModel> secondaryAnalysisProducts = [] as ObservableList
    List<ProductItemViewModel> managementProducts = [] as ObservableList
    List<ProductItemViewModel> storageProducts = [] as ObservableList

    List<Customer> foundCustomers = []
    List<ProjectManager> projectManagers = []


    @Bindable String projectTitle
    @Bindable String projectDescription
    @Bindable Customer customer
    @Bindable Affiliation customerAffiliation
    @Bindable ProjectManager projectManager
    @Bindable List<ProductItemViewModel> productItems
    @Bindable double offerPrice
}
