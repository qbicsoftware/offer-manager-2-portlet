package life.qbic.portal.offermanager.components.offer.update.UpdateOfferViewLayouts;

import com.vaadin.ui.Button;
import com.vaadin.ui.Grid;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.VerticalLayout;
import life.qbic.portal.offermanager.components.offer.create.ProductItemViewModel;

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

  public Button updateItemButton;
  public Button removeItemButton;

  private HorizontalLayout buttonBarLayout;
  public Grid<ProductItemViewModel> productItemsGrid;

  public ProductItemsLayout() {
    initLayout();
    fillGrid();
    styleLayout();
  }

  private void initLayout() {
    productItemsGrid = new Grid<>();
    buttonBarLayout = new HorizontalLayout();
    updateItemButton = new Button("Update Items");
    removeItemButton = new Button("Remove Items");
    buttonBarLayout.addComponents(updateItemButton, removeItemButton);
    this.addComponents(buttonBarLayout, productItemsGrid);
  }

  private void fillGrid() {
    productItemsGrid.addColumn(
            productItemViewModel -> productItemViewModel.getProduct().getProductId()).setCaption("ID")
        .setId("ID");
    productItemsGrid.addColumn(
            productItemViewModel -> productItemViewModel.getProduct().getProductName())
        .setCaption("Name")
        .setId("Name");
    productItemsGrid.addColumn(
            productItemViewModel -> productItemViewModel.getProduct().getDescription())
        .setCaption("Description")
        .setId("Description");
    productItemsGrid.addColumn(ProductItemViewModel::getQuantity).setCaption("Quantity");
    //ToDo Which Unit Price is necessary
    productItemsGrid.addColumn(
            productItemViewModel -> productItemViewModel.getProduct().getUnitPrice())
        .setCaption("Unit Price")
        .setId("Unit Price");
    productItemsGrid.addColumn(
            productItemViewModel -> productItemViewModel.getProduct().getUnit().toString())
        .setCaption("Unit")
        .setId("Unit");
    productItemsGrid.addColumn(
            productItemViewModel -> productItemViewModel.getProduct().getServiceProvider().toString())
        .setCaption("Facility")
        .setId("Facility");
  }

  private void styleLayout() {
    productItemsGrid.setWidthFull();
    this.setSizeFull();
  }
}
