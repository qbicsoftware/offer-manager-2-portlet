package life.qbic.portal.portlet

import com.vaadin.data.Binder
import com.vaadin.data.Binder.Binding
import com.vaadin.data.BinderValidationStatus
import com.vaadin.data.validator.BeanValidator
import com.vaadin.data.validator.EmailValidator
import com.vaadin.data.validator.StringLengthValidator
import com.vaadin.ui.Button
import com.vaadin.ui.ComboBox
import com.vaadin.ui.FormLayout
import groovy.util.logging.Log4j2
import life.qbic.portal.qoffer2.web.Controller
import life.qbic.portal.qoffer2.web.ViewModel
import life.qbic.datamodel.persons.Affiliation
import com.vaadin.ui.TextField
import life.qbic.portal.portlet.customers.Customer

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
class CreateCustomer extends FormLayout {
    final private ViewModel viewModel
    final private Controller controller

    private Customer editableCustomer
    private Binder<Affiliation> customerBinder
    private TextField firstNameField
    private TextField lastNameField
    private TextField emailField
    private ComboBox affiliationComboBox
    private Button submitButton

    CreateCustomer(Controller controller, ViewModel viewModel) {
        super()
        this.controller = controller
        this.viewModel = viewModel
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
        println createCustomerForm
        this.customerBinder = new Binder<>()

        this.firstNameField = new TextField("First Name")
        firstNameField.setPlaceholder("First Name")

        this.lastNameField = new TextField("Last Name")
        lastNameField.setPlaceholder("First Name")
        this.emailField = new TextField("Email Address")
        emailField.setPlaceholder("First Name")

        this.affiliationComboBox = generateAffiliationSelector(viewModel.affiliations)
        this.submitButton = new Button("Create Customer")

        //Add the components to the FormLayout
        createCustomerForm.addComponent(firstNameField)
        createCustomerForm.addComponent(lastNameField)
        createCustomerForm.addComponent(emailField)
        createCustomerForm.addComponent(affiliationComboBox)
        createCustomerForm.addComponent(submitButton)

        /*
        // Retrieve user input from fields and add them to the the Binder if entries are valid
        Binder.Binding<Customer, String> bindFirstName = customerBinder.forField(firstNameField).withValidator(new StringLengthValidator(
                "Please add the first name", 1, null)).bind(Customer.&setFirstName, Customer.&getFirstName)
        Binding<Customer, String> bindLastName = customerBinder.forField(lastNameField).withValidator(new StringLengthValidator(
                "Please add the last name", 1, null)).bind(Customer.&setLastName, Customer.&getLastName)
        Binding<Customer, String> bindEmail = customerBinder.forField(emailField).withValidator(new EmailValidator()).bind(Customer.&setEmail, Customer.&getEmail)
        Binding<Customer, Affiliation> bindAffiliation = customerBinder.forField(affiliationComboBox).withValidator(new BeanValidator(Affiliation)).bind(Customer.&setAffiliation, Customer.&getAffiliation)
         */
        this.addComponent(createCustomerForm)

    }

    /**
     * This method calls the individual Listener generating methods
     */

    private def registerListeners() {

        submitButton.addClickListener({ event ->
            submitCustomer()
        })
    }

    /**
     * Method which generates a listener for the submitbutton
     *
     * This Method checks the correctness of the user input for all fields in the affilationBinder.
     * If the specified values are valid, the createCustomer use case is initialized, otherwise a Notification specifying the incorrect values is shown
     */

    private def submitCustomer() {
        if (customerBinder.writeBeanIfValid(editableCustomer)) {
            controller.createNewCustomer(editableCustomer)
            viewModel.successNotifications.add("Customer ${firstNameField.value} ${lastNameField.value} could be added correctly")
        } else {
            BinderValidationStatus<Customer> validate = customerBinder.validate();
            String errorText = validate.getFieldValidationStatuses()
                    .stream().filter(BindingValidationStatus.&isError)
                    .map(BindingValidationStatus.&getMessage)
                    .map(Optional.&get).distinct()
                    .collect(Collectors.joining(", "))
            viewModel.failureNotifications.add("Errors in Fields ${errorText}")
            log.warn("Field ${errorText} not filled out correctly")
        }
    }

    /**
     * Generates a Combobox, which can be used for Affiliation selection by the user
     * and retrieves the selected Affiliation for customer creation
     * @param affiliationList :
     * @return Vaadin Combobox component with integrated Selectionlistener
     */
    private def generateAffiliationSelector(List<Affiliation> affiliationList) {

        ComboBox<Affiliation> affiliationComboBox =
                new ComboBox<>("Select an Affiliation")
        affiliationComboBox.setPlaceholder("Select Affiliation")
        affiliationComboBox.setItems(affiliationList)
        affiliationComboBox.setItemCaptionGenerator(Affiliation.&GroupName);
        affiliationComboBox.setEmptySelectionAllowed(false)

        return affiliationComboBox
    }
}
