package life.qbic.portal.qoffer2.web.controllers

import groovy.util.logging.Log4j2
import life.qbic.datamodel.dtos.business.AcademicTitle
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
@Log4j2
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
     * @param title the title if any of the customer. The title has to match the value of a known AcademicTitle.
     * @param email the email address of the customer
     * @param affiliations the affiliations of the customer
     *
     * @see AcademicTitle
     * @since 1.0.0
     */
    void createNewCustomer(String firstName, String lastName, String title, String email, List<? extends Affiliation> affiliations) {
        AcademicTitle academicTitle
        academicTitle = AcademicTitle.values().find {it.getValue().equals(title)}
        if (!academicTitle) {
            throw new IllegalArgumentException("No ${AcademicTitle.getSimpleName()} found for $title")
        }
        Customer customer = new Customer(firstName, lastName, academicTitle, email, affiliations as List<Affiliation>)
        this.useCaseInput.createCustomer(customer)
    }
}
