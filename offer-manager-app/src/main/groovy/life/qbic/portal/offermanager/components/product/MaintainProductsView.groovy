package life.qbic.portal.offermanager.components.product

import com.vaadin.data.provider.ListDataProvider
import com.vaadin.icons.VaadinIcons
import com.vaadin.ui.Alignment
import com.vaadin.ui.Button
import com.vaadin.ui.FormLayout
import com.vaadin.ui.Grid
import com.vaadin.ui.HorizontalLayout
import com.vaadin.ui.Label
import com.vaadin.ui.Panel
import com.vaadin.ui.VerticalLayout
import com.vaadin.ui.components.grid.HeaderRow
import com.vaadin.ui.themes.ValoTheme
import com.vaadin.ui.Grid.Column
import com.vaadin.ui.renderers.TextRenderer
import life.qbic.business.offers.Currency
import life.qbic.datamodel.dtos.business.services.Product
import life.qbic.portal.offermanager.components.GridUtils
import life.qbic.portal.offermanager.components.product.copy.CopyProductView
import life.qbic.portal.offermanager.components.product.create.CreateProductView

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
class MaintainProductsView extends FormLayout {

    private final MaintainProductsViewModel viewModel
    private final MaintainProductsController controller

    private Grid<Product> productGrid
    private HorizontalLayout buttonLayout
    private Button addProduct
    private Button copyProduct
    private Button archiveProduct
    private Panel productDescription
    private VerticalLayout maintenanceLayout

    CreateProductView createProductView
    CopyProductView copyProductView

    MaintainProductsView(MaintainProductsViewModel viewModel, CreateProductView createProductView,
                         CopyProductView copyProductView,
                         MaintainProductsController controller){
        this.controller = controller
        this.viewModel = viewModel
        this.createProductView = createProductView
        this.copyProductView = copyProductView
        setupTitle()
        setupPanel()
        createButtons()
        setupGrid()
        setupOverviewLayout()
        addSubViews()
        setupListeners()
    }

    private void setupTitle(){
        Label label = new Label("Service Product Maintenance")
        label.setStyleName(ValoTheme.LABEL_HUGE)
        this.setMargin(false)
        this.addComponent(label, 0)
    }

    private void createButtons(){
        addProduct = new Button("Add Product", VaadinIcons.PLUS)
        copyProduct = new Button ("Copy Product", VaadinIcons.COPY)
        archiveProduct = new Button("Archive Product", VaadinIcons.ARCHIVE)
        copyProduct.setEnabled(false)
        archiveProduct.setEnabled(false)

        buttonLayout = new HorizontalLayout(productDescription, addProduct,copyProduct,archiveProduct)
        buttonLayout.setMargin(false)
    }

    private void setupGrid(){
        productGrid = new Grid<>()

        productGrid.addColumn({ product -> product.productId.toString() })
                .setCaption("Product Id").setId("ProductId")
        productGrid.addColumn({ product -> product.productName })
                .setCaption("Name").setId("ProductName")
        // Format price by using a column renderer. This way the sorting will happen on the underlying double values, leading to expected behaviour.
        Column<Product, Double> priceColumn = productGrid.addColumn({product -> product.unitPrice}).setCaption("Price").setId("UnitPrice")
        priceColumn.setRenderer(price -> Currency.getFormatterWithSymbol().format(price), new TextRenderer())
        productGrid.addColumn({ product -> product.unit.value})
                .setCaption("Unit").setId("ProductUnit")

        productGrid.setWidthFull()

        ListDataProvider<Product> productsDataProvider = setupDataProvider()
        setupFilters(productsDataProvider)
    }

    private ListDataProvider setupDataProvider(){
        ListDataProvider<Product> dataProvider = new ListDataProvider(viewModel.products)
        productGrid.setDataProvider(dataProvider)
        return dataProvider
    }

    private void setupFilters(ListDataProvider<Product> dataProvider){
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
        maintenanceLayout = new VerticalLayout(productGrid, buttonLayout)
        maintenanceLayout.setSizeFull()
        maintenanceLayout.setMargin(false)
        maintenanceLayout.addComponents()

        maintenanceLayout.setComponentAlignment(buttonLayout,Alignment.TOP_RIGHT)
        this.addComponents(maintenanceLayout)
    }

    private void addSubViews(){
        this.addComponents(createProductView)
        this.addComponent(copyProductView)
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
                viewModel.selectedProduct = it.firstSelectedItem
                checkProductSelected()
            }
        })

        addProduct.addClickListener({
            maintenanceLayout.setVisible(false)
            createProductView.setVisible(true)
        })

        createProductView.abortButton.addClickListener({
            maintenanceLayout.setVisible(true)
            createProductView.setVisible(false)
        })

        copyProduct.addClickListener({
            viewModel.productUpdate.emit(viewModel.selectedProduct.get())
            maintenanceLayout.setVisible(false)
            copyProductView.setVisible(true)
        })

        copyProductView.abortButton.addClickListener({
            maintenanceLayout.setVisible(true)
            copyProductView.setVisible(false)
        })

        copyProductView.createProductButton.addClickListener({
            maintenanceLayout.setVisible(true)
            copyProductView.setVisible(false)
        })

        archiveProduct.addClickListener({
            controller.archiveProduct(viewModel.selectedProduct.get().productId)
        })

        viewModel.products.addPropertyChangeListener({
            productGrid.dataProvider.refreshAll()
        })

        viewModel.addPropertyChangeListener("productCreatedSuccessfully", {
            if(it.newValue as Boolean){
                createProductView.setVisible(false)
                maintenanceLayout.setVisible(true)
                viewModel.reset()
            }
        })
    }

    private void checkProductSelected() {
        if (viewModel.selectedProduct.get()) {
            copyProduct.setEnabled(true)
            archiveProduct.setEnabled(true)
        } else {
            copyProduct.setEnabled(false)
            archiveProduct.setEnabled(false)
        }
    }

}
