package life.qbic.portal.offermanager.components.projectcreation

import com.vaadin.icons.VaadinIcons
import com.vaadin.ui.Button
import com.vaadin.ui.ComboBox
import com.vaadin.ui.HorizontalLayout
import com.vaadin.ui.Label
import com.vaadin.ui.RadioButtonGroup
import com.vaadin.ui.TextField
import com.vaadin.ui.VerticalLayout
import com.vaadin.ui.themes.ValoTheme
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

    private RadioButtonGroup<CreateProjectModel.SPACE_SELECTION> projectSpaceSelection

    static final EnumMap<CreateProjectModel.SPACE_SELECTION, String> spaceSelectionText =
            new EnumMap(CreateProjectModel.SPACE_SELECTION.class)

    static {
        spaceSelectionText.put(
                CreateProjectModel.SPACE_SELECTION.EXISTING_SPACE,
                "an existing project space")
        spaceSelectionText.put(
                CreateProjectModel.SPACE_SELECTION.NEW_SPACE,
                "a new project space")
    }

    private final CreateProjectModel model

    private TextField desiredSpaceName

    private TextField resultingSpaceName

    private HorizontalLayout customSpaceLayout

    private HorizontalLayout existingSpaceLayout

    private ComboBox<ProjectSpace> availableSpacesBox

    private VerticalLayout projectCodeLayout

    private TextField desiredProjectCode

    private TextField resultingProjectCode

    private HorizontalLayout projectAvailability

    private Button createProjectButton

    CreateProjectView(CreateProjectModel createProjectModel) {
        this.model = createProjectModel
        setupVaadinComponents()
        configureListeners()
        bindData()
    }

    private void setupVaadinComponents() {
        projectSpaceSelection = new RadioButtonGroup<>("Create project in",
                model.spaceSelectionDataProvider)
        projectSpaceSelection.setItemCaptionGenerator(item -> spaceSelectionText.get(item))
        this.addComponent(projectSpaceSelection)

        customSpaceLayout = new HorizontalLayout()
        desiredSpaceName = new TextField("Desired space name")
        resultingSpaceName = new TextField("Resulting name")
        resultingSpaceName.setEnabled(false)
        customSpaceLayout.addComponents(desiredSpaceName, resultingSpaceName)
        customSpaceLayout.setVisible(false)
        this.addComponent(customSpaceLayout)

        existingSpaceLayout = new HorizontalLayout()
        availableSpacesBox = new ComboBox<>("Available project spaces")
        existingSpaceLayout.addComponent(availableSpacesBox)
        existingSpaceLayout.setVisible(false)
        this.addComponent(existingSpaceLayout)

        Label label = new Label("Please set a project code")

        this.addComponent(label)

        projectCodeLayout = new VerticalLayout()
        projectCodeLayout.setMargin(false)
        def container = new HorizontalLayout()
        desiredProjectCode = new TextField("Desired project code")
        resultingProjectCode = new TextField("Resulting project code")
        resultingProjectCode.setEnabled(false)
        container.addComponents(desiredProjectCode, resultingProjectCode)
        projectCodeLayout.addComponent(container)
        projectAvailability = new HorizontalLayout()
        projectCodeLayout.addComponent(projectAvailability)
        this.addComponent(projectCodeLayout)

        createProjectButton = new Button("Create Project", VaadinIcons.CHECK_SQUARE)
        createProjectButton.setStyleName(ValoTheme.BUTTON_LARGE)
        this.addComponent(createProjectButton)
        createProjectButton.setEnabled(model.createProjectEnabled)
    }

    private void configureListeners() {
        this.desiredSpaceName.addValueChangeListener({model.desiredSpaceName = it.value})
        this.model.addPropertyChangeListener("resultingSpaceName", {this.resultingSpaceName
                .setValue(model.resultingSpaceName)})
        this.projectSpaceSelection.addValueChangeListener({
            if (it.value == CreateProjectModel.SPACE_SELECTION.NEW_SPACE) {
                existingSpaceLayout.setVisible(false)
                customSpaceLayout.setVisible(true)
            } else {
                existingSpaceLayout.setVisible(true)
                customSpaceLayout.setVisible(false)
            }
        })
        this.desiredProjectCode.addValueChangeListener({model.desiredProjectCode = it.value})
        this.model.addPropertyChangeListener("projectCodeValidationResult", {
            this.projectAvailability.removeAllComponents()
            this.resultingProjectCode.setValue(model.resultingProjectCode)
            if (model.codeIsValid) {
                def label = new Label(model.projectCodeValidationResult)
                label.setStyleName(ValoTheme.LABEL_SUCCESS)
                this.projectAvailability.addComponent(label)
            } else {
                def label = new Label(model.projectCodeValidationResult)
                label.setStyleName(ValoTheme.LABEL_FAILURE)
                this.projectAvailability.addComponent(label)
            }
        })
        this.model.addPropertyChangeListener("createProjectEnabled", {
            this.createProjectButton.setEnabled(model.createProjectEnabled)
        })
    }

    private void bindData() {
        availableSpacesBox.setDataProvider(model.availableSpaces)
    }
}
