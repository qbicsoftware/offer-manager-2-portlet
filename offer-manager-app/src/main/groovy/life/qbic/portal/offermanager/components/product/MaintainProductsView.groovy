package life.qbic.portal.offermanager.components.product

import com.vaadin.data.provider.DataProvider
import com.vaadin.data.provider.ListDataProvider
import com.vaadin.icons.VaadinIcons
import com.vaadin.shared.data.sort.SortDirection
import com.vaadin.ui.Alignment
import com.vaadin.ui.Button
import com.vaadin.ui.FormLayout
import com.vaadin.ui.Grid
import com.vaadin.ui.HorizontalLayout
import com.vaadin.ui.Label
import com.vaadin.ui.VerticalLayout
import com.vaadin.ui.components.grid.HeaderRow
import com.vaadin.ui.themes.ValoTheme
import life.qbic.business.offers.Currency
import life.qbic.datamodel.dtos.business.services.Product
import life.qbic.portal.offermanager.components.GridUtils
import life.qbic.portal.offermanager.components.product.archive.ArchiveProductView
import life.qbic.portal.offermanager.components.product.create.CreateProductView
import life.qbic.portal.offermanager.dataresources.offers.OfferOverview

/**
 *
 * <h1>This class generates a Form Layout in which the user can maintain the service products</h1>
 *
 * <p>{@link MaintainProductsViewModel} will be integrated into the qOffer 2.0 Portlet and provides an User Interface
 * with the intention of enabling an {@value life.qbic.portal.offermanager.security.Role#OFFER_ADMIN} to create, archive and copy products.</p>
 *
 * @since 1.0.0
 *
 */
class MaintainProductsView extends FormLayout{

    private final MaintainProductsViewModel viewModel

    VerticalLayout overviewLayout
    Grid<Product> productGrid
    HorizontalLayout buttonLayout
    Button addProduct
    Button copyProduct
    Button archiveProduct

    CreateProductView createProductView
    ArchiveProductView archiveProductView
    CreateProductView copyProductView

    MaintainProductsView(MaintainProductsViewModel viewModel, CreateProductView createProductView
                         , ArchiveProductView archiveProductView, CreateProductView copyProductView){
        //todo add the controller
        this.viewModel = viewModel
        this.createProductView = createProductView
        this.copyProductView = copyProductView
        this.archiveProductView = archiveProductView

        setupTitle()
        createButtons()
        setupGrid()
        setupDataProvider()
        setupOverviewLayout()
        addSubViews()
        setupListeners()
    }

    private void setupTitle(){
        Label label = new Label("Service Product Maintenance")
        label.setStyleName(ValoTheme.LABEL_HUGE)
        this.addComponent(label)
    }

    private void createButtons(){
        addProduct = new Button("Add Product", VaadinIcons.PLUS)
        copyProduct = new Button ("Copy Product", VaadinIcons.COPY)
        archiveProduct = new Button("Archive Product", VaadinIcons.ARCHIVE)
        buttonLayout = new HorizontalLayout()

        buttonLayout.addComponents(addProduct,copyProduct,archiveProduct)
        buttonLayout.setComponentAlignment(addProduct, Alignment.MIDDLE_RIGHT)
        buttonLayout.setComponentAlignment(copyProduct, Alignment.MIDDLE_RIGHT)
        buttonLayout.setComponentAlignment(archiveProduct, Alignment.MIDDLE_RIGHT)

    }

    private void setupOverviewLayout(){
        overviewLayout = new VerticalLayout()
        overviewLayout.addComponents(productGrid,buttonLayout)
        this.addComponents(overviewLayout)
    }

    private void setupGrid(){
        productGrid = new Grid<>()

        productGrid.addColumn({ product -> product.productId.toString() })
                .setCaption("Product Id").setId("ProductId")
        productGrid.addColumn({ product -> product.productName })
                .setCaption("Name").setId("ProductName")
        productGrid.addColumn({ product -> product.description })
                .setCaption("Description").setId("ProductDescription")
        productGrid.addColumn({ product -> Currency.getFormatterWithSymbol().format(product.unitPrice) })
                .setCaption("Price").setId("UnitPrice")
        productGrid.addColumn({ product -> product.unit.value})
                .setCaption("Unit").setId("ProductUnit")

        productGrid.setWidthFull()

        def productsDataProvider = setupDataProvider()
        setupFilters(productsDataProvider)
    }

    private ListDataProvider setupDataProvider(){
        def dataProvider = new ListDataProvider(viewModel.products)

        productGrid.setDataProvider(dataProvider)
        return dataProvider
    }

    private void setupFilters(ListDataProvider<OfferOverview> dataProvider){
        HeaderRow productsFilterRow = productGrid.appendHeaderRow()
        GridUtils.setupColumnFilter(dataProvider,
                productGrid.getColumn("ProductId"),
                productsFilterRow)
        GridUtils.setupColumnFilter(dataProvider,
                productGrid.getColumn("ProductName"),
                productsFilterRow)
    }

    private void addSubViews(){
        this.addComponents(createProductView,copyProductView,archiveProductView)
        createProductView.setVisible(false)
        copyProductView.setVisible(false)
        archiveProductView.setVisible(false)
    }

    private void setupListeners(){
        addProduct.addClickListener({
            overviewLayout.setVisible(false)
            createProductView.setVisible(true)
        })

        copyProduct.addClickListener({
            overviewLayout.setVisible(false)
            copyProduct.setVisible(true)
        })

        archiveProduct.addClickListener({
            overviewLayout.setVisible(false)
            archiveProductView.setVisible(true)
        })
    }

}