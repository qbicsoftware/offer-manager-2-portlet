package life.qbic.portal.qoffer2.web.views

import com.vaadin.data.Binder
import com.vaadin.data.ValidationResult
import com.vaadin.data.Validator
import com.vaadin.data.ValueContext
import com.vaadin.data.validator.EmailValidator
import com.vaadin.server.UserError
import com.vaadin.shared.ui.MarginInfo
import com.vaadin.ui.*
import groovy.util.logging.Log4j2
import life.qbic.datamodel.dtos.business.Affiliation
import life.qbic.portal.qoffer2.web.controllers.CreateCustomerController
import life.qbic.portal.qoffer2.web.viewmodel.CreateCustomerViewModel
import life.qbic.portal.qoffer2.web.viewmodel.ViewModel

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
        bindViewModel(this.createCustomerViewModel)
        setupFieldValidators(this.createCustomerViewModel)
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
        firstNameField.setRequiredIndicatorVisible(true)

        this.lastNameField = new TextField("Last Name")
        lastNameField.setPlaceholder("customer last name")
        lastNameField.setRequiredIndicatorVisible(true)

        this.emailField = new TextField("Email Address")
        emailField.setPlaceholder("customer email address")
        emailField.setRequiredIndicatorVisible(true)

        this.affiliationComboBox = generateAffiliationSelector(sharedViewModel.affiliations)
        affiliationComboBox.setRequiredIndicatorVisible(true)

        this.submitButton = new Button("Create Customer")

        HorizontalLayout submitButtonLayout = new HorizontalLayout(submitButton)
        submitButtonLayout.setComponentAlignment(submitButton, Alignment.BOTTOM_RIGHT)

        //Add the components to the FormLayout
        createCustomerForm.addComponents(titleField)
        createCustomerForm.addComponents(firstNameField, lastNameField)
        createCustomerForm.addComponent(emailField)
        createCustomerForm.addComponent(affiliationComboBox)
        createCustomerForm.addComponent(submitButtonLayout)

        titleField.setSizeFull()
        firstNameField.setSizeFull()
        lastNameField.setSizeFull()
        emailField.setSizeFull()
        affiliationComboBox.setSizeFull()
        submitButtonLayout.setSizeFull()

        createCustomerForm.setSpacing(true)
        createCustomerForm.setMargin(new MarginInfo(false, true, false, false))
        this.addComponent(createCustomerForm)
    }

    /**
     * This method connects the form fields to the corresponding values in the view model
     * @param viewModel the view model holding the data to be displayed
     */
    private void bindViewModel(CreateCustomerViewModel viewModel) {
        Binder<CreateCustomerViewModel> binder = new Binder<>()

        Validator<String> nameValidator =  Validator.from({String value -> (value && !value.trim().empty)}, "Please provide a valid name.")
        Validator<String> emailValidator = new EmailValidator("Please provide a valid email address.")
        Validator<? extends Object> selectionValidator = Validator.from({o -> o != null}, "Please make a selection.")


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

        Please NOTE: we cannot use the binder.readBean(binder.getBean) refresh here since it would
        overwrite all validators attached to the fields. We furthermore cannot use the
        BinderBuilder#withValidator method since this would prevent the form from showing invalid
        information that is stored within the viewModel. We want the view to reflect the view model
        at all times!
         */
        viewModel.addPropertyChangeListener({it ->
            switch (it.propertyName) {
                case "academicTitle":
                    String newValue = it.newValue as String
                    titleField.selectedItem = newValue ?: titleField.emptyValue
                    break
                case "firstName":
                    String newValue = it.newValue as String
                    firstNameField.value = newValue ?: firstNameField.emptyValue
                    break
                case "lastName":
                    String newValue = it.newValue as String
                    lastNameField.value = newValue ?: lastNameField.emptyValue
                    break
                case "email":
                    String newValue = it.newValue as String
                    emailField.value = newValue ?: emailField.emptyValue
                    break
                case "affiliation":
                    Affiliation newValue = it.newValue as Affiliation
                    affiliationComboBox.selectedItem = newValue ?: affiliationComboBox.emptyValue
                    break
                default:
                    break
            }
        })
    }

    /**
     * This method adds validation to the fields of this view
     */
    private void setupFieldValidators(CreateCustomerViewModel viewModel) {

        Validator<String> nameValidator =  Validator.from({String value -> (value && !value.trim().empty)}, "Please provide a valid name.")
        Validator<String> emailValidator = new EmailValidator("Please provide a valid email address.")
        Validator<? extends Object> selectionValidator = Validator.from({o -> o != null}, "Please make a selection.")

        //Add Listeners to all Fields in the Formlayout
        this.firstNameField.addValueChangeListener({ event ->
            ValidationResult result = nameValidator.apply(event.getValue(), new ValueContext(this.firstNameField))
            if (result.isError()) {
                UserError error = new UserError(result.getErrorMessage())
                firstNameField.setComponentError(error)
            } else {
                firstNameField.setComponentError(null)
            }
        })

        this.lastNameField.addValueChangeListener({ event ->
            ValidationResult result = nameValidator.apply(event.getValue(), new ValueContext(this.lastNameField))
            if (result.isError()) {
                UserError error = new UserError(result.getErrorMessage())
                lastNameField.setComponentError(error)
            } else {
                lastNameField.setComponentError(null)
            }
        })

        this.emailField.addValueChangeListener({ event ->
            ValidationResult result = emailValidator.apply(event.getValue(), new ValueContext(this.emailField))
            if (result.isError()) {
                UserError error = new UserError(result.getErrorMessage())
                emailField.setComponentError(error)
            } else {
                emailField.setComponentError(null)
            }
        })

        this.affiliationComboBox.addSelectionListener({selection ->
            ValidationResult result = selectionValidator.apply(selection.getValue(), new ValueContext(this.affiliationComboBox))
            if (result.isError()) {
                UserError error = new UserError(result.getErrorMessage())
                affiliationComboBox.setComponentError(error)
            } else {
                affiliationComboBox.setComponentError(null)
            }
        })
    }

    /**
     * Generates a Combobox, which can be used for Affiliation selection by the user
     * @param affiliationList list of all selectable affiliations
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
            && !affiliationComboBox.getComponentError()
    }

    void registerListeners() {
        this.submitButton.addClickListener({ event ->
            try {
                if (allValuesValid()) {
                    // we assume that the view model and the view always contain the same information
                    String title = createCustomerViewModel.academicTitle
                    String firstName = createCustomerViewModel.firstName
                    String lastName = createCustomerViewModel.lastName
                    String email = createCustomerViewModel.email
                    List<Affiliation> affiliations = new ArrayList()

                    affiliations.add(createCustomerViewModel.affiliation)

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
