package life.qbic.portal.qoffer2.web.views.create.offer

import com.vaadin.icons.VaadinIcons
import com.vaadin.ui.Alignment
import com.vaadin.ui.Button
import com.vaadin.ui.Grid
import com.vaadin.ui.HorizontalLayout
import com.vaadin.ui.Label
import com.vaadin.ui.Panel
import com.vaadin.ui.VerticalLayout
import com.vaadin.ui.renderers.NumberRenderer
import com.vaadin.ui.themes.ValoTheme
import life.qbic.datamodel.dtos.business.ProductItem
import life.qbic.portal.portlet.offers.Currency
import life.qbic.portal.qoffer2.web.viewmodel.CreateOfferViewModel
import life.qbic.portal.qoffer2.web.viewmodel.ProductItemViewModel

import java.text.DecimalFormat

/**
 * This class generates a Layout in which the user
 * gets an overview over the offer he has just created.
 *
 * OfferOverviewView will be integrated into the CreateOfferView and provides an User Interface
 * with the intention of allowing the user to get an overview over all information he has entered before he creates and stores an
 * offer in the QBiC database.
 *
 * @since: 0.1.0
 *
 */
class OfferOverviewView extends VerticalLayout{

    private final CreateOfferViewModel createOfferViewModel

    Panel offerOverview
    Grid<ProductItemViewModel> itemGrid
    Button previous
    Button save

    OfferOverviewView(CreateOfferViewModel viewModel){
        this.createOfferViewModel = viewModel

        initLayout()
    }

    /**
     * Initializes the start layout for this view
     */
    private void initLayout(){
        this.previous = new Button(VaadinIcons.CHEVRON_CIRCLE_LEFT)
        previous.addStyleName(ValoTheme.LABEL_LARGE)

        this.save = new Button(VaadinIcons.SAFE)
        save.addStyleName(ValoTheme.LABEL_LARGE)


        HorizontalLayout buttonLayout = new HorizontalLayout(previous,save)
        buttonLayout.setSizeFull()
        buttonLayout.setComponentAlignment(previous, Alignment.MIDDLE_LEFT)
        buttonLayout.setComponentAlignment(save, Alignment.MIDDLE_RIGHT)

        this.offerOverview = new Panel("Offer Details:")

        this.itemGrid = new Grid<>("Selected items:")
        this.itemGrid.setItems(createOfferViewModel.productItems)
        generateProductGrid(itemGrid)

        this.addComponents(offerOverview,buttonLayout)
    }

    /**
     * Method which generates the grid and populates the columns with the set product information from the setupDataProvider Method
     *
     * This Method is responsible for setting up the grid and setting the product information to the individual grid columns.
     */
    private static void generateProductGrid(Grid<ProductItemViewModel> grid) {
        try {

            grid.addColumn({ productItem -> productItem.quantity }).setCaption("Quantity")
            grid.addColumn({ productItem -> productItem.product.productName }).setCaption("Product Name")
            grid.addColumn({ productItem -> productItem.product.description }).setCaption("Product Description")
            grid.addColumn({ productItem -> productItem.product.unitPrice }, new NumberRenderer(Currency.currencyFormat)).setCaption("Product Unit Price")
            grid.addColumn({ productItem -> productItem.product.unit }).setCaption("Product Unit")


            //specify size of grid and layout
            grid.setSizeFull()

        } catch (Exception e) {
            new Exception("Unexpected exception in building the product item grid", e)
        }
    }

    /**
     * Fills the information of the offer into the panel
     */
    void fillPanel(){
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
        VerticalLayout projectInfo = new VerticalLayout()
        projectInfo.addComponent(new Label("${createOfferViewModel.projectTitle}"))
        projectInfo.addComponent(new Label("${createOfferViewModel.projectDescription}"))
        projectInfo.addComponent(new Label("${createOfferViewModel.customer}"))
        projectInfo.addComponent(new Label("${createOfferViewModel.customerAffiliation}"))
        projectInfo.addComponent(new Label("${createOfferViewModel.projectManager}"))
        /*
        Here we set the header components, which is the project info
        on the left and a basic cost overview on the right
         */
        header.addComponent(projectInfo)
        header.addComponent(createCostOverview())
        header.setWidthFull()
        header.setDefaultComponentAlignment(Alignment.TOP_LEFT)
        /*
        We add the header as top component in the final view
         */
        content.addComponent(header)
        content.addComponent(itemGrid)

        offerOverview.setContent(content)
    }

    private Panel createCostOverview() {
        final Panel panel = new Panel("Cost overview")
        panel.setSizeUndefined()
        Grid<PriceField> gridLayout = new Grid<>()
        gridLayout.setHeightByRows(4)
        gridLayout.setItems([
                new PriceField("Net price", createOfferViewModel.netPrice),
                new PriceField("Overheads", createOfferViewModel.overheads),
                new PriceField("Taxes", createOfferViewModel.taxes),
                new PriceField("Total price", createOfferViewModel.totalPrice)
        ])
        gridLayout.addColumn(PriceField::getName)
        gridLayout.addColumn(  {
            costs -> costs.value},
                new NumberRenderer(Currency.currencyFormat))

        gridLayout.headerVisible = false
        panel.setContent(gridLayout)

        return panel
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
