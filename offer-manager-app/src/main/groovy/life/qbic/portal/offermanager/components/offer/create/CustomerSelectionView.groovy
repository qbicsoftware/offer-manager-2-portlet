package life.qbic.portal.offermanager.components.offer.create

import com.vaadin.data.provider.ListDataProvider
import com.vaadin.icons.VaadinIcons
import com.vaadin.shared.ui.grid.HeightMode
import com.vaadin.ui.*
import com.vaadin.ui.components.grid.HeaderRow
import com.vaadin.ui.themes.ValoTheme
import life.qbic.datamodel.dtos.business.AcademicTitle
import life.qbic.datamodel.dtos.business.Affiliation
import life.qbic.datamodel.dtos.business.Customer
import life.qbic.portal.offermanager.components.GridUtils
import life.qbic.portal.offermanager.components.Resettable

/**
 * This class generates a Layout in which the user
 * can select the customer for a whom the offer will be created
 *
 * CustomerSelectionView will be integrated into the CreateOfferView and provides an User Interface
 * with the intention of enabling a user the selecting a customer for whom an offer will be created in the
 * QBiC database.
 *
 * @since 0.1.0
 */
class CustomerSelectionView extends VerticalLayout implements Resettable {

    private final CreateOfferViewModel viewModel

    Button next
    Button previous

    Button updatePerson

    HorizontalLayout addButtonsLayout
    Button createCustomerButton
    Grid<Customer> customerGrid
    HorizontalLayout customerLayout
    Grid<Affiliation> affiliationGrid
    HorizontalLayout affiliationLayout
    VerticalLayout affiliationLabelLayout

    Label selectedCustomer

    Label selectedAffiliation

    VerticalLayout affiliationSelectionContainer

    CustomerSelectionView(CreateOfferViewModel viewModel) {
        this.viewModel = viewModel

        initLayout()
        generateCustomerGrid()
        generateAffiliationGrid()
        bindViewModel()
    }

