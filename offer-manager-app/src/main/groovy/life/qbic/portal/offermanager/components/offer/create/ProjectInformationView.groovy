package life.qbic.portal.offermanager.components.offer.create

import com.vaadin.data.Binder
import com.vaadin.icons.VaadinIcons
import com.vaadin.ui.Alignment
import com.vaadin.ui.Button
import com.vaadin.ui.HorizontalLayout
import com.vaadin.ui.TextArea
import com.vaadin.ui.TextField
import com.vaadin.ui.VerticalLayout
import com.vaadin.ui.themes.ValoTheme

/**
 * This class generates a Layout in which the user
 * can input the necessary information about a project
 *
 * ProjectInformationView will be integrated into the CreateOfferView and provides an User Interface
 * with the intention of enabling a user the adding the project information for which an offer will be created in the
 * QBiC database.
 *
 * @since: 0.1.0
 *
 */
class ProjectInformationView extends VerticalLayout {

    private final CreateOfferViewModel createOfferViewModel

    TextField projectTitle
    TextArea projectObjective
    TextArea experimentalDesign
    Button next

    ProjectInformationView(CreateOfferViewModel createOfferViewModel) {
        this.createOfferViewModel = createOfferViewModel

        initLayout()
        bindViewModel()
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


    /**
     * This method connects the form fields to the corresponding values in the view model
     */
    private void bindViewModel() {
        Binder<CreateOfferViewModel> binder = new Binder<>()

        // by binding the fields to the view model, the model is updated when the user input changed
        binder.setBean(createOfferViewModel)

        binder.forField(projectTitle)
                .bind({ it.projectTitle }, { it, updatedValue -> it.setProjectTitle(updatedValue) })
        binder.forField(projectObjective)
                .bind({ it.projectObjective }, { it, updatedValue -> it.setProjectObjective(updatedValue) })
        binder.forField(experimentalDesign)
                .bind({ it.experimentalDesign }, { it, updatedValue -> it.setExperimentalDesign(updatedValue) })


        /*
        Here we setup a listener to the viewModel that hold displayed information.
        The listener is needed since Vaadin bindings only work one-way

        Please NOTE: we cannot use the binder.readBean(binder.getBean) refresh here since it would
        overwrite all validators attached to the fields. We furthermore cannot use the
        BinderBuilder#withValidator method since this would prevent the form from showing invalid
        information that is stored within the viewModel. We want the view to reflect the view model
        at all times!
         */
        createOfferViewModel.addPropertyChangeListener({it ->
            switch (it.propertyName) {
                case "projectTitle":
                    String newValue = it.newValue as String
                    projectTitle.value = newValue ?: projectTitle.emptyValue
                    break
                case "projectObjective":
                    String newValue = it.newValue as String
                    projectObjective.value = newValue ?: projectObjective.emptyValue
                    break
                case "experimentalDesign":
                    String newValue = it.newValue as String
                    experimentalDesign.value = newValue ?: experimentalDesign.emptyValue
                    break
                default:
                    break
            }
        })

        /*
        we listen to the valid properties. whenever the presenter resets values in the viewmodel
        and resets the valid properties the component error on the respective component is removed
        */
        createOfferViewModel.addPropertyChangeListener({it ->
            switch (it.propertyName) {
                case "projectTitle":
                    if (it.newValue || it.newValue == null) {
                        projectTitle.componentError = null
                    }
                    break
                case "projectObjective":
                    if (it.newValue || it.newValue == null) {
                        projectObjective.componentError = null
                    }
                    break
                default:
                    break
            }
            next.setEnabled(true)
        })
    }

}
