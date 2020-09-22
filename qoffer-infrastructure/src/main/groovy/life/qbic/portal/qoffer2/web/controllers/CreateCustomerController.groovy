package life.qbic.portal.qoffer2.web.controllers


import life.qbic.datamodel.dtos.business.Affiliation
import life.qbic.datamodel.dtos.business.Customer
import life.qbic.portal.portlet.customers.create.CreateCustomerInput

/**
 * Controller class adapter from view information into use case input interface
 *
 * This class translates the information that was received from the view into method calls to the use case
 *
 * @since: 1.0.0
 * @author: Jennifer Bödker
 */
class CreateCustomerController {

    CreateCustomerInput useCaseInput

    CreateCustomerController(CreateCustomerInput useCaseInput) {
        this.useCaseInput = useCaseInput
    }

    /**
     * This method starts the create customer use case based on information that is provided from the view
     *
     * @param firstName the first name of the customer
     * @param lastName the last name of the customer
     * @param title the title if any of the customer
     * @param email the email address of the customer
     * @param affiliations the affiliations of the customer
     *
     * @since 1.0.0
     */
    void createNewCustomer(String firstName, String lastName, String title, String email, List<? extends Affiliation> affiliations) {
        Customer customer = new Customer(firstName, lastName, title, email, affiliations as List<Affiliation>)
        this.useCaseInput.createCustomer(customer)
    }
}
