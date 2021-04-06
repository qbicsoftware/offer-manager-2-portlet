package life.qbic.portal.offermanager.components.offer.create

import com.vaadin.data.ValidationResult
import com.vaadin.data.Validator
import com.vaadin.data.ValueContext
import com.vaadin.data.converter.StringToDoubleConverter
import com.vaadin.data.provider.ListDataProvider
import com.vaadin.icons.VaadinIcons
import com.vaadin.server.UserError
import com.vaadin.shared.ui.grid.HeightMode
import com.vaadin.ui.Alignment
import com.vaadin.ui.Button
import com.vaadin.ui.Grid
import com.vaadin.ui.HorizontalLayout
import com.vaadin.ui.TabSheet
import com.vaadin.ui.TextField
import com.vaadin.ui.VerticalLayout
import com.vaadin.ui.components.grid.HeaderRow
import com.vaadin.ui.renderers.NumberRenderer
import com.vaadin.ui.themes.ValoTheme
import life.qbic.datamodel.dtos.business.services.Product
import life.qbic.business.offers.Currency
import life.qbic.portal.offermanager.components.GridUtils
import life.qbic.portal.offermanager.components.AppViewModel

/**
 * This class generates a Layout in which the user
 * can select the the different packages requested by the customer.
 *
 * SelectItemsView will be integrated into the CreateOfferView and provides an User Interface
 * with the intention of enabling a user the selecting of packages which the customer ordered. This will be the bases for
 * the calculated offer price.
 *
 * @since: 0.1.0
 *
 */
class SelectItemsView extends VerticalLayout{

    private final CreateOfferViewModel createOfferViewModel
    private final AppViewModel viewModel

    private List<ProductItemViewModel> sequencingProduct
    private List<ProductItemViewModel> projectManagementProduct
    private List<ProductItemViewModel> storageProduct
    private List<ProductItemViewModel> primaryAnalyseProduct
    private List<ProductItemViewModel> secondaryAnalyseProduct
    private List<ProductItemViewModel> proteomicAnalysisProduct
    private List<ProductItemViewModel> metabolomicAnalysisProduct

    Grid<ProductItemViewModel> sequencingGrid
    Grid<ProductItemViewModel> projectManagementGrid
    Grid<ProductItemViewModel> storageGrid
    Grid<ProductItemViewModel> primaryAnalyseGrid
    Grid<ProductItemViewModel> secondaryAnalyseGrid
    Grid<ProductItemViewModel> proteomicsAnalysisGrid
    Grid<ProductItemViewModel> metabolomicsAnalysisGrid
    Grid<ProductItemViewModel> overviewGrid

    Button applySequencing
    Button applyProjectManagement
    Button applyPrimaryAnalysis
    Button applySecondaryAnalysis
    Button applyProteomicAnalysis
    Button applyMetabolomicAnalysis
    Button applyDataStorage
    Button next
    Button previous

    TextField amountSequencing
    TextField amountProjectManagement
    TextField amountPrimaryAnalysis
    TextField amountSecondaryAnalysis
    TextField amountProteomicAnalysis
    TextField amountMetabolomicAnalysis
    TextField amountDataStorage


    SelectItemsView(CreateOfferViewModel createOfferViewModel, AppViewModel viewModel){
        this.createOfferViewModel = createOfferViewModel
        this.viewModel = viewModel

        sequencingProduct = createOfferViewModel.sequencingProducts as ObservableList
        sequencingProduct.addPropertyChangeListener({
            if (it instanceof ObservableList.ElementEvent) {
              sequencingGrid.dataProvider.refreshAll()
            }
        })

        projectManagementProduct = createOfferViewModel.managementProducts as ObservableList
        projectManagementProduct.addPropertyChangeListener({
            if (it instanceof ObservableList.ElementEvent) {
              projectManagementGrid.dataProvider.refreshAll()
            }
        })

        storageProduct = createOfferViewModel.storageProducts as ObservableList
        storageProduct.addPropertyChangeListener({
            if (it instanceof ObservableList.ElementEvent) {
                storageGrid.dataProvider.refreshAll()
            }
        })

        primaryAnalyseProduct = createOfferViewModel.primaryAnalysisProducts as ObservableList
        primaryAnalyseProduct.addPropertyChangeListener({
            if (it instanceof ObservableList.ElementEvent) {
              primaryAnalyseGrid.dataProvider.refreshAll()
            }
        })

        secondaryAnalyseProduct = createOfferViewModel.secondaryAnalysisProducts as ObservableList
        secondaryAnalyseProduct.addPropertyChangeListener({
            if (it instanceof ObservableList.ElementEvent) {
                secondaryAnalyseGrid.dataProvider.refreshAll()
            }
        })

        proteomicAnalysisProduct = createOfferViewModel.proteomicAnalysisProducts as ObservableList
        proteomicAnalysisProduct.addPropertyChangeListener({
            if (it instanceof ObservableList.ElementEvent) {
                proteomicsAnalysisGrid.dataProvider.refreshAll()
            }
        })

        metabolomicAnalysisProduct = createOfferViewModel.metabolomicAnalysisProduct as ObservableList
        metabolomicAnalysisProduct.addPropertyChangeListener({
            if (it instanceof ObservableList.ElementEvent) {
                metabolomicsAnalysisGrid.dataProvider.refreshAll()
            }
        })

        initLayout()
        setupDataProvider()
        addValueChangeListeners()
        addSelectionListeners()
        addClickListeners()
        addPropertyChangeListener()
    }


