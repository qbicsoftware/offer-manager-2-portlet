package life.qbic.portal.qoffer2.web.presenters

import life.qbic.datamodel.dtos.business.AcademicTitle
import life.qbic.datamodel.dtos.business.Customer
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
class SearchCustomerPresenter {
    private ViewModel viewModel
    private SearchCustomerViewModel searchCustomerViewModel

    SearchCustomerPresenter(ViewModel viewModel, SearchCustomerViewModel searchCustomerViewModel) {
        this.viewModel = viewModel
        this.searchCustomerViewModel = searchCustomerViewModel
        //ToDo Remove this list as soon as the backend is implemented
        List<Customer> customerList = new ArrayList<Customer>()
        def customer1 = new Customer("Ash", "Ketchum", AcademicTitle.NONE, "gottacatchem@all.de", [])
        def customer2 = new Customer("Samuel", "Oak", AcademicTitle.PROFESSOR, "giveMeMyPokemon@geezer.de", [])
        def customer3 = new Customer("Pikachu", "Pichu", AcademicTitle.DOCTOR, "Pokdex@25.de", [])
        customerList.add(customer1)
        customerList.add(customer2)
        customerList.add(customer3)
        reportFoundCustomers(customerList)
    }

    void reportFoundCustomers(List<Customer> customers) {
        searchCustomerViewModel.foundCustomers.clear()
        searchCustomerViewModel.foundCustomers.addAll(customers)
    }

}
