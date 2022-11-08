package life.qbic.portal.offermanager.components.offer.create;

import com.vaadin.data.Binder;
import com.vaadin.data.Binder.Binding;
import com.vaadin.data.ValidationResult;
import com.vaadin.data.Validator;
import com.vaadin.data.ValueContext;
import com.vaadin.shared.ui.grid.HeightMode;
import com.vaadin.ui.Grid;
import com.vaadin.ui.components.grid.GridRowDragger;
import com.vaadin.ui.renderers.NumberRenderer;
import groovy.util.ObservableList;
import java.util.Objects;
import life.qbic.business.offers.Currency;
import life.qbic.portal.offermanager.components.GridUtils;
import life.qbic.portal.offermanager.components.ValidatorCombination;
import life.qbic.portal.offermanager.components.offer.create.AmountEditFactory.AmountEdit;
import life.qbic.portal.offermanager.components.offer.create.AmountEditFactory.InputPattern;

/**
 * <b>short description</b>
 *
 * <p>detailed description</p>
 *
 * @since <version tag>
 */
public class ItemsGrid extends Grid<ProductItemViewModel> {


  public ItemsGrid() {

    AmountEdit editorComponent = new AmountEdit();
    Binder<ProductItemViewModel> binder = getEditor().getBinder();
    ValidatorCombination<String> validatorCombination = new ValidatorCombination<>();
    validatorCombination.addValidator(
        Validator.from(InputPattern.PARTIAL::test, "Please provide a decimal input"));
    Binding<ProductItemViewModel, String> quantityBinding = binder
        .bind(editorComponent,
            model -> String.valueOf(model.getQuantity()),
            (model, value) -> {
              ValidationResult validationResult = validatorCombination.apply(value,
                  new ValueContext());
              if (!validationResult.isError()) {
                model.setQuantity(Double.parseDouble(value));
                throw new IllegalArgumentException("Blubb");
              } else {
                throw new IllegalArgumentException("Bla");
              }
            });
    getEditor().setEnabled(true);

    this.addColumn(ProductItemViewModel::getQuantity)
        .setEditorBinding(quantityBinding)
        .setCaption("Quantity")
        .setId("Quantity");
    this.addColumn(it -> it.getProduct().getProductId())
        .setCaption("Product Id")
        .setId("ProductId");
    this.addColumn(it -> it.getProduct().getProductName())
        .setCaption("Product Name")
        .setId("ProductName");
    Grid.Column<ProductItemViewModel, String> descriptionColumn = this.addColumn(it ->
            it.getProduct().getDescription())
        .setDescriptionGenerator(it -> it.getProduct().getDescription())
        .setCaption("Product Description")
        .setId("ProductDescription");
    this.addColumn(it -> it.getProduct().getInternalUnitPrice(), new NumberRenderer(Currency
            .getFormatterWithSymbol()))
        .setCaption("Internal Unit Price")
        .setId("InternalUnitPrice");
    this.addColumn(it -> it.getProduct().getExternalUnitPrice(), new NumberRenderer(Currency
            .getFormatterWithSymbol()))
        .setCaption("External Unit Price")
        .setId("ExternalUnitPrice");
    this.addColumn(it -> it.getProduct().getServiceProvider().getFullName())
        .setCaption("Facility")
        .setId("Facility");
    this.addColumn(it -> it.getProduct().getUnit().getValue())
        .setCaption("Product Unit")
        .setId("ProductUnit");
    //specify size of this and layout

    this.setWidthFull();
    descriptionColumn.setWidth(GridUtils.DESCRIPTION_MAX_WIDTH);
    this.setHeightMode(HeightMode.ROW);
  }

  private GridRowDragger<ProductItemViewModel> gridRowDragger;

  public void setItems(ObservableList items) {
    super.setItems(items);
    items.addPropertyChangeListener(it -> {
      if (it instanceof ObservableList.ElementEvent) {
        this.getDataProvider().refreshAll();
      }
    });
  }

  public void enableDragAndDrop() {
    if (isDragEnabled()) {
      return;
    }
    getColumns().forEach(it -> it.setSortable(false));
    gridRowDragger = new GridRowDragger<>(this);
    this.setStyleGenerator(row -> "draggable-row-grab");
  }

  public boolean isDragEnabled() {
    return Objects.nonNull(gridRowDragger);
  }
}
