package life.qbic.portal.offermanager.components.offer.create

import com.vaadin.data.provider.ListDataProvider
import com.vaadin.icons.VaadinIcons
import com.vaadin.shared.ui.grid.HeightMode
import com.vaadin.ui.*
import com.vaadin.ui.components.grid.HeaderRow
import com.vaadin.ui.themes.ValoTheme
import life.qbic.datamodel.dtos.business.ProjectManager
import life.qbic.portal.offermanager.components.GridUtils
import life.qbic.portal.offermanager.components.Resettable

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
class ProjectManagerSelectionView extends VerticalLayout implements Resettable {

    private final CreateOfferViewModel viewModel

    Button next
    Button previous

    Grid<ProjectManager> projectManagerGrid
    HorizontalLayout projectManagerLayout

    Label selectedProjectManager

    ProjectManagerSelectionView(CreateOfferViewModel viewModel){
        this.viewModel = viewModel
        initLayout()
        generateCustomerGrid()
        addListener()
    }

    /**
     * Initializes the start layout for this view
     */
    private void initLayout(){
        /*
        We start with the header, that contains a descriptive
        title of what the view is about.
         */
        final def title = new HorizontalLayout()
        final def label = new Label("Select Project Manager")
        label.addStyleName(ValoTheme.LABEL_HUGE)
        title.addComponent(label)
        this.addComponent(title)

        /*
        Provide a display the current selected customer with the selected affiliation
         */
        HorizontalLayout selectedManagerOverview = new HorizontalLayout()
        def managerFullName =
                "${ viewModel.projectManager?.firstName ?: "" } " +
                        "${viewModel.projectManager?.lastName ?: "" }"
        selectedProjectManager =
                new Label(viewModel.projectManager?.lastName ? managerFullName : "-")
        selectedProjectManager.setCaption("Current Project Manager")
        selectedManagerOverview.addComponents(selectedProjectManager)


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

        this.addComponents(
                selectedManagerOverview,
                projectManagerLayout,
                buttonLayout
        )

        this.setMargin(false)
    }

    /**
     * This method adds the retrieved Customer Information to the Customer grid
     */
    private ListDataProvider<ProjectManager> setupDataProvider() {
        def dataProvider = new ListDataProvider<ProjectManager>(viewModel.availableProjectManagers)
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
            projectManagerGrid.setHeightMode(HeightMode.ROW)
            projectManagerGrid.setHeightByRows(7)

        } catch (Exception e) {
            new Exception("Unexpected exception in building the project manager grid", e)
        }
        /*
        We need to add a data provider for the grid content
         */
        ListDataProvider<ProjectManager> projectManagerDataProvider = setupDataProvider()
        /*
        Lastly, we add some nice content filters
         */
        setupFilters(projectManagerDataProvider as ListDataProvider<ProjectManager>)
    }

    /**
     * Adds listener to handle the logic after the user selected a project manager
     */
    private void addListener() {

        projectManagerGrid.addSelectionListener({ selection ->
            //vaadin is in single selection mode, selecting the first item will be fine
            ProjectManager projectManager = projectManagerGrid.getSelectedItems().getAt(0)

            viewModel.projectManager = projectManager
        })

        /*
       Let's listen to changes to the project manager selection and update it in the
       display, if the manager selection has changed.
        */
        viewModel.addPropertyChangeListener("projectManager", {
            def projectManagerFullName =
                    "${viewModel.projectManager?.firstName ?: ""} " +
                            "${viewModel.projectManager?.lastName ?: ""}"
            selectedProjectManager.setValue(projectManagerFullName)
            /*
            If a project manager has been selected, we can let the user continue
            with the offer creation.
             */
            if (it.newValue) {
                next.setEnabled(true)
            }
        })

        viewModel.addPropertyChangeListener("availableProjectManagers", {
            if (it instanceof ObservableList.ElementEvent) {
                this.projectManagerGrid.getDataProvider().refreshAll()
            }
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

    @Override
    void reset() {
        resetSelection()
    }

    private void resetSelection() {
        projectManagerGrid.deselectAll()
        selectedProjectManager.setValue("-")
    }
}
