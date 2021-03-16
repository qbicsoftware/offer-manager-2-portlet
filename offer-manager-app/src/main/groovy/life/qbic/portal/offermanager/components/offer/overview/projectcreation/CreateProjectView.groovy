package life.qbic.portal.offermanager.components.offer.overview.projectcreation

import com.vaadin.icons.VaadinIcons
import com.vaadin.shared.ui.ContentMode
import com.vaadin.ui.Button
import com.vaadin.ui.ComboBox
import com.vaadin.ui.GridLayout
import com.vaadin.ui.HorizontalLayout
import com.vaadin.ui.Label
import com.vaadin.ui.Panel
import com.vaadin.ui.RadioButtonGroup
import com.vaadin.ui.TextArea
import com.vaadin.ui.TextField
import com.vaadin.ui.VerticalLayout
import com.vaadin.ui.themes.ValoTheme
import life.qbic.datamodel.dtos.business.Offer
import life.qbic.datamodel.dtos.projectmanagement.ProjectCode
import life.qbic.datamodel.dtos.projectmanagement.ProjectIdentifier
import life.qbic.datamodel.dtos.projectmanagement.ProjectSpace



/**
 * <h1>Enables a user to create a project based on an existing offer</h1>
 *
 * <p>This view provides access the the use cases <code>Create Project Space</code> and
 * <code>Create Project</code>.</p>
 * <p>Both use cases are part of the same scenario, when a user wants to create a project
 * in QBiC's data management platform based on the information of an existing offer.</p>
 *
 * @since 1.0.0
 */
class CreateProjectView extends VerticalLayout{

    private RadioButtonGroup<CreateProjectViewModel.SPACE_SELECTION> projectSpaceSelection

    static final EnumMap<CreateProjectViewModel.SPACE_SELECTION, String> spaceSelectionActionText =
            new EnumMap(CreateProjectViewModel.SPACE_SELECTION.class)

    static {
        spaceSelectionActionText.put(
                CreateProjectViewModel.SPACE_SELECTION.EXISTING_SPACE,
                "an existing project space")
        spaceSelectionActionText.put(
                CreateProjectViewModel.SPACE_SELECTION.NEW_SPACE,
                "a new project space")
    }

    final CreateProjectViewModel model

    private TextField desiredSpaceName

    private TextField resultingSpaceName

    private HorizontalLayout customSpaceLayout

    private HorizontalLayout existingSpaceLayout

    private ComboBox<ProjectSpace> availableSpacesBox

    private HorizontalLayout projectCodeLayout

    private TextField desiredProjectCode

    private TextField resultingProjectCode

    private HorizontalLayout projectAvailability

    private Button createProjectButton

    private Panel selectedOfferInformation

    private GridLayout viewContainerGrid

    private VerticalLayout inputFields

    private CreateProjectController createProjectController

    /**
     * This button enables the user to leave the create project view
     * and navigate back to the previous view.
     * This means that a click listener must be attached by the parent
     * component that displayed this view in the first place.
     */
    Button navigateBack

    CreateProjectView(CreateProjectViewModel createProjectModel, CreateProjectController createProjectController) {
        this.model = createProjectModel
        this.createProjectController = createProjectController
        setupVaadinComponents()
        configureListeners()
        bindData()
    }

    private void setupVaadinComponents() {
        createGridAndContainers()
        createSiteNavigation()
        createTitle()
        createOfferInfo()
        createProjectSpaceElements()
        createProjectCodeElements()
        createProjectIdOverview()
        setupVisibility()
        setupActivity()
        this.addComponent(viewContainerGrid)
    }

    private void createGridAndContainers() {
        viewContainerGrid = new GridLayout(2,3)
        viewContainerGrid.setWidth("100%")
        viewContainerGrid.setColumnExpandRatio(0, 0.6f)
        viewContainerGrid.setColumnExpandRatio(1, 0.4f)
        inputFields = new VerticalLayout()
        inputFields.setMargin(false)
        viewContainerGrid.addComponent(inputFields, 0,0)
    }

