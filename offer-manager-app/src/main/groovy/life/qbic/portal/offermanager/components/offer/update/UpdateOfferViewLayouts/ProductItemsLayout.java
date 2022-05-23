package life.qbic.portal.offermanager.components.offer.update.UpdateOfferViewLayouts;

import com.vaadin.ui.Button;
import com.vaadin.ui.Grid;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.VerticalLayout;
import life.qbic.business.products.ProductItem;

/**
 * This class generates a Layout in which the user can see the productItems of the offer selected in
 * the UpdateOfferView
 * <p>
 * ProductItemsLayout will be integrated into the UpdateOfferView and will provide an overview of
 * all the productItems associated with an offer. Furthermore, it will provide the ability to inline
 * edit and delete productItems directly in the OfferUpdateView
 *
 * @since 1.6.0
 */
public class ProductItemsLayout extends VerticalLayout {

  private Button updateItemButton;
  private Button removeItemButton;

  HorizontalLayout buttonBarLayout;
  Grid<ProductItem> productItemsGrid;

  public ProductItemsLayout() {
    initLayout();
    styleLayout();
  }

  private void initLayout() {
    productItemsGrid = new Grid<ProductItem>();
    buttonBarLayout = new HorizontalLayout();
    updateItemButton = new Button("Update Items");
    removeItemButton = new Button("Remove Items");
    buttonBarLayout.addComponents(updateItemButton, removeItemButton);
    this.addComponents(buttonBarLayout, productItemsGrid);
  }

  private void styleLayout() {
    productItemsGrid.setWidthFull();
    this.setSizeFull();
  }
}
