package life.qbic.portal.offermanager.components.createperson

import groovy.util.logging.Log4j2
import life.qbic.datamodel.dtos.business.AcademicTitle
import life.qbic.datamodel.dtos.business.AcademicTitleFactory
import life.qbic.datamodel.dtos.business.Affiliation
import life.qbic.datamodel.dtos.business.Customer
import life.qbic.business.customers.create.CreateCustomerInput

/**
 * Controller class adapter from view information into use case input interface
 *
 * This class translates the information that was received from the view into method calls to the use case
 *
 * @since: 1.0.0
 * @author: Jennifer Bödker
 */
@Log4j2
class CreatePersonController {

    private final CreateCustomerInput useCaseInput

    CreatePersonController(CreateCustomerInput useCaseInput) {
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
        AcademicTitleFactory academicTitleFactory = new AcademicTitleFactory()
        AcademicTitle academicTitle
        if (!title || title?.isEmpty()) {
            academicTitle = AcademicTitle.NONE
        } else {
            academicTitle = academicTitleFactory.getForString(title)
        }

        try {
            Customer customer = new Customer.Builder(firstName, lastName, email).title(academicTitle).affiliations(affiliations).build()
            this.useCaseInput.createCustomer(customer)
        } catch(Exception ignored) {
            throw new IllegalArgumentException("Could not create customer from provided arguments.")
        }
    }
}
