package life.qbic.portal.offermanager.components.person.search

import com.vaadin.data.provider.ListDataProvider
import com.vaadin.shared.ui.ContentMode
import com.vaadin.shared.ui.grid.HeightMode
import com.vaadin.ui.Alignment
import com.vaadin.ui.Button
import com.vaadin.ui.FormLayout
import com.vaadin.ui.Grid
import com.vaadin.ui.Label
import com.vaadin.ui.Panel
import com.vaadin.ui.VerticalLayout
import com.vaadin.ui.components.grid.HeaderRow
import com.vaadin.ui.themes.ValoTheme
import life.qbic.datamodel.dtos.business.AcademicTitle
import life.qbic.datamodel.dtos.business.Customer
import life.qbic.datamodel.dtos.general.Person
import life.qbic.portal.offermanager.components.GridUtils

import life.qbic.portal.offermanager.components.person.create.CreatePersonView

/**
 * Constructs the UI for the SearchPerson use case
 *
 * This class provides the view elements so that a user can search for a customer through the UI
 *
 * @since: 1.0.0
 *
 */
class SearchPersonView extends FormLayout{

    private final SearchPersonViewModel viewModel
    private final CreatePersonView updatePersonView

    Grid<Person> customerGrid
    Panel selectedCustomerInformation
    Button updateCustomer
    VerticalLayout detailsLayout
    VerticalLayout searchPersonLayout

    SearchPersonView(SearchPersonViewModel searchPersonViewModel, CreatePersonView updatePersonView) {
        this.viewModel = searchPersonViewModel
        this.updatePersonView = updatePersonView


        initLayout()
        generateCustomerGrid()
        addListeners()
    }

    private void initLayout(){
        Label gridLabel = new Label("Available Person Entries")
        gridLabel.addStyleName(ValoTheme.LABEL_HUGE)


        updateCustomer = new Button("Update Customer")
        updateCustomer.setEnabled(false)

        detailsLayout = new VerticalLayout()
        detailsLayout.addComponent(updateCustomer)
        detailsLayout.setComponentAlignment(updateCustomer, Alignment.MIDDLE_RIGHT)

        customerGrid = new Grid<>()
        selectedCustomerInformation = new Panel()

        Label detailsLabel = new Label("Person Details: ")
        detailsLayout.addComponent(detailsLabel)
        detailsLabel.addStyleName(ValoTheme.LABEL_LARGE)


        detailsLayout.addComponent(selectedCustomerInformation)
        detailsLayout.setVisible(false)
        detailsLayout.setMargin(false)

        searchPersonLayout = new VerticalLayout(gridLabel,customerGrid,detailsLayout)
        searchPersonLayout.setMargin(false)

        this.addComponents(searchPersonLayout,updatePersonView)
        this.setMargin(false)
        updatePersonView.setVisible(false)
    }

    private void addListeners(){

        customerGrid.addSelectionListener({
            if (it.firstSelectedItem.isPresent()) {
                fillPanel(it.firstSelectedItem.get() as Customer)
                detailsLayout.setVisible(true)
                updateCustomer.setEnabled(true)
                viewModel.selectedPerson = it.firstSelectedItem
            } else {
                detailsLayout.setVisible(false)
            }
        })

        updateCustomer.addClickListener({
            viewModel.personEvent.emit(viewModel.selectedPerson)
            searchPersonLayout.setVisible(false)
            updatePersonView.setVisible(true)
        })

        updatePersonView.abortButton.addClickListener({
            searchPersonLayout.setVisible(true)
            updatePersonView.setVisible(false)
        })

        updatePersonView.submitButton.addClickListener({
            searchPersonLayout.setVisible(true)
            updatePersonView.setVisible(false)
        })

    }

    /**
     * Fills the panel with the detailed customer information of the currently selected customer
     * @param person The customer which
     */
    private void fillPanel(Person person){
        VerticalLayout content = new VerticalLayout()

        content.addComponent(new Label("<strong>${person.title == AcademicTitle.NONE ? "" : person.title} ${person.firstName} ${person.lastName}</strong>", ContentMode.HTML))
        content.addComponent(new Label("${person.emailAddress}", ContentMode.HTML))


        person.affiliations.each { affiliation ->
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
        def customerListDataProvider = new ListDataProvider<>(viewModel.getAvailablePersons())
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
            this.customerGrid.addColumn({ customer -> customer.firstName })
                    .setCaption("First Name").setId("FirstName")
            this.customerGrid.addColumn({ customer -> customer.lastName })
                    .setCaption("Last Name").setId("LastName")
            this.customerGrid.addColumn({ customer -> customer.emailAddress })
                    .setCaption("Email Address").setId("EmailAddress")
            this.customerGrid.addColumn({ customer ->
                customer.title == AcademicTitle.NONE ? "" : customer.title})
                    .setCaption("Title").setId("Title")

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