    /**
     * Initializes the start layout of this view class
     */
    private void initLayout() {
        affiliationLabelLayout = new VerticalLayout()
        Label affiliationLabel = new Label("Select Customer Affiliation")
        affiliationLabelLayout.addComponent(affiliationLabel)
        affiliationLabelLayout.setComponentAlignment(affiliationLabel, Alignment.MIDDLE_LEFT)
        affiliationLabelLayout.setMargin(false)

        /*
        We start with the header, that contains a descriptive
        title of what the view is about.
         */
        final def title = new HorizontalLayout()
        final def label = new Label("Select Customer")
        label.addStyleName(ValoTheme.LABEL_HUGE)
        title.addComponent(label)
        this.addComponent(title)

        /*
        Provide a display the current selected customer with the selected affiliation
         */
        HorizontalLayout selectedCustomerOverview = new HorizontalLayout()
        def customerFullName =
                "${viewModel.customer?.firstName ?: ""} " +
                        "${viewModel.customer?.lastName ?: ""}"
        selectedCustomer = new Label(viewModel.customer?.lastName ? customerFullName : "-")
        selectedCustomer.setCaption("Current Customer")
        selectedCustomerOverview.addComponents(selectedCustomer)

        // We also add some basic affiliation information in the overview
        def affiliationInfo = "${viewModel.customerAffiliation?.organisation ?: "-"}"
        selectedAffiliation = new Label(affiliationInfo)
        selectedAffiliation.setCaption("Current Affiliation")
        selectedCustomerOverview.addComponents(selectedAffiliation)

        /*
        Add navigation elements
         */
        addButtonsLayout = new HorizontalLayout()
        this.createCustomerButton = new Button("Create Customer", VaadinIcons.USER)
        this.updatePerson = new Button("Update Customer", VaadinIcons.USER)
        updatePerson.setEnabled(false)
        createCustomerButton.addStyleName(ValoTheme.BUTTON_FRIENDLY)
        updatePerson.addStyleName(ValoTheme.BUTTON_FRIENDLY)
        addButtonsLayout.addComponents(createCustomerButton, updatePerson)
        addButtonsLayout.setComponentAlignment(createCustomerButton, Alignment.MIDDLE_RIGHT)
        addButtonsLayout.setComponentAlignment(updatePerson, Alignment.MIDDLE_RIGHT)


        addButtonsLayout.setHeightFull()

        this.next = new Button(VaadinIcons.CHEVRON_CIRCLE_RIGHT)
        next.setEnabled(false)
        next.addStyleName(ValoTheme.LABEL_LARGE)

        this.previous = new Button(VaadinIcons.CHEVRON_CIRCLE_LEFT)
        previous.addStyleName(ValoTheme.LABEL_LARGE)

        HorizontalLayout buttonLayout = new HorizontalLayout(previous, next)
        buttonLayout.setComponentAlignment(next, Alignment.BOTTOM_RIGHT)
        buttonLayout.setComponentAlignment(previous, Alignment.BOTTOM_LEFT)
        buttonLayout.setSizeFull()

        this.customerGrid = new Grid<>()
        customerLayout = new HorizontalLayout(customerGrid)

        this.affiliationGrid = new Grid<>()
        affiliationLayout = new HorizontalLayout(affiliationGrid)

        affiliationSelectionContainer = new VerticalLayout()
        affiliationSelectionContainer.addComponents(
                affiliationLabelLayout,
                affiliationLayout)
        affiliationSelectionContainer.setMargin(false)

        this.addComponents(
                selectedCustomerOverview,
                customerLayout,
                addButtonsLayout,
                affiliationSelectionContainer,
                buttonLayout)

        this.setComponentAlignment(addButtonsLayout, Alignment.MIDDLE_RIGHT)
        affiliationSelectionContainer.setVisible(false)

        this.setMargin(false)
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
                customer.title == AcademicTitle.NONE ? "" : customer.title
            })
                    .setCaption("Title").setId("Title")
            this.customerGrid.addColumn({ customer -> customer.firstName })
                    .setCaption("First Name").setId("FirstName")
            this.customerGrid.addColumn({ customer -> customer.lastName })
                    .setCaption("Last Name").setId("LastName")
            this.customerGrid.addColumn({ customer -> customer.emailAddress })
                    .setCaption("Email Address").setId("EmailAddress")

            //specify size of grid and layout
            customerLayout.setSizeFull()
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
            affiliationGrid.setWidthFull()

            affiliationGrid.setHeightMode(HeightMode.UNDEFINED)

        } catch (Exception e) {
            new Exception("Unexpected exception in building the affiliation grid", e)
        }

    }

    private void bindViewModel() {

        customerGrid.addSelectionListener({ selection ->
            if (selection.firstSelectedItem.isPresent()) {
                Customer selectedCustomer = selection.getFirstSelectedItem().get()
                //vaadin is in single selection mode, selecting the first item will be fine
                List<Affiliation> affiliations = selectedCustomer.affiliations

                viewModel.customer = selectedCustomer
                // We explicitly reset any existing selected affiliation, as the user
                // must provide it again after changing the customer.
                viewModel.customerAffiliation = null

                affiliationGrid.setItems(affiliations)

                affiliationSelectionContainer.setVisible(true)
            } else {
                affiliationSelectionContainer.setVisible(false)
                //removing selection from view model
                viewModel.customer = null
                viewModel.customerAffiliation = null
                //clear grid on removing the selection
                affiliationGrid.setItems([])
            }

        })

        affiliationGrid.addSelectionListener({
            if (it.firstSelectedItem.isPresent()) {
                Affiliation affiliation = it.firstSelectedItem.get()
                viewModel.customerAffiliation = affiliation
            }
        })

        /*
        Let's listen to changes in customer selections and update it in the
        display, if the customer or affiliation selection has changed.
         */
        viewModel.addPropertyChangeListener({
            if (it.propertyName.equals("customer")) {
                if (it.getNewValue()) {
                    updatePerson.setEnabled(true)
                    def customerFullName =
                            "${viewModel.customer?.firstName ?: ""} " +
                                    "${viewModel.customer?.lastName ?: ""}"
                    selectedCustomer.setValue(customerFullName)
                } else {
                    updatePerson.setEnabled(false)
                    selectedCustomer.setValue("-")
                }
            }
            if (it.propertyName.equals("customerAffiliation")) {
                def affiliationInfo = "${viewModel.customerAffiliation?.organisation ?: "-"}"
                selectedAffiliation.setValue(affiliationInfo)
            }
            /*
            We allow the user to continue with the offer,
            if a customer and an affiliation has been selected.
             */
            if (viewModel.customer && viewModel.customerAffiliation) {
                next.setEnabled(true)
            } else {
                next.setEnabled(false)
            }
        })

        viewModel.addPropertyChangeListener("foundCustomers", {
            if (it instanceof ObservableList.ElementEvent) {
                this.customerGrid.getDataProvider().refreshAll()
            }
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

    @Override
    void reset() {
        resetSelection()
        viewModel.customer = null
        viewModel.customerAffiliation = null
    }


    private void resetSelection() {
        selectedCustomer.setValue("-")
        selectedAffiliation.setValue("-")
        customerGrid.deselectAll()
        affiliationGrid.deselectAll()
    }
}