    /**
     * Initializes the start layout for this view
     */
    private void initLayout(){
        this.sequencingGrid = new Grid<>()
        this.primaryAnalyseGrid = new Grid<>()
        this.secondaryAnalyseGrid = new Grid<>()
        this.proteomicsAnalysisGrid = new Grid<>()
        this.metabolomicsAnalysisGrid = new Grid<>()
        this.projectManagementGrid = new Grid<>()
        this.storageGrid = new Grid<>()
        this.overviewGrid = new Grid<>("Overview:")

        amountSequencing = new TextField("Quantity:")
        amountSequencing.setPlaceholder("e.g. 1")
        amountPrimaryAnalysis = new TextField("Quantity:")
        amountPrimaryAnalysis.setPlaceholder("e.g. 1")
        amountSecondaryAnalysis = new TextField("Quantity:")
        amountSecondaryAnalysis.setPlaceholder("e.g. 1")
        amountProteomicAnalysis = new TextField("Quantity:")
        amountProteomicAnalysis.setPlaceholder("e.g. 1")
        amountMetabolomicAnalysis = new TextField("Quantity:")
        amountMetabolomicAnalysis.setPlaceholder("e.g. 1")
        amountProjectManagement = new TextField("Quantity:")
        amountProjectManagement.setPlaceholder("e.g. 1.5")
        amountDataStorage = new TextField("Quantity:")
        amountDataStorage.setPlaceholder("e.g. 1.6")

        this.next = new Button(VaadinIcons.CHEVRON_CIRCLE_RIGHT)
        next.setEnabled(false)
        next.addStyleName(ValoTheme.LABEL_LARGE)

        this.previous = new Button(VaadinIcons.CHEVRON_CIRCLE_LEFT)
        previous.addStyleName(ValoTheme.LABEL_LARGE)

        this.applySequencing = new Button("Apply", VaadinIcons.PLUS)
        applySequencing.setEnabled(false)

        this.applyPrimaryAnalysis = new Button("Apply",VaadinIcons.PLUS)
        applyPrimaryAnalysis.setEnabled(false)

        this.applySecondaryAnalysis = new Button("Apply", VaadinIcons.PLUS)
        applySecondaryAnalysis.setEnabled(false)

        this.applyProteomicAnalysis = new Button("Apply", VaadinIcons.PLUS)
        applyProteomicAnalysis.setEnabled(false)

        this.applyMetabolomicAnalysis= new Button("Apply", VaadinIcons.PLUS)
        applyMetabolomicAnalysis.setEnabled(false)

        this.applyDataStorage = new Button("Apply", VaadinIcons.PLUS)
        applyDataStorage.setEnabled(false)

        this.applyProjectManagement = new Button("Apply", VaadinIcons.PLUS)
        applyProjectManagement.setEnabled(false)

        HorizontalLayout buttonLayout = new HorizontalLayout(previous,next)
        buttonLayout.setComponentAlignment(next, Alignment.BOTTOM_RIGHT)
        buttonLayout.setComponentAlignment(previous, Alignment.BOTTOM_LEFT)
        buttonLayout.setSizeFull()

        HorizontalLayout quantitySeq = new HorizontalLayout(amountSequencing, applySequencing)
        quantitySeq.setComponentAlignment(applySequencing, Alignment.BOTTOM_LEFT)
        VerticalLayout seqLayout = new VerticalLayout(sequencingGrid,quantitySeq)
        seqLayout.setSizeFull()

        HorizontalLayout quantityPrimary = new HorizontalLayout(amountPrimaryAnalysis,applyPrimaryAnalysis)
        quantityPrimary.setComponentAlignment(applyPrimaryAnalysis, Alignment.BOTTOM_RIGHT)
        VerticalLayout primaryAnalysisLayout = new VerticalLayout(primaryAnalyseGrid, quantityPrimary)
        primaryAnalysisLayout.setSizeFull()

        HorizontalLayout quantitySecondary = new HorizontalLayout(amountSecondaryAnalysis,applySecondaryAnalysis)
        quantitySecondary.setComponentAlignment(applySecondaryAnalysis, Alignment.BOTTOM_RIGHT)
        VerticalLayout secondaryAnalysisLayout = new VerticalLayout(secondaryAnalyseGrid, quantitySecondary)
        secondaryAnalysisLayout.setSizeFull()

        HorizontalLayout quantityProteomic = new HorizontalLayout(amountProteomicAnalysis,applyProteomicAnalysis)
        quantityProteomic.setComponentAlignment(applyProteomicAnalysis, Alignment.BOTTOM_RIGHT)
        VerticalLayout proteomicsLayout = new VerticalLayout(proteomicsAnalysisGrid, quantityProteomic)
        proteomicsLayout.setSizeFull()

        HorizontalLayout quantityMetabolomic = new HorizontalLayout(amountMetabolomicAnalysis ,applyMetabolomicAnalysis)
        quantityMetabolomic.setComponentAlignment(applyMetabolomicAnalysis, Alignment.BOTTOM_RIGHT)
        VerticalLayout metabolomicsLayout = new VerticalLayout(metabolomicsAnalysisGrid, quantityMetabolomic)
        metabolomicsLayout.setSizeFull()

        HorizontalLayout quantityStorage = new HorizontalLayout(amountDataStorage,applyDataStorage)
        quantityStorage.setComponentAlignment(applyDataStorage, Alignment.BOTTOM_RIGHT)
        VerticalLayout dataStorageLayout = new VerticalLayout(storageGrid, quantityStorage)
        dataStorageLayout.setSizeFull()

        HorizontalLayout quantityManagement = new HorizontalLayout(amountProjectManagement,applyProjectManagement)
        quantityManagement.setComponentAlignment(applyProjectManagement, Alignment.BOTTOM_RIGHT)
        VerticalLayout projectManagementLayout = new VerticalLayout(projectManagementGrid, quantityManagement)
        projectManagementLayout.setSizeFull()

        HorizontalLayout overview = new HorizontalLayout(overviewGrid)
        overview.setSizeFull()


        generateProductGrid(sequencingGrid)
        generateProductGrid(primaryAnalyseGrid)
        generateProductGrid(secondaryAnalyseGrid)
        generateProductGrid(proteomicsAnalysisGrid)
        generateProductGrid(metabolomicsAnalysisGrid)
        generateProductGrid(storageGrid)
        generateProductGrid(projectManagementGrid)
        // This grid summarises product items selected for this specific offer, so we set quantity = true
        generateProductGrid(overviewGrid, true)

        //make the overview over selected items grow dynamically
        overviewGrid.setHeightMode(HeightMode.UNDEFINED)


        TabSheet packageAccordion = new TabSheet()
        packageAccordion.addTab(seqLayout,"Sequencing")
        packageAccordion.addTab(primaryAnalysisLayout,"Primary Bioinformatics")
        packageAccordion.addTab(secondaryAnalysisLayout,"Secondary Bioinformatics")
        packageAccordion.addTab(proteomicsLayout,"Proteomics")
        packageAccordion.addTab(metabolomicsLayout,"Metabolomics")
        packageAccordion.addTab(projectManagementLayout,"Project Management")
        packageAccordion.addTab(dataStorageLayout,"Data Storage")

        this.addComponents(packageAccordion, overview, buttonLayout)
        this.setSizeFull()
        this.setMargin(false)
    }

