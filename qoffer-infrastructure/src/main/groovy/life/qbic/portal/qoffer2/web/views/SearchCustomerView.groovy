package life.qbic.portal.qoffer2.web.views

import com.vaadin.data.ValidationException
import com.vaadin.data.ValidationResult
import com.vaadin.data.Validator
import com.vaadin.data.ValueContext
import com.vaadin.icons.VaadinIcons
import com.vaadin.server.UserError
import com.vaadin.ui.Alignment
import com.vaadin.ui.Button
import com.vaadin.ui.FormLayout
import com.vaadin.ui.Grid
import com.vaadin.ui.HorizontalLayout
import com.vaadin.ui.TextField
import groovy.util.logging.Log4j2
import life.qbic.datamodel.dtos.business.Customer
import life.qbic.portal.qoffer2.web.viewmodel.SearchCustomerViewModel
import life.qbic.portal.qoffer2.web.viewmodel.ViewModel

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

    private final ViewModel viewModel
    private final SearchCustomerViewModel searchCustomerViewModel

    private TextField firstNameField
    private TextField lastNameField
    private Button submitButton
    private Button clearButton
    public Grid<Customer> customerGrid
    public HorizontalLayout gridLayout
    private List<Customer> foundCustomerList

    private Boolean firstNameSet
    private Boolean lastNameSet

    SearchCustomerView(ViewModel viewModel, SearchCustomerViewModel searchCustomerViewModel) {
        super()
        this.viewModel = viewModel
        this.searchCustomerViewModel = searchCustomerViewModel
        this.foundCustomerList = searchCustomerViewModel.foundCustomers
        initLayout()
        setupDataProvider()
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

        this.customerGrid = new Grid<Customer>()
        this.gridLayout = new HorizontalLayout()

        HorizontalLayout row1 = new HorizontalLayout(firstNameField, lastNameField)
        row1.setSizeFull()
        row1.setDefaultComponentAlignment(Alignment.BOTTOM_LEFT)

        HorizontalLayout row2 = new HorizontalLayout(clearButton, submitButton)
        row2.setSizeFull()
        row2.setDefaultComponentAlignment(Alignment.BOTTOM_LEFT)

        HorizontalLayout row3 = new HorizontalLayout(gridLayout)
        row3.setSizeFull()
        row3.setDefaultComponentAlignment(Alignment.BOTTOM_LEFT)

        //Add the components to the FormLayout
        searchCustomerForm.addComponent(row1)
        searchCustomerForm.addComponent(row2)
        searchCustomerForm.addComponent(row3)

        firstNameField.setSizeFull()
        lastNameField.setSizeFull()

        this.setSpacing(true)
        this.addComponent(searchCustomerForm)

    }

    /**
     * This method calls the individual Listener generation methods
     */

    private def registerListeners() {

        submitButton.addClickListener({ event ->
            submitCustomer()

        })
        clearButton.addClickListener({ event ->
            clearSearch()
            //clear the search fields
            firstNameField.clear()
            lastNameField.clear()
            firstNameField.setComponentError(null)
            lastNameField.setComponentError(null)
        })
    }

    /**
     * This method adds the retrieved Customer Information to the Customer grid
     */
    private void setupDataProvider() {

        this.customerGrid.setItems(foundCustomerList)
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
                firstNameSet = false
                UserError error = new UserError(result.getErrorMessage())
                firstNameField.setComponentError(error)
            } else {
                firstNameSet = true
                firstNameField.setComponentError(null)
            }
        })
        this.lastNameField.addValueChangeListener({ event ->
            ValidationResult result = nameValidator.apply(event.getValue(), new ValueContext(this.lastNameField))
            if (result.isError()) {
                lastNameSet = false
                UserError error = new UserError(result.getErrorMessage())
                lastNameField.setComponentError(error)
            } else {
                lastNameSet = true
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
        try {
            //If an input is provided to the first name and last name field
            if (firstNameSet && lastNameSet) {
                //Add values to ViewModel
                searchCustomerViewModel.searchedFirstName= firstNameField.value
                searchCustomerViewModel.searchedLastName = lastNameField.value
                //generate grid with new data
                generateGrid()
                viewModel.successNotifications.add("Customer ${firstNameField.value} ${lastNameField.value} was found in database")
            }
            //If an input is only provided to the first name field
            else if (firstNameSet && !lastNameSet) {
                viewModel.failureNotifications.add("Please specify a last name")
                lastNameSet = false
            }
            //If an input is only provided to the last name field
            else if (!firstNameSet && lastNameSet){
                viewModel.failureNotifications.add("Please specify a first name")
                firstNameSet = false
            }
            //If no input was provided
            else{
                viewModel.failureNotifications.add("Please specify a first and last name")
            }

        } catch (NullPointerException nullException) {
            viewModel.failureNotifications.add("Values missing for Customer ${firstNameField.value} ${lastNameField.value}")
        }
        catch (ValidationException validationException) {
            viewModel.failureNotifications.add("Invalid Values set for Customer ${firstNameField.value} ${lastNameField.value}")
        }
    }
    /**
     * Method which generates the grid and populates the columns with the set Customer information from the setupDataProvider Method
     *
     * This Method is responsible for setting up the grid and setting the customer information to the individual grid columns.
     * If the grid is generated correctly it will be added to the preset grid layout and also enables the clear customer grid button.
     */
    private def generateGrid() {
        //Clear Grid before the new grid is populated
        clearSearch()
        try {
            this.customerGrid.addColumn({ customer -> customer.getFirstName() }).setCaption("First Name")
            this.customerGrid.addColumn({ customer -> customer.getLastName() }).setCaption("Last Name")
            this.customerGrid.addColumn({ customer -> customer.getEmailAddress() }).setCaption("Email Address")
            this.customerGrid.addColumn({ customer -> customer.getTitle() }).setCaption("Title")
            this.customerGrid.addColumn({ customer -> customer.getAffiliations().toString() }).setCaption("Affiliation")

            //add grid to layout
            gridLayout.addComponent(customerGrid)

            //specify size of grid and layout
            gridLayout.setSizeFull()
            customerGrid.setSizeFull()

            this.clearButton.setEnabled(true)

        } catch (Exception e) {
            log.error("Unexpected exception in building the customer grid", e)
        }

    }

    /**
     * Method which clears the Customer Grid and removes it from the preset Grid Layout
     *
     * This Method is responsible for clearing the Customer information from the Customer grid and removes the empty grid from the preset Layout.
     * If the grid and layout are removed correctly, the clear button is disabled
     */
    private def clearSearch() {
        //Clear Grid before the new grid is populated
        try {
            customerGrid.removeAllColumns()
            gridLayout.removeAllComponents()
            this.clearButton.setEnabled(false)
        } catch (Exception e) {
            log.error("Unexpected exception occurred during the removal of the customer grid", e)
        }
    }

}
