package life.qbic.portal.qoffer2.web.views.create.offer

import com.vaadin.icons.VaadinIcons
import com.vaadin.ui.Alignment
import com.vaadin.ui.Button
import com.vaadin.ui.Grid
import com.vaadin.ui.HorizontalLayout
import com.vaadin.ui.Label
import com.vaadin.ui.VerticalLayout
import life.qbic.datamodel.dtos.business.AcademicTitle
import life.qbic.datamodel.dtos.business.Affiliation
import life.qbic.datamodel.dtos.business.Customer
import life.qbic.portal.qoffer2.web.viewmodel.CreateOfferViewModel

/**
 * This class generates a Layout in which the user
 * can input the necessary information about a project
 *
 * CustomerSelectionView will be integrated into the CreateOfferView and provides an User Interface
 * with the intention of enabling a user the selecting a customer for whom an offer will be created in the
 * QBiC database.
 *
 * @since: 0.1.0
 *
 */
class CustomerSelectionView extends VerticalLayout{

    private final CreateOfferViewModel viewModel
    //private final SearchCustomerView searchCustomerView
    private final List<Customer> foundCustomerList

    Button next
    Grid<Customer> customerGrid
    HorizontalLayout gridLayout

    CustomerSelectionView(CreateOfferViewModel viewModel){
        this.viewModel = viewModel
        //this.searchCustomerView = searchCustomerView

        Customer customer = new Customer.Builder("Max", null, "a.b@c.de").title(AcademicTitle.DOCTOR).affiliation([new Affiliation.Builder("organization","Street","postal code","city").build()]).build()

        this.foundCustomerList = [customer] //searchCustomerViewModel.foundCustomers

        initLayout()
        setupDataProvider()
        generateGrid()
        bindViewModel()
    }

    private void initLayout(){
        VerticalLayout layout = new VerticalLayout()
        Label titleLabel = new Label("Select Customer")
        layout.addComponent(titleLabel)
        layout.setComponentAlignment(titleLabel, Alignment.BOTTOM_LEFT)

        this.next = new Button(VaadinIcons.CHEVRON_CIRCLE_RIGHT)
        next.setEnabled(false)

        HorizontalLayout buttonLayout = new HorizontalLayout(next)
        buttonLayout.setComponentAlignment(next, Alignment.BOTTOM_RIGHT)
        buttonLayout.setSizeFull()

        this.customerGrid = new Grid<Customer>()
        gridLayout = new HorizontalLayout(customerGrid)

        this.addComponents(layout, gridLayout, buttonLayout)
    }

    /**
     * This method adds the retrieved Customer Information to the Customer grid
     */
    private void setupDataProvider() {
        this.customerGrid.setItems(foundCustomerList)
    }

    /**
     * Method which generates the grid and populates the columns with the set Customer information from the setupDataProvider Method
     *
     * This Method is responsible for setting up the grid and setting the customer information to the individual grid columns.
     * If the grid is generated correctly it will be added to the preset grid layout and also enables the clear customer grid button.
     */
    private def generateGrid() {
        //Clear Grid before the new grid is populated
        try {
            this.customerGrid.addColumn({ customer -> customer.getFirstName() }).setCaption("First Name")
            this.customerGrid.addColumn({ customer -> customer.getLastName() }).setCaption("Last Name")
            this.customerGrid.addColumn({ customer -> customer.geteMailAddress() }).setCaption("Email Address")
            this.customerGrid.addColumn({ customer -> customer.getTitle() }).setCaption("Title")
            this.customerGrid.addColumn({ customer -> customer.getAffiliations().toString() }).setCaption("Affiliation")

            //specify size of grid and layout
            gridLayout.setSizeFull()
            customerGrid.setSizeFull()

        } catch (Exception e) {
            new Exception("Unexpected exception in building the customer grid", e)
        }

    }

    private void bindViewModel() {

        customerGrid.addSelectionListener({ selection ->
            println selection.firstSelectedItem
        })

    }
}
