package life.qbic.portal.qoffer2.web.viewmodel

import groovy.beans.Bindable
import life.qbic.datamodel.dtos.business.Affiliation
import life.qbic.datamodel.dtos.business.Customer
import life.qbic.datamodel.dtos.business.ProductItem
import life.qbic.datamodel.dtos.business.ProjectManager
import life.qbic.datamodel.dtos.business.services.Product
import life.qbic.portal.qoffer2.web.viewmodel.create.offer.CustomerSelectionViewModel
import life.qbic.portal.qoffer2.web.viewmodel.create.offer.OfferOverviewViewModel
import life.qbic.portal.qoffer2.web.views.create.offer.CustomerSelectionView

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
    private OfferOverviewViewModel offerOverviewViewModel
    
    CreateOfferViewModel( OfferOverviewViewModel offerOverviewViewModel) {
      this.offerOverviewViewModel = offerOverviewViewModel
    }

    String getProjectTitle() {
        return this.offerOverviewViewModel.projectTitle
    }

    String getProjectDescription() {
        return this.offerOverviewViewModel.projectDescription
    }

    Customer getSelectedCustomer() {
        return this.offerOverviewViewModel.selectedCustomer
    }

    Affiliation getCustomerAffiliation() {
        return this.offerOverviewViewModel.customerAffiliation
    }

    ProjectManager getSelectedProjectManager() {
        return this.offerOverviewViewModel.selectedProjectManager
    }

    List getSelectedProductItems() {
        return this.offerOverviewViewModel.selectedProductItems
    }

    double getOfferPrice() {
        return this.offerOverviewViewModel.offerPrice
    }
}