    private void createOfferInfo() {
        VerticalLayout container = new VerticalLayout()
        selectedOfferInformation = new Panel("Selected Offer")
        selectedOfferInformation.setContent(new Label("Offer Info Placeholder"))
        container.addComponent(selectedOfferInformation)
        viewContainerGrid.addComponent(container, 1, 0)
    }

    private void createSiteNavigation() {
        navigateBack = new Button("Go Back", VaadinIcons.ARROW_CIRCLE_LEFT)
        navigateBack.setStyleName(ValoTheme.BUTTON_BORDERLESS_COLORED)
        this.addComponent(navigateBack)
    }

    private void createTitle() {
        Label label = new Label("Project Creation")
        label.setStyleName(ValoTheme.LABEL_HUGE)
        this.addComponent(label)
    }

    private void setupVisibility() {
        customSpaceLayout.setVisible(false)
        existingSpaceLayout.setVisible(false)
    }

    private void setupActivity() {
        resultingSpaceName.setEnabled(false)
        resultingProjectCode.setEnabled(false)
        createProjectButton.setEnabled(model.createProjectEnabled)
    }

    private void createProjectSpaceElements() {
        // Set a nice header
        Label label = new Label("1. Please select/create a project space first")
        label.setStyleName(ValoTheme.LABEL_H3)
        this.inputFields.addComponent(label)

        /* The user needs to choose between creating a new project space
         or select an existing one */
        // First we create a ratio group with the choices available
        projectSpaceSelection = new RadioButtonGroup<>("Create project in",
                model.spaceSelectionDataProvider)
        projectSpaceSelection.setItemCaptionGenerator(item -> spaceSelectionActionText.get(item))
        this.inputFields.addComponent(projectSpaceSelection)

        // Case A: A new space needs to be created
        customSpaceLayout = new HorizontalLayout()
        desiredSpaceName = new TextField("New space name")
        desiredSpaceName.setPlaceholder("Your space name")
        desiredSpaceName.setWidth(300, Unit.PIXELS)
        customSpaceLayout.addComponents(desiredSpaceName)
        this.inputFields.addComponent(customSpaceLayout)

        // Case B: An existing space is selected
        existingSpaceLayout = new HorizontalLayout()
        availableSpacesBox = new ComboBox<>("Available project spaces")
        existingSpaceLayout.addComponent(availableSpacesBox)
        availableSpacesBox.setWidth(300, Unit.PIXELS)
        this.inputFields.addComponent(existingSpaceLayout)
    }

    private void createProjectCodeElements() {
        // Set a nice header
        Label label = new Label("2. Please set a project code")
        label.setStyleName(ValoTheme.LABEL_H3)
        this.inputFields.addComponent(label)

        // then a input field for the code
        projectCodeLayout = new HorizontalLayout()
        projectCodeLayout.setMargin(false)
        def container = new HorizontalLayout()
        desiredProjectCode = new TextField()
        desiredProjectCode.setPlaceholder("Your desired code")
        container.addComponents(desiredProjectCode)
        // We also define some dynamic validation place holder
        projectCodeLayout.addComponent(container)
        projectAvailability = new HorizontalLayout()
        projectCodeLayout.addComponent(projectAvailability)
        this.inputFields.addComponent(projectCodeLayout)
    }

    private void createProjectIdOverview() {
        def projectIdContainer = new HorizontalLayout()
        def caption = new Label("Resulting project identifier")
        caption.setStyleName(ValoTheme.LABEL_H3)
        resultingSpaceName = new TextField()
        resultingSpaceName.setWidth(300, Unit.PIXELS)
        resultingProjectCode = new TextField()
        projectIdContainer.addComponents(
                resultingSpaceName,
                new Label("/"),
                resultingProjectCode)
        // Last but not least, the project creation button
        createProjectButton = new Button("Create Project", VaadinIcons.CHECK_SQUARE)
        projectIdContainer.addComponent(createProjectButton)
        // Add the ui elements to the parent layout
        this.inputFields.addComponent(caption)
        this.inputFields.addComponent(projectIdContainer)
    }

