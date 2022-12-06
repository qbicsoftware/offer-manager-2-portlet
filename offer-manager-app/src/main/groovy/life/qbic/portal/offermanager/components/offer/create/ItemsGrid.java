package life.qbic.portal.offermanager.components.offer.create;

import com.vaadin.data.Binder;
import com.vaadin.data.Binder.Binding;
import com.vaadin.data.Validator;
import com.vaadin.shared.ui.grid.HeightMode;
import com.vaadin.ui.Grid;
import com.vaadin.ui.TextField;
import com.vaadin.ui.components.grid.GridRowDragger;
import com.vaadin.ui.renderers.NumberRenderer;
import groovy.util.ObservableList;
import java.util.Objects;
import life.qbic.business.offers.Currency;
import life.qbic.portal.offermanager.components.GridUtils;
import life.qbic.portal.offermanager.components.ValidatorCombination;

/**
 * <b>short description</b>
 *
 * <p>detailed description</p>
 *
 * @since <version tag>
 */
public class ItemsGrid extends Grid<ProductItemViewModel> {


  public ItemsGrid(CreateOfferViewModel createOfferViewModel) {
    TextField editorComponent = new TextField();
    //We want to refresh the dataprovider as soon as the user focus outside the editor component
    editorComponent.addBlurListener(it -> {
      //We need to explicitly cancel the focus on the editor component since vaadin only cancels it if you click on product(
      this.getEditor().cancel();
      this.getDataProvider().refreshAll();
    });
    Binder<ProductItemViewModel> binder = getEditor().getBinder();
    ValidatorCombination<String> validatorCombination = new ValidatorCombination<>();
    validatorCombination.addValidator(
        Validator.from(AmountInputPattern.PARTIAL::test, "Please provide a decimal input"));
    Binding<ProductItemViewModel, String> binding = binder.forField(editorComponent)
        .withValidator(validatorCombination)
        .withNullRepresentation(editorComponent.getEmptyValue())
        .bind(
            (model) -> String.valueOf(model.getQuantity()),
            (model, value) -> {
              model.setQuantity(Double.parseDouble(value));
              createOfferViewModel.updateItem(model);
            });
    getEditor().setEnabled(true);
    getEditor().setBuffered(false);
    this.addColumn(ProductItemViewModel::getQuantity)
        .setEditorBinding(binding)
        .setEditable(true)
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
