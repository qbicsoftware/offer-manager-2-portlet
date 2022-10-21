package life.qbic.portal.offermanager.components.offer.create

import com.vaadin.data.ValidationResult
import com.vaadin.data.Validator
import com.vaadin.data.ValueContext
import com.vaadin.data.provider.ListDataProvider
import com.vaadin.data.validator.RegexpValidator
import com.vaadin.icons.VaadinIcons
import com.vaadin.server.UserError
import com.vaadin.shared.data.sort.SortDirection
import com.vaadin.shared.ui.grid.HeightMode
import com.vaadin.ui.*
import com.vaadin.ui.components.grid.GridRowDragger
import com.vaadin.ui.components.grid.HeaderRow
import com.vaadin.ui.renderers.NumberRenderer
import com.vaadin.ui.themes.ValoTheme
import life.qbic.business.offers.Currency
import life.qbic.datamodel.dtos.business.services.Product
import life.qbic.portal.offermanager.components.AppViewModel
import life.qbic.portal.offermanager.components.GridUtils
import life.qbic.portal.offermanager.components.Resettable
import life.qbic.portal.offermanager.components.product.ProductIdContainsString

/**
 * This class generates a Layout in which the user
 * can select the the different packages requested by the customer.
 *
 * SelectItemsView will be integrated into the CreateOfferView and provides an User Interface
 * with the intention of enabling a user the selecting of packages which the customer ordered. This will be the bases for
 * the calculated offer price.
 *
 * @since 0.1.0
 */
class SelectItemsView extends VerticalLayout implements Resettable {

    private final CreateOfferViewModel createOfferViewModel
    private final AppViewModel viewModel

    private List<ProductItemViewModel> sequencingProduct
    private List<ProductItemViewModel> projectManagementProduct
    private List<ProductItemViewModel> storageProduct
    private List<ProductItemViewModel> primaryAnalyseProduct
    private List<ProductItemViewModel> secondaryAnalyseProduct
    private List<ProductItemViewModel> proteomicAnalysisProduct
    private List<ProductItemViewModel> metabolomicAnalysisProduct
    private List<ProductItemViewModel> externalServiceProduct

    Grid<Product> sequencingGrid
    Grid<Product> projectManagementGrid
    Grid<Product> storageGrid
    Grid<Product> primaryAnalyseGrid
    Grid<Product> secondaryAnalyseGrid
    Grid<Product> proteomicsAnalysisGrid
    Grid<Product> metabolomicsAnalysisGrid
    Grid<Product> externalServiceGrid

    Grid<ProductItemViewModel> overviewGrid

    Button applySequencing
    Button applyProjectManagement
    Button applyPrimaryAnalysis
    Button applySecondaryAnalysis
    Button applyProteomicAnalysis
    Button applyMetabolomicAnalysis
    Button applyDataStorage
    Button applyExternalService
    Button removeItemsButton
    Button next
    Button previous

    TextField amountSequencing
    TextField amountProjectManagement
    TextField amountPrimaryAnalysis
    TextField amountSecondaryAnalysis
    TextField amountProteomicAnalysis
    TextField amountMetabolomicAnalysis
    TextField amountDataStorage
    TextField amountExternalService

    TabSheet packageAccordion

    static void enableDraggable(Grid<ProductItemViewModel> grid) {
        new GridRowDragger<>(grid)
    }

    /**
     * Contains regex for filtering the different product types
     *
     * This enum stores the regex for validating a user input and differences between an atomic product
     * which only allows integer input and a partial product which allows double input.
     *
     */
    enum ProductTypeRegex {
        ATOMIC("^[0-9]+\$"),
        PARTIAL("[-]?[0-9]*\\.?[0-9]+"),

        private String regex

        ProductTypeRegex(String regex) {
            this.regex = regex
        }

        String getRegex() {
            return this.regex
        }
    }

    SelectItemsView(CreateOfferViewModel createOfferViewModel, AppViewModel viewModel) {
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

        externalServiceProduct = createOfferViewModel.externalServiceProduct as ObservableList
        externalServiceProduct.addPropertyChangeListener({
            if (it instanceof ObservableList.ElementEvent) {
                externalServiceGrid.dataProvider.refreshAll()
            }
        })

        initLayout()
        setupDataProvider()
        addListener()
    }

    @Override
    void reset() {
        resetSelectedItems()
    }

    private void resetSelectedItems() {
        overviewGrid.deselectAll()
        packageAccordion.setSelectedTab(0)
    }


