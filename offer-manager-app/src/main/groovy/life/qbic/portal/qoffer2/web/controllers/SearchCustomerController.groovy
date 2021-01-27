package life.qbic.portal.qoffer2.web.controllers


import life.qbic.business.customers.search.SearchCustomerInput

/**
 * Controller class adapter which in from view information into use case input interface
 *
 * This class translates the information that was received from the view into method calls to the use case
 *
 * @since: 1.0.0
 *
 */
class SearchCustomerController {

    SearchCustomerInput input

    SearchCustomerController(SearchCustomerInput input){
        this.input = input
    }

    /**
     * Search customer by its first and last name
     * @param firstName of the customer
     * @param lastName of the customer
     */
    void searchCustomerByName(String firstName, String lastName){
        try {
            input.searchCustomer(firstName,lastName)
        } catch(Exception ignored) {
            throw new IllegalArgumentException("Could not search customer from provided arguments.")
        }
    }

}
