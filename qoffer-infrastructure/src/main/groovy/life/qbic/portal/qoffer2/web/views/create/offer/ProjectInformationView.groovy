package life.qbic.portal.qoffer2.web.views.create.offer

import com.vaadin.data.Binder
import com.vaadin.icons.VaadinIcons
import com.vaadin.ui.Alignment
import com.vaadin.ui.Button
import com.vaadin.ui.HorizontalLayout
import com.vaadin.ui.Label
import com.vaadin.ui.TextArea
import com.vaadin.ui.TextField
import com.vaadin.ui.VerticalLayout
import com.vaadin.ui.themes.ValoTheme
import life.qbic.portal.qoffer2.web.viewmodel.CreateOfferViewModel
import life.qbic.portal.qoffer2.web.viewmodel.create.offer.ProjectInformationViewModel

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

    private final ProjectInformationViewModel projectInformationViewModel

    TextField projectTitle
    TextArea projectDescription
    Button next

    ProjectInformationView(ProjectInformationViewModel projectInformationViewModel) {
        this.projectInformationViewModel = projectInformationViewModel

        initLayout()
        bindViewModel()
    }

    /**
     * Initializes the ProjectInformationView with the view elements that should be placed in it
     */
    private void initLayout() {
        Label titleLabel = new Label("Project Information")

        this.projectTitle = new TextField("Project Title")
        projectTitle.setPlaceholder("Enter the project title here")
        projectTitle.setRequiredIndicatorVisible(true)
        projectTitle.setSizeFull()

        this.projectDescription = new TextArea("Project Description")
        projectDescription.setPlaceholder("Enter the project description here")
        projectDescription.setRequiredIndicatorVisible(true)
        projectDescription.setSizeFull()

        this.next = new Button(VaadinIcons.CHEVRON_CIRCLE_RIGHT)
        next.addStyleName(ValoTheme.LABEL_LARGE)
        next.setEnabled(false)

        VerticalLayout textLayout = new VerticalLayout(projectTitle, projectDescription)
        textLayout.setComponentAlignment(projectTitle, Alignment.TOP_CENTER)
        textLayout.setComponentAlignment(projectDescription, Alignment.BOTTOM_CENTER)
        textLayout.setSizeFull()

        HorizontalLayout buttonLayout = new HorizontalLayout(next)
        buttonLayout.setComponentAlignment(next, Alignment.BOTTOM_RIGHT)
        buttonLayout.setSizeFull()

        this.addComponents(titleLabel, textLayout, buttonLayout)
    }


    /**
     * This method connects the form fields to the corresponding values in the view model
     */
    private void bindViewModel() {
        Binder<CreateOfferViewModel> binder = new Binder<>()

        // by binding the fields to the view model, the model is updated when the user input changed
        binder.setBean(projectInformationViewModel)

        binder.forField(projectTitle)
                .bind({ it.projectTitle }, { it, updatedValue -> it.setProjectTitle(updatedValue) })
        binder.forField(projectDescription)
                .bind({ it.projectDescription }, { it, updatedValue -> it.setProjectDescription(updatedValue) })


        /*
        Here we setup a listener to the viewModel that hold displayed information.
        The listener is needed since Vaadin bindings only work one-way

        Please NOTE: we cannot use the binder.readBean(binder.getBean) refresh here since it would
        overwrite all validators attached to the fields. We furthermore cannot use the
        BinderBuilder#withValidator method since this would prevent the form from showing invalid
        information that is stored within the viewModel. We want the view to reflect the view model
        at all times!
         */
        projectInformationViewModel.addPropertyChangeListener({ it ->
            switch (it.propertyName) {
                case "projectTitle":
                    String newValue = it.newValue as String
                    projectTitle.value = newValue ?: projectTitle.emptyValue
                    break
                case "projectDescription":
                    String newValue = it.newValue as String
                    projectDescription.value = newValue ?: projectDescription.emptyValue
                    break
                default:
                    break
            }
        })

        /*
        we listen to the valid properties. whenever the presenter resets values in the viewmodel
        and resets the valid properties the component error on the respective component is removed
        */
        projectInformationViewModel.addPropertyChangeListener({ it ->
            switch (it.propertyName) {
                case "projectTitle":
                    if (it.newValue || it.newValue == null) {
                        projectTitle.componentError = null
                    }
                    break
                case "projectDescription":
                    if (it.newValue || it.newValue == null) {
                        projectDescription.componentError = null
                    }
                    break
                default:
                    break
            }
            next.setEnabled(true)
        })
    }

}
