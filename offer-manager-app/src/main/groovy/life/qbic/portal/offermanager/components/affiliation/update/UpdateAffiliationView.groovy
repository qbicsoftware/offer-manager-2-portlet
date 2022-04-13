package life.qbic.portal.offermanager.components.affiliation.update


import com.vaadin.icons.VaadinIcons
import com.vaadin.shared.ui.ContentMode
import com.vaadin.ui.*
import com.vaadin.ui.themes.ValoTheme
import groovy.util.logging.Log4j2
import life.qbic.business.Constants
import life.qbic.business.persons.affiliation.Affiliation
import life.qbic.portal.offermanager.components.*
import life.qbic.portal.offermanager.components.affiliation.AffiliationUserInput
import life.qbic.portal.offermanager.dataresources.ResourcesService

@Log4j2
class UpdateAffiliationView extends FormLayout implements Resettable, Updatable<Affiliation>, SubmitNotifier, AbortNotifier {

  final public AppViewModel sharedViewModel
  private final UpdateAffiliationController controller
  private final ResourcesService<life.qbic.datamodel.dtos.business.Affiliation> affiliationResourcesService
  private Affiliation outdatedAffiliation = null

  private List<SubmitListener> submitListeners = new ArrayList<>()
  private List<AbortListener> abortListeners = new ArrayList<>()

  private Button abortButton
  private Button submitButton
  private AffiliationUserInput affiliationFormView
  private String unexpectedErrorMessage = "An unexpected error occurred. We apologize for any inconveniences. Please inform us via email to $Constants.QBIC_HELPDESK_EMAIL"

  UpdateAffiliationView(AppViewModel sharedViewModel, UpdateAffiliationController controller, ResourcesService<life.qbic.datamodel.dtos.business.Affiliation> affiliationResourcesService) {
    this.sharedViewModel = sharedViewModel
    this.controller = controller
    this.affiliationResourcesService = affiliationResourcesService
    this.affiliationFormView = new AffiliationUserInput(affiliationResourcesService)

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

  @Override
  void addSubmitListener(SubmitListener submitListener) {
    submitListeners.add(submitListener)
  }

  @Override
  void addAbortListener(AbortListener abortListener) {
    abortListeners.add(abortListener)
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
    HorizontalLayout buttonRow = new HorizontalLayout(buttonLayout)
    buttonRow.setComponentAlignment(buttonLayout, Alignment.MIDDLE_RIGHT)
    buttonRow.setWidthFull()
    buttonRow.setSpacing(false)
    buttonRow.setMargin(false)

    this.addComponents(label, affiliationFormView, buttonRow)
    this.setMargin(false)
  }

  private void registerListeners() {
    submitButton.addClickListener(withHandledException(it -> onSubmit()))
    abortButton.addClickListener(withHandledException(it -> onAbort()))
    affiliationFormView.addChangeListener(it -> submitButton.setEnabled(affiliationFormView.isValid() && hasDataChanged()))
  }

  private boolean hasDataChanged() {
    affiliationFormView.get() != outdatedAffiliation
  }

  private void onSubmit() {
    Affiliation affiliation = affiliationFormView.get()
    affiliation.setId(outdatedAffiliation.getId())
    if (isAffiliationCategoryChanged(affiliation)) {
      ConfirmAffiliationChangeWindow confirmAffiliationChangeWindow = new ConfirmAffiliationChangeWindow(outdatedAffiliation.getCategory().getLabel(), affiliation.getCategory().getLabel())
      this.getUI().addWindow(confirmAffiliationChangeWindow)
      confirmAffiliationChangeWindow.addCloseListener(it -> {
        if (confirmAffiliationChangeWindow.wasConfirmed()) {
          triggerAffiliationUpdate(affiliation)
        }
      })
    } else {
      triggerAffiliationUpdate(affiliation)
    }
  }

  private void triggerAffiliationUpdate(Affiliation affiliation) {
    this.controller.updateAffiliation(affiliation)
    fireUpdateSubmitted()
  }

  private void onAbort() {
    reset()
    fireUpdateAborted()
  }

  private void fireUpdateSubmitted() {
    submitListeners.forEach(SubmitListener::onSubmit)
  }

  private void fireUpdateAborted() {
    abortListeners.forEach(AbortListener::onAbort)
  }

  private boolean isAffiliationCategoryChanged(Affiliation updatedAffiliation) {
    return !updatedAffiliation.getCategory().equals(outdatedAffiliation.getCategory())
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

  /**
   * Modal window which prompts the user to confirm that the affiliation category change was intended
   * @param Affiliation affiliation the to be updated affiliation for which the category was changed
   */

  private class ConfirmAffiliationChangeWindow extends Window {
    private Button cancelButton
    private Button confirmButton
    private final String oldCategory
    private final String newCategory
    private boolean wasConfirmed = false

    ConfirmAffiliationChangeWindow(String oldCategory, String newCategory) {
      this.oldCategory = oldCategory
      this.newCategory = newCategory
      generateAffiliationChangeWindow()
    }

    private void generateAffiliationChangeWindow() {
      this.setCaption("<b>Changes can affect existing offers!</b>")
      this.setCaptionAsHtml(true)
      this.setIcon(VaadinIcons.WARNING)
      this.setContent(generateWindowContent())
      setupWindowStyle()
      registerListeners()
    }

    private VerticalLayout generateWindowContent() {
      VerticalLayout windowContent = new VerticalLayout()
      windowContent.addComponents(generateText(), generateButtonRow())
      return windowContent
    }

    private Label generateText() {
      Label windowText = new Label("Do you want to change the affiliation category from <b>$oldCategory</b> to <b>$newCategory</b> ?")
      windowText.setContentMode(ContentMode.HTML)
      return windowText
    }

    private void setupButtons() {
      this.cancelButton = new Button("Cancel")
      this.cancelButton.setIcon(VaadinIcons.CLOSE_CIRCLE)
      this.confirmButton = new Button("Update Affiliation")
      this.confirmButton.setIcon(VaadinIcons.CHECK)
      this.confirmButton.addStyleName(ValoTheme.BUTTON_PRIMARY)
    }

    private HorizontalLayout generateButtonRow() {
      setupButtons()
      HorizontalLayout buttonLayout = new HorizontalLayout(cancelButton, confirmButton)
      HorizontalLayout buttonRow = new HorizontalLayout()
      buttonRow.setDefaultComponentAlignment(Alignment.BOTTOM_CENTER)
      buttonRow.addComponent(buttonLayout)
      buttonRow.setWidthFull()
      buttonRow.setSpacing(false)
      buttonRow.setMargin(false)
      return buttonRow
    }

    private void setupWindowStyle() {
      this.setWidthUndefined()
      this.center()
      this.setClosable(false)
      this.setModal(true)
      this.setResizable(false)
    }

    private void registerListeners() {
      this.confirmButton.addClickListener(withHandledException(it -> onConfirmation()))
      this.cancelButton.addClickListener(withHandledException(it -> onCancellation()))
    }

    private void onConfirmation() {
      wasConfirmed = true
      this.close()
    }

    private void onCancellation() {
      wasConfirmed = false
      this.close()
    }

    boolean wasConfirmed() {
      return wasConfirmed
    }
  }
}
