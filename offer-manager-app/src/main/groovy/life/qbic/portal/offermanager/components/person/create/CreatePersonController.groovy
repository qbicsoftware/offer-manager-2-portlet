package life.qbic.portal.offermanager.components.person.create

import groovy.util.logging.Log4j2
import life.qbic.datamodel.dtos.business.AcademicTitle
import life.qbic.datamodel.dtos.business.AcademicTitleFactory
import life.qbic.datamodel.dtos.business.Affiliation
import life.qbic.datamodel.dtos.business.Customer
import life.qbic.business.persons.create.CreatePersonInput
import life.qbic.datamodel.dtos.general.Person

/**
 * Controller class adapter from view information into use case input interface
 *
 * This class translates the information that was received from the view into method calls to the use case
 *
 * @since 1.0.0
 */
@Log4j2
class CreatePersonController {

    private final CreatePersonInput useCaseInput

    CreatePersonController(CreatePersonInput useCaseInput) {
        this.useCaseInput = useCaseInput
    }

    /**
     * This method starts the create person use case based on information that is provided from the view
     *
     * @param firstName the first name of the person
     * @param lastName the last name of the person
     * @param title the title if any of the person. The title has to match the value of a known AcademicTitle.
     * @param email the email address of the person
     * @param affiliations the affiliations of the person
     *
     * @see AcademicTitle
     * @since 1.0.0
     */
    void createNewPerson(String firstName, String lastName, String title, String email, List<? extends Affiliation> affiliations) {
        AcademicTitleFactory academicTitleFactory = new AcademicTitleFactory()
        AcademicTitle academicTitle
        if (!title || title?.isEmpty()) {
            academicTitle = AcademicTitle.NONE
        } else {
            academicTitle = academicTitleFactory.getForString(title)
        }

        try {
            Person person = new Customer.Builder(firstName, lastName, email).title(academicTitle).affiliations(affiliations).build()
            this.useCaseInput.createPerson(person)
        } catch(Exception ignored) {
            throw new IllegalArgumentException("Could not create customer from provided arguments.")
        }
    }

    /**
     * This method creates a new person and triggers the create customer use case to update the old customer entry
     *
     * @param oldEntry The person that needs to be updated
     * @param firstName the first name of the person
     * @param lastName the last name of the person
     * @param title the title if any of the person. The title has to match the value of a known AcademicTitle.
     * @param email the email address of the person
     * @param affiliations the affiliations of the person
     *
     */
    void updatePerson(Person oldEntry, String firstName, String lastName, String title, String email, List<? extends Affiliation> affiliations){
        AcademicTitleFactory academicTitleFactory = new AcademicTitleFactory()
        AcademicTitle academicTitle
        if (!title || title?.isEmpty()) {
            academicTitle = AcademicTitle.NONE
        } else {
            academicTitle = academicTitleFactory.getForString(title)
        }

        try{
            Person person = new Customer.Builder(firstName, lastName, email).title(academicTitle).affiliations(affiliations).build()
            this.useCaseInput.updatePerson(oldEntry,person)
        }catch(Exception ignored) {
            throw new IllegalArgumentException("Could not update customer from provided arguments.")
        }
    }
}
