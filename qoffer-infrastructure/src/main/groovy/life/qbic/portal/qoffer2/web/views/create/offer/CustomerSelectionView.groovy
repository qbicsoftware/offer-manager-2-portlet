package life.qbic.portal.qoffer2.web.views.create.offer

import com.vaadin.data.provider.DataProvider
import com.vaadin.data.provider.ListDataProvider
import com.vaadin.icons.VaadinIcons
import com.vaadin.shared.ui.ValueChangeMode
import com.vaadin.ui.Alignment
import com.vaadin.ui.Button
import com.vaadin.ui.Grid
import com.vaadin.ui.HorizontalLayout
import com.vaadin.ui.Label
import com.vaadin.ui.TextField
import com.vaadin.ui.VerticalLayout
import com.vaadin.ui.components.grid.HeaderRow
import com.vaadin.ui.themes.ValoTheme
import life.qbic.datamodel.dtos.business.AcademicTitle
import life.qbic.datamodel.dtos.business.Affiliation
import life.qbic.datamodel.dtos.business.AffiliationCategory
import life.qbic.datamodel.dtos.business.Customer

import life.qbic.portal.qoffer2.web.viewmodel.CreateOfferViewModel
import life.qbic.portal.qoffer2.web.views.GridUtils
import org.apache.commons.lang3.StringUtils

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

    HorizontalLayout addButtonsLayout
    Button createCustomerButton
    Grid<Customer> customerGrid
    HorizontalLayout customerLayout
    Grid<Affiliation> affiliationGrid
    HorizontalLayout affiliationLayout
    VerticalLayout affiliationLabelLayout
    HorizontalLayout createAffiliationLayout
    Button createAffiliationButton

    CustomerSelectionView(CreateOfferViewModel viewModel){
        this.viewModel = viewModel
        //this.searchCustomerView = searchCustomerView
        Affiliation testAffiliation = new Affiliation.Builder("organization","Street","postal code","city").category(AffiliationCategory.INTERNAL).build()
        Affiliation testAffiliation2 = new Affiliation.Builder("QBiC","Street","postal code","city").category(AffiliationCategory.EXTERNAL_ACADEMIC).build()


        this.foundCustomerList = viewModel.foundCustomers

        initLayout()
        generateCustomerGrid()
        generateAffiliationGrid()
        bindViewModel()
    }

    /**
     * Initializes the start layout of this view class
     */
    private void initLayout(){
        affiliationLabelLayout = new VerticalLayout()
        Label affiliationLabel = new Label("Select the Customers Affiliation")
        affiliationLabelLayout.addComponent(affiliationLabel)
        affiliationLabelLayout.setComponentAlignment(affiliationLabel, Alignment.MIDDLE_LEFT)

        addButtonsLayout = new HorizontalLayout()
        this.createCustomerButton = new Button("Create Customer", VaadinIcons.USER)
        createCustomerButton.addStyleName(ValoTheme.BUTTON_FRIENDLY)
        addButtonsLayout.addComponent(createCustomerButton)
        addButtonsLayout.setComponentAlignment(createCustomerButton, Alignment.MIDDLE_RIGHT)

        addButtonsLayout.setSizeFull()

        this.next = new Button(VaadinIcons.CHEVRON_CIRCLE_RIGHT)
        next.setEnabled(false)
        next.addStyleName(ValoTheme.LABEL_LARGE)

        this.previous = new Button(VaadinIcons.CHEVRON_CIRCLE_LEFT)
        previous.addStyleName(ValoTheme.LABEL_LARGE)

        HorizontalLayout buttonLayout = new HorizontalLayout(previous,next)
        buttonLayout.setComponentAlignment(next, Alignment.BOTTOM_RIGHT)
        buttonLayout.setComponentAlignment(previous, Alignment.BOTTOM_LEFT)
        buttonLayout.setSizeFull()

        this.customerGrid = new Grid<>()
        customerLayout = new HorizontalLayout(customerGrid)

        this.affiliationGrid = new Grid<>()
        affiliationLayout = new HorizontalLayout(affiliationGrid)

        this.addComponents(customerLayout, addButtonsLayout , buttonLayout)
    }

    /**
     * This method adds the retrieved Customer Information to the Customer grid
     */
    private ListDataProvider setupCustomerDataProvider() {
        def customerListDataProvider = new ListDataProvider<>(foundCustomerList)
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
            this.customerGrid.addColumn({ customer -> customer.title })
                    .setCaption("Title").setId("Title")
            this.customerGrid.addColumn({ customer -> customer.firstName })
                    .setCaption("First Name").setId("FirstName")
            this.customerGrid.addColumn({ customer -> customer.lastName })
                    .setCaption("Last Name").setId("LastName")
            this.customerGrid.addColumn({ customer -> customer.emailAddress })
                    .setCaption("Email Address").setId("EmailAddress")

            //specify size of grid and layout
            customerLayout.setSizeFull()
            customerGrid.setSizeFull()

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
            createAffiliationLayout = new HorizontalLayout()
            createAffiliationButton = new Button("Create Affiliation", VaadinIcons.OFFICE)
            createAffiliationButton.addStyleName(ValoTheme.BUTTON_FRIENDLY)

            createAffiliationLayout.addComponent(createAffiliationButton)
            createAffiliationLayout.setComponentAlignment(createAffiliationButton, Alignment.MIDDLE_RIGHT)
            createAffiliationLayout.setSizeFull()

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

            this.addComponent(affiliationLabelLayout,2)
            this.addComponent(affiliationGrid,3)
            this.addComponent(createAffiliationLayout,4)

        })

        affiliationGrid.addSelectionListener({
            Affiliation affiliation = affiliationGrid.getSelectedItems().getAt(0)
            viewModel.customerAffiliation = affiliation

            next.setEnabled(true)
        })
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
