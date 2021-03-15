package life.qbic.portal.offermanager.components.person.create


import com.vaadin.data.ValidationResult
import com.vaadin.data.Validator
import com.vaadin.data.ValueContext
import com.vaadin.data.provider.ListDataProvider
import com.vaadin.data.validator.EmailValidator
import com.vaadin.icons.VaadinIcons
import com.vaadin.server.UserError
import com.vaadin.shared.data.sort.SortDirection
import com.vaadin.shared.ui.ContentMode
import com.vaadin.ui.*
import com.vaadin.ui.themes.ValoTheme
import groovy.util.logging.Log4j2
import life.qbic.datamodel.dtos.business.Affiliation
import life.qbic.portal.offermanager.components.AppViewModel

/**
 * This class generates a Form Layout in which the user
 * can input the necessary information for the creation of a new person
 *
 * CreatePersonViewModel will be integrated into the qOffer 2.0 Portlet and provides an User Interface
 * with the intention of enabling a user the creation of a new person in the QBiC Database
 *
 * @since: 1.0.0
 */

@Log4j2
class CreatePersonView extends VerticalLayout {
    private final AppViewModel sharedViewModel
    private final CreatePersonViewModel createPersonViewModel
    final CreatePersonController controller

    ComboBox<String> titleField
    TextField firstNameField
    TextField lastNameField
    TextField emailField
    ComboBox<Affiliation> affiliationComboBox
    ComboBox<Affiliation> addressAdditionComboBox
    Button submitButton
    Button abortButton
    Panel affiliationDetails

    CreatePersonView(CreatePersonController controller, AppViewModel sharedViewModel, CreatePersonViewModel createPersonViewModel) {
        super()
        this.controller = controller
        this.sharedViewModel = sharedViewModel
        this.createPersonViewModel = createPersonViewModel
        initLayout()
        bindViewModel()
        setupFieldValidators()
        registerListeners()
    }

    /**
     * Generates a vaadin Form Layout as an UserInterface consisting of vaadin components
     * to enable user input for person creation
     */
    private def initLayout() {

        this.titleField = generateTitleSelector(createPersonViewModel.academicTitles)

        this.firstNameField = new TextField("First Name")
        firstNameField.setPlaceholder("First name")
        firstNameField.setRequiredIndicatorVisible(true)

        this.lastNameField = new TextField("Last Name")
        lastNameField.setPlaceholder("Last name")
        lastNameField.setRequiredIndicatorVisible(true)

        this.emailField = new TextField("Email Address")
        emailField.setPlaceholder("Email address")
        emailField.setRequiredIndicatorVisible(true)

        this.affiliationComboBox = generateAffiliationSelector(createPersonViewModel.availableAffiliations)
        affiliationComboBox.setRequiredIndicatorVisible(true)

        this.addressAdditionComboBox = generateAffiliationSelector(createPersonViewModel.availableAffiliations)
        addressAdditionComboBox.setRequiredIndicatorVisible(false)
        addressAdditionComboBox.setItemCaptionGenerator({it.addressAddition})
        addressAdditionComboBox.setCaption("Address Addition")
        addressAdditionComboBox.enabled = false

        this.submitButton = new Button("Create Person")
        submitButton.setIcon(VaadinIcons.USER_CHECK)
        submitButton.addStyleName(ValoTheme.BUTTON_FRIENDLY)
        submitButton.enabled = allValuesValid()

        this.abortButton = new Button("Abort Person Creation")
        abortButton.setIcon(VaadinIcons.CLOSE_CIRCLE)
        abortButton.addStyleName(ValoTheme.BUTTON_DANGER)

        this.affiliationDetails = new Panel("Affiliation Details")


        HorizontalLayout row1 = new HorizontalLayout(titleField)
        row1.setSizeFull()
        row1.setDefaultComponentAlignment(Alignment.BOTTOM_LEFT)

        HorizontalLayout row2 = new HorizontalLayout(firstNameField, lastNameField, emailField)
        row2.setDefaultComponentAlignment(Alignment.BOTTOM_LEFT)
        row2.setSizeFull()

        HorizontalLayout row3 = new HorizontalLayout(affiliationComboBox, addressAdditionComboBox)
        row3.setComponentAlignment(affiliationComboBox, Alignment.TOP_LEFT)
        row3.setComponentAlignment(addressAdditionComboBox, Alignment.TOP_LEFT)
        row3.setSizeFull()

        VerticalLayout affiliationPanel = new VerticalLayout(affiliationDetails)
        affiliationPanel.setMargin(false)
        affiliationPanel.setComponentAlignment(affiliationDetails, Alignment.TOP_LEFT)
        HorizontalLayout buttonLayout = new HorizontalLayout(abortButton,
                submitButton)
        buttonLayout.setMargin(false)
        HorizontalLayout row4 = new HorizontalLayout(affiliationPanel, buttonLayout)
        row4.setComponentAlignment(buttonLayout, Alignment.BOTTOM_RIGHT)
        row4.setSizeFull()


        //Add the components to the FormLayout
        this.addComponents(row1, row2, row3, row4)


        firstNameField.setSizeFull()
        lastNameField.setSizeFull()
        emailField.setSizeFull()
        affiliationComboBox.setSizeFull()
        addressAdditionComboBox.setSizeFull()
        affiliationDetails.setSizeFull()

        this.setSpacing(true)
        this.setMargin(false)
    }

