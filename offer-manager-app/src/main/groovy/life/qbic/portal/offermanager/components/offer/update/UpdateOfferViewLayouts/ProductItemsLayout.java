package life.qbic.portal.offermanager.components.offer.update.UpdateOfferViewLayouts;

import com.vaadin.ui.Button;
import com.vaadin.ui.Grid;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.VerticalLayout;
import life.qbic.business.products.ProductItem;

/**
 * <class short description - One Line!>
 * <p>
 * <More detailed description - When to use, what it solves, etc.>
 *
 * @since <version tag>
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
  }
}
