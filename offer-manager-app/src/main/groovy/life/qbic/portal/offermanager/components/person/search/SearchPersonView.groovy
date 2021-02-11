package life.qbic.portal.offermanager.components.person.search

import com.vaadin.data.provider.ListDataProvider
import com.vaadin.shared.ui.ContentMode
import com.vaadin.shared.ui.grid.HeightMode
import com.vaadin.ui.Alignment
import com.vaadin.ui.Button
import com.vaadin.ui.FormLayout
import com.vaadin.ui.Grid
import com.vaadin.ui.HorizontalLayout
import com.vaadin.ui.Label
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
class SearchPersonView extends FormLayout{

    private final SearchPersonViewModel viewModel

    Grid<Customer> customerGrid
    Panel selectedCustomerInformation
    Button showDetails
    VerticalLayout buttonLayout

    SearchPersonView(SearchPersonViewModel searchPersonViewModel) {
        this.viewModel = searchPersonViewModel

        initLayout()
        generateCustomerGrid()
        addListeners()
    }

    private void initLayout(){
        buttonLayout = new VerticalLayout()

        customerGrid = new Grid<>()
        selectedCustomerInformation = new Panel()

        showDetails = new Button("Show Details")
        showDetails.setEnabled(false)

        buttonLayout.addComponent(showDetails)
        buttonLayout.setComponentAlignment(showDetails, Alignment.MIDDLE_RIGHT)
        buttonLayout.setMargin(false)

        this.addComponents(customerGrid,buttonLayout)
        this.setMargin(false)
    }

    private void addListeners(){
        customerGrid.addSelectionListener({
            buttonLayout.removeComponent(selectedCustomerInformation)
            fillPanel(it.firstSelectedItem.get())
            showDetails.setEnabled(true)
        })
        showDetails.addClickListener({
            buttonLayout.addComponent(selectedCustomerInformation)
            buttonLayout.setComponentAlignment(selectedCustomerInformation, Alignment.MIDDLE_LEFT)
        })
    }

    /**
     * Fills the panel with the detailed customer information of the currently selected customer
     * @param customer The customer which
     */
    private void fillPanel(Customer customer){
        VerticalLayout content = new VerticalLayout()

        content.addComponent(new Label("<strong>${customer.title == AcademicTitle.NONE ? "" : customer.title} ${customer.firstName} ${customer.lastName}</strong>", ContentMode.HTML))
        content.addComponent(new Label("${customer.emailAddress}", ContentMode.HTML))


        customer.affiliations.each { affiliation ->
            content.addComponent(new Label("<strong>${affiliation.category.value}</strong>", ContentMode.HTML))
            content.addComponent(new Label("${affiliation.organisation}"))
            if (affiliation.addressAddition) {
                content.addComponent(new Label("${affiliation.addressAddition}"))
            }
            content.addComponent(new Label("${affiliation.street}"))
            content.addComponent(new Label("${affiliation.postalCode} ${affiliation.city} - ${affiliation.country}"))
        }
        content.setMargin(true)
        content.setSpacing(false)

        selectedCustomerInformation.setContent(content)
        selectedCustomerInformation.setWidthUndefined()
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
