package life.qbic.portal.portlet

import com.vaadin.shared.ui.ValueChangeMode
import com.vaadin.ui.Button
import com.vaadin.ui.ComboBox
import com.vaadin.ui.FormLayout
import com.vaadin.ui.GridLayout
import com.vaadin.ui.Notification
import groovy.util.logging.Log4j2
import life.qbic.ViewModel
import life.qbic.datamodel.persons.Affiliation
import com.vaadin.ui.TextField

/**
 * This class generates a Form Layout in which the user
 * can input the necessary information for the creation of a new customer
 *
 * CreateCustomerView will be integrated into the qOffer 2.0 Portlet and provides an User Interface
 * with the intention of enabling a user the creation of a new Customer in the QBiC Database
 *
 * @since: 1.0.0
 *
 */

@Log4j2
class CreateCustomerView extends GridLayout{
    final private ViewModel viewModel

    // This should be provided from the backend right now an empty list is provided for testing purposes
    List<Affiliation> affiliationList = new ArrayList<>()
    // This Map stores the UserInput and will be transferred to the Customer Creation Interface
    Map userInputMap = new HashMap()

    CreateCustomerView(ViewModel viewModel) {
        super()
        this.viewModel = viewModel
        initLayout()
    }

    /**
     * Generates a vaadin Form Layout as an UserInterface consisting of vaadin components
     * to enable user input for Customer creation
     */
    private def initLayout() {

        FormLayout form = new FormLayout();

        //Add TextFields for User Input
        form.addComponent(generateInputTextField("First Name"))
        form.addComponent(generateInputTextField("Last Name"))
        form.addComponent(generateInputTextField("Email Address"))

        // Add Selection Component to select Affiliation from List
        form.addComponent(generateAffiliationSelector(affiliationList))

        // Add Submit Button to trigger Customer Generation with the set Values
        form.addComponent(generateSubmitButton())
        this.addComponent(form)
    }

    /**
     * Generates a Textfield for user input and
     * retrieves the provided information for customer creation
     * @param textFieldLabel:
     * @return Vaadin Textfield component with integrated ValueChangeListener
     */
    private def generateInputTextField(String textFieldLabel) {

        TextField inputTextField = new TextField(textFieldLabel)
        inputTextField.setPlaceholder("Write your stuff here!")

        inputTextField.addValueChangeListener({ value ->

            userInputMap.put(textFieldLabel, value.value)
        })
        inputTextField.setValueChangeMode(ValueChangeMode.BLUR)

        return inputTextField

    }

    /**
     * Generates a Combobox, which can be used for Affiliation selection by the user
     * and retrieves the selected Affiliation for customer creation
     * @param affiliationList:
     * @return Vaadin Combobox component with integrated Selectionlistener
     */
    private def generateAffiliationSelector(List<Affiliation> affiliationList) {

        ComboBox<Affiliation> affiliationComboBox =
                new ComboBox<>("Select an Affiliation");
        affiliationComboBox.setItems(affiliationList)
        affiliationComboBox.setItemCaptionGenerator(Affiliation.&GroupName);
        affiliationComboBox.setEmptySelectionAllowed(false)
        affiliationComboBox.addSelectionListener({ selectionEvent ->
            //Add selected value to Map
            userInputMap.update("Affiliation", selectionEvent.selectedItem)
        })

        return affiliationComboBox
    }

    /**
     * Generates a Button component, which sends the customer information to the backend
     * and checks if the provided information is correct
     * @return Vaadin Button component with integrated ClickeventListener
     */
    private generateSubmitButton() {
        Button submitButton = new Button("Create Customer");

        submitButton.addClickListener({ clickEvent ->
            //Add Logic to Check if all Fields were filled correctly here
            // Exceptions should be sent to the viewModel

            //Add connection to Customer creation here
            Notification.show("You did it!")

        })

        return submitButton

    }
}