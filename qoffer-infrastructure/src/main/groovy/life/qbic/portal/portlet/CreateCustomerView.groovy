package life.qbic.portal.portlet

import com.vaadin.shared.ui.ValueChangeMode
import com.vaadin.ui.Button
import com.vaadin.ui.ComboBox
import com.vaadin.ui.GridLayout
import com.vaadin.ui.Label
import com.vaadin.ui.Notification
import com.vaadin.ui.VerticalLayout
import groovy.util.logging.Log4j2
import life.qbic.Controller
import life.qbic.ViewModel
import life.qbic.datamodel.persons.Affiliation
import com.vaadin.ui.TextField

/**
 * <class short description - One Line!>
 *
 * <More detailed description - When to use, what it solves, etc.>
 *
 * @since: <versiontag>
 *
 */

@Log4j2
class CreateCustomerView extends GridLayout{
    final private Controller controller
    final private ViewModel viewModel

    // This should be provided from the backend right now an empty list is provided for testing purposes
    List<Affiliation> affiliationList = new ArrayList<>()
    // This Map stores the UserInput and will be transferred to the Customer Creation Interface
    Map userInputMap = new HashMap()

    CreateCustomerView(Controller controller, ViewModel viewModel) {
        super()
        this.controller = controller
        this.viewModel = viewModel
        initLayout()
    }

    /**
     *
     * @return
     */
    private def initLayout() {

        GridLayout grid = new GridLayout(2, 5);
        grid.addStyleName("example-gridlayout")

        //Add Labels to Gridlayout
        grid.addComponent(generateLabel("First Name"), 0, 0)
        grid.addComponent(generateLabel("Last Name"), 0, 1)
        grid.addComponent(generateLabel("Email Address"), 0, 2)
        grid.addComponent(generateLabel("Affiliation"), 0, 3)

        //Add TextFields for User Input
        grid.addComponent(generateInputTextField("First Name"), 1, 0)
        grid.addComponent(generateInputTextField("Last Name"), 1, 1)
        grid.addComponent(generateInputTextField("Email Address"), 1, 2)

        // Add Selection Component to select Affiliation from List
        grid.addComponent(generateAffiliationSelector(affiliationList), 1, 3)

        // Add Submit Button to trigger Customer Generation with the set Values
        grid.addComponent(generateSubmitButton(), 1, 4)
        this.addComponent(grid)
    }

    /**
     * Generates a Label Field with the provided labelText
     * @param labelText:
     * @return Label Vaadin Component
     */
    public def generateLabel(String labelText) {
        Label label = new Label(labelText)
        return label
    }

    /**
     * Generates a Textfield for user input and
     * retrieves the provided information for later customer creation
     * @param textFieldLabel:
     * @return Vaadin Textfield with Listener
     */
    private def generateInputTextField(String textFieldLabel) {

        TextField inputTextField = new TextField()
        inputTextField.setValue("Write your stuff here!")

        inputTextField.addValueChangeListener({ value ->

            //Add input Value to Map
            userInputMap.put(textFieldLabel, value.value)
        })
        inputTextField.setValueChangeMode(ValueChangeMode.BLUR)

        return inputTextField

    }

    /**
     *
     * @param affiliationList:
     * @return
     */
    private def generateAffiliationSelector(List<Affiliation> affiliationList) {


        ComboBox<Affiliation> affiliationComboBox =
                new ComboBox<>("Select an Affiliation");
        affiliationComboBox.setItems(affiliationList)
        affiliationComboBox.setItemCaptionGenerator(Affiliation.&GroupName);
        affiliationComboBox.addSelectionListener({ selectionEvent ->
            //Add selected value to Map
            userInputMap.update("Affiliation", selectionEvent.selectedItem)
        })

        return affiliationComboBox
    }

    /**
     *
     * @return
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