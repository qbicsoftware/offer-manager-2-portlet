package life.qbic.portal.offermanager.components.offer.update.UpdateOfferViewLayouts;

import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;

/**
 * <class short description - One Line!>
 * <p>
 * <More detailed description - When to use, what it solves, etc.>
 *
 * @since <version tag>
 */
public class PricingLayout extends HorizontalLayout {

  Label netPrice;
  Label overheadPrice;
  Label taxesPrice;
  Label totalPrice;

  public PricingLayout() {
    initLayout();
    styleLayout();
  }

  private void initLayout() {
    netPrice = new Label("Net Sum");
    netPrice.setCaption("Net Price");
    overheadPrice = new Label("Overhead Sum");
    overheadPrice.setCaption("Overheads");
    taxesPrice = new Label("Taxes Sum");
    taxesPrice.setCaption("Taxes");
    totalPrice = new Label("Total Sum");
    totalPrice.setCaption("Total Costs");
    this.addComponents(netPrice, overheadPrice, taxesPrice, totalPrice);
  }

  private void styleLayout() {

  }

}