    /**
     * This method connects the form fields to the corresponding values in the view model
     */
    private void bindViewModel() {

        this.titleField.addValueChangeListener({this.createPersonViewModel.academicTitle = it.value })
        createPersonViewModel.addPropertyChangeListener("academicTitle", {
            String newValue = it.newValue as String
            titleField.value = newValue ?: titleField.emptyValue
        })

        this.firstNameField.addValueChangeListener({this.createPersonViewModel.firstName = it.value })
        createPersonViewModel.addPropertyChangeListener("firstName", {
            String newValue = it.newValue as String
            firstNameField.value = newValue ?: firstNameField.emptyValue
        })

        this.lastNameField.addValueChangeListener({this.createPersonViewModel.lastName = it.value })
        createPersonViewModel.addPropertyChangeListener("lastName", {
            String newValue = it.newValue as String
            lastNameField.value = newValue ?: lastNameField.emptyValue
        })

        this.emailField.addValueChangeListener({this.createPersonViewModel.email = it.value })
        createPersonViewModel.addPropertyChangeListener("email", {
            String newValue = it.newValue as String
            emailField.value = newValue ?: emailField.emptyValue
        })
        this.affiliationComboBox.addValueChangeListener({
            this.createPersonViewModel.setAffiliation(it.value)
        })
        this.addressAdditionComboBox.addValueChangeListener({
            this.createPersonViewModel.setAffiliation(it.value)
        })

        createPersonViewModel.addPropertyChangeListener("affiliation", {
            Affiliation newValue = it.newValue as Affiliation
            if (newValue) {
                affiliationComboBox.value = newValue
                refreshAddressAdditions()
                addressAdditionComboBox.value = newValue
            } else {
                affiliationComboBox.value = affiliationComboBox.emptyValue
                addressAdditionComboBox.value = addressAdditionComboBox.emptyValue
            }
        })
        /*
        we listen to the valid properties. whenever the presenter resets values in the viewmodel
        and resets the valid properties the component error on the respective component is removed
        */
        createPersonViewModel.addPropertyChangeListener({it ->
            switch (it.propertyName) {
                case "academicTitleValid":
                    if (it.newValue || it.newValue == null) {
                        titleField.componentError = null
                    }
                    break
                case "firstNameValid":
                    if (it.newValue || it.newValue == null) {
                        firstNameField.componentError = null
                    }
                    break
                case "lastNameValid":
                    if (it.newValue || it.newValue == null) {
                        lastNameField.componentError = null
                    }
                    break
                case "emailValid":
                    if (it.newValue || it.newValue == null) {
                        emailField.componentError = null
                    }
                    break
                case "affiliationValid":
                    if (it.newValue || it.newValue == null) {
                        affiliationComboBox.componentError = null
                        addressAdditionComboBox.componentError = null
                    }
                    break
                default:
                    break
            }
            submitButton.enabled = allValuesValid()
            addressAdditionComboBox.enabled = !Objects.isNull(createPersonViewModel.affiliation)
        })

        /* refresh affiliation list and set added item as selected item. This is needed to keep this
        field up to date and select an affiliation after it was created */
        createPersonViewModel.availableAffiliations.addPropertyChangeListener({
            affiliationComboBox.getDataProvider().refreshAll()
            refreshAddressAdditions()
            if (it instanceof ObservableList.ElementAddedEvent) {
                affiliationComboBox.setSelectedItem(it.newValue as Affiliation)
            }
        })
    }

