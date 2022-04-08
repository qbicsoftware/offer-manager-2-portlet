package life.qbic.portal.offermanager.components.affiliation.update

import com.vaadin.icons.VaadinIcons
import com.vaadin.ui.*
import com.vaadin.ui.themes.ValoTheme
import groovy.util.logging.Log4j2
import life.qbic.business.Constants
import life.qbic.business.persons.affiliation.Affiliation
import life.qbic.portal.offermanager.components.AppViewModel
import life.qbic.portal.offermanager.components.Resettable
import life.qbic.portal.offermanager.components.Updatable
import life.qbic.portal.offermanager.components.affiliation.AffiliationFormView

@Log4j2
class UpdateAffiliationView extends FormLayout implements Resettable, Updatable<Affiliation> {
  final public AppViewModel sharedViewModel
  private final UpdateAffiliationController controller
  private Affiliation outdatedAffiliation = null

  private Button abortButton
  private Button submitButton
  private AffiliationFormView affiliationFormView
  private String unexpectedErrorMessage = "An unexpected error occurred. We apologize for any inconveniences. Please inform us via email to $Constants.QBIC_HELPDESK_EMAIL"

  UpdateAffiliationView(AppViewModel sharedViewModel, UpdateAffiliationController controller) {
    this.sharedViewModel = sharedViewModel
    this.controller = controller
    this.affiliationFormView = new AffiliationFormView()

    initLayout()
    registerListeners()
  }

  @Override
  void reset() {
    affiliationFormView.reset()
    outdatedAffiliation = null
  }

  @Override
  void update(Affiliation affiliation) {
    this.outdatedAffiliation = affiliation
    affiliationFormView.update(affiliation)
  }

  private void initLayout() {
    final Label label = new Label("Update An Affiliation")
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
    buttonLayout.setComponentAlignment(submitButton, Alignment.MIDDLE_RIGHT)
    buttonLayout.setSizeFull()

    this.addComponents(label, affiliationFormView, buttonLayout)
    this.setComponentAlignment(buttonLayout, Alignment.BOTTOM_RIGHT)
    this.setMargin(false)
  }

  private void registerListeners() {
    submitButton.addClickListener(withHandledException( it -> onSubmit()))
    abortButton.addClickListener(withHandledException(this::reset))
    affiliationFormView.addChangeListener(it -> submitButton.setEnabled(affiliationFormView.isValid() && hasDataChanged()))
  }

  private boolean hasDataChanged() {
    affiliationFormView.get() == outdatedAffiliation
  }


  private void onSubmit() {
    Affiliation affiliation = affiliationFormView.get()
    //todo make sure changes in country and category are confirmed again by the user
    this.controller.updateAffiliation(affiliation)
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
}
