package life.qbic.portal.offermanager.components.person.create

import groovy.util.logging.Log4j2
import life.qbic.business.persons.Person
import life.qbic.business.persons.affiliation.Affiliation
import life.qbic.business.persons.create.CreatePersonInput
import life.qbic.datamodel.dtos.business.AcademicTitle

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
        try {
            Person person = new Person(email, firstName, lastName, title, email, affiliations)
            this.useCaseInput.createPerson(person) //FIXME use new person in use case
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
        try{
            Person person = new Person(oldEntry.userId, firstName, lastName, title, email, affiliations)
            this.useCaseInput.updatePerson(oldEntry, person) //FIXME use new person in use case
        }catch(Exception ignored) {
            throw new IllegalArgumentException("Could not update customer from provided arguments.")
        }
    }
}
