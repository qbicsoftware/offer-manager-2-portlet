package life.qbic.portal.qoffer2.web.controllers


import life.qbic.datamodel.dtos.business.Affiliation
import life.qbic.datamodel.dtos.business.Customer
import life.qbic.portal.portlet.customers.create.CreateCustomerInput

/**
 * Controller class responsible for the data flow into qoffer-2
 *
 * This class creates instances of qoffer-2 classes and injects them as described in the architectural draft.
 *
 * @since: 1.0.0
 * @author: Jennifer Bödker
 */
class CreateCustomerController {

    CreateCustomerInput useCaseInput

    CreateCustomerController(CreateCustomerInput useCaseInput) {
        this.useCaseInput = useCaseInput
    }

    public void createNewCustomer(String firstName, String lastName, String title, String email, List<? extends Affiliation> affiliations) {
        //todo add title to the dto
        Customer customer = new Customer(firstName, lastName, title, email, affiliations as List<Affiliation>)
        this.useCaseInput.createCustomer(customer)
    }
}
