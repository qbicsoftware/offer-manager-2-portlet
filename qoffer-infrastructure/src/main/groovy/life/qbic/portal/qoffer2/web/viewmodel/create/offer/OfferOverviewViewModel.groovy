package life.qbic.portal.qoffer2.web.viewmodel.create.offer

import groovy.beans.Bindable
import life.qbic.datamodel.dtos.business.Affiliation
import life.qbic.datamodel.dtos.business.Customer
import life.qbic.datamodel.dtos.business.ProjectManager

/**
 * The view model for the {@link life.qbic.portal.qoffer2.web.views.create.offer.OfferOverviewView}
 *
 * Describes the view components of the OfferOverviewView
 *
 * @since: 0.1.0
 *
 */
class OfferOverviewViewModel {

    private ProjectInformationViewModel projectInformationViewModel
    private CustomerSelectionViewModel customerSelectionViewModel
    private ProjectManagerSelectionViewModel projectManagerSelectionViewModel
    private SelectItemsViewModel selectItemsViewModel

    @Bindable double offerPrice

    OfferOverviewViewModel(ProjectInformationViewModel projectInformationViewModel, CustomerSelectionViewModel customerSelectionViewModel
                           , ProjectManagerSelectionViewModel projectManagerSelectionViewModel, SelectItemsViewModel selectItemsViewModel){
        this.projectInformationViewModel = projectInformationViewModel
        this.customerSelectionViewModel = customerSelectionViewModel
        this.projectManagerSelectionViewModel = projectManagerSelectionViewModel
        this.selectItemsViewModel = selectItemsViewModel
    }

    String getProjectTitle() {
        return this.projectInformationViewModel.projectTitle
    }

    String getProjectDescription() {
        return this.projectInformationViewModel.projectDescription
    }

    Customer getSelectedCustomer() {
        return this.customerSelectionViewModel.customer
    }

    Affiliation getCustomerAffiliation() {
        return this.customerSelectionViewModel.customerAffiliation
    }

    ProjectManager getSelectedProjectManager() {
        return this.projectManagerSelectionViewModel.projectManager
    }

    List getSelectedProductItems() {
        return this.selectItemsViewModel.selectedProductItems
    }
}

