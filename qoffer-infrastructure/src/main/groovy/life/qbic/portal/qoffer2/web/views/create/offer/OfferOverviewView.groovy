package life.qbic.portal.qoffer2.web.views.create.offer

import com.vaadin.icons.VaadinIcons
import com.vaadin.ui.Alignment
import com.vaadin.ui.Button
import com.vaadin.ui.Grid
import com.vaadin.ui.HorizontalLayout
import com.vaadin.ui.Label
import com.vaadin.ui.Panel
import com.vaadin.ui.TextArea
import com.vaadin.ui.TextField
import com.vaadin.ui.VerticalLayout
import com.vaadin.ui.themes.ValoTheme
import life.qbic.portal.qoffer2.web.viewmodel.CreateOfferViewModel
import life.qbic.portal.qoffer2.web.viewmodel.ProductItemViewModel

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
        Label titleLabel = new Label("Offer Overview")

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

        this.addComponents(titleLabel,offerOverview,buttonLayout)
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
            grid.addColumn({ productItem -> productItem.product.unitPrice }).setCaption("Product Unit Price")
            grid.addColumn({ productItem -> productItem.product.unit.value }).setCaption("Product Unit")

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
        VerticalLayout content = new VerticalLayout()
        content.addComponent(new Label("${createOfferViewModel.projectTitle}"))
        content.addComponent(new Label("${createOfferViewModel.projectDescription}"))
        content.addComponent(new Label("${createOfferViewModel.customer}"))
        content.addComponent(new Label("${createOfferViewModel.customerAffiliation}"))
        content.addComponent(new Label("${createOfferViewModel.projectManager}"))
        content.addComponent(itemGrid)

        Label price = new Label("total price: ${createOfferViewModel.offerPrice}")
        content.addComponent(price)

        content.setComponentAlignment(price,Alignment.MIDDLE_RIGHT)

        offerOverview.setContent(content)
    }

}
