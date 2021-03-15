package life.qbic.portal.offermanager.components.product.create

import com.vaadin.data.ValidationResult
import com.vaadin.data.Validator
import com.vaadin.data.ValueContext
import com.vaadin.icons.VaadinIcons
import com.vaadin.server.UserError
import com.vaadin.ui.Button
import com.vaadin.ui.ComboBox
import com.vaadin.ui.HorizontalLayout
import com.vaadin.ui.Label
import com.vaadin.ui.TextField
import com.vaadin.ui.VerticalLayout
import com.vaadin.ui.themes.ValoTheme
import life.qbic.datamodel.dtos.business.ProductCategory
import life.qbic.datamodel.dtos.business.services.ProductUnit

/**
 * <h1>This view serves the user to create a new service product</h1>
 * <br>
 * <p>The view contains several text input fields and combo boxes in order to fully describe the new service products</p>
 *
 * @since 1.0.0
 *
*/
class CreateProductView extends HorizontalLayout{

    private final CreateProductViewModel viewModel

    TextField productName
    TextField productDescription
    TextField productUnitPrice

    ComboBox<String> productUnit
    ComboBox<String> productCategories

    Button createProduct
    Button abort

    CreateProductView(CreateProductViewModel viewModel){

        this.viewModel = viewModel

        initTextFields()
        initComboBoxes()
        initButtons()
        initLayout()
        //bindViewModel()
        //setupFieldValidator()
    }

    private void initLayout(){
        Label label = new Label("Create Service Product")
        label.setStyleName(ValoTheme.LABEL_HUGE)
        this.addComponent(label)

        //add textfields and boxes
        HorizontalLayout sharedLayout = new HorizontalLayout(productUnitPrice,productUnit)
        HorizontalLayout buttons = new HorizontalLayout(abort,createProduct)
        VerticalLayout sideLayout = new VerticalLayout(label,productName,productDescription,sharedLayout,productCategories,buttons)

        sideLayout.setSizeFull()

        this.addComponents(sideLayout)
    }

    private void initTextFields(){
        productName = new TextField("Product Name")
        productName.setPlaceholder("Product Name")
        productName.setRequiredIndicatorVisible(true)
        productName.setWidthFull()

        productDescription = new TextField("Product Description")
        productDescription.setPlaceholder("Product Description")
        productDescription.setRequiredIndicatorVisible(true)
        productDescription.setWidthFull()

        productUnitPrice = new TextField("Product Unit Price")
        productUnitPrice.setPlaceholder("00.00")
        productUnitPrice.setRequiredIndicatorVisible(true)
    }

    private void initComboBoxes(){
        productUnit = new ComboBox<>("Product Unit")
        productUnit.setRequiredIndicatorVisible(true)
        productUnit.setPlaceholder("Select Product Unit")
        productUnit.setEmptySelectionAllowed(false)
        productUnit.setItems(Arrays.asList(ProductUnit.values()) as List<String>)

        productCategories = new ComboBox<>("Product Category")
        productCategories.setRequiredIndicatorVisible(true)
        productCategories.setPlaceholder("Select Product Category")
        productCategories.setEmptySelectionAllowed(false)
        productCategories.setItems(Arrays.asList(ProductCategory.values()) as List<String>)
    }

    private void initButtons(){
        abort = new Button("Abort", VaadinIcons.CLOSE)
        createProduct = new Button("Create", VaadinIcons.CHECK)
    }

    private void bindViewModel(){
        //bind all textfields
        this.productName.addValueChangeListener({this.viewModel.productName = it.value })

        viewModel.addPropertyChangeListener("productName", {
            String newValue = it.newValue as String
            productName.value = newValue ?: productName.emptyValue
        })

        this.productDescription.addValueChangeListener({this.viewModel.productDescription = it.value })

        viewModel.addPropertyChangeListener("productDescription", {
            String newValue = it.newValue as String
            productDescription.value = newValue ?: productDescription.emptyValue
        })

        this.productUnitPrice.addValueChangeListener({
            if(it.value.isNumber()){
                this.viewModel.productUnitPrice = Double.parseDouble(it.value)
            }
            else{
                def label = new Label("Please enter a number")
                label.setStyleName(ValoTheme.LABEL_FAILURE)
                this.addComponent(label)
            }
        })

        viewModel.addPropertyChangeListener("productUnitPrice", {
            String newValue = it.newValue as String
            productUnitPrice.value = newValue ?: productUnitPrice.emptyValue
        })

        //bind combo boxes
        viewModel.addPropertyChangeListener("productUnit", {
            ProductUnit newValue = it.newValue as ProductUnit
            if (newValue) {
                productUnit.value = newValue
            } else {
                productUnit.value = productUnit.emptyValue
            }
        })

        viewModel.addPropertyChangeListener("productCategories", {
            ProductCategory newValue = it.newValue as ProductCategory
            if (newValue) {
                productCategories.value = newValue
            } else {
                productCategories.value = productCategories.emptyValue
            }
        })

    }

}
