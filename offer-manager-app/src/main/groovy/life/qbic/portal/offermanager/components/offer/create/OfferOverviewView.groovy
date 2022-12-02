package life.qbic.portal.offermanager.components.offer.create

import com.vaadin.data.provider.ListDataProvider
import com.vaadin.icons.VaadinIcons
import com.vaadin.ui.*
import com.vaadin.ui.components.grid.HeaderRow
import com.vaadin.ui.renderers.NumberRenderer
import com.vaadin.ui.themes.ValoTheme
import groovy.util.logging.Log4j2
import life.qbic.business.offers.Currency
import life.qbic.portal.offermanager.components.GridUtils

/**
 * This class generates a Layout in which the user
 * gets an overview over the offer he has just created.
 *
 * OfferOverviewView will be integrated into the CreateOfferView and provides an User Interface
 * with the intention of allowing the user to get an overview over all information he has entered before he creates and stores an
 * offer in the QBiC database.
 *
 * @since 0.1.0
 */
@Log4j2
class OfferOverviewView extends VerticalLayout {

    private final CreateOfferViewModel createOfferViewModel

    Panel offerOverview
    ItemsGrid itemGrid
    Button previous
    Button save
    Grid priceFieldGrid

    OfferOverviewView(CreateOfferViewModel viewModel) {
        this.createOfferViewModel = viewModel
        initLayout()
        setUpGrid()
    }

    private void setUpGrid() {
        itemGrid.setHeightByRows(6)
        itemGrid.enableDragAndDrop()
        this.itemGrid.setItems(createOfferViewModel.getProductItems())
        def provider = itemGrid.getDataProvider() as ListDataProvider<ProductItemViewModel>
        setupFilters(provider, itemGrid)
        addListeners()
    }

    private static <T> void setupFilters(ListDataProvider<T> productListDataProvider,
                                         Grid targetGrid) {
        HeaderRow customerFilterRow = targetGrid.appendHeaderRow()
        GridUtils.setupColumnFilter(productListDataProvider,
                targetGrid.getColumn("ProductName"),
                customerFilterRow)
        GridUtils.setupColumnFilter(productListDataProvider,
                targetGrid.getColumn("ProductDescription"),
                customerFilterRow)
    }

    /**
     * Initializes the start layout for this view
     */
    private void initLayout() {
        this.previous = new Button(VaadinIcons.CHEVRON_CIRCLE_LEFT)
        previous.addStyleName(ValoTheme.LABEL_LARGE)

        this.save = new Button("Save Offer", VaadinIcons.CHECK_SQUARE)
        save.addStyleName(ValoTheme.LABEL_LARGE)

        HorizontalLayout offerActionButtons = new HorizontalLayout(save)
        HorizontalLayout buttonLayout = new HorizontalLayout(previous, offerActionButtons)
        buttonLayout.setSizeFull()

        buttonLayout.setComponentAlignment(previous, Alignment.MIDDLE_LEFT)
        buttonLayout.setComponentAlignment(offerActionButtons, Alignment.MIDDLE_RIGHT)

        this.offerOverview = new Panel("Offer Details:")

        this.itemGrid = new ItemsGrid(createOfferViewModel)
        this.itemGrid.setCaption("Selected Items:")

        this.addComponents(offerOverview, buttonLayout)
        this.setMargin(false)
    }

    /**
     * Fills the information of the offer into the panel
     */
    void fillPanel() {
        /*
        Container for the complete overview
         */
        VerticalLayout content = new VerticalLayout()
        /*
        A header that contains basic project info and the price overview
         */
        HorizontalLayout header = new HorizontalLayout()
        /*
        The detailed project information container
         */

        VerticalLayout projectInfoForm = new VerticalLayout()

        Label customer = new Label("${createOfferViewModel.customer}")
        customer.setIcon(VaadinIcons.USER)
        customer.setCaption("Customer")
        projectInfoForm.addComponent(customer)

        Label affiliation = new Label("${createOfferViewModel.customerAffiliation}")
        affiliation.setIcon(VaadinIcons.WORKPLACE)
        affiliation.setCaption("Affiliation")
        projectInfoForm.addComponent(affiliation)

        Label projectManager = new Label("${createOfferViewModel.projectManager}")
        projectManager.setIcon(VaadinIcons.USER_STAR)
        projectManager.setCaption("Project Manager")
        projectInfoForm.addComponent(projectManager)

        TextArea objective = new TextArea("Objective")
        objective.setIcon(VaadinIcons.TRENDING_UP)
        objective.setValue(createOfferViewModel.projectObjective)
        objective.setEnabled(false)
        objective.setWidth("100%")
        projectInfoForm.addComponent(objective)

        TextArea experimentInfo = new TextArea("Experimental design")
        experimentInfo.setIcon(VaadinIcons.NOTEBOOK)
        experimentInfo.setValue(
                createOfferViewModel.experimentalDesign ? createOfferViewModel.experimentalDesign : "No design defined.")
        experimentInfo.setEnabled(false)
        experimentInfo.setWidth("100%")
        projectInfoForm.addComponent(experimentInfo)


        /*
        Here we set the header components, which is the project info
        on the left and a basic cost overview on the right
         */
        header.addComponent(projectInfoForm)
        Panel costOverview = createCostOverview()
        header.addComponent(costOverview)
        header.setWidthFull()
        header.setComponentAlignment(costOverview, Alignment.TOP_CENTER)
        header.setDefaultComponentAlignment(Alignment.TOP_LEFT)
        /*
        We add the header as top component in the final view
         */
        Label spacer = new Label("")
        Label title = new Label("${createOfferViewModel.projectTitle}")
        title.addStyleName(ValoTheme.LABEL_HUGE)
        content.addComponents(spacer, title)
        content.addComponent(header)
        content.addComponent(itemGrid)

        offerOverview.setContent(content)
    }

    private Panel createCostOverview() {
        final Panel panel = new Panel("Cost Overview")
        panel.setSizeUndefined()
        priceFieldGrid = new Grid<>()
        priceFieldGrid.setHeightByRows(5)
        priceFieldGrid.setItems([
                new PriceField("Net Price (incl. discount)", createOfferViewModel.netPrice),
                new PriceField("Overheads", createOfferViewModel.overheads),
                new PriceField("Taxes", createOfferViewModel.taxes),
                new PriceField("Total Discount (considered)", createOfferViewModel.totalDiscountAmount * -1),
                new PriceField("Total Price", createOfferViewModel.totalPrice)
        ])
        priceFieldGrid.addColumn(PriceField::getName)
        priceFieldGrid.addColumn({
            costs -> costs.value
        },
                new NumberRenderer(Currency.getFormatterWithSymbol()))

        priceFieldGrid.headerVisible = false
        panel.setContent(priceFieldGrid)

        return panel
    }

    void refreshPricePanel() {
        if (priceFieldGrid)
            priceFieldGrid.setItems([
                    new PriceField("Net Price (incl. discount)", createOfferViewModel.totalPrice),
                    new PriceField("Overheads", createOfferViewModel.overheads),
                    new PriceField("Taxes", createOfferViewModel.taxes),
                    new PriceField("Total Discount (considered)", createOfferViewModel.totalDiscountAmount * -1),
                    new PriceField("Total Price", createOfferViewModel.totalPrice)
            ])
    }

    /*
    Small helper object, that will display information
    about individual price positions for offer overviews.
     */

    private class PriceField {

        String name

        Double value

        PriceField(String name, Double value) {
            this.name = name
            this.value = value
        }
    }
}
