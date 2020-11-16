package life.qbic.portal.qoffer2.web.views.create.offer

import com.vaadin.icons.VaadinIcons
import com.vaadin.ui.Accordion
import com.vaadin.ui.Alignment
import com.vaadin.ui.Button
import com.vaadin.ui.Grid
import com.vaadin.ui.HorizontalLayout
import com.vaadin.ui.Label
import com.vaadin.ui.TabSheet
import com.vaadin.ui.VerticalLayout
import life.qbic.datamodel.dtos.business.ProjectManager
import life.qbic.datamodel.dtos.business.services.AtomicProduct
import life.qbic.datamodel.dtos.business.services.DataStorage
import life.qbic.datamodel.dtos.business.services.PartialProduct
import life.qbic.datamodel.dtos.business.services.PrimaryAnalysis
import life.qbic.datamodel.dtos.business.services.Product
import life.qbic.datamodel.dtos.business.services.ProductUnit
import life.qbic.datamodel.dtos.business.services.ProjectManagement
import life.qbic.datamodel.dtos.business.services.SecondaryAnalysis
import life.qbic.datamodel.dtos.business.services.Sequencing
import life.qbic.portal.qoffer2.web.viewmodel.CreateOfferViewModel

/**
 * This class generates a Layout in which the user
 * can select the the different packages requested by the customer.
 *
 * SelectItemsView will be integrated into the CreateOfferView and provides an User Interface
 * with the intention of enabling a user the selecting packages which the customer ordered. This will be the bases for
 * the calculated offer price.
 *
 * @since: 0.1.0
 *
 */
class SelectItemsView extends VerticalLayout{

    final private CreateOfferViewModel viewModel

    private List<Sequencing> sequencingProduct
    private List<ProjectManagement> projectManagementProduct
    private List<DataStorage> storageProduct
    private List<PrimaryAnalysis> primaryAnalyseProduct
    private List<SecondaryAnalysis> secondaryAnalyseProduct

    Grid<Sequencing> sequencingGrid
    Grid<ProjectManagement> projectManagementGrid
    Grid<DataStorage> storageGrid
    Grid<PrimaryAnalysis> primaryAnalyseGrid
    Grid<SecondaryAnalysis> secondaryAnalyseGrid
    Button next


    SelectItemsView(CreateOfferViewModel viewModel){
        this.viewModel = viewModel

        this.sequencingProduct = []
        this.projectManagementProduct = []
        this.storageProduct = []
        this.primaryAnalyseProduct = []
        this.secondaryAnalyseProduct = []

        initLayout()
    }

    /**
     * Initializes the start layout for this view
     */
    private void initLayout(){
        VerticalLayout layout = new VerticalLayout()
        Label titleLabel = new Label("Select Items")
        layout.addComponent(titleLabel)
        layout.setComponentAlignment(titleLabel, Alignment.BOTTOM_LEFT)

        this.next = new Button(VaadinIcons.CHEVRON_CIRCLE_RIGHT)
        next.setEnabled(false)

        HorizontalLayout buttonLayout = new HorizontalLayout(next)
        buttonLayout.setComponentAlignment(next, Alignment.BOTTOM_RIGHT)
        buttonLayout.setSizeFull()

        /**sequencingProduct = []
        projectManagementProduct = []
        storageProduct = []
        primaryAnalyseProduct = []
        secondaryAnalyseProduct = []**/

        addDummyValues()

        this.sequencingGrid = new Grid<Sequencing>()
        this.primaryAnalyseGrid = new Grid<>()
        this.secondaryAnalyseGrid = new Grid<>()
        this.projectManagementGrid = new Grid<>()
        this.storageGrid = new Grid<>()

        generateProductGrid(sequencingGrid)
        generateProductGrid(primaryAnalyseGrid)
        generateProductGrid(secondaryAnalyseGrid)
        generateProductGrid(storageGrid)
        generateProductGrid(projectManagementGrid)

        TabSheet packageAccordion = new TabSheet()
        packageAccordion.addTab(sequencingGrid,"Sequencing Products")
        packageAccordion.addTab(primaryAnalyseGrid,"Primary Bioinformatics Products")
        packageAccordion.addTab(secondaryAnalyseGrid,"Secondary Bioinformatics Products")
        packageAccordion.addTab(projectManagementGrid,"Project Management Products")
        packageAccordion.addTab(storageGrid,"Data Storage Products")

        this.addComponents(layout, packageAccordion, buttonLayout)
        this.setSizeFull()

        setupDataProvider()
    }

    /**
     * This method adds the retrieved Customer Information to the Customer grid
     */
    private void setupDataProvider() {
        this.sequencingGrid.setItems(sequencingProduct)
        this.projectManagementGrid.setItems(projectManagementProduct)
        this.primaryAnalyseGrid.setItems(primaryAnalyseProduct)
        this.secondaryAnalyseGrid.setItems(secondaryAnalyseProduct)
        this.storageGrid.setItems(storageProduct)
    }

    private void addDummyValues(){
        Sequencing sequencing = new Sequencing("RNA sequencing","Sequencing RNA sequences",1.4, ProductUnit.PER_SAMPLE)
        Sequencing sequencing2 = new Sequencing("DNA sequencing","Sequencing DNA sequences",2.5, ProductUnit.PER_SAMPLE)
        sequencingProduct = [sequencing, sequencing2]

        //todo add product unit per hour?
        ProjectManagement management = new ProjectManagement("Consultation","Initial consultation for a project",4,ProductUnit.PER_DATASET)
        ProjectManagement management2 = new ProjectManagement("Project Design","Advising customers on how to design their project",5,ProductUnit.PER_SAMPLE)
        projectManagementProduct = [management,management2]

        DataStorage dataStorage = new DataStorage("Sequencing Data","Storage for all sequencing related data",3,ProductUnit.PER_GIGABYTE)
        storageProduct = [dataStorage]

        PrimaryAnalysis primaryAnalysis = new PrimaryAnalysis("Primary analysis","Analsis of primary data",2,ProductUnit.PER_DATASET)
        primaryAnalyseProduct = [primaryAnalysis]

        SecondaryAnalysis secondaryAnalysis = new SecondaryAnalysis("Secondary analysis","Analsis of secondary data",4,ProductUnit.PER_DATASET)
        secondaryAnalyseProduct = [secondaryAnalysis]
    }
    /**
     * Method which generates the grid and populates the columns with the set product information from the setupDataProvider Method
     *
     * This Method is responsible for setting up the grid and setting the product information to the individual grid columns.
     */
    private static void generateProductGrid(Grid<Product> grid) {
        try {
            grid.addColumn({ product -> product.productName }).setCaption("Product Name")
            grid.addColumn({ product -> product.description }).setCaption("Product Description")
            grid.addColumn({ product -> product.unitPrice }).setCaption("Product Unit Price")
            grid.addColumn({ product -> product.unit.value }).setCaption("Product Unit")

            //specify size of grid and layout
            grid.setSizeFull()

        } catch (Exception e) {
            new Exception("Unexpected exception in building the project manager grid", e)
        }
    }


    /**
     * Adds listener to handle the logic after the user selected a project manager
     */
    private void addListener() {

        sequencingGrid.addSelectionListener({ selection ->
            //vaadin is in single selection mode, selecting the first item will be fine
            ProjectManager projectManager = projectManagerGrid.getSelectedItems().getAt(0)

            viewModel.projectManager = projectManager
            next.setEnabled(true)
        })
    }



}