    /**
     * This method adds the retrieved Customer Information to the Customer grid
     */
    private void setupDataProvider() {
        ListDataProvider<ProductItemViewModel> sequencingProductDataProvider = new ListDataProvider(createOfferViewModel.sequencingProducts)
        this.sequencingGrid.setDataProvider(sequencingProductDataProvider)
        setupFilters(sequencingProductDataProvider, sequencingGrid)


        ListDataProvider<ProductItemViewModel> managementProductDataProvider = new ListDataProvider(createOfferViewModel.managementProducts)
        this.projectManagementGrid.setDataProvider(managementProductDataProvider)
        setupFilters(managementProductDataProvider, projectManagementGrid)


        ListDataProvider<ProductItemViewModel> primaryAnalysisProductDataProvider = new ListDataProvider(createOfferViewModel.primaryAnalysisProducts)
        this.primaryAnalyseGrid.setDataProvider(primaryAnalysisProductDataProvider)
        setupFilters(primaryAnalysisProductDataProvider, primaryAnalyseGrid)

        ListDataProvider<ProductItemViewModel> secondaryAnalysisProductDataProvider = new ListDataProvider(createOfferViewModel.secondaryAnalysisProducts)
        this.secondaryAnalyseGrid.setDataProvider(secondaryAnalysisProductDataProvider)
        setupFilters(secondaryAnalysisProductDataProvider, secondaryAnalyseGrid)

        ListDataProvider<ProductItemViewModel> proteomicAnalysisProductDataProvider = new ListDataProvider(createOfferViewModel.proteomicAnalysisProducts)
        this.proteomicsAnalysisGrid.setDataProvider(proteomicAnalysisProductDataProvider)
        setupFilters(proteomicAnalysisProductDataProvider, proteomicsAnalysisGrid)

        ListDataProvider<ProductItemViewModel> metabolomicAnalysisProductDataProvider = new ListDataProvider(createOfferViewModel.metabolomicAnalysisProduct)
        this.metabolomicsAnalysisGrid.setDataProvider(metabolomicAnalysisProductDataProvider)
        setupFilters(metabolomicAnalysisProductDataProvider, metabolomicsAnalysisGrid)

        ListDataProvider<ProductItemViewModel> storageProductDataProvider = new ListDataProvider(createOfferViewModel.storageProducts)
        this.storageGrid.setDataProvider(storageProductDataProvider)
        setupFilters(storageProductDataProvider, storageGrid)

        ListDataProvider<ProductItemViewModel> selectedItemsDataProvider =
                new ListDataProvider(createOfferViewModel.getProductItems())
        this.overviewGrid.setDataProvider(selectedItemsDataProvider)
        setupFilters(selectedItemsDataProvider, overviewGrid)
    }

