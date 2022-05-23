package life.qbic.portal.offermanager.components.offer.update.UpdateOfferViewLayouts;

import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;

/**
 * This class generates a Layout in which the user can see the pricing of the offer selected in the
 * UpdateOfferView
 * <p>
 * PricingLayout will be integrated into the UpdateOfferView and will list the net price, overhead
 * price, price after taxes and the total Price of an offer to be updated
 *
 * @since 1.6.0
 */
public class PricingLayout extends HorizontalLayout {

  public Label netPrice;
  public Label overheadPrice;
  public Label taxesPrice;
  public Label totalPrice;

  public PricingLayout() {
    initLayout();
    styleLayout();
  }

  private void initLayout() {
    netPrice = new Label("");
    netPrice.setCaption("Net Price");
    overheadPrice = new Label("");
    overheadPrice.setCaption("Overheads");
    taxesPrice = new Label("");
    taxesPrice.setCaption("Taxes");
    totalPrice = new Label("");
    totalPrice.setCaption("Total Costs");
    this.addComponents(netPrice, overheadPrice, taxesPrice, totalPrice);
  }

  private void styleLayout() {

  }

}