    private void refreshAddressAdditions() {
        ListDataProvider<Affiliation> dataProvider = this.addressAdditionComboBox.dataProvider as ListDataProvider<Affiliation>
        dataProvider.clearFilters()
        dataProvider.addFilterByValue({it.organisation },
                createPersonViewModel.affiliation?.organisation)
        dataProvider.setSortOrder({it.addressAddition}, SortDirection.ASCENDING)
    }
    /**
     * This method adds validation to the fields of this view
     */
    private void setupFieldValidators() {

        Validator<String> nameValidator =  Validator.from({String value -> (value && !value.trim().empty)}, "Please provide a valid name.")
        Validator<String> emailValidator = new EmailValidator("Please provide a valid email address.")
        Validator<? extends Object> selectionValidator = Validator.from({o -> o != null}, "Please make a selection.")

        //Add Listeners to all Fields in the Form layout
        this.firstNameField.addValueChangeListener({ event ->
            ValidationResult result = nameValidator.apply(event.getValue(), new ValueContext(this.firstNameField))
            if (result.isError()) {
                createPersonViewModel.firstNameValid = false
                UserError error = new UserError(result.getErrorMessage())
                firstNameField.setComponentError(error)
            } else {
                createPersonViewModel.firstNameValid = true
            }
        })
        this.lastNameField.addValueChangeListener({ event ->
            ValidationResult result = nameValidator.apply(event.getValue(), new ValueContext(this.lastNameField))
            if (result.isError()) {
                createPersonViewModel.lastNameValid = false
                UserError error = new UserError(result.getErrorMessage())
                lastNameField.setComponentError(error)
            } else {
                createPersonViewModel.lastNameValid = true
            }
        })
        this.emailField.addValueChangeListener({ event ->
            ValidationResult result = emailValidator.apply(event.getValue(), new ValueContext(this.emailField))
            if (result.isError()) {
                createPersonViewModel.emailValid = false
                UserError error = new UserError(result.getErrorMessage())
                emailField.setComponentError(error)
            } else {
                createPersonViewModel.emailValid = true
            }
        })
        this.affiliationComboBox.addSelectionListener({selection ->
            ValidationResult result = selectionValidator.apply(selection.getValue(), new ValueContext(this.affiliationComboBox))
            if (result.isError()) {
                createPersonViewModel.affiliationValid = false
                UserError error = new UserError(result.getErrorMessage())
                affiliationComboBox.setComponentError(error)
            } else {
                createPersonViewModel.affiliationValid = true
            }
        })
    }

    /**
     * Generates a Combobox, which can be used for Affiliation selection by the user
     * @param affiliationList list of all selectable affiliations
     * @return Vaadin Combobox component
     */
    private static ComboBox<Affiliation> generateAffiliationSelector(List<Affiliation> affiliationList) {
        ComboBox<Affiliation> affiliationComboBox =
                new ComboBox<>("Affiliation")
        affiliationComboBox.setPlaceholder("Select person affiliation")
        ListDataProvider<Affiliation> dataProvider = new ListDataProvider<>(affiliationList)
        affiliationComboBox.setDataProvider(dataProvider)
        affiliationComboBox.setEmptySelectionAllowed(false)
        affiliationComboBox.setItemCaptionGenerator({it.organisation})
        return affiliationComboBox
    }

