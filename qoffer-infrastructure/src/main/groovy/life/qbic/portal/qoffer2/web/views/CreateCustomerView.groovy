package life.qbic.portal.qoffer2.web.views

import com.vaadin.data.Binder
import com.vaadin.data.ValidationResult
import com.vaadin.data.ValueContext
import com.vaadin.data.validator.EmailValidator
import com.vaadin.data.validator.StringLengthValidator
import com.vaadin.server.UserError
import com.vaadin.ui.Alignment
import com.vaadin.ui.Button
import com.vaadin.ui.ComboBox
import com.vaadin.ui.FormLayout
import com.vaadin.ui.HorizontalLayout
import com.vaadin.ui.TextField

import groovy.util.logging.Log4j2
import life.qbic.datamodel.dtos.business.Affiliation
import life.qbic.portal.qoffer2.web.viewmodel.CreateCustomerViewModel
import life.qbic.portal.qoffer2.web.viewmodel.ViewModel
import life.qbic.portal.qoffer2.web.controllers.CreateCustomerController

/**
 * This class generates a Form Layout in which the user
 * can input the necessary information for the creation of a new customer
 *
 * CreateCustomerViewModel will be integrated into the qOffer 2.0 Portlet and provides an User Interface
 * with the intention of enabling a user the creation of a new Customer in the QBiC Database
 *
 * @since: 1.0.0
 */

@Log4j2
class CreateCustomerView extends FormLayout {
    final private ViewModel sharedViewModel
    final private CreateCustomerViewModel createCustomerViewModel
    final private CreateCustomerController controller

    ComboBox<String> titleField
    TextField firstNameField
    TextField lastNameField
    TextField emailField
    ComboBox<Affiliation> affiliationComboBox
    Button submitButton

    CreateCustomerView(CreateCustomerController controller, ViewModel sharedViewModel, CreateCustomerViewModel createCustomerViewModel) {
        super()
        this.controller = controller
        this.sharedViewModel = sharedViewModel
        this.createCustomerViewModel = createCustomerViewModel
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

        this.titleField = generateTitleSelector()

        this.firstNameField = new TextField("First Name")
        firstNameField.setPlaceholder("customer first name")

        this.lastNameField = new TextField("Last Name")
        lastNameField.setPlaceholder("customer last name")

        this.emailField = new TextField("Email Address")
        emailField.setPlaceholder("customer email address")

        this.affiliationComboBox = generateAffiliationSelector(sharedViewModel.affiliations)

        this.submitButton = new Button("Create Customer")

        HorizontalLayout submitButtonLayout = new HorizontalLayout(submitButton)
        submitButtonLayout.setComponentAlignment(submitButton, Alignment.BOTTOM_RIGHT)

        //Add the components to the FormLayout
        createCustomerForm.addComponent(titleField)
        createCustomerForm.addComponent(firstNameField)
        createCustomerForm.addComponent(lastNameField)
        createCustomerForm.addComponent(emailField)
        createCustomerForm.addComponent(affiliationComboBox)
        createCustomerForm.addComponent(submitButtonLayout)


        titleField.setSizeFull()
        firstNameField.setSizeFull()
        lastNameField.setSizeFull()
        emailField.setSizeFull()
        affiliationComboBox.setSizeFull()
        submitButtonLayout.setSizeFull()

        bindViewModel(this.createCustomerViewModel)

        createCustomerForm.setSpacing(true)
        this.addComponent(createCustomerForm)
    }

    /**
     * This method connects the form fields to the corresponding values in the view model
     * @param viewModel the view model holding the data to be displayed
     */
    private void bindViewModel(CreateCustomerViewModel viewModel) {
        Binder<CreateCustomerViewModel> binder = new Binder<>()

        // by binding the fields to the view model, the model is updated when the user input changed
        binder.setBean(viewModel)

        binder.forField(this.titleField)
                .bind({ it.academicTitle }, { it, updatedValue -> it.setAcademicTitle(updatedValue) })
        binder.forField(this.firstNameField)
                .bind({ it.firstName }, { it, updatedValue -> it.setFirstName(updatedValue) })
        binder.forField(this.lastNameField)
                .bind({ it.lastName }, { it, updatedValue -> it.setLastName(updatedValue) })
        binder.forField(this.emailField)
                .bind({ it.email }, { it, updatedValue -> it.setEmail(updatedValue) })
        binder.forField(this.affiliationComboBox)
                .bind({ it.affiliation }, { it, updatedValue -> it.setAffiliation(updatedValue) })

        /*
        Here we setup a listener to the viewModel that hold displayed information.
        The listener is needed since Vaadin bindings only work one-way
         */
        viewModel.addPropertyChangeListener({it ->
            binder.readBean(binder.getBean())
        })

    }

    /**
     * Generates a Combobox, which can be used for Affiliation selection by the user
     * @param affiliationList :
     * @return Vaadin Combobox component
     */
    private ComboBox<Affiliation> generateAffiliationSelector(List<Affiliation> affiliationList) {
        ComboBox<Affiliation> affiliationComboBox =
                new ComboBox<>("Affiliation")
        affiliationComboBox.setPlaceholder("select customer affiliation")
        affiliationComboBox.setItems(sharedViewModel.affiliations)
        affiliationComboBox.setEmptySelectionAllowed(false)
        affiliationComboBox.setItemCaptionGenerator({it.organisation})
        return affiliationComboBox
    }

    /**
     * Generates a Combobox, which can be used for AcademicTitle selection for a customer
     * @return Vaadin Combobox component
     */
    private ComboBox<String> generateTitleSelector() {
        ComboBox<String> titleCombobox =
                new ComboBox<>("Academic Title")
        titleCombobox.setPlaceholder("select academic title")
        titleCombobox.setItems(sharedViewModel.academicTitles)
        titleCombobox.setEmptySelectionAllowed(true)
        return titleCombobox
    }

    /**
     * This is used to indicate whether all fields of this view are filled correctly.
     * It relies on the separate fields for validation.
     * @return
     */
    private boolean allValuesValid() {
        return !firstNameField.getComponentError() \
            && !lastNameField.getComponentError() \
            && !emailField.getComponentError() \
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
            try {
                String title
                String firstName = this.firstNameField.getValue().trim()
                String lastName = this.lastNameField.getValue().trim()
                String email = this.emailField.getValue().trim()
                List<Affiliation> affiliations = new ArrayList()
                if (affiliationComboBox.selectedItem.isPresent()) {
                    affiliations.add(affiliationComboBox.getSelectedItem().get())
                }
                if (titleField.selectedItem.isPresent()) {
                    title = titleField.selectedItem.get()
                } else {
                    title = ""
                }
                if (allValuesValid()) {
                    controller.createNewCustomer(firstName, lastName, title, email, affiliations)
                } else {
                    this.sharedViewModel.failureNotifications.add("Please fill out the customer information correctly.")
                }
            } catch (IllegalArgumentException illegalArgumentException) {
                log.error("Illegal arguments for customer creation. ${illegalArgumentException.getMessage()}", illegalArgumentException)
                sharedViewModel.failureNotifications.add("Could not create the customer. Please verify that your input is correct and try again.")
            } catch (Exception e) {
                log.error("Unexpected error after customer creation form submission.", e)
                sharedViewModel.failureNotifications.add("An unexpected error occurred. We apologize for any inconveniences. Please inform us via email to support@qbic.zendesk.com.")
            }
        })
    }
}