    /**
     * Initializes the start layout for this view
     */
    private void initLayout() {
        this.sequencingGrid = new Grid<>()
        this.primaryAnalyseGrid = new Grid<>()
        this.secondaryAnalyseGrid = new Grid<>()
        this.proteomicsAnalysisGrid = new Grid<>()
        this.metabolomicsAnalysisGrid = new Grid<>()
        this.projectManagementGrid = new Grid<>()
        this.externalServiceGrid = new Grid<>()
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
        amountExternalService = new TextField("Quantity:")
        amountExternalService.setPlaceholder("e.g. 1.6")

        this.next = new Button(VaadinIcons.CHEVRON_CIRCLE_RIGHT)
        next.setEnabled(false)
        next.addStyleName(ValoTheme.LABEL_LARGE)

        this.previous = new Button(VaadinIcons.CHEVRON_CIRCLE_LEFT)
        previous.addStyleName(ValoTheme.LABEL_LARGE)

        this.applySequencing = new Button("Apply", VaadinIcons.PLUS)
        applySequencing.setEnabled(false)

        this.applyPrimaryAnalysis = new Button("Apply", VaadinIcons.PLUS)
        applyPrimaryAnalysis.setEnabled(false)

        this.applySecondaryAnalysis = new Button("Apply", VaadinIcons.PLUS)
        applySecondaryAnalysis.setEnabled(false)

        this.applyProteomicAnalysis = new Button("Apply", VaadinIcons.PLUS)
        applyProteomicAnalysis.setEnabled(false)

        this.applyMetabolomicAnalysis = new Button("Apply", VaadinIcons.PLUS)
        applyMetabolomicAnalysis.setEnabled(false)

        this.applyDataStorage = new Button("Apply", VaadinIcons.PLUS)
        applyDataStorage.setEnabled(false)

        this.applyProjectManagement = new Button("Apply", VaadinIcons.PLUS)
        applyProjectManagement.setEnabled(false)

        this.applyExternalService = new Button("Apply", VaadinIcons.PLUS)
        applyExternalService.setEnabled(false)

        this.removeItemsButton = new Button("Remove Item", VaadinIcons.MINUS)
        removeItemsButton.setEnabled(false)
        removeItemsButton.setStyleName(ValoTheme.BUTTON_DANGER)

        HorizontalLayout buttonLayout = new HorizontalLayout(previous, removeItemsButton, next)
        buttonLayout.setComponentAlignment(removeItemsButton, Alignment.BOTTOM_CENTER)
        buttonLayout.setComponentAlignment(next, Alignment.BOTTOM_RIGHT)
        buttonLayout.setComponentAlignment(previous, Alignment.BOTTOM_LEFT)
        buttonLayout.setSizeFull()

        HorizontalLayout quantitySeq = new HorizontalLayout(amountSequencing, applySequencing)
        quantitySeq.setComponentAlignment(applySequencing, Alignment.BOTTOM_LEFT)
        VerticalLayout seqLayout = new VerticalLayout(sequencingGrid, quantitySeq)
        sequencingGrid.setHeightByRows(6)
        seqLayout.setSizeFull()

        HorizontalLayout quantityPrimary = new HorizontalLayout(amountPrimaryAnalysis, applyPrimaryAnalysis)
        quantityPrimary.setComponentAlignment(applyPrimaryAnalysis, Alignment.BOTTOM_LEFT)
        VerticalLayout primaryAnalysisLayout = new VerticalLayout(primaryAnalyseGrid, quantityPrimary)
        primaryAnalyseGrid.setHeightByRows(6)
        primaryAnalysisLayout.setSizeFull()

        HorizontalLayout quantitySecondary = new HorizontalLayout(amountSecondaryAnalysis, applySecondaryAnalysis)
        quantitySecondary.setComponentAlignment(applySecondaryAnalysis, Alignment.BOTTOM_LEFT)
        VerticalLayout secondaryAnalysisLayout = new VerticalLayout(secondaryAnalyseGrid, quantitySecondary)
        secondaryAnalyseGrid.setHeightByRows(6)
        secondaryAnalysisLayout.setSizeFull()

        HorizontalLayout quantityProteomic = new HorizontalLayout(amountProteomicAnalysis, applyProteomicAnalysis)
        quantityProteomic.setComponentAlignment(applyProteomicAnalysis, Alignment.BOTTOM_LEFT)
        VerticalLayout proteomicsLayout = new VerticalLayout(proteomicsAnalysisGrid, quantityProteomic)
        proteomicsAnalysisGrid.setHeightByRows(6)
        proteomicsLayout.setSizeFull()

        HorizontalLayout quantityMetabolomic = new HorizontalLayout(amountMetabolomicAnalysis, applyMetabolomicAnalysis)
        quantityMetabolomic.setComponentAlignment(applyMetabolomicAnalysis, Alignment.BOTTOM_LEFT)
        VerticalLayout metabolomicsLayout = new VerticalLayout(metabolomicsAnalysisGrid, quantityMetabolomic)
        metabolomicsAnalysisGrid.setHeightByRows(6)
        metabolomicsLayout.setSizeFull()

        HorizontalLayout quantityStorage = new HorizontalLayout(amountDataStorage, applyDataStorage)
        quantityStorage.setComponentAlignment(applyDataStorage, Alignment.BOTTOM_LEFT)
        VerticalLayout dataStorageLayout = new VerticalLayout(storageGrid, quantityStorage)
        storageGrid.setHeightByRows(6)
        dataStorageLayout.setSizeFull()

        HorizontalLayout quantityManagement = new HorizontalLayout(amountProjectManagement, applyProjectManagement)
        quantityManagement.setComponentAlignment(applyProjectManagement, Alignment.BOTTOM_LEFT)
        VerticalLayout projectManagementLayout = new VerticalLayout(projectManagementGrid, quantityManagement)
        projectManagementGrid.setHeightByRows(6)
        projectManagementLayout.setSizeFull()

        HorizontalLayout externalService = new HorizontalLayout(amountExternalService, applyExternalService)
        externalService.setComponentAlignment(applyExternalService, Alignment.BOTTOM_LEFT)
        VerticalLayout externalServiceLayout = new VerticalLayout(externalServiceGrid, externalService)
        externalServiceGrid.setHeightByRows(6)
        externalServiceLayout.setSizeFull()


        HorizontalLayout overview = new HorizontalLayout(overviewGrid)
        overviewGrid.setHeightByRows(6)
        overview.setSizeFull()


        generateProductGrid(sequencingGrid)
        generateProductGrid(primaryAnalyseGrid)
        generateProductGrid(secondaryAnalyseGrid)
        generateProductGrid(proteomicsAnalysisGrid)
        generateProductGrid(metabolomicsAnalysisGrid)
        generateProductGrid(storageGrid)
        generateProductGrid(projectManagementGrid)
        generateProductGrid(externalServiceGrid)
        // This grid summarises product items selected for this specific offer, so we set quantity = true
        generateItemGrid(overviewGrid)
        enableDraggable(overviewGrid)

        packageAccordion = new TabSheet()
        packageAccordion.addTab(seqLayout, "Sequencing")
        packageAccordion.addTab(primaryAnalysisLayout, "Primary Bioinformatics")
        packageAccordion.addTab(secondaryAnalysisLayout, "Secondary Bioinformatics")
        packageAccordion.addTab(proteomicsLayout, "Proteomics")
        packageAccordion.addTab(metabolomicsLayout, "Metabolomics")
        packageAccordion.addTab(projectManagementLayout, "Project Management")
        packageAccordion.addTab(dataStorageLayout, "Data Storage")
        packageAccordion.addTab(externalServiceLayout, "External Services")

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

        ListDataProvider<ProductItemViewModel> externalServiceProvider = new ListDataProvider(createOfferViewModel.externalServiceProduct)
        this.externalServiceGrid.setDataProvider(externalServiceProvider)
        setupFilters(externalServiceProvider, externalServiceGrid)

        ListDataProvider<ProductItemViewModel> selectedItemsDataProvider =
                new ListDataProvider(createOfferViewModel.getProductItems())
        this.overviewGrid.setDataProvider(selectedItemsDataProvider)
        setupFilters(selectedItemsDataProvider, overviewGrid)
    }