    /**
     * Generates a Combobox, which can be used for AcademicTitle selection for a person
     * @return Vaadin Combobox component
     */
    private static ComboBox<String> generateTitleSelector(List<String> academicTitles) {
        ComboBox<String> titleCombobox =
                new ComboBox<>("Academic Title")
        titleCombobox.setPlaceholder("Select academic title")
        titleCombobox.setItems(academicTitles)
        titleCombobox.setEmptySelectionAllowed(true)
        return titleCombobox
    }

    /**
     * This is used to indicate whether all fields of this view are filled correctly.
     * It relies on the separate fields for validation.
     * @return
     */
    private boolean allValuesValid() {
        return createPersonViewModel.firstNameValid \
            && createPersonViewModel.lastNameValid \
            && createPersonViewModel.emailValid \
            && createPersonViewModel.affiliationValid
    }

    private void registerListeners() {
        this.submitButton.addClickListener({ event ->
            try {
                // we assume that the view model and the view always contain the same information
                String title = createPersonViewModel.academicTitle
                String firstName = createPersonViewModel.firstName
                String lastName = createPersonViewModel.lastName
                String email = createPersonViewModel.email
                List<Affiliation> affiliations = new ArrayList()
                affiliations.add(createPersonViewModel.affiliation)

                if(createPersonViewModel.outdatedPerson){
                    controller.updatePerson(createPersonViewModel.outdatedPerson, firstName, lastName, title, email, affiliations)
                }
                else{
                    controller.createNewPerson(firstName, lastName, title, email, affiliations)
                }

            } catch (IllegalArgumentException illegalArgumentException) {
                log.error("Illegal arguments for person creation. ${illegalArgumentException.getMessage()}")
                log.debug("Illegal arguments for person creation. ${illegalArgumentException.getMessage()}", illegalArgumentException)
                sharedViewModel.failureNotifications.add("Could not create the person. Please verify that your input is correct and try again.")
            } catch (Exception e) {
                log.error("Unexpected error after person creation form submission.", e)
                sharedViewModel.failureNotifications.add("An unexpected error occurred. We apologize for any inconveniences. Please inform us via email to support@qbic.zendesk.com.")
            }
        })

        this.affiliationComboBox.addSelectionListener({
            updateAffiliationDetails(it.value)
        })

        this.abortButton.addClickListener({ event ->
            try {
                clearAllFields()
            }
            catch (Exception e) {
                log.error("Unexpected error aborting the person creation.", e)
                sharedViewModel.failureNotifications.add("An unexpected error occurred. We apologize for any inconveniences. Please inform us via email to support@qbic.zendesk.com.")
            }
        })

    }

    private void updateAffiliationDetails(Affiliation affiliation) {
        if (affiliation) {
            VerticalLayout content = new VerticalLayout()
            content.addComponent(new Label("<strong>${affiliation.category.value}</strong>", ContentMode.HTML))
            content.addComponent(new Label("${affiliation.organisation}"))
            if (affiliation.addressAddition) {
                content.addComponent(new Label("${affiliation.addressAddition}"))
            }
            content.addComponent(new Label("${affiliation.street}"))
            content.addComponent(new Label("${affiliation.postalCode} ${affiliation.city} - ${affiliation.country}"))
            content.setMargin(true)
            content.setSpacing(false)
            this.affiliationDetails.setContent(content)
        } else {
            this.affiliationDetails.content = null
        }
    }

    /**
     *  Clears User Input from all fields in the Create Person View and reset validation status of all Fields
     */
    private void clearAllFields() {

        titleField.clear()
        firstNameField.clear()
        lastNameField.clear()
        emailField.clear()
        affiliationComboBox.selectedItem = affiliationComboBox.clear()
        addressAdditionComboBox.selectedItem = addressAdditionComboBox.clear()
        affiliationDetails.setContent(null)

        createPersonViewModel.academicTitleValid = null
        createPersonViewModel.firstNameValid = null
        createPersonViewModel.lastNameValid = null
        createPersonViewModel.emailValid = null
        createPersonViewModel.affiliationValid = null
        createPersonViewModel.outdatedPerson = null

    }
}