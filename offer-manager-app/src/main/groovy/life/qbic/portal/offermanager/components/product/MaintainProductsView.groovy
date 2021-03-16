package life.qbic.portal.offermanager.components.product

import com.vaadin.data.provider.ListDataProvider
import com.vaadin.icons.VaadinIcons
import com.vaadin.ui.Alignment
import com.vaadin.ui.Button
import com.vaadin.ui.Grid
import com.vaadin.ui.HorizontalLayout
import com.vaadin.ui.Label
import com.vaadin.ui.Panel
import com.vaadin.ui.VerticalLayout
import com.vaadin.ui.components.grid.HeaderRow
import com.vaadin.ui.themes.ValoTheme
import life.qbic.business.offers.Currency
import life.qbic.datamodel.dtos.business.services.Product
import life.qbic.portal.offermanager.components.GridUtils
import life.qbic.portal.offermanager.components.product.create.CreateProductView
import life.qbic.portal.offermanager.dataresources.offers.OfferOverview

/**
 *
 * <h1>This class generates a VerticalLayout in which the user can maintain the service products</h1>
 *
 * <p>{@link MaintainProductsViewModel} will be integrated into the qOffer 2.0 Portlet and provides an User Interface
 * with the intention of enabling an {@value life.qbic.portal.offermanager.security.Role#OFFER_ADMIN} to create, archive and copy products.</p>
 *
 * @since 1.0.0
 *
 */
class MaintainProductsView extends VerticalLayout{

    private final MaintainProductsViewModel viewModel

    Grid<Product> productGrid
    HorizontalLayout buttonLayout
    Button addProduct
    Button copyProduct
    Button archiveProduct
    Panel productDescription

    CreateProductView createProductView
    CreateProductView copyProductView

    MaintainProductsView(MaintainProductsViewModel viewModel, CreateProductView createProductView
                         , CreateProductView copyProductView){
        //todo add the controller
        this.viewModel = viewModel
        this.createProductView = createProductView
        this.copyProductView = copyProductView

        setupTitle()
        setupPanel()
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

        buttonLayout = new HorizontalLayout(productDescription, addProduct,copyProduct,archiveProduct)
        buttonLayout.setMargin(false)
    }

    private void setupGrid(){
        productGrid = new Grid<>()

        productGrid.addColumn({ product -> product.productId.toString() })
                .setCaption("Product Id").setId("ProductId")
        productGrid.addColumn({ product -> product.productName })
                .setCaption("Name").setId("ProductName")
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

    private void setupPanel(){
        productDescription = new Panel("Product Description")
    }

    private void setupOverviewLayout(){
        this.setSizeFull()
        this.setMargin(false)
        this.addComponents(productGrid,buttonLayout)

        this.setComponentAlignment(buttonLayout,Alignment.TOP_RIGHT)
    }

    private void addSubViews(){
        this.addComponents(createProductView,copyProductView)
        createProductView.setVisible(false)
        copyProductView.setVisible(false)
    }

    private void updateProductDescription(Product product){
        VerticalLayout content = new VerticalLayout()
        //todo get product category
        //content.addComponent(new Label("<strong>${product}</strong>", ContentMode.HTML))
        content.addComponent(new Label("${product.description}"))
        content.setMargin(true)
        content.setSpacing(false)
        this.productDescription.setContent(content)
    }

    private void setupListeners(){

        productGrid.addSelectionListener({
            if(it.firstSelectedItem.isPresent()){
                updateProductDescription(it.firstSelectedItem.get())
            }
        })

        addProduct.addClickListener({
            this.setVisible(false)
            createProductView.setVisible(true)
        })

        copyProduct.addClickListener({
            this.setVisible(false)
            copyProduct.setVisible(true)
        })

        archiveProduct.addClickListener({
            //todo use the controller to trigger the use case
        })

    }

}
