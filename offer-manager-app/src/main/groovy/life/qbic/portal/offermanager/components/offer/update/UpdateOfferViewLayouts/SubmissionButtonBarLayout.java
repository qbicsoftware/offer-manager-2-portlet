package life.qbic.portal.offermanager.components.offer.update.UpdateOfferViewLayouts;

import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;

/**
 * <class short description - One Line!>
 * <p>
 * <More detailed description - When to use, what it solves, etc.>
 *
 * @since <version tag>
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

  }
}
