package life.qbic.portal.offermanager.components.offer.create

import com.vaadin.data.provider.ListDataProvider
import com.vaadin.icons.VaadinIcons
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
import life.qbic.datamodel.dtos.business.services.DataStorage
import life.qbic.datamodel.dtos.business.services.PrimaryAnalysis
import life.qbic.datamodel.dtos.business.services.Product
import life.qbic.datamodel.dtos.business.services.ProductUnit
import life.qbic.datamodel.dtos.business.services.ProjectManagement
import life.qbic.datamodel.dtos.business.services.SecondaryAnalysis
import life.qbic.datamodel.dtos.business.services.Sequencing
import life.qbic.business.offers.Currency
import life.qbic.portal.offermanager.components.GridUtils
import life.qbic.portal.offermanager.components.offer.create.CreateOfferViewModel
import life.qbic.portal.offermanager.components.offer.create.ProductItemViewModel
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


    Grid<ProductItemViewModel> sequencingGrid
    Grid<ProductItemViewModel> projectManagementGrid
    Grid<ProductItemViewModel> storageGrid
    Grid<ProductItemViewModel> primaryAnalyseGrid
    Grid<ProductItemViewModel> secondaryAnalyseGrid
    Grid<ProductItemViewModel> overviewGrid

    Button applySequencing
    Button applyProjectManagement
    Button applyPrimaryAnalysis
    Button applySecondaryAnalysis
    Button applyDataStorage
    Button next
    Button previous

    TextField amountSequencing
    TextField amountProjectManagement
    TextField amountPrimaryAnalysis
    TextField amountSecondaryAnalysis
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

        initLayout()
        setupDataProvider()
        addListener()
    }


    /**
     * Initializes the start layout for this view
     */
    private void initLayout(){
        this.sequencingGrid = new Grid<>()
        this.primaryAnalyseGrid = new Grid<>()
        this.secondaryAnalyseGrid = new Grid<>()
        this.projectManagementGrid = new Grid<>()
        this.storageGrid = new Grid<>()
        this.overviewGrid = new Grid<>("Overview:")

        amountSequencing = new TextField("Quantity:")
        amountSequencing.setPlaceholder("e.g. 1")
        amountPrimaryAnalysis = new TextField("Quantity:")
        amountPrimaryAnalysis.setPlaceholder("e.g. 1")
        amountSecondaryAnalysis = new TextField("Quantity:")
        amountSecondaryAnalysis.setPlaceholder("e.g. 1")
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

        this.applyDataStorage = new Button("Apply", VaadinIcons.PLUS)
        applyDataStorage.setEnabled(false)

        this.applyProjectManagement = new Button("Apply", VaadinIcons.PLUS)
        applyProjectManagement.setEnabled(false)

        HorizontalLayout buttonLayout = new HorizontalLayout(previous,next)
        buttonLayout.setComponentAlignment(next, Alignment.BOTTOM_RIGHT)
        buttonLayout.setComponentAlignment(previous, Alignment.BOTTOM_LEFT)
        buttonLayout.setSizeFull()

        HorizontalLayout quantitySeq = new HorizontalLayout(amountSequencing,applySequencing)
        quantitySeq.setSizeFull()
        quantitySeq.setComponentAlignment(applySequencing, Alignment.BOTTOM_RIGHT)
        VerticalLayout seqLayout = new VerticalLayout(sequencingGrid,quantitySeq)
        seqLayout.setSizeFull()

        HorizontalLayout quantityPrimary = new HorizontalLayout(amountPrimaryAnalysis,applyPrimaryAnalysis)
        quantityPrimary.setSizeFull()
        quantityPrimary.setComponentAlignment(applyPrimaryAnalysis, Alignment.BOTTOM_RIGHT)
        VerticalLayout primaryAnalysisLayout = new VerticalLayout(primaryAnalyseGrid, quantityPrimary)
        primaryAnalysisLayout.setSizeFull()

        HorizontalLayout quantitySecondary = new HorizontalLayout(amountSecondaryAnalysis,applySecondaryAnalysis)
        quantitySecondary.setSizeFull()
        quantitySecondary.setComponentAlignment(applySecondaryAnalysis, Alignment.BOTTOM_RIGHT)
        VerticalLayout secondaryAnalysisLayout = new VerticalLayout(secondaryAnalyseGrid, quantitySecondary)
        secondaryAnalysisLayout.setSizeFull()

        HorizontalLayout quantityStorage = new HorizontalLayout(amountDataStorage,applyDataStorage)
        quantityStorage.setSizeFull()
        quantityStorage.setComponentAlignment(applyDataStorage, Alignment.BOTTOM_RIGHT)
        VerticalLayout dataStorageLayout = new VerticalLayout(storageGrid, quantityStorage)
        dataStorageLayout.setSizeFull()

        HorizontalLayout quantityManagement = new HorizontalLayout(amountProjectManagement,applyProjectManagement)
        quantityManagement.setSizeFull()
        quantityManagement.setComponentAlignment(applyProjectManagement, Alignment.BOTTOM_RIGHT)
        VerticalLayout projectManagementLayout = new VerticalLayout(projectManagementGrid, quantityManagement)
        projectManagementLayout.setSizeFull()

        HorizontalLayout overview = new HorizontalLayout(overviewGrid)
        overview.setSizeFull()


        generateProductGrid(sequencingGrid)
        generateProductGrid(primaryAnalyseGrid)
        generateProductGrid(secondaryAnalyseGrid)
        generateProductGrid(storageGrid)
        generateProductGrid(projectManagementGrid)
        generateProductGrid(overviewGrid)

        //make the overview over selected items grow dynamically
        overviewGrid.setHeightMode(HeightMode.UNDEFINED)


        TabSheet packageAccordion = new TabSheet()
        packageAccordion.addTab(seqLayout,"Sequencing Products")
        packageAccordion.addTab(primaryAnalysisLayout,"Primary Bioinformatics Products")
        packageAccordion.addTab(secondaryAnalysisLayout,"Secondary Bioinformatics Products")
        packageAccordion.addTab(projectManagementLayout,"Project Management Products")
        packageAccordion.addTab(dataStorageLayout,"Data Storage Products")

        this.addComponents(packageAccordion, overview, buttonLayout)
        this.setSizeFull()
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

    private void addDummyValues(){
        Sequencing sequencing = new Sequencing("RNA sequencing","Sequencing RNA sequences",1.4, ProductUnit.PER_SAMPLE)
        Sequencing sequencing2 = new Sequencing("DNA sequencing","Sequencing DNA sequences",2.5, ProductUnit.PER_SAMPLE)
        sequencingProduct = [new ProductItemViewModel(0,sequencing), new ProductItemViewModel(0,sequencing2)]

        //todo add product unit per hour?
        ProjectManagement management = new ProjectManagement("Consultation","Initial consultation for a project",4,ProductUnit.PER_DATASET)
        ProjectManagement management2 = new ProjectManagement("Project Design","Advising customers on how to design their project",5,ProductUnit.PER_SAMPLE)
        projectManagementProduct = [new ProductItemViewModel(0,management), new ProductItemViewModel(0,management2)]

        DataStorage dataStorage = new DataStorage("Sequencing Data","Storage for all sequencing related data",3,ProductUnit.PER_GIGABYTE)
        storageProduct = [new ProductItemViewModel(0,dataStorage)]

        PrimaryAnalysis primaryAnalysis = new PrimaryAnalysis("Primary analysis","Analsis of primary data",2,ProductUnit.PER_DATASET)
        primaryAnalyseProduct = [new ProductItemViewModel(0,primaryAnalysis)]

        SecondaryAnalysis secondaryAnalysis = new SecondaryAnalysis("Secondary analysis","Analsis of secondary data",4,ProductUnit.PER_DATASET)
        secondaryAnalyseProduct = [new ProductItemViewModel(0,secondaryAnalysis)]
    }

    /**
     * Method which generates the grid and populates the columns with the set product information from the setupDataProvider Method
     *
     * This Method is responsible for setting up the grid and setting the product information to the individual grid columns.
     */
    private static void generateProductGrid(Grid<ProductItemViewModel> grid) {
        try {
            grid.addColumn({ productItem -> productItem.quantity })
                    .setCaption("Quantity").setId("Quantity")
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
    private void addListener() {
        sequencingGrid.addSelectionListener({
            applySequencing.setEnabled(true)
        })
        applySequencing.addClickListener({
            if(sequencingGrid.getSelectedItems() != null){
                String amount = amountSequencing.getValue()

                try{
                    if(amount != null && amount.isNumber()){
                        sequencingGrid.getSelectedItems().each {
                            if(Integer.parseInt(amount) >= 0){
                                it.setQuantity(Integer.parseInt(amount))
                                updateOverviewGrid(it)
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
        })

        primaryAnalyseGrid.addSelectionListener({
            applyPrimaryAnalysis.setEnabled(true)
        })
        applyPrimaryAnalysis.addClickListener({
            if(primaryAnalyseGrid.getSelectedItems() != null) {
                String amount = amountPrimaryAnalysis.getValue()
                
                try{
                    if(amount != null && amount.isNumber()) {
                        primaryAnalyseGrid.getSelectedItems().each {
                            if(Integer.parseInt(amount) >= 0){
                                it.setQuantity(Integer.parseInt(amount))
                                updateOverviewGrid(it)
                            }
                        }
                        primaryAnalyseGrid.getDataProvider().refreshAll()
                    }
                } catch(NumberFormatException e) {
                    viewModel.failureNotifications.add("The quantity must be an integer number bigger than 0")
                } catch (Exception e) {
                    viewModel.failureNotifications.add("Ups, something went wrong. Please contact support@qbic.zendesk.com")
                }
            }
            amountPrimaryAnalysis.clear()
            primaryAnalyseGrid.deselectAll()
            applyPrimaryAnalysis.setEnabled(false)
        })

        secondaryAnalyseGrid.addSelectionListener({
            applySecondaryAnalysis.setEnabled(true)
        })
        applySecondaryAnalysis.addClickListener({
            if(secondaryAnalyseGrid.getSelectedItems() != null){
                String amount = amountSecondaryAnalysis.getValue()

                try{
                    if(amount != null && amount.isNumber()){
                        secondaryAnalyseGrid.getSelectedItems().each {

                            if(Integer.parseInt(amount) >= 0){
                                it.setQuantity(Integer.parseInt(amount))
                                updateOverviewGrid(it)
                            }
                        }
                        secondaryAnalyseGrid.getDataProvider().refreshAll()
                    }
                }
                catch(NumberFormatException e){
                    viewModel.failureNotifications.add("The quantity must be an integer number bigger than 0")
                } catch (Exception e) {
                    viewModel.failureNotifications.add("Ups, something went wrong. Please contact support@qbic.zendesk.com")
                }
            }
            amountSecondaryAnalysis.clear()
            secondaryAnalyseGrid.deselectAll()
            applySecondaryAnalysis.setEnabled(false)
        })

        projectManagementGrid.addSelectionListener({
            applyProjectManagement.setEnabled(true)
        })
        applyProjectManagement.addClickListener({
            if(projectManagementGrid.getSelectedItems() != null){
                String amount = amountProjectManagement.getValue()

                try{
                    if(amount != null && amount.isNumber()){
                        projectManagementGrid.getSelectedItems().each {

                            if(Double.parseDouble(amount) >= 0.0){
                                it.setQuantity(Double.parseDouble(amount))
                                updateOverviewGrid(it)
                            }
                        }
                        projectManagementGrid.getDataProvider().refreshAll()
                    }
                }
                catch(Exception e){
                    viewModel.failureNotifications.add("The quantity must be a number bigger than 0")
                }
            }
            amountProjectManagement.clear()
            projectManagementGrid.deselectAll()
            applyProjectManagement.setEnabled(false)
        })

        storageGrid.addSelectionListener({
            applyDataStorage.setEnabled(true)
        })
        applyDataStorage.addClickListener({
            if(storageGrid.getSelectedItems() != null){
                String amount = amountDataStorage.getValue()

                try{
                    if(amount != null && amount.isNumber()){
                        storageGrid.getSelectedItems().each {

                            if(Double.parseDouble(amount) >= 0.0){
                                it.setQuantity(Double.parseDouble(amount))
                                updateOverviewGrid(it)
                            }
                        }
                        storageGrid.getDataProvider().refreshAll()
                    }
                }
                catch(Exception e){
                    viewModel.failureNotifications.add("The quantity must be a number bigger than 0")
                }
            }
            amountDataStorage.clear()
            storageGrid.deselectAll()
            applyDataStorage.setEnabled(false)
        })

        createOfferViewModel.productItems.addPropertyChangeListener({
            if (createOfferViewModel.productItems) {
                next.setEnabled(true)
            } else {
                next.setEnabled(false)
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


