package life.qbic.portal.qoffer2.web.views.create.offer

import com.vaadin.icons.VaadinIcons
import com.vaadin.ui.Alignment
import com.vaadin.ui.Button
import com.vaadin.ui.Grid
import com.vaadin.ui.HorizontalLayout
import com.vaadin.ui.Label
import com.vaadin.ui.VerticalLayout
import life.qbic.datamodel.dtos.business.Affiliation
import life.qbic.datamodel.dtos.business.Customer
import life.qbic.datamodel.dtos.business.ProjectManager
import life.qbic.datamodel.dtos.general.Person
import life.qbic.portal.qoffer2.web.viewmodel.CreateOfferViewModel

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

    final private CreateOfferViewModel viewModel

    private final List<ProjectManager> foundProjectManagers

    Button next
    Grid<ProjectManager> projectManagerGrid
    HorizontalLayout projectManagerLayout

    ProjectManagerSelectionView(CreateOfferViewModel viewModel){
        this.viewModel = viewModel

        ProjectManager manager = new ProjectManager.Builder("John","Doe","john.do@web.de").build()
        ProjectManager manager2 = new ProjectManager.Builder("Janet","Doe","janet.do@web.de").build()

        foundProjectManagers = [manager,manager2]

        initLayout()
        setupDataProvider()
        generateCustomerGrid()
        addListener()
    }

    /**
     * Initializes the start layout for this view
     */
    private void initLayout(){
        VerticalLayout layout = new VerticalLayout()
        Label titleLabel = new Label("Select Project Manager")
        layout.addComponent(titleLabel)
        layout.setComponentAlignment(titleLabel, Alignment.BOTTOM_LEFT)

        this.next = new Button(VaadinIcons.CHEVRON_CIRCLE_RIGHT)
        next.setEnabled(false)

        HorizontalLayout buttonLayout = new HorizontalLayout(next)
        buttonLayout.setComponentAlignment(next, Alignment.BOTTOM_RIGHT)
        buttonLayout.setSizeFull()

        this.projectManagerGrid = new Grid<ProjectManager>()
        projectManagerLayout = new HorizontalLayout(projectManagerGrid)

        this.addComponents(layout, projectManagerLayout, buttonLayout)
    }

    /**
     * This method adds the retrieved Customer Information to the Customer grid
     */
    private void setupDataProvider() {
        this.projectManagerGrid.setItems(foundProjectManagers)
    }

    /**
     * Method which generates the grid and populates the columns with the set Customer information from the setupDataProvider Method
     *
     * This Method is responsible for setting up the grid and setting the customer information to the individual grid columns.
     */
    private def generateCustomerGrid() {
        try {
            this.projectManagerGrid.addColumn({ customer -> customer.getFirstName() }).setCaption("First Name")
            this.projectManagerGrid.addColumn({ customer -> customer.getLastName() }).setCaption("Last Name")
            this.projectManagerGrid.addColumn({ customer -> customer.getEmailAddress() }).setCaption("Email Address")

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

}
