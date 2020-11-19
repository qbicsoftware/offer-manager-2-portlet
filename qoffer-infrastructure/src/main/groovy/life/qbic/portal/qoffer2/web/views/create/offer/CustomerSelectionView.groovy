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
import life.qbic.datamodel.dtos.business.AffiliationCategory
import life.qbic.datamodel.dtos.business.Customer

import life.qbic.portal.qoffer2.web.viewmodel.CreateOfferViewModel

/**
 * This class generates a Layout in which the user
 * can select the customer for a whom the offer will be created
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
    Button previous

    Grid<Customer> customerGrid
    HorizontalLayout customerLayout
    Grid<Affiliation> affiliationGrid
    HorizontalLayout affiliationLayout

    CustomerSelectionView(CreateOfferViewModel viewModel){
        this.viewModel = viewModel
        //this.searchCustomerView = searchCustomerView
        Affiliation testAffiliation = new Affiliation.Builder("organization","Street","postal code","city").category(AffiliationCategory.INTERNAL).build()
        Affiliation testAffiliation2 = new Affiliation.Builder("QBiC","Street","postal code","city").category(AffiliationCategory.EXTERNAL_ACADEMIC).build()

        Customer customer = new Customer.Builder("Max", "Mustermann", "a.b@c.de").title(AcademicTitle.DOCTOR).affiliation(testAffiliation).build()
        Customer customer2 = new Customer.Builder("Max2", "Mustermann", "a.b@c.de").title(AcademicTitle.DOCTOR).affiliations([testAffiliation,testAffiliation2]).build()
        Customer customer3 = new Customer.Builder("Max3", "Mustermann", "a.b@c.de").title(AcademicTitle.DOCTOR).affiliation(testAffiliation2).build()

        this.foundCustomerList = [customer,customer2,customer3] //searchCustomerViewModel.foundCustomers

        initLayout()
        setupDataProvider()
        generateCustomerGrid()
        generateAffiliationGrid()
        bindViewModel()
    }

    /**
     * Initializes the start layout of this view class
     */
    private void initLayout(){
        VerticalLayout layout = new VerticalLayout()
        Label titleLabel = new Label("Select Customer")
        layout.addComponent(titleLabel)
        layout.setComponentAlignment(titleLabel, Alignment.BOTTOM_LEFT)

        this.next = new Button(VaadinIcons.CHEVRON_CIRCLE_RIGHT)
        next.setEnabled(false)
        this.previous = new Button(VaadinIcons.CHEVRON_CIRCLE_LEFT)

        HorizontalLayout buttonLayout = new HorizontalLayout(previous,next)
        buttonLayout.setComponentAlignment(next, Alignment.BOTTOM_RIGHT)
        buttonLayout.setComponentAlignment(previous, Alignment.BOTTOM_LEFT)
        buttonLayout.setSizeFull()

        this.customerGrid = new Grid<>()
        customerLayout = new HorizontalLayout(customerGrid)

        this.affiliationGrid = new Grid<>()
        affiliationLayout = new HorizontalLayout(affiliationGrid)

        this.addComponents(layout, customerLayout, buttonLayout)
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
     */
    private def generateCustomerGrid() {
        try {
            this.customerGrid.addColumn({ customer -> customer.title }).setCaption("Title")
            this.customerGrid.addColumn({ customer -> customer.firstName }).setCaption("First Name")
            this.customerGrid.addColumn({ customer -> customer.lastName }).setCaption("Last Name")
            this.customerGrid.addColumn({ customer -> customer.emailAddress }).setCaption("Email Address")
            //this.customerGrid.addColumn({ customer -> customer.getAffiliations().toString() }).setCaption("Affiliation")

            //specify size of grid and layout
            customerLayout.setSizeFull()
            customerGrid.setSizeFull()

        } catch (Exception e) {
            new Exception("Unexpected exception in building the customer grid", e)
        }
    }

    /**
     * Method which generates the grid and populates the columns with the set Customer information from the setupDataProvider Method
     *
     * This Method is responsible for setting up the grid and setting the affiliation information to the individual grid columns.
     */
    private def generateAffiliationGrid() {
        try {
            this.affiliationGrid.addColumn({ affiliation -> affiliation.organisation }).setCaption("Organization")
            this.affiliationGrid.addColumn({ affiliation -> affiliation.addressAddition }).setCaption("Address Addition")
            this.affiliationGrid.addColumn({ affiliation -> affiliation.street }).setCaption("Street")
            this.affiliationGrid.addColumn({ affiliation -> affiliation.postalCode }).setCaption("Postal Code")
            this.affiliationGrid.addColumn({ affiliation -> affiliation.city }).setCaption("City")
            this.affiliationGrid.addColumn({ affiliation -> affiliation.country }).setCaption("Country")
            this.affiliationGrid.addColumn({ affiliation -> affiliation.category.value }).setCaption("Category")

            //specify size of grid and layout
            affiliationLayout.setSizeFull()
            affiliationGrid.setSizeFull()

        } catch (Exception e) {
            new Exception("Unexpected exception in building the affiliation grid", e)
        }

    }

    private void bindViewModel() {

        customerGrid.addSelectionListener({ selection ->
            //vaadin is in single selection mode, selecting the first item will be fine
            List<Affiliation> affiliations = customerGrid.getSelectedItems().getAt(0).affiliations
            Customer customer = customerGrid.getSelectedItems().getAt(0)

            viewModel.customer = customer

            //todo do we need to clear the grid for another selection?
            affiliationGrid.setItems(affiliations)

            this.addComponent(affiliationGrid,2)
        })

        affiliationGrid.addSelectionListener({
            Affiliation affiliation = affiliationGrid.getSelectedItems().getAt(0)
            viewModel.customerAffiliation = affiliation

            next.setEnabled(true)
        })
    }
}
