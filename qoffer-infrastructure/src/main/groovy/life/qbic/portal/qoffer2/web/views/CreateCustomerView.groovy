package life.qbic.portal.qoffer2.web.views

import com.vaadin.data.ValidationResult
import com.vaadin.data.ValueContext
import com.vaadin.data.validator.EmailValidator
import com.vaadin.data.validator.StringLengthValidator
import com.vaadin.server.Page
import com.vaadin.server.UserError
import com.vaadin.ui.Button
import com.vaadin.ui.ComboBox
import com.vaadin.ui.FormLayout
import com.vaadin.ui.Notification
import com.vaadin.ui.TextField

import groovy.util.logging.Log4j2
import life.qbic.datamodel.dtos.business.Affiliation
import life.qbic.portal.qoffer2.web.StyledNotification
import life.qbic.portal.qoffer2.web.ViewModel
import life.qbic.portal.qoffer2.web.controllers.CreateCustomerController

import java.beans.PropertyChangeEvent
import java.beans.PropertyChangeListener

/**
 * This class generates a Form Layout in which the user
 * can input the necessary information for the creation of a new customer
 *
 * CreateCustomerView will be integrated into the qOffer 2.0 Portlet and provides an User Interface
 * with the intention of enabling a user the creation of a new Customer in the QBiC Database
 *
 * @since: 1.0.0
 */

@Log4j2
class CreateCustomerView extends FormLayout {
    final private ViewModel viewModel
    final private CreateCustomerController controller

    TextField titleField
    TextField firstNameField
    TextField lastNameField
    TextField emailField
    ComboBox affiliationComboBox
    Button submitButton

    StyledNotification failureNotification
    StyledNotification successNotification

    CreateCustomerView(CreateCustomerController controller, ViewModel viewModel) {
        super()
        this.controller = controller
        this.viewModel = viewModel
        showErrorNotification()
        showSuccessNotification()
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

        this.titleField = new TextField("Title")
        titleField.setPlaceholder("customer title if any")

        this.firstNameField = new TextField("First Name")
        firstNameField.setPlaceholder("customer first name")

        this.lastNameField = new TextField("Last Name")
        lastNameField.setPlaceholder("customer last name")

        this.emailField = new TextField("Email Address")
        emailField.setPlaceholder("customer email address")

        generateAffiliationSelector(viewModel.affiliations)
        affiliationComboBox.emptySelectionAllowed = false

        this.submitButton = new Button("Create Customer")

        //Add the components to the FormLayout
        createCustomerForm.addComponent(firstNameField)
        createCustomerForm.addComponent(lastNameField)
        createCustomerForm.addComponent(emailField)
        createCustomerForm.addComponent(affiliationComboBox)
        createCustomerForm.addComponent(submitButton)


        // Retrieve user input from fields and add them to the the Binder if entries are valid

        //Add Validators to the components
        this.addComponent(createCustomerForm)
    }

    /**
     * Generates a Combobox, which can be used for Affiliation selection by the user
     * @param affiliationList :
     * @return Vaadin Combobox component
     */
    private void generateAffiliationSelector(List<Affiliation> affiliationList) {

        this.affiliationComboBox =
                new ComboBox<>("Affiliation")
        affiliationComboBox.setPlaceholder("select customer affiliation")
        affiliationComboBox.setItems(affiliationList)
        affiliationComboBox.setItemCaptionGenerator({ Affiliation affiliation -> affiliation.organisation })
        affiliationComboBox.setEmptySelectionAllowed(false)
    }

    /**
     *
     */
    void showErrorNotification(){
        viewModel.failureNotifications.addPropertyChangeListener(new PropertyChangeListener() {
            @Override
            void propertyChange(PropertyChangeEvent evt) {
                String message = evt.newValue
                if(!message.isNumber()){
                    failureNotification = new StyledNotification("Error", message, Notification.Type.ERROR_MESSAGE)
                    failureNotification.show(Page.getCurrent())
                }
            }
        })
    }

    /**
     *
     */
    void showSuccessNotification(){
        viewModel.successNotifications.addPropertyChangeListener(new PropertyChangeListener() {
            @Override
            void propertyChange(PropertyChangeEvent evt) {
                String message = evt.newValue
                successNotification = new StyledNotification("Success", message, Notification.Type.HUMANIZED_MESSAGE)
                successNotification.show(Page.getCurrent())
            }
        })
    }


    /**
     * This is used to indicate whether all fields of this view are filled correctly.
     * It relies on the separate fields for validation.
     * @return
     */
    private boolean allValuesValid() {
        return !firstNameField.getComponentError()
                && !lastNameField.getComponentError()
                && !emailField.getComponentError()
                && affiliationComboBox.getSelectedItem().isPresent()
    }

    void registerListeners() {
        //Add Listeners to all Fields in the Formlayout
        this.firstNameField.addValueChangeListener({ event ->
            ValidationResult result = new StringLengthValidator(
                    "Please input a valid first name", 1, null)
                    .apply(event.getValue(), new ValueContext(firstNameField))
            if (result.isError()) {
                UserError error = new UserError(result.getErrorMessage())
                firstNameField.setComponentError(error)
            } else {
                firstNameField.setComponentError(null)
            }
        })

        this.lastNameField.addValueChangeListener({ event ->
            ValidationResult result = new StringLengthValidator("Please input a valid last name",
                1, null).apply(event.getValue(), new ValueContext(this.lastNameField))
            if (result.isError()) {
                UserError error = new UserError(result.getErrorMessage())
                lastNameField.setComponentError(error)
            } else {
                lastNameField.setComponentError(null)
            }
        })

        this.emailField.addValueChangeListener({ event ->
            ValidationResult result = new EmailValidator("Please input a valid email address")
                .apply(event.getValue(), new ValueContext(emailField))
            if (result.isError()) {
                UserError error = new UserError(result.getErrorMessage())
                emailField.setComponentError(error)
            } else {
                emailField.setComponentError(null)
            }
        })

        this.submitButton.addClickListener({ event ->
            String firstName = this.firstNameField.getValue().trim()
            String lastName = this.lastNameField.getValue().trim()
            String title = this.titleField.getValue()?.trim() ?: "None"
            String email = this.emailField.getValue().trim()
            List<Affiliation> affiliations = new ArrayList()
            if (affiliationComboBox.selectedItem.isPresent() ) {
                affiliations.add(affiliationComboBox.getSelectedItem().get())
            }
            if (allValuesValid) {
                controller.createNewCustomer(firstName, lastName, title, email, affiliations)
            } else {
                this.viewModel.showFailNotification("Please fill out the customer information correctly.")
            }
        })
    }
}