    private static void setupFilters(ListDataProvider<Product> productListDataProvider,
                                     Grid targetGrid) {
        HeaderRow productFilterRow = targetGrid.appendHeaderRow()
        GridUtils.setupColumnFilter(productListDataProvider,
                targetGrid.getColumn("ProductId"), new ProductIdContainsString(),
                productFilterRow)
        GridUtils.setupColumnFilter(productListDataProvider,
                targetGrid.getColumn("ProductName"),
                productFilterRow)
        GridUtils.setupColumnFilter(productListDataProvider,
                targetGrid.getColumn("ProductDescription"),
                productFilterRow)
    }

    /**
     * Method which generates the grid and populates the columns with the set product information from the setupDataProvider Method
     *
     * This Method is responsible for setting up the grid and setting the product information to the individual grid columns.
     */
    private static void generateProductGrid(Grid<Product> grid) {
        try {
            grid.addColumn({ it.productId })
                    .setCaption("Product Id").setId("ProductId")
            grid.addColumn({ it.productName })
                    .setCaption("Product Name").setId("ProductName")
            Grid.Column<Product,String> descriptionColumn = grid.addColumn({ it.description })
                    .setCaption("Product Description").setId("ProductDescription").setDescriptionGenerator({it.description})
            grid.addColumn({ it.internalUnitPrice }, new NumberRenderer(Currency.getFormatterWithSymbol()))
                    .setCaption("Internal Unit Price").setId("InternalUnitPrice")
            grid.addColumn({ it.externalUnitPrice }, new NumberRenderer(Currency.getFormatterWithSymbol()))
                    .setCaption("External Unit Price").setId("ExternalUnitPrice")
            grid.addColumn({ it.serviceProvider.fullName})
                    .setCaption("Facility").setId("Facility")
            grid.addColumn({ it.unit.value })
                    .setCaption("Product Unit").setId("ProductUnit")

            //specify size of grid and layout
            grid.setWidthFull()
            descriptionColumn.setWidth(GridUtils.DESCRIPTION_MAX_WIDTH)
            grid.setHeightMode(HeightMode.ROW)
            grid.sort("ProductId", SortDirection.ASCENDING)
        } catch (Exception e) {
            new Exception("Unexpected exception in building the product item grid", e)
        }
    }