    private static void setupFilters(ListDataProvider<Product> productListDataProvider,
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
     * Method which generates the grid and populates the columns with the set product information from the setupDataProvider Method
     *
     * This Method is responsible for setting up the grid and setting the product information to the individual grid columns.
     */
    private static void generateProductGrid(Grid<ProductItemViewModel> grid, boolean showQuantity = false) {
        try {
            if(showQuantity){
            grid.addColumn({ productItem -> productItem.quantity })
                    .setCaption("Quantity").setId("Quantity")
            }
            grid.addColumn({ productItem -> productItem.product.productId})
                    .setCaption("Product Id").setId("ProductId")
            grid.addColumn({ productItem -> productItem.product.productName })
                    .setCaption("Product Name").setId("ProductName")
            grid.addColumn({ productItem -> productItem.product.description })
                    .setCaption("Product Description").setId("ProductDescription")
            grid.addColumn({ productItem -> productItem.product.unitPrice }, new NumberRenderer(Currency.getFormatterWithSymbol()))
                    .setCaption("Product Unit Price").setId("ProductUnitPrice")
            grid.addColumn({ productItem -> productItem.product.unit.value })
                    .setCaption("Product Unit").setId("ProductUnit")

            //specify size of grid and layout
            grid.setWidthFull()
            grid.setHeightMode(HeightMode.ROW)
        } catch (Exception e) {
            new Exception("Unexpected exception in building the product item grid", e)
        }
    }

    /**
     * Adds listener to handle the logic after the user selected a product
     */
    private void addSelectionListeners() {
        sequencingGrid.addSelectionListener({
            createOfferViewModel.sequencingGridSelected = true})

        primaryAnalyseGrid.addSelectionListener({
            createOfferViewModel.primaryAnalyseGridSelected = true})

        secondaryAnalyseGrid.addSelectionListener({
            createOfferViewModel.secondaryAnalyseGridSelected = true})

        proteomicsAnalysisGrid.addSelectionListener({
            createOfferViewModel.proteomicsAnalysisGridSelected = true})

        metabolomicsAnalysisGrid.addSelectionListener({
            createOfferViewModel.metabolomicsAnalysisGridSelected = true})

        projectManagementGrid.addSelectionListener({
            createOfferViewModel.projectManagementGridSelected = true})

        storageGrid.addSelectionListener({
            createOfferViewModel.storageGridSelected = true})
    }
    /**
     * Adds listener to handle the logic after the user selected a product
     */
    private void addClickListeners() {

        addProductButtonListener(applySequencing, sequencingGrid, amountSequencing)
        addProductButtonListener(applyPrimaryAnalysis, primaryAnalyseGrid, amountPrimaryAnalysis)
        addProductButtonListener(applySecondaryAnalysis, secondaryAnalyseGrid, amountSecondaryAnalysis)
        addProductButtonListener(applyProteomicAnalysis, proteomicsAnalysisGrid, amountProteomicAnalysis)
        addProductButtonListener(applyMetabolomicAnalysis, metabolomicsAnalysisGrid, amountMetabolomicAnalysis)
        addProductButtonListener(applyProjectManagement, projectManagementGrid, amountProjectManagement)
        addProductButtonListener(applyDataStorage, storageGrid, amountDataStorage)

    }

    private void addValueChangeListeners() {
        addProductFieldListener(amountSequencing, createOfferViewModel.sequencingQuantity)
        addProductFieldListener(amountSequencing, createOfferViewModel.primaryAnalysisQuantity)
        addProductFieldListener(amountSequencing, createOfferViewModel.secondaryAnalysisQuantity)
        addProductFieldListener(amountSequencing, createOfferViewModel.proteomicsAnalysisQuantity)
        addProductFieldListener(amountSequencing, createOfferViewModel.metabolomicsAnalysisQuantity)
        addProductFieldListener(amountSequencing, createOfferViewModel.projectManagementQuantity)
        addProductFieldListener(amountSequencing, createOfferViewModel.storageQuantity)
    }

    private void addPropertyChangeListener() {
        createOfferViewModel.productItems.addPropertyChangeListener({
            if (createOfferViewModel.productItems) {
                next.setEnabled(true)
            } else {
                next.setEnabled(false)
            }
        })
    }

    private void addProductButtonListener(Button productButton, Grid<ProductItemViewModel> productGrid, TextField productTextField){

        productButton.addClickListener({
            if(productGrid.getSelectedItems() != null){
                String amount = productTextField.getValue()
                try{
                    if(amount != null && amount.isNumber()){
                        productGrid.getSelectedItems().each {
                            if(Double.parseDouble(amount) >= 0.0){
                                it.setQuantity(Double.parseDouble(amount))
                                updateOverviewGrid(it)
                            }
                        }
                        productGrid.getDataProvider().refreshAll()
                    }
                }
                catch(Exception e){
                    viewModel.failureNotifications.add("The quantity must be a number bigger than 0")
                }
            }
            productTextField.clear()
            productGrid.deselectAll()
            productButton.setEnabled(false)
        })
    }

    private void addProductFieldListener(TextField productField, double productQuantity){

        Validator<String> nonEmptyStringValidator =  Validator.from({String value -> (value && !value.trim().empty)}, "Empty input not supported.")
        Validator<Object> stringIsNumberValidator = Validator.from({String value -> new StringToDoubleConverter(value)}, "Input must be a number")

        productField.addValueChangeListener({event ->
            ValidationResult emptyResult = nonEmptyStringValidator.apply(event.getValue(), new ValueContext(productField))
            ValidationResult noNumberResult = stringIsNumberValidator.apply(event.getValue(), new ValueContext(productField))
            if (emptyResult.isError()) {
                UserError error = new UserError(emptyResult.getErrorMessage())
                productField.setComponentError(error)
            }
            //ToDo find out why this error is thrown
            else if (noNumberResult.isError()){
                UserError error = new UserError(noNumberResult.getErrorMessage())
                productField.setComponentError(error)
            }
            else {
                productQuantity = Double.parseDouble(event.getValue())
            }
        })
    }

    /**
     * This method should be called whenever the quantity of a ProductItemViewModel changes. It updates the items in overview grid respectively
     */
    void updateOverviewGrid(ProductItemViewModel item){
        if(!createOfferViewModel.productItems.contains(item)){
            createOfferViewModel.productItems.add(item)
        }
        if(item.quantity == 0.0 as Double){
            createOfferViewModel.productItems.remove(item)
        }
        overviewGrid.getDataProvider().refreshAll()

        if(createOfferViewModel.productItems.size() > 0){
            next.setEnabled(true)
        }else{
            next.setEnabled(false)
        }
    }

}
