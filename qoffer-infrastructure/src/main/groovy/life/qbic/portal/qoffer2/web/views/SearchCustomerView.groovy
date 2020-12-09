package life.qbic.portal.qoffer2.web.views

import com.vaadin.data.*
import com.vaadin.data.provider.DataProvider
import com.vaadin.data.provider.ListDataProvider
import com.vaadin.icons.VaadinIcons
import com.vaadin.server.UserError
import com.vaadin.shared.ui.ValueChangeMode
import com.vaadin.ui.*
import com.vaadin.ui.components.grid.HeaderRow
import groovy.util.logging.Log4j2
import life.qbic.datamodel.dtos.business.Customer
import life.qbic.portal.qoffer2.web.controllers.SearchCustomerController
import life.qbic.portal.qoffer2.web.viewmodel.SearchCustomerViewModel
import life.qbic.portal.qoffer2.web.viewmodel.ViewModel
import org.apache.commons.lang3.StringUtils

/**
 * This class generates a Form Layout with which the user can search if a customer is already contained in the database
 *
 * SearchCustomerView will be integrated into the qOffer 2.0 Portlet and provides an User Interface
 * with the intention of enabling a user the search of a customer by looking for the customer's first and last name to the QBiC Database
 *
 * @since: 1.0.0*
 */
@Log4j2
class SearchCustomerView extends FormLayout {

    final private ViewModel viewModel
    final private SearchCustomerViewModel searchCustomerViewModel
    final private SearchCustomerController controller
    private TextField firstNameField
    private TextField lastNameField
    private Button submitButton
    private Button clearButton
    private Grid<Customer> customerGrid

    SearchCustomerView(SearchCustomerController controller, ViewModel viewModel, SearchCustomerViewModel searchCustomerViewModel) {
        super()
        this.controller = controller
        this.viewModel = viewModel
        this.searchCustomerViewModel = searchCustomerViewModel
        initLayout()
        bindViewModel()
        setupFieldValidators()
        registerListeners()
    }

    /**
     * Generates a vaadin Form Layout as an UserInterface consisting of vaadin components
     * to enable user input for Customer creation
     */
    private def initLayout() {

        //Generate FormLayout and the individual components
        FormLayout searchCustomerForm = new FormLayout()

        this.firstNameField = new TextField("First Name")
        firstNameField.setPlaceholder("First Name")
        firstNameField.setRequiredIndicatorVisible(true)

        this.lastNameField = new TextField("Last Name")
        lastNameField.setPlaceholder("Last Name")
        lastNameField.setRequiredIndicatorVisible(true)

        this.submitButton = new Button("Search Customer")
        submitButton.setIcon(VaadinIcons.SEARCH)

        this.clearButton = new Button("Clear Customers")
        clearButton.setIcon(VaadinIcons.CLOSE_CIRCLE)
        clearButton.setEnabled(false)

        this.customerGrid = generateCustomerGrid()
        customerGrid.visible = false

        HorizontalLayout row1 = new HorizontalLayout(firstNameField, lastNameField)
        row1.setSizeFull()
        row1.setDefaultComponentAlignment(Alignment.BOTTOM_LEFT)

        HorizontalLayout row2 = new HorizontalLayout(clearButton, submitButton)
        row2.setSizeFull()
        row2.setDefaultComponentAlignment(Alignment.BOTTOM_LEFT)

        HorizontalLayout row3 = new HorizontalLayout(customerGrid)
        row3.setSizeFull()
        row3.setDefaultComponentAlignment(Alignment.BOTTOM_LEFT)

        //Add the components to the FormLayout
        searchCustomerForm.addComponent(row1)
        searchCustomerForm.addComponent(row2)
        searchCustomerForm.addComponent(row3)

        firstNameField.setSizeFull()
        lastNameField.setSizeFull()
        customerGrid.setSizeFull()

        this.setSpacing(true)
        this.addComponent(searchCustomerForm)

    }


    /**
     * registers listeners for user events to components
     */
    private void registerListeners() {

        submitButton.addClickListener({ event ->
            if (searchCustomerViewModel.firstNameValid && searchCustomerViewModel.lastNameValid) {
                submitCustomer()
            } else {
                viewModel.failureNotifications.add("Please make sure all entered values are valid and try again.")
            }
        })

        clearButton.addClickListener({ event ->
            clearSearchResults()
            //clear the search fields
            searchCustomerViewModel.firstName = null
            searchCustomerViewModel.lastName = null
            searchCustomerViewModel.firstNameValid = null
            searchCustomerViewModel.lastNameValid = null
        })
    }

    /**
     * This method adds validation to the fields of this view
     */
    private void setupFieldValidators() {

        Validator<String> nameValidator = Validator.from({ String value -> (value && !value.trim().empty) }, "Please provide a valid name.")

        //Add Listeners to all Fields in the FormLayout
        this.firstNameField.addValueChangeListener({ event ->
            ValidationResult result = nameValidator.apply(event.getValue(), new ValueContext(this.firstNameField))
            if (result.isError()) {
                searchCustomerViewModel.firstNameValid = false
                UserError error = new UserError(result.getErrorMessage())
                firstNameField.setComponentError(error)
            } else {
                searchCustomerViewModel.firstNameValid = true
                firstNameField.setComponentError(null)

            }
        })
        this.lastNameField.addValueChangeListener({ event ->
            ValidationResult result = nameValidator.apply(event.getValue(), new ValueContext(this.lastNameField))
            if (result.isError()) {
                searchCustomerViewModel.lastNameValid = false
                UserError error = new UserError(result.getErrorMessage())
                lastNameField.setComponentError(error)
            } else {
                searchCustomerViewModel.lastNameValid = true
                lastNameField.setComponentError(null)
            }
        })
    }

