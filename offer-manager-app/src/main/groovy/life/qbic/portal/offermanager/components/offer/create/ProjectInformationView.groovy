package life.qbic.portal.offermanager.components.offer.create


import com.vaadin.data.ValidationResult
import com.vaadin.data.Validator
import com.vaadin.data.ValueContext
import com.vaadin.icons.VaadinIcons
import com.vaadin.server.UserError
import com.vaadin.ui.*
import com.vaadin.ui.themes.ValoTheme
import life.qbic.portal.offermanager.components.Resettable

/**
 * This class generates a Layout in which the user
 * can input the necessary information about a project
 *
 * ProjectInformationView will be integrated into the CreateOfferView and provides an User Interface
 * with the intention of enabling a user the adding the project information for which an offer will be created in the
 * QBiC database.
 *
 * @since 0.1.0
 */
class ProjectInformationView extends VerticalLayout implements Resettable {

    private final CreateOfferViewModel createOfferViewModel

    TextField projectTitle
    TextArea projectObjective
    TextArea experimentalDesign
    Button next

    ProjectInformationView(CreateOfferViewModel createOfferViewModel) {
        this.createOfferViewModel = createOfferViewModel

        initLayout()
        bindViewModel()
        setupValidators()
    }

    /**
     * Initializes the ProjectInformationView with the view elements that should be placed in it
     */
    private void initLayout() {
        this.projectTitle = new TextField("Project Title")
        projectTitle.setPlaceholder("Enter the project title here")
        projectTitle.setRequiredIndicatorVisible(true)
        projectTitle.setSizeFull()

        this.projectObjective = new TextArea("Project Objective")
        projectObjective.setPlaceholder("Enter the project objective here")
        projectObjective.setRequiredIndicatorVisible(true)
        projectObjective.setSizeFull()

        this.experimentalDesign = new TextArea("Experimental Design")
        experimentalDesign.setPlaceholder("Enter the experimental design here")
        experimentalDesign.setSizeFull()

        this.next = new Button(VaadinIcons.CHEVRON_CIRCLE_RIGHT)
        next.addStyleName(ValoTheme.LABEL_LARGE)
        next.setEnabled(false)

        VerticalLayout textLayout = new VerticalLayout(projectTitle, projectObjective, experimentalDesign)
        textLayout.setComponentAlignment(projectTitle, Alignment.TOP_CENTER)
        textLayout.setComponentAlignment(projectObjective, Alignment.BOTTOM_CENTER)
        textLayout.setSizeFull()
        textLayout.setMargin(false)

        HorizontalLayout buttonLayout = new HorizontalLayout(next)
        buttonLayout.setComponentAlignment(next, Alignment.BOTTOM_RIGHT)
        buttonLayout.setSizeFull()

        this.setMargin(false)
        this.addComponents(textLayout, buttonLayout)
    }

    private void resetContent() {
        this.createOfferViewModel.projectTitle = null
        this.createOfferViewModel.projectObjective = null
        this.createOfferViewModel.experimentalDesign = null
    }


    /**
     * This method connects the form fields to the corresponding values in the view model
     */
    private void bindViewModel() {

        // bind projectTitle
        createOfferViewModel.addPropertyChangeListener("projectTitle") {
            String newValue = it.newValue as String
            projectTitle.value = newValue ?: projectTitle.emptyValue
        }
        projectTitle.addValueChangeListener({
            createOfferViewModel.projectTitle = it.value
        })

        // bind projectObjective
        createOfferViewModel.addPropertyChangeListener("projectObjective", {
            String newValue = it.newValue as String
            projectObjective.value = newValue ?: projectObjective.emptyValue
        })
        projectObjective.addValueChangeListener({
            createOfferViewModel.projectObjective = it.value
        })

        // bind experimentalDesign
        createOfferViewModel.addPropertyChangeListener("experimentalDesign", {
            String newValue = it.newValue as String
            experimentalDesign.value = newValue ?: experimentalDesign.emptyValue
        })
        experimentalDesign.addValueChangeListener({
            createOfferViewModel.experimentalDesign = it.value
        })

        /*
        we listen to the valid properties. whenever the presenter resets values in the viewmodel
        and resets the valid properties the component error on the respective component is removed
        */
        createOfferViewModel.addPropertyChangeListener({ it ->
            switch (it.propertyName) {
                case "projectTitleValid":
                    if (it.newValue || it.newValue == null) {
                        projectTitle.componentError = null
                    }
                    break
                case "projectObjectiveValid":
                    if (it.newValue || it.newValue == null) {
                        projectObjective.componentError = null
                    }
                    break
                case "experimentalDesignValid":
                    if (it.newValue || it.newValue == null) {
                        experimentalDesign.componentError = null
                    }
                    break
                default:
                    break
            }
            next.enabled = allValuesValid()
        })
    }

    private boolean allValuesValid() {

        return createOfferViewModel.projectTitleValid &&
                createOfferViewModel.projectObjectiveValid &&
                createOfferViewModel.experimentalDesignValid
    }

    private void setupValidators() {
        Validator<String> notNullValidator = Validator.from({ String value -> (value != null) }, "Please provide a valid description.")
        Validator<String> notEmptyValidator = Validator.from({ String value -> (value && !value.trim().empty) }, "Please provide a valid description.")


        //Add Listeners to all Fields in the Form layout
        this.projectTitle.addValueChangeListener({ event ->
            ValidationResult result = notEmptyValidator.apply(event.getValue(), new ValueContext(this.projectTitle))
            if (result.isError()) {
                createOfferViewModel.projectTitleValid = false
                UserError error = new UserError(result.getErrorMessage())
                projectTitle.setComponentError(error)
            } else {
                createOfferViewModel.projectTitleValid = true
            }
        })

        this.projectObjective.addValueChangeListener({ event ->
            ValidationResult result = notEmptyValidator.apply(event.getValue(), new ValueContext(this.projectObjective))
            if (result.isError()) {
                createOfferViewModel.projectObjectiveValid = false
                UserError error = new UserError(result.getErrorMessage())
                projectObjective.setComponentError(error)
            } else {
                createOfferViewModel.projectObjectiveValid = true
            }
        })

        this.experimentalDesign.addValueChangeListener({ event ->
            ValidationResult result = notNullValidator.apply(event.getValue(), new ValueContext(this.experimentalDesign))
            if (result.isError()) {
                createOfferViewModel.experimentalDesignValid = false
                UserError error = new UserError(result.getErrorMessage())
                experimentalDesign.setComponentError(error)
            } else {
                createOfferViewModel.experimentalDesignValid = true
            }
        })

    }

    @Override
    void reset() {
        resetContent()
    }
}
