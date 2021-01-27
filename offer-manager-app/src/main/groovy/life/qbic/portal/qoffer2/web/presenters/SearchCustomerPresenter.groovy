package life.qbic.portal.qoffer2.web.presenters

import life.qbic.datamodel.dtos.business.Customer
import life.qbic.business.customers.search.SearchCustomerOutput
import life.qbic.portal.qoffer2.web.viewmodel.SearchCustomerViewModel
import life.qbic.portal.qoffer2.web.viewmodel.ViewModel

/**
 * Presenter for the SearchCustomerView
 *
 * This presenter handles the output of the Search Customer use case and prepares it for the
 * SearchCustomerView
 *
 * @since: 1.0.0
 */
class SearchCustomerPresenter implements SearchCustomerOutput {
    private ViewModel viewModel
    private SearchCustomerViewModel searchCustomerViewModel

    SearchCustomerPresenter(ViewModel viewModel, SearchCustomerViewModel searchCustomerViewModel) {
        this.viewModel = viewModel
        this.searchCustomerViewModel = searchCustomerViewModel
    }

    @Override
    void successNotification(List<Customer> customers) {
        searchCustomerViewModel.foundCustomers.clear()
        searchCustomerViewModel.foundCustomers.addAll(customers)
    }

    /**
     * Sends failure notifications that have been
     * recorded during the use case.
     * @param notification containing a failure message
     * @since 1.0.0
     */
    @Override
    void failNotification(String notification) {
        viewModel.failureNotifications.add(notification)
    }
}