    /**
     * Method which generates a listener for the searchButton
     *
     * This Method checks the correctness of the user input for all fields
     * If the specified values are valid, the searchCustomer use case is initialized, otherwise a Notification specifying the incorrect values is shown
     */

    private def submitCustomer() {
        controller.searchCustomerByName(searchCustomerViewModel.firstName, searchCustomerViewModel.lastName)
    }

    /**
     * This method generates a vaadin grid to be used with the current view model
     * @return a customer grid linked to the customer list in the view model
     */
    private Grid<Customer> generateCustomerGrid() {
        Grid<Customer> grid = new Grid<>()

        Grid.Column<Customer, String> firstNameColumn = grid.addColumn({ customer -> customer.getFirstName() }).setCaption("First Name")
        Grid.Column<Customer, String> lastNameColumn = grid.addColumn({ customer -> customer.getLastName() }).setCaption("Last Name")
        Grid.Column<Customer, String> emailColumn = grid.addColumn({ customer -> customer.getEmailAddress() }).setCaption("Email Address")
        Grid.Column<Customer, String> titleColumn = grid.addColumn({ customer -> customer.title.value }).setCaption("Title")
        Grid.Column<Customer, String> affiliationColumn = grid.addColumn({ customer -> customer.getAffiliations().toString() }).setCaption("Affiliation")

        ListDataProvider<Customer> customerDataProvider = new ListDataProvider(searchCustomerViewModel.foundCustomers)
        grid.setDataProvider(customerDataProvider)
        grid.setSelectionMode(Grid.SelectionMode.NONE)

        HeaderRow customerFilterRow = grid.appendHeaderRow()

        setupColumnFilter(customerDataProvider, firstNameColumn, customerFilterRow)
        setupColumnFilter(customerDataProvider, lastNameColumn, customerFilterRow)
        setupColumnFilter(customerDataProvider, emailColumn, customerFilterRow)
        setupColumnFilter(customerDataProvider, titleColumn, customerFilterRow)
        setupColumnFilter(customerDataProvider, affiliationColumn, customerFilterRow)
        return grid
    }

    /**
     * Method which clears the Customer Grid and removes it from the preset Grid Layout
     *
     * This Method is responsible for clearing the Customer information from the Customer grid and removes the empty grid from the preset Layout.
     * If the grid and layout are removed correctly, the clear button is disabled
     */
    private def clearSearchResults() {
        searchCustomerViewModel.foundCustomers.clear()
    }

    /**
     * This method creates a TextField to filter a given column
     * @param dataProvider a {@link ListDataProvider} on which the filtering is applied on
     * @param column the column to be filtered
     * @param headerRow a {@link com.vaadin.ui.components.grid.HeaderRow} to the corresponding {@link Grid}
     */
    private static <T> void setupColumnFilter(ListDataProvider<T> dataProvider,
                                              Grid.Column<T, String> column, HeaderRow headerRow) {
        TextField filterTextField = new TextField()
        filterTextField.addValueChangeListener(event -> {
            dataProvider.addFilter(element ->
                    StringUtils.containsIgnoreCase(column.getValueProvider().apply(element), filterTextField.getValue())
            )
        })
        filterTextField.setValueChangeMode(ValueChangeMode.EAGER)

        headerRow.getCell(column).setComponent(filterTextField)
        filterTextField.setSizeFull()
    }

    /**
     * This method binds field and view model values. Values are only written to the model when valid.
     */
    private void bindViewModel() {
        Binder<SearchCustomerViewModel> binder = new Binder<>()
        binder.setBean(searchCustomerViewModel)
        bindFirstNameField(binder)
        bindLastNameField(binder)

        searchCustomerViewModel.foundCustomers.addPropertyChangeListener({
            customerGrid.getDataProvider().refreshAll()
            if (searchCustomerViewModel.foundCustomers.empty) {
                clearButton.enabled = false
                customerGrid.visible = false
            } else {
                clearButton.enabled = true
                customerGrid.visible = true
            }
        })
    }

    private void bindFirstNameField(Binder<SearchCustomerViewModel> binder) {
        binder.forField(this.firstNameField)
                .bind({ it.firstName }, { it, updatedValue -> it.setFirstName(updatedValue) })
        searchCustomerViewModel.addPropertyChangeListener("firstName", {
            String newValue = it.getNewValue() as String
            firstNameField.value = newValue ?: firstNameField.emptyValue
        })
        searchCustomerViewModel.addPropertyChangeListener("firstNameValid", {
            if (it.newValue || it.newValue == null) {
                firstNameField.componentError = null
            }
        })
    }

    private void bindLastNameField(Binder<SearchCustomerViewModel> binder) {
        binder.forField(this.lastNameField)
                .bind({ it.lastName }, { it, updatedValue -> it.setLastName(updatedValue) })
        searchCustomerViewModel.addPropertyChangeListener("lastName", {
            String newValue = it.getNewValue() as String
            lastNameField.value = newValue ?: lastNameField.emptyValue
        })
        searchCustomerViewModel.addPropertyChangeListener("lastNameValid", {
            if (it.newValue || it.newValue == null) {
                lastNameField.componentError = null
            }
        })
    }
}
