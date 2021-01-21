package life.qbic.portal.qoffer2.web.viewmodel

import groovy.beans.Bindable
import life.qbic.datamodel.dtos.business.Customer

/**
 * A ViewModel holding data that is presented in a
 * life.qbic.portal.qoffer2.web.views.SearchCustomerView
 *
 * This class holds all specific fields that are mutable in the view
 * Whenever values change it should be reflected in the corresponding view. This class can be used
 * for UI unit testing purposes.
 *
 *
 * @since: 1.0.0
 */
class SearchCustomerViewModel {

    final ObservableList foundCustomers = new ObservableList(new ArrayList<Customer>())

    @Bindable String firstName
    @Bindable String lastName

    @Bindable Boolean firstNameValid
    @Bindable Boolean lastNameValid

}