    private void configureListeners() {
        // We update the model with the desired space name content
        this.desiredSpaceName.addValueChangeListener({model.desiredSpaceName = it.value})
        // We update the model with the desired project code
        this.desiredProjectCode.addValueChangeListener({model.desiredProjectCode = it.value})
        // Enable back navigation
        this.navigateBack.addClickListener({
            this.setVisible(false)
            if (model.startedFromView.isPresent()) {
                model.startedFromView.get().setVisible(true)
            }
        })
        // We toggle between the two cases, weather a new space needs to be created
        // or an existing space needs to be selected
        this.projectSpaceSelection.addValueChangeListener({
            if (it.value == CreateProjectViewModel.SPACE_SELECTION.NEW_SPACE) {
                existingSpaceLayout.setVisible(false)
                customSpaceLayout.setVisible(true)
            } else {
                existingSpaceLayout.setVisible(true)
                customSpaceLayout.setVisible(false)
            }
        })
        this.availableSpacesBox.addValueChangeListener({
            if (it.value) {
                model.desiredSpaceName = it.value
            } else {
                model.desiredSpaceName = ""
            }
        })
        // Whenever the resulting space name is updated, we update the view
        this.model.addPropertyChangeListener("resultingSpaceName", {this.resultingSpaceName
                .setValue(model.resultingSpaceName)})
        // Whenever new project code validation messages are available, we update the view
        this.model.addPropertyChangeListener("projectCodeValidationResult", {
            this.projectAvailability.removeAllComponents()
            this.resultingProjectCode.setValue(model.resultingProjectCode)
            if (model.codeIsValid) {
                // If the project code is valid, we display some nice success label
                def label = new Label(model.projectCodeValidationResult)
                label.setStyleName(ValoTheme.LABEL_SUCCESS)
                this.projectAvailability.addComponent(label)
            } else {
                // otherwise we inform the user with a formatted failure label
                def label = new Label(model.projectCodeValidationResult)
                label.setStyleName(ValoTheme.LABEL_FAILURE)
                this.projectAvailability.addComponent(label)
            }
        })
        // Whenever all validation is fine, we enable the button to create a project
        this.model.addPropertyChangeListener("createProjectEnabled", {
            this.createProjectButton.setEnabled(model.createProjectEnabled)
        })
        this.model.addPropertyChangeListener("selectedOffer", {
            displaySelectedOfferInfo()
        })
        this.createProjectButton.addClickListener({
            createProjectController.createProject(model.selectedOffer.get(),
                    new ProjectIdentifier(
                            new ProjectSpace(model.resultingSpaceName),
                            new ProjectCode(model.resultingProjectCode)))
        })
    }

    private void bindData() {
        availableSpacesBox.setDataProvider(model.availableSpaces)
    }

    private void displaySelectedOfferInfo() {
        if (model.selectedOffer.isPresent()) {
          loadOfferData(model.selectedOffer.get())
        }
    }

    private void loadOfferData(Offer offer) {
        VerticalLayout content = new VerticalLayout()
        content.addComponent(new Label("<strong>Offer ID</strong>", ContentMode.HTML))
        content.addComponent(new Label("${offer.identifier}"))
        content.addComponent(new Label("<strong>Customer</strong>", ContentMode.HTML))
        content.addComponent(new Label("${offer.customer.firstName} ${offer.customer.lastName}"))
        content.addComponent(new Label("<strong>Title</strong>", ContentMode.HTML))
        TextArea title = new TextArea()
        title.setWidth("100%")
        title.setRows(3)
        title.setEnabled(false)
        title.setValue(offer.projectTitle)
        content.addComponent(title)
        content.setSpacing(false)
        selectedOfferInformation.setContent(content)
    }
}
