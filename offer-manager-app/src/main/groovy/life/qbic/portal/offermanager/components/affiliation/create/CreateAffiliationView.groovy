package life.qbic.portal.offermanager.components.affiliation.create

import com.vaadin.icons.VaadinIcons
import com.vaadin.ui.*
import com.vaadin.ui.themes.ValoTheme
import groovy.util.logging.Log4j2
import life.qbic.business.Constants
import life.qbic.portal.offermanager.components.AbortNotifier
import life.qbic.portal.offermanager.components.AppViewModel
import life.qbic.portal.offermanager.components.Resettable
import life.qbic.portal.offermanager.components.SubmitNotifier
import life.qbic.portal.offermanager.components.affiliation.AffiliationFormView

/**
 * This class generates a Layout in which the user
 * can input the necessary information for the creation of a new affiliation
 *
 * CreateAffiliationView will be integrated into the qOffer 2.0 Portlet and provides an User Interface
 * with the intention of enabling a user the creation of a new Affiliation in the QBiC Database
 *
 * @since: 1.0.0
 */
@Log4j2
class CreateAffiliationView extends FormLayout implements Resettable, SubmitNotifier, AbortNotifier {
    final public AppViewModel sharedViewModel
    private final CreateAffiliationController controller

    private List<SubmitListener> submitListeners = new ArrayList<>()
    private List<AbortListener> abortListeners = new ArrayList<>()

    private Button abortButton
    private Button submitButton
    private AffiliationFormView affiliationFormView
    private String unexpectedErrorMessage = "An unexpected error occurred. We apologize for any inconveniences. Please inform us via email to $Constants.QBIC_HELPDESK_EMAIL"

    CreateAffiliationView(AppViewModel sharedViewModel, CreateAffiliationController controller) {
        this.sharedViewModel = sharedViewModel
        this.controller = controller
        this.affiliationFormView = new AffiliationFormView()

        initLayout()
        registerListeners()
    }

    private void initLayout() {
        final Label label = new Label("Create A New Affiliation")
        label.addStyleName(ValoTheme.LABEL_HUGE)

        this.abortButton = new Button("Abort Affiliation Creation")
        abortButton.setIcon(VaadinIcons.CLOSE_CIRCLE)
        abortButton.addStyleName(ValoTheme.BUTTON_DANGER)

        this.submitButton = new Button("Create Affiliation")
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
        submitButton.addClickListener(withHandledException( it -> onSubmit()))
        abortButton.addClickListener(withHandledException(it -> onAbort()))
        affiliationFormView.addChangeListener(it -> submitButton.setEnabled(affiliationFormView.isValid()))
    }

    private void onSubmit() {
        this.controller.createAffiliation(affiliationFormView.get())
        fireCreationSubmitted()
    }

    private void onAbort() {
        reset()
        fireCreationAborted()
    }


    /**
     * Wraps a button click listener to hide exceptions from the user.
     * @param clickListener the click listener to wrap
     * @return a click listener with proper exception display
     */
    private Button.ClickListener withHandledException(Button.ClickListener clickListener) {
        return (it) -> {
            try {
                clickListener.buttonClick(it)
            } catch (Exception unexpectedException) {
                log.error(unexpectedErrorMessage, unexpectedException)
                sharedViewModel.failureNotifications.add(unexpectedErrorMessage)
            }
        }
    }

    private void fireCreationSubmitted() {
        submitListeners.forEach(SubmitListener::onSubmit)
    }

    private void fireCreationAborted() {
        abortListeners.forEach(AbortListener::onAbort)
    }


    @Override
    void reset() {
        affiliationFormView.reset()
    }

    @Override
    void addAbortListener(AbortListener abortListener) {
        abortListeners.add(abortListener)
    }

    @Override
    void addSubmitListener(SubmitListener submitListener) {
        submitListeners.add(submitListener)
    }
}
