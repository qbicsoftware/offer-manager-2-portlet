package life.qbic.portal.qoffer2.web.views.create.offer

import com.vaadin.data.provider.ListDataProvider
import com.vaadin.icons.VaadinIcons
import com.vaadin.ui.Alignment
import com.vaadin.ui.Button
import com.vaadin.ui.Grid
import com.vaadin.ui.HorizontalLayout
import com.vaadin.ui.VerticalLayout
import com.vaadin.ui.components.grid.HeaderRow
import com.vaadin.ui.themes.ValoTheme
import life.qbic.datamodel.dtos.business.ProjectManager
import life.qbic.portal.qoffer2.web.viewmodel.CreateOfferViewModel
import life.qbic.portal.qoffer2.web.views.GridUtils

/**
 * This class generates a Layout in which the user
 * can select the project manager assigned to this project
 *
 * ProjectManagerSelectionView will be integrated into the CreateOfferView and provides an User Interface
 * with the intention of enabling a user the selecting a project manager who will be responsible for the created offer.
 *
 * @since: 0.1.0
 *
 */
class ProjectManagerSelectionView extends VerticalLayout{

    private final CreateOfferViewModel viewModel

    Button next
    Button previous

    Grid<ProjectManager> projectManagerGrid
    HorizontalLayout projectManagerLayout

    ProjectManagerSelectionView(CreateOfferViewModel viewModel){
        this.viewModel = viewModel
        initLayout()
        def projectManagerDataProvider = setupDataProvider()
        generateCustomerGrid()
        addListener()
        setupFilters(projectManagerDataProvider)
    }

    /**
     * Initializes the start layout for this view
     */
    private void initLayout(){
        this.next = new Button(VaadinIcons.CHEVRON_CIRCLE_RIGHT)
        next.addStyleName(ValoTheme.LABEL_LARGE)
        next.setEnabled(false)

        this.previous = new Button(VaadinIcons.CHEVRON_CIRCLE_LEFT)
        previous.addStyleName(ValoTheme.LABEL_LARGE)

        HorizontalLayout buttonLayout = new HorizontalLayout(previous,next)
        buttonLayout.setComponentAlignment(next, Alignment.BOTTOM_RIGHT)
        buttonLayout.setComponentAlignment(previous, Alignment.BOTTOM_LEFT)
        buttonLayout.setSizeFull()

        this.projectManagerGrid = new Grid<ProjectManager>()
        projectManagerLayout = new HorizontalLayout(projectManagerGrid)

        this.addComponents(projectManagerLayout, buttonLayout)
    }

    /**
     * This method adds the retrieved Customer Information to the Customer grid
     */
    private ListDataProvider setupDataProvider() {
        def dataProvider = new ListDataProvider<>(viewModel.availableProjectManagers)
        this.projectManagerGrid.setDataProvider(dataProvider)
        return dataProvider
    }

    /**
     * Method which generates the grid and populates the columns with the set Customer information from the setupDataProvider Method
     *
     * This Method is responsible for setting up the grid and setting the customer information to the individual grid columns.
     */
    private def generateCustomerGrid() {
        try {
            this.projectManagerGrid.addColumn({ customer -> customer.getFirstName() })
                    .setCaption("First Name").setId("FirstName")
            this.projectManagerGrid.addColumn({ customer -> customer.getLastName() })
                    .setCaption("Last Name").setId("LastName")
            this.projectManagerGrid.addColumn({ customer -> customer.getEmailAddress() })
                    .setCaption("Email Address").setId("EmailAddress")

            //specify size of grid and layout
            projectManagerLayout.setSizeFull()
            projectManagerGrid.setSizeFull()
        } catch (Exception e) {
            new Exception("Unexpected exception in building the project manager grid", e)
        }
    }

    /**
     * Adds listener to handle the logic after the user selected a project manager
     */
    private void addListener() {

        projectManagerGrid.addSelectionListener({ selection ->
            //vaadin is in single selection mode, selecting the first item will be fine
            ProjectManager projectManager = projectManagerGrid.getSelectedItems().getAt(0)

            viewModel.projectManager = projectManager
            next.setEnabled(true)
        })
    }

    private void setupFilters(ListDataProvider<ProjectManager> projectManagerListDataProvider) {
        HeaderRow customerFilterRow = projectManagerGrid.appendHeaderRow()
        GridUtils.setupColumnFilter(projectManagerListDataProvider,
                projectManagerGrid.getColumn("FirstName"),
                customerFilterRow)
        GridUtils.setupColumnFilter(projectManagerListDataProvider,
                projectManagerGrid.getColumn("LastName"),
                customerFilterRow)
        GridUtils.setupColumnFilter(projectManagerListDataProvider,
                projectManagerGrid.getColumn("EmailAddress"),
                customerFilterRow)
    }
}