    private static void generateItemGrid(Grid<ProductItemViewModel> grid) {
        try {
            grid.addColumn({ it.quantity })
                    .setCaption("Quantity").setId("Quantity")
            grid.addColumn({ it.product.productId })
                    .setCaption("Product Id").setId("ProductId")
            grid.addColumn({ it.product.productName })
                    .setCaption("Product Name").setId("ProductName")
            Grid.Column<ProductItemViewModel,String> descriptionColumn = grid.addColumn({ it.product.description })
                    .setCaption("Product Description").setId("ProductDescription").setDescriptionGenerator({it.product.description})
            grid.addColumn({ it.product.internalUnitPrice }, new NumberRenderer(Currency
                    .getFormatterWithSymbol()))
                    .setCaption("Internal Unit Price").setId("InternalUnitPrice")
            grid.addColumn({ it.product.externalUnitPrice }, new NumberRenderer(Currency
                    .getFormatterWithSymbol()))
                    .setCaption("External Unit Price").setId("ExternalUnitPrice")
            grid.addColumn({ it.product.serviceProvider.fullName})
                    .setCaption("Facility").setId("Facility")
            grid.addColumn({ it.product.unit.value })
                    .setCaption("Product Unit").setId("ProductUnit")

            //specify size of grid and layout
            grid.setWidthFull()
            descriptionColumn.setWidth(GridUtils.DESCRIPTION_MAX_WIDTH)
            grid.setHeightMode(HeightMode.ROW)
        } catch (Exception e) {
            new Exception("Unexpected exception in building the product item grid", e)
        }
    }

