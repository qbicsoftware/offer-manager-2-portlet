package life.qbic.portal.qoffer2.web.viewmodel.create.offer

import groovy.beans.Bindable
import life.qbic.datamodel.dtos.business.Affiliation
import life.qbic.datamodel.dtos.business.Customer

/**
 * The view model for the {@link life.qbic.portal.qoffer2.web.views.create.offer.CustomerSelectionView}
 *
 * Describes the view components of the CustomerSelectionView
 *
 * @since: 0.1.0
 *
 */
class CustomerSelectionViewModel {

  @Bindable Customer customer
  @Bindable Affiliation customerAffiliation
  List<Customer> foundCustomers = []
}
