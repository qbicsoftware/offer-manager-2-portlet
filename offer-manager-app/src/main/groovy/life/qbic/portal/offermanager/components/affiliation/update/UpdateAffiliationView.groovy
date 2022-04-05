package life.qbic.portal.offermanager.components.affiliation.update

import com.vaadin.icons.VaadinIcons
import com.vaadin.ui.Alignment
import com.vaadin.ui.Button
import com.vaadin.ui.FormLayout
import com.vaadin.ui.HorizontalLayout
import com.vaadin.ui.Label
import com.vaadin.ui.themes.ValoTheme
import life.qbic.business.persons.affiliation.Affiliation
import life.qbic.business.persons.affiliation.AffiliationCategoryConverter
import life.qbic.portal.offermanager.components.AppViewModel
import life.qbic.portal.offermanager.components.Resettable
import life.qbic.portal.offermanager.components.affiliation.AffiliationFormView
import life.qbic.portal.offermanager.components.affiliation.create.CreateAffiliationViewModel

class UpdateAffiliationView extends FormLayout implements Resettable{

    private final AppViewModel sharedViewModel
    private final CreateAffiliationViewModel viewModel
    private final UpdateAffiliationController controller


    Button abortButton
    Button submitButton
    private final AffiliationFormView affiliationFormView

    UpdateAffiliationView(AppViewModel sharedViewModel, CreateAffiliationViewModel createAffiliationViewModel, UpdateAffiliationController controller) {
        this.sharedViewModel = sharedViewModel
        this.viewModel = createAffiliationViewModel
        this.controller = controller
        this.affiliationFormView = new AffiliationFormView(createAffiliationViewModel)

        initLayout()
        registerListeners()
    }

    private void initLayout() {
        final Label label = new Label("Update Affiliation")
        label.addStyleName(ValoTheme.LABEL_HUGE)

        this.abortButton = new Button("Abort Affiliation Update")
        abortButton.setIcon(VaadinIcons.CLOSE_CIRCLE)
        abortButton.addStyleName(ValoTheme.BUTTON_DANGER)

        this.submitButton = new Button("Update Affiliation")
        submitButton.enabled = false
        submitButton.setIcon(VaadinIcons.OFFICE)
        submitButton.addStyleName(ValoTheme.BUTTON_FRIENDLY)

        HorizontalLayout buttonLayout = new HorizontalLayout(abortButton, submitButton)
        buttonLayout.setComponentAlignment(abortButton, Alignment.MIDDLE_RIGHT)
        buttonLayout.setComponentAlignment(submitButton,Alignment.MIDDLE_RIGHT)
        buttonLayout.setSizeFull()

        this.addComponents(label, affiliationFormView, buttonLayout)
        this.setComponentAlignment(buttonLayout, Alignment.BOTTOM_RIGHT)
        this.setMargin(false)
    }

    private void registerListeners() {
        submitButton.addClickListener({
            Affiliation affiliation = viewModel.affiliationEntry
            AffiliationCategoryConverter categoryConverter = new AffiliationCategoryConverter()

            affiliation.addressAddition = viewModel.addressAddition
            affiliation.category =  categoryConverter.convertToEntityAttribute(viewModel.affiliationCategory)
            affiliation.city = viewModel.city
            affiliation.country = viewModel.country
            affiliation.organization = viewModel.organisation
            affiliation.postalCode = viewModel.postalCode
            affiliation.street = viewModel.street

            this.controller.updateAffiliation(viewModel.affiliationEntry)
        })
        this.abortButton.addClickListener({ event ->
            try {
                reset()
            }
            catch (Exception ignored) {
                sharedViewModel.failureNotifications.add("An unexpected error occurred. We apologize for any inconveniences. Please inform us via email to support@qbic.zendesk.com.")
            }
        })

        viewModel.addPropertyChangeListener({
            submitButton.setEnabled(allValuesValid())
        })
    }

    private boolean allValuesValid() {
        return viewModel.affiliationCategoryValid \
              && viewModel.cityValid \
              && viewModel.countryValid \
              && viewModel.organisationValid \
              && viewModel.postalCodeValid \
              && viewModel.streetValid
    }

    @Override
    void reset() {
        affiliationFormView.reset()
    }
}
