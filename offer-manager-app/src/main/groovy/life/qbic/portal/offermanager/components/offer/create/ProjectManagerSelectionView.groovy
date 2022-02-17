package life.qbic.portal.offermanager.components.offer.create

import com.vaadin.data.provider.ListDataProvider
import com.vaadin.event.selection.SingleSelectionEvent
import com.vaadin.icons.VaadinIcons
import com.vaadin.shared.ui.grid.HeightMode
import com.vaadin.ui.*
import com.vaadin.ui.components.grid.HeaderRow
import com.vaadin.ui.themes.ValoTheme
import life.qbic.business.persons.Person
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

    Grid<Person> projectManagerGrid
    HorizontalLayout projectManagerLayout

    Label selectedProjectManager

    ProjectManagerSelectionView(CreateOfferViewModel viewModel){
        this.viewModel = viewModel
        initLayout()
        generateProjectManagerGrid()
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
        Provide a display the current selected person with the selected affiliation
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

        this.projectManagerGrid = new Grid<Person>()
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
    private ListDataProvider<Person> setupDataProvider() {
        def dataProvider = new ListDataProvider<>(viewModel.getPersons() as List<Person>)
        this.projectManagerGrid.setDataProvider(dataProvider)
        return dataProvider
    }

    /**
     * Method which generates the grid and populates the columns with the set Customer information from the setupDataProvider Method
     *
     * This Method is responsible for setting up the grid and setting the customer information to the individual grid columns.
     */
    private def generateProjectManagerGrid() {
        try {
            this.projectManagerGrid.addColumn({ person -> person.getFirstName() })
                    .setCaption("First Name").setId("FirstName")
            this.projectManagerGrid.addColumn({ person -> person.getLastName() })
                    .setCaption("Last Name").setId("LastName")
            this.projectManagerGrid.addColumn({ person -> person.getEmail() })
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
        ListDataProvider<Person> projectManagerDataProvider = setupDataProvider()
        /*
        Lastly, we add some nice content filters
         */
        setupFilters(projectManagerDataProvider)
    }

    /**
     * Adds listener to handle the logic after the user selected a project manager
     */
    private void addListener() {

        projectManagerGrid.addSelectionListener({ selection ->
            if (selection instanceof SingleSelectionEvent<Person>) {
                viewModel.projectManager = selection.getValue()
            }
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

        viewModel.addPropertyChangeListener("persons", {
            if (it instanceof ObservableList.ElementEvent) {
                this.projectManagerGrid.getDataProvider().refreshAll()
            }
        })
    }

    private void setupFilters(ListDataProvider<Person> projectManagerListDataProvider) {
        HeaderRow projectManagerFilterRow = projectManagerGrid.appendHeaderRow()
        GridUtils.setupColumnFilter(projectManagerListDataProvider,
                projectManagerGrid.getColumn("FirstName"),
                projectManagerFilterRow)
        GridUtils.setupColumnFilter(projectManagerListDataProvider,
                projectManagerGrid.getColumn("LastName"),
                projectManagerFilterRow)
        GridUtils.setupColumnFilter(projectManagerListDataProvider,
                projectManagerGrid.getColumn("EmailAddress"),
                projectManagerFilterRow)
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
