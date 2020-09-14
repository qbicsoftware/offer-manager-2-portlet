package life.qbic.portal.portlet


import com.vaadin.data.ValidationResult
import com.vaadin.data.ValueContext
import com.vaadin.data.validator.EmailValidator
import com.vaadin.data.validator.StringLengthValidator
import com.vaadin.server.UserError
import com.vaadin.ui.Button
import com.vaadin.ui.ComboBox
import com.vaadin.ui.FormLayout
import groovy.util.logging.Log4j2
import life.qbic.portal.qoffer2.web.Controller
import life.qbic.portal.qoffer2.web.ViewModel
import life.qbic.datamodel.persons.Affiliation
import com.vaadin.ui.TextField

/**
 * This class generates a Form Layout in which the user
 * can input the necessary information for the creation of a new customer
 *
 * CreateCustomerView will be integrated into the qOffer 2.0 Portlet and provides an User Interface
 * with the intention of enabling a user the creation of a new Customer in the QBiC Database
 *
 * @since: 1.0.0*
 */

@Log4j2
class CreateCustomerView extends FormLayout {
    final private ViewModel viewModel
    final private Controller controller

    private String firstName
    private String lastName
    private String email
    private Affiliation affiliation
    private HashMap customerInfo

    private TextField firstNameField
    private TextField lastNameField
    private TextField emailField
    private ComboBox affiliationComboBox
    private Button submitButton

    private boolean firstNameValidity
    private boolean lastNameValidity
    private boolean emailValidity
    private boolean affiliationValidity


    CreateCustomerView(Controller controller, ViewModel viewModel) {
        super()
        this.controller = controller
        this.viewModel = viewModel
        this.customerInfo = new HashMap()
        initLayout()
        registerListeners()
    }

    /**
     * Generates a vaadin Form Layout as an UserInterface consisting of vaadin components
     * to enable user input for Customer creation
     */
    private def initLayout() {

        //Generate FormLayout and the individual components
        FormLayout createCustomerForm = new FormLayout()

        this.firstNameField = new TextField("First Name")
        firstNameField.setPlaceholder("First Name")

        this.lastNameField = new TextField("Last Name")
        lastNameField.setPlaceholder("Last Name")

        this.emailField = new TextField("Email Address")
        emailField.setPlaceholder("Email Address")

        this.affiliationComboBox = generateAffiliationSelector(viewModel.affiliations)
        affiliationComboBox.emptySelectionAllowed = false

        this.submitButton = new Button("Create Customer")

        //Add the components to the FormLayout
        createCustomerForm.addComponent(firstNameField)
        createCustomerForm.addComponent(lastNameField)
        createCustomerForm.addComponent(emailField)
        createCustomerForm.addComponent(affiliationComboBox)
        createCustomerForm.addComponent(submitButton)

        //Add Validators to the components
        this.addComponent(createCustomerForm)
    }

    /**
     * This method calls the individual Listener generation methods.
     * Additionally it will validate the given user input on a field level after deselection and set validation booleans accordingly
     */

    private def registerListeners() {
        //Add Listeners to all Fields in the Formlayout
        firstNameField.addValueChangeListener({ event ->
            ValidationResult result = new StringLengthValidator("Please input a valid first Name", 1, null).apply(event.getValue(), new ValueContext(firstNameField))
            if (result.isError()) {
                UserError error = new UserError(result.getErrorMessage())
                firstNameField.setComponentError(error)
                firstName = null
                firstNameValidity = false
            } else {
                firstNameField.setComponentError(null)
                firstName = event.getValue().toString()
                customerInfo.put("First name", firstName)
                firstNameValidity = true
            }
        })

        lastNameField.addValueChangeListener({ event ->
            ValidationResult result = new StringLengthValidator("Please input a valid last Name", 1, null).apply(event.getValue(), new ValueContext(lastNameField))
            if (result.isError()) {
                UserError error = new UserError(result.getErrorMessage())
                lastNameField.setComponentError(error)
                lastName = null
                lastNameValidity = false
            } else {
                lastNameField.setComponentError(null)
                lastName = event.getValue().toString()
                customerInfo.put("Last Name", lastName)
                lastNameValidity = true
            }
        })

        emailField.addValueChangeListener({ event ->
            ValidationResult result = new EmailValidator("Please input a valid email address").apply(event.getValue(), new ValueContext(emailField))
            if (result.isError()) {
                UserError error = new UserError(result.getErrorMessage())
                email = null
                emailField.setComponentError(error)
                emailValidity = false
            } else {
                emailField.setComponentError(null)
                email = event.getValue().toString()
                customerInfo.put("email", email)
                emailValidity = true
            }
        })

        affiliationComboBox.addSelectionListener({ event ->
            affiliation = event.getValue()
            customerInfo.put("Affiliaton", affiliation)
            affiliationValidity = true
        })

        submitButton.addClickListener({ event ->
            submitCustomer()
        })
    }

    /**
     * Method which generates a listener for the submit button
     *
     * This method checks the correctness of the user input with the help of the predefined boolean values.
     * If the specified values are valid, the createCustomer use case is initialized, otherwise a Notification specifying the incorrect values is shown
     */

    private def submitCustomer() {

        if (firstNameValidity && lastNameValidity && emailValidity && affiliationValidity) {
            try {
                controller.createNewCustomer(customerInfo)
                viewModel.successNotifications.add("Correct field values found")
            }
            catch (Exception e) {
                log.error("Unexpected error occurred during the customer creation process", e)
            }

        } else {
            viewModel.failureNotifications.add("Incorrect field values found!")
        }
    }

    /**
     * Generates a Combobox, which can be used for Affiliation selection by the user
     * @param affiliationList :
     * @return Vaadin Combobox component
     */
    private def generateAffiliationSelector(List<Affiliation> affiliationList) {

        ComboBox<Affiliation> affiliationComboBox =
                new ComboBox<>("Select an Affiliation")
        affiliationComboBox.setPlaceholder("Select Affiliation")
        affiliationComboBox.setItems(affiliationList)
        affiliationComboBox.setItemCaptionGenerator({ Affiliation af -> af.groupName })
        affiliationComboBox.setEmptySelectionAllowed(false)

        return affiliationComboBox
    }

}
