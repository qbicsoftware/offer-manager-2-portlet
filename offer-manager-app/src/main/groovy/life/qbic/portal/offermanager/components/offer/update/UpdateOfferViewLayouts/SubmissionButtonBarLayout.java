package life.qbic.portal.offermanager.components.offer.update.UpdateOfferViewLayouts;

import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;

/**
 * This class generates a Layout in which the user can cancel or save changes made to an offer in
 * the UpdateOfferView
 * <p>
 * SelectCustomerLayout will be integrated into the UpdateOfferView and will provide the
 * functionality to discard or save the changes made to an offer in the UpdateOfferView
 *
 * @since 1.6.0
 */
public class SubmissionButtonBarLayout extends HorizontalLayout {

  private Button cancelOfferUpdateButton;
  private Button saveOfferUpdateButton;


  public SubmissionButtonBarLayout() {
    initLayout();
    styleLayout();
  }

  private void initLayout() {
    cancelOfferUpdateButton = new Button("Cancel");
    saveOfferUpdateButton = new Button("Save");
    this.addComponents(cancelOfferUpdateButton, saveOfferUpdateButton);
  }

  private void styleLayout() {
    this.setMargin(false);
    this.setSpacing(true);
  }
}