    /**
     * Adds listener to handle the logic after the user selected a product
     */
    private void addListener() {
        sequencingGrid.addSelectionListener({
            createOfferViewModel.sequencingGridSelected = it.firstSelectedItem.isPresent()
        })

        Validator<String> nonEmptyStringValidator = Validator.from({ String value -> (value && !value.trim().empty) }, "Please provide a number as input.")
        Validator<String> atomicValidator = new RegexpValidator("Please provide an integer Input", ProductTypeRegex.ATOMIC.regex)
        Validator<String> partialValidator = new RegexpValidator("Please provide a decimal Input", ProductTypeRegex.PARTIAL.regex)
        this.amountSequencing.addValueChangeListener({ event ->
            ValidationResult emptyResult = nonEmptyStringValidator.apply(event.getValue(), new ValueContext(amountSequencing))
            ValidationResult numberResult = atomicValidator.apply(event.getValue(), new ValueContext(amountSequencing))
            UserError error
            if (emptyResult.isError()) {
                error = new UserError(emptyResult.getErrorMessage())
                this.amountSequencing.setComponentError(error)
                createOfferViewModel.sequencingQuantityValid = false
            } else if (numberResult.isError()) {
                error = new UserError(numberResult.getErrorMessage())
                this.amountSequencing.setComponentError(error)
                createOfferViewModel.sequencingQuantityValid = false
            } else {
                this.amountSequencing.setComponentError(null)
                createOfferViewModel.sequencingQuantityValid = true
            }
        })

        applySequencing.addClickListener({
            if (sequencingGrid.getSelectedItems() != null) {
                String amount = amountSequencing.getValue()

                try {
                    if (amount != null && amount.isNumber()) {
                        sequencingGrid.getSelectedItems().each {
                            def amountParsed = Integer.parseInt(amount)
                            if (amountParsed >= 0) {
                                ProductItemViewModel offerItem = new ProductItemViewModel(amountParsed, it)
                                updateOverviewGrid(offerItem)
                            }
                        }
                        sequencingGrid.getDataProvider().refreshAll()
                    }
                } catch (NumberFormatException e) {
                    viewModel.failureNotifications.add("The quantity must be an integer value bigger than 0")
                } catch (Exception e) {
                    viewModel.failureNotifications.add("Ups, something went wrong. Please contact support@qbic.zendesk.com")
                }
            }
            amountSequencing.clear()
            sequencingGrid.deselectAll()
            applySequencing.setEnabled(false)
            amountSequencing.setComponentError(null)
        })

        primaryAnalyseGrid.addSelectionListener({
            createOfferViewModel.primaryAnalysisGridSelected = it.firstSelectedItem.isPresent()
        })

        this.amountPrimaryAnalysis.addValueChangeListener({ event ->
            ValidationResult emptyResult = nonEmptyStringValidator.apply(event.getValue(), new ValueContext(amountPrimaryAnalysis))
            ValidationResult numberResult = atomicValidator.apply(event.getValue(), new ValueContext(amountPrimaryAnalysis))
            UserError error
            if (emptyResult.isError()) {
                error = new UserError(emptyResult.getErrorMessage())
                this.amountPrimaryAnalysis.setComponentError(error)
                createOfferViewModel.primaryAnalysisQuantityValid = false
            } else if (numberResult.isError()) {
                error = new UserError(numberResult.getErrorMessage())
                this.amountPrimaryAnalysis.setComponentError(error)
                createOfferViewModel.primaryAnalysisQuantityValid = false
            } else {
                this.amountPrimaryAnalysis.setComponentError(null)
                createOfferViewModel.primaryAnalysisQuantityValid = true
            }
        })

        applyPrimaryAnalysis.addClickListener({
            if (primaryAnalyseGrid.getSelectedItems() != null) {
                String amount = amountPrimaryAnalysis.getValue()

                try {
                    if (amount != null && amount.isNumber()) {
                        primaryAnalyseGrid.getSelectedItems().each {
                            def amountParsed = Integer.parseInt(amount)
                            if (amountParsed >= 0) {
                                ProductItemViewModel offerItem = new ProductItemViewModel(amountParsed, it)
                                updateOverviewGrid(offerItem)
                            }
                        }
                        primaryAnalyseGrid.getDataProvider().refreshAll()

                    }
                } catch (NumberFormatException e) {
                    viewModel.failureNotifications.add("The quantity must be an integer number bigger than 0")
                } catch (Exception e) {
                    viewModel.failureNotifications.add("Ups, something went wrong. Please contact support@qbic.zendesk.com")
                }
            }
            amountPrimaryAnalysis.clear()
            primaryAnalyseGrid.deselectAll()
            applyPrimaryAnalysis.setEnabled(false)
            amountPrimaryAnalysis.setComponentError(null)
        })

        secondaryAnalyseGrid.addSelectionListener({
            createOfferViewModel.secondaryAnalysisGridSelected = it.firstSelectedItem.isPresent()
        })

        this.amountSecondaryAnalysis.addValueChangeListener({ event ->
            ValidationResult emptyResult = nonEmptyStringValidator.apply(event.getValue(), new ValueContext(amountSecondaryAnalysis))
            ValidationResult numberResult = atomicValidator.apply(event.getValue(), new ValueContext(amountSecondaryAnalysis))
            UserError error
            if (emptyResult.isError()) {
                error = new UserError(emptyResult.getErrorMessage())
                this.amountSecondaryAnalysis.setComponentError(error)
                createOfferViewModel.secondaryAnalysisQuantityValid = false
            } else if (numberResult.isError()) {
                error = new UserError(numberResult.getErrorMessage())
                this.amountSecondaryAnalysis.setComponentError(error)
                createOfferViewModel.secondaryAnalysisQuantityValid = false
            } else {
                this.amountPrimaryAnalysis.setComponentError(null)
                createOfferViewModel.secondaryAnalysisQuantityValid = true
            }
        })

        applySecondaryAnalysis.addClickListener({
            if (secondaryAnalyseGrid.getSelectedItems() != null) {
                String amount = amountSecondaryAnalysis.getValue()

                try {
                    if (amount != null && amount.isNumber()) {
                        secondaryAnalyseGrid.getSelectedItems().each {
                            def amountParsed = Integer.parseInt(amount)
                            if (amountParsed >= 0) {
                                ProductItemViewModel offerItem = new ProductItemViewModel(amountParsed, it)
                                updateOverviewGrid(offerItem)
                            }
                        }
                        secondaryAnalyseGrid.getDataProvider().refreshAll()
                    }
                }
                catch (NumberFormatException e) {
                    viewModel.failureNotifications.add("The quantity must be an integer number bigger than 0")
                } catch (Exception e) {
                    viewModel.failureNotifications.add("Ups, something went wrong. Please contact support@qbic.zendesk.com")
                }
            }
            amountSecondaryAnalysis.clear()
            secondaryAnalyseGrid.deselectAll()
            applySecondaryAnalysis.setEnabled(false)
            amountSecondaryAnalysis.setComponentError(null)
        })

        proteomicsAnalysisGrid.addSelectionListener({
            createOfferViewModel.proteomicsAnalysisGridSelected = it.firstSelectedItem.isPresent()
        })

        this.amountProteomicAnalysis.addValueChangeListener({ event ->
            ValidationResult emptyResult = nonEmptyStringValidator.apply(event.getValue(), new ValueContext(amountProteomicAnalysis))
            ValidationResult numberResult = atomicValidator.apply(event.getValue(), new ValueContext(amountProteomicAnalysis))
            UserError error
            if (emptyResult.isError()) {
                error = new UserError(emptyResult.getErrorMessage())
                this.amountProteomicAnalysis.setComponentError(error)
                createOfferViewModel.proteomicsAnalysisQuantityValid = false
            } else if (numberResult.isError()) {
                error = new UserError(numberResult.getErrorMessage())
                this.amountProteomicAnalysis.setComponentError(error)
                createOfferViewModel.proteomicsAnalysisQuantityValid = false
            } else {
                this.amountProteomicAnalysis.setComponentError(null)
                createOfferViewModel.proteomicsAnalysisQuantityValid = true
            }
        })

        applyProteomicAnalysis.addClickListener({
            if (proteomicsAnalysisGrid.getSelectedItems() != null) {
                String amount = amountProteomicAnalysis.getValue()
                try {
                    if (amount != null && amount.isNumber()) {
                        proteomicsAnalysisGrid.getSelectedItems().each {
                            def amountParsed = Integer.parseInt(amount)
                            if (amountParsed >= 0) {
                                ProductItemViewModel offerItem = new ProductItemViewModel(amountParsed, it)
                                updateOverviewGrid(offerItem)
                            }
                        }
                        proteomicsAnalysisGrid.getDataProvider().refreshAll()
                    }
                } catch (NumberFormatException e) {
                    viewModel.failureNotifications.add("The quantity must be an integer number bigger than 0")
                } catch (Exception e) {
                    viewModel.failureNotifications.add("Ups, something went wrong. Please contact support@qbic.zendesk.com")
                }
            }
            amountProteomicAnalysis.clear()
            proteomicsAnalysisGrid.deselectAll()
            applyProteomicAnalysis.setEnabled(false)
            amountProteomicAnalysis.setComponentError(null)
        })

        metabolomicsAnalysisGrid.addSelectionListener({
            createOfferViewModel.metabolomicsAnalysisGridSelected = it.firstSelectedItem.isPresent()
        })

        this.amountMetabolomicAnalysis.addValueChangeListener({ event ->
            ValidationResult emptyResult = nonEmptyStringValidator.apply(event.getValue(), new ValueContext(amountMetabolomicAnalysis))
            ValidationResult numberResult = atomicValidator.apply(event.getValue(), new ValueContext(amountMetabolomicAnalysis))
            UserError error
            if (emptyResult.isError()) {
                error = new UserError(emptyResult.getErrorMessage())
                this.amountMetabolomicAnalysis.setComponentError(error)
                createOfferViewModel.metabolomicsAnalysisQuantityValid = false
            } else if (numberResult.isError()) {
                error = new UserError(numberResult.getErrorMessage())
                this.amountMetabolomicAnalysis.setComponentError(error)
                createOfferViewModel.metabolomicsAnalysisQuantityValid = false
            } else {
                this.amountMetabolomicAnalysis.setComponentError(null)
                createOfferViewModel.metabolomicsAnalysisQuantityValid = true
            }
        })

        applyMetabolomicAnalysis.addClickListener({
            if (metabolomicsAnalysisGrid.getSelectedItems() != null) {
                String amount = amountMetabolomicAnalysis.getValue()
                try {
                    if (amount != null && amount.isNumber()) {
                        metabolomicsAnalysisGrid.getSelectedItems().each {
                            def amountParsed = Integer.parseInt(amount)
                            if (amountParsed >= 0) {
                                ProductItemViewModel offerItem = new ProductItemViewModel(amountParsed, it)
                                updateOverviewGrid(offerItem)
                            }
                        }
                        metabolomicsAnalysisGrid.getDataProvider().refreshAll()
                    }
                } catch (NumberFormatException e) {
                    viewModel.failureNotifications.add("The quantity must be an integer number bigger than 0")
                } catch (Exception e) {
                    viewModel.failureNotifications.add("Ups, something went wrong. Please contact support@qbic.zendesk.com")
                }
            }
            amountMetabolomicAnalysis.clear()
            metabolomicsAnalysisGrid.deselectAll()
            applyMetabolomicAnalysis.setEnabled(false)
            amountMetabolomicAnalysis.setComponentError(null)
        })

        projectManagementGrid.addSelectionListener({
            createOfferViewModel.projectManagementGridSelected = it.firstSelectedItem.isPresent()
        })

        this.amountProjectManagement.addValueChangeListener({ event ->
            ValidationResult emptyResult = nonEmptyStringValidator.apply(event.getValue(), new ValueContext(amountProjectManagement))
            ValidationResult numberResult = partialValidator.apply(event.getValue(), new ValueContext(amountProjectManagement))
            UserError error
            if (emptyResult.isError()) {
                error = new UserError(emptyResult.getErrorMessage())
                this.amountProjectManagement.setComponentError(error)
                createOfferViewModel.projectManagementQuantityValid = false
            } else if (numberResult.isError()) {
                error = new UserError(numberResult.getErrorMessage())
                this.amountProjectManagement.setComponentError(error)
                createOfferViewModel.projectManagementQuantityValid = false
            } else {
                this.amountProjectManagement.setComponentError(null)
                createOfferViewModel.projectManagementQuantityValid = true
            }
        })

        applyProjectManagement.addClickListener({
            if (projectManagementGrid.getSelectedItems() != null) {
                String amount = amountProjectManagement.getValue()

                try {
                    if (amount != null && amount.isNumber()) {
                        projectManagementGrid.getSelectedItems().each {
                            def amountParsed = Double.parseDouble(amount)
                            if (amountParsed >= 0.0) {
                                ProductItemViewModel offerItem = new ProductItemViewModel(amountParsed, it)
                                updateOverviewGrid(offerItem)
                            }
                        }
                        projectManagementGrid.getDataProvider().refreshAll()
                    }
                }
                catch (Exception e) {
                    viewModel.failureNotifications.add("The quantity must be a number bigger than 0")
                }
            }
            amountProjectManagement.clear()
            projectManagementGrid.deselectAll()
            applyProjectManagement.setEnabled(false)
            amountProjectManagement.setComponentError(null)
        })

        storageGrid.addSelectionListener({
            createOfferViewModel.storageGridSelected = it.firstSelectedItem.isPresent()
        })

        this.amountDataStorage.addValueChangeListener({ event ->
            ValidationResult emptyResult = nonEmptyStringValidator.apply(event.getValue(), new ValueContext(amountDataStorage))
            ValidationResult numberResult = partialValidator.apply(event.getValue(), new ValueContext(amountDataStorage))
            UserError error
            if (emptyResult.isError()) {
                error = new UserError(emptyResult.getErrorMessage())
                this.amountDataStorage.setComponentError(error)
                createOfferViewModel.storageQuantityValid = false
            } else if (numberResult.isError()) {
                error = new UserError(numberResult.getErrorMessage())
                this.amountDataStorage.setComponentError(error)
                createOfferViewModel.storageQuantityValid = false
            } else {
                this.amountDataStorage.setComponentError(null)
                createOfferViewModel.storageQuantityValid = true
            }
        })

        applyDataStorage.addClickListener({
            if (storageGrid.getSelectedItems() != null) {
                String amount = amountDataStorage.getValue()

                try {
                    if (amount != null && amount.isNumber()) {
                        storageGrid.getSelectedItems().each {
                            def amountParsed = Double.parseDouble(amount)
                            if (amountParsed >= 0.0) {
                                ProductItemViewModel offerItem = new ProductItemViewModel(amountParsed, it)
                                updateOverviewGrid(offerItem)
                            }
                        }
                        storageGrid.getDataProvider().refreshAll()
                    }
                }
                catch (Exception e) {
                    viewModel.failureNotifications.add("The quantity must be a number bigger than 0")
                }
            }
            amountDataStorage.clear()
            storageGrid.deselectAll()
            applyDataStorage.setEnabled(false)
            amountDataStorage.setComponentError(null)
        })

        externalServiceGrid.addSelectionListener({
            createOfferViewModel.externalServiceGridSelected = it.firstSelectedItem.isPresent()
        })

        this.amountExternalService.addValueChangeListener({ event ->
            ValidationResult emptyResult = nonEmptyStringValidator.apply(event.getValue(), new ValueContext(amountExternalService))
            ValidationResult numberResult = partialValidator.apply(event.getValue(), new ValueContext(amountExternalService))
            UserError error
            if (emptyResult.isError()) {
                error = new UserError(emptyResult.getErrorMessage())
                this.amountExternalService.setComponentError(error)
                createOfferViewModel.externalServiceQuantityValid = false
            } else if (numberResult.isError()) {
                error = new UserError(numberResult.getErrorMessage())
                this.amountDataStorage.setComponentError(error)
                createOfferViewModel.externalServiceQuantityValid = false
            } else {
                this.amountDataStorage.setComponentError(null)
                createOfferViewModel.externalServiceQuantityValid = true
            }
        })

        applyExternalService.addClickListener({
            if (externalServiceGrid.getSelectedItems() != null) {
                String amount = amountExternalService.getValue()

                try {
                    if (amount != null && amount.isNumber()) {
                        externalServiceGrid.getSelectedItems().each {
                            def amountParsed = Double.parseDouble(amount)
                            if (amountParsed >= 0.0) {
                                ProductItemViewModel offerItem = new ProductItemViewModel(amountParsed, it)
                                updateOverviewGrid(offerItem)
                            }
                        }
                        externalServiceGrid.getDataProvider().refreshAll()
                    }
                }
                catch (Exception e) {
                    viewModel.failureNotifications.add("The quantity must be a number bigger than 0")
                }
            }
            amountExternalService.clear()
            externalServiceGrid.deselectAll()
            applyExternalService.setEnabled(false)
            amountExternalService.setComponentError(null)
        })

        createOfferViewModel.productItems.addPropertyChangeListener({
            if (createOfferViewModel.productItems) {
                next.setEnabled(true)
            } else {
                next.setEnabled(false)
            }
        })

        createOfferViewModel.addPropertyChangeListener({
            applySequencing.setEnabled(createOfferViewModel.sequencingGridSelected && createOfferViewModel.sequencingQuantityValid)
            applyPrimaryAnalysis.setEnabled(createOfferViewModel.primaryAnalysisGridSelected && createOfferViewModel.primaryAnalysisQuantityValid)
            applySecondaryAnalysis.setEnabled(createOfferViewModel.secondaryAnalysisGridSelected && createOfferViewModel.secondaryAnalysisQuantityValid)
            applyProteomicAnalysis.setEnabled(createOfferViewModel.proteomicsAnalysisGridSelected && createOfferViewModel.proteomicsAnalysisQuantityValid)
            applyMetabolomicAnalysis.setEnabled(createOfferViewModel.metabolomicsAnalysisGridSelected && createOfferViewModel.metabolomicsAnalysisQuantityValid)
            applyProjectManagement.setEnabled(createOfferViewModel.projectManagementGridSelected && createOfferViewModel.projectManagementQuantityValid)
            applyDataStorage.setEnabled(createOfferViewModel.storageGridSelected && createOfferViewModel.storageQuantityValid)
            applyExternalService.setEnabled(createOfferViewModel.externalServiceGridSelected && createOfferViewModel.externalServiceQuantityValid)
        })

        overviewGrid.addSelectionListener({
            if (it.allSelectedItems) {
                removeItemsButton.setEnabled(true)
            } else {
                removeItemsButton.setEnabled(false)
            }
        })

        removeItemsButton.addClickListener({
            def selectedItems = overviewGrid.getSelectedItems()
            if (selectedItems) {
                createOfferViewModel.productItems.removeAll(selectedItems)
                overviewGrid.dataProvider.refreshAll()
                overviewGrid.deselectAll()
            }
        })
    }

    /**
     * This method should be called whenever the quantity of a ProductItemViewModel changes. It updates the items in overview grid respectively
     */
    void updateOverviewGrid(ProductItemViewModel item) {
        createOfferViewModel.addItem(item)
        overviewGrid.getDataProvider().refreshAll()
        refreshNavButtons()
    }

    private void refreshNavButtons() {
        if (createOfferViewModel.productItems.size() > 0) {
            next.setEnabled(true)
        } else {
            next.setEnabled(false)
        }
    }

}
