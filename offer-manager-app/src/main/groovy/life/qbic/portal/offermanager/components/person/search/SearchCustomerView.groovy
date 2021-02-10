package life.qbic.portal.offermanager.components.person.search

import com.vaadin.data.provider.ListDataProvider
import com.vaadin.shared.ui.grid.HeightMode
import com.vaadin.ui.Button
import com.vaadin.ui.FormLayout
import com.vaadin.ui.Grid
import com.vaadin.ui.HorizontalLayout
import com.vaadin.ui.Panel
import com.vaadin.ui.VerticalLayout
import com.vaadin.ui.components.grid.HeaderRow
import life.qbic.datamodel.dtos.business.AcademicTitle
import life.qbic.datamodel.dtos.business.Customer
import life.qbic.portal.offermanager.components.AppViewModel
import life.qbic.portal.offermanager.components.GridUtils
import life.qbic.portal.offermanager.dataresources.offers.OfferResourcesService

/**
 * <class short description - One Line!>
 *
 * <More detailed description - When to use, what it solves, etc.>
 *
 * @since: <versiontag>
 *
 */
class SearchCustomerView extends FormLayout{

    private final AppViewModel sharedViewModel
    private final SearchPersonViewModel viewModel
    private final SearchPersonController controller

    Grid<Customer> customerGrid
    Panel selectedCustomerInformation
    Button showDetails
    VerticalLayout buttonLayout

    SearchCustomerView(AppViewModel sharedViewModel, SearchPersonViewModel searchPersonViewModel, SearchPersonController controller) {
        this.sharedViewModel = sharedViewModel
        this.viewModel = searchPersonViewModel
        this.controller = controller

        initLayout()
        generateCustomerGrid()
    }

    private void initLayout(){
        buttonLayout = new VerticalLayout()

        customerGrid = new Grid<>()
        selectedCustomerInformation = new Panel()

        showDetails = new Button("Show Details")

        buttonLayout.addComponent(showDetails)

        this.addComponents(customerGrid,buttonLayout)
    }

    private void addListeners(){
        customerGrid.addSelectionListener({

        })
        showDetails.addClickListener({

        })
    }

    /**
     * This method adds the retrieved Customer Information to the Customer grid
     */
    private ListDataProvider setupCustomerDataProvider() {
        def customerListDataProvider = new ListDataProvider<>(viewModel.getFoundCustomers())
        this.customerGrid.setDataProvider(customerListDataProvider)
        return customerListDataProvider
    }

    /**
     * Method which generates the grid and populates the columns with the set Customer information from the setupDataProvider Method
     *
     * This Method is responsible for setting up the grid and setting the customer information to the individual grid columns.
     */
    private def generateCustomerGrid() {
        try {
            this.customerGrid.addColumn({ customer ->
                customer.title == AcademicTitle.NONE ? "" : customer.title})
                    .setCaption("Title").setId("Title")
            this.customerGrid.addColumn({ customer -> customer.firstName })
                    .setCaption("First Name").setId("FirstName")
            this.customerGrid.addColumn({ customer -> customer.lastName })
                    .setCaption("Last Name").setId("LastName")
            this.customerGrid.addColumn({ customer -> customer.emailAddress })
                    .setCaption("Email Address").setId("EmailAddress")

            //specify size of grid and layout
            customerGrid.setWidthFull()
            customerGrid.setHeightMode(HeightMode.ROW)
            customerGrid.setHeightByRows(5)

        } catch (Exception e) {
            new Exception("Unexpected exception in building the customer grid", e)
        }
        /*
        Let's not forget to setup the grid's data provider
         */
        def customerDataProvider = setupCustomerDataProvider()
        /*
        Lastly, we add some content filters for the columns
         */
        addFilters(customerDataProvider)
    }

    private void addFilters(ListDataProvider customerListDataProvider) {
        HeaderRow customerFilterRow = customerGrid.appendHeaderRow()
        GridUtils.setupColumnFilter(customerListDataProvider,
                customerGrid.getColumn("FirstName"),
                customerFilterRow)
        GridUtils.setupColumnFilter(customerListDataProvider,
                customerGrid.getColumn("LastName"),
                customerFilterRow)
        GridUtils.setupColumnFilter(customerListDataProvider,
                customerGrid.getColumn("EmailAddress"),
                customerFilterRow)
    }

}
