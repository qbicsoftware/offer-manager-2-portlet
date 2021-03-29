package life.qbic.portal.offermanager.components.product.create

import com.vaadin.data.ValidationResult
import com.vaadin.data.Validator
import com.vaadin.data.ValueContext
import com.vaadin.data.validator.RegexpValidator
import com.vaadin.icons.VaadinIcons
import com.vaadin.server.UserError
import com.vaadin.shared.Registration
import com.vaadin.ui.Alignment
import com.vaadin.ui.Button
import com.vaadin.ui.ComboBox
import com.vaadin.ui.HorizontalLayout
import com.vaadin.ui.Label
import com.vaadin.ui.TextField
import com.vaadin.ui.VerticalLayout
import com.vaadin.ui.themes.ValoTheme
import life.qbic.datamodel.dtos.business.ProductCategory
import life.qbic.datamodel.dtos.business.services.ProductUnit
import life.qbic.portal.offermanager.components.product.MaintainProductsController

/**
 * <h1>This view serves the user to create a new service product</h1>
 * <br>
 * <p>The view contains several text input fields and combo boxes in order to fully describe the new service products</p>
 *
 * @since 1.0.0
 *
*/
class CreateProductView extends HorizontalLayout{

    protected final CreateProductViewModel viewModel
    protected final MaintainProductsController controller

    TextField productNameField
    TextField productDescriptionField
    TextField productUnitPriceField

    ComboBox<String> productUnitComboBox
    ComboBox<String> productCategoryComboBox

    Button createProductButton
    Button abortButton

    Registration createProductButtonRegistration

    Label titleLabel

    CreateProductView(CreateProductViewModel createProductViewModel, MaintainProductsController controller){
        this.controller = controller
        this.viewModel = createProductViewModel

        initTextFields()
        initComboBoxes()
        initButtons()
        initLayout()
        bindViewModel()
        setupFieldValidators()
        setupListeners()
    }

    private void initLayout(){
        label = new Label("Create Service Product")
        label.setStyleName(ValoTheme.LABEL_HUGE)
        this.addComponent(label)

        //add textfields and boxes
        HorizontalLayout sharedLayout = new HorizontalLayout(productUnitPriceField,productUnitComboBox)
        sharedLayout.setWidthFull()
        HorizontalLayout buttons = new HorizontalLayout(abortButton,createProductButton)

        VerticalLayout sideLayout = new VerticalLayout(label,productNameField,productDescriptionField,sharedLayout,productCategoryComboBox,buttons)
        sideLayout.setSizeFull()
        sideLayout.setComponentAlignment(buttons, Alignment.BOTTOM_RIGHT)

        this.setMargin(false)
        this.setSpacing(false)
        this.setSizeFull()

        this.addComponents(sideLayout)
    }

    private void initTextFields(){
        productNameField = new TextField("Product Name")
        productNameField.setPlaceholder("Product Name")
        productNameField.setRequiredIndicatorVisible(true)
        productNameField.setWidthFull()

        productDescriptionField = new TextField("Product Description")
        productDescriptionField.setPlaceholder("Product Description")
        productDescriptionField.setRequiredIndicatorVisible(true)
        productDescriptionField.setWidthFull()

        productUnitPriceField = new TextField("Product Unit Price")
        productUnitPriceField.setPlaceholder("00.00")
        productUnitPriceField.setRequiredIndicatorVisible(true)
        productUnitPriceField.setWidthFull()
    }

    private void initComboBoxes(){
        productUnitComboBox = new ComboBox<>("Product Unit")
        productUnitComboBox.setRequiredIndicatorVisible(true)
        productUnitComboBox.setPlaceholder("Select Product Unit")
        productUnitComboBox.setEmptySelectionAllowed(false)
        productUnitComboBox.setItems(Arrays.asList(ProductUnit.values()) as List<String>)
        productUnitComboBox.setWidthFull()

        productCategoryComboBox = new ComboBox<>("Product Category")
        productCategoryComboBox.setRequiredIndicatorVisible(true)
        productCategoryComboBox.setPlaceholder("Select Product Category")
        productCategoryComboBox.setEmptySelectionAllowed(false)
        productCategoryComboBox.setItems(Arrays.asList(ProductCategory.values()) as List<String>)
        productCategoryComboBox.setWidthFull()
    }

    private void initButtons(){
        abortButton = new Button("Abort", VaadinIcons.CLOSE)
        createProductButton = new Button("Create", VaadinIcons.CHECK)
        createProductButton.setEnabled(allValuesValid())
    }

    private void bindViewModel(){
        //bind all textfields
        this.productNameField.addValueChangeListener({this.viewModel.productName = it.value })

        viewModel.addPropertyChangeListener("productName", {
            String newValue = it.newValue as String
            productNameField.value = newValue ?: productNameField.emptyValue
        })

        this.productDescriptionField.addValueChangeListener({this.viewModel.productDescription = it.value })

        viewModel.addPropertyChangeListener("productDescription", {
            String newValue = it.newValue as String
            productDescriptionField.value = newValue ?: productDescriptionField.emptyValue
        })

        this.productUnitPriceField.addValueChangeListener({this.viewModel.productUnitPrice = it.value})

        viewModel.addPropertyChangeListener("productUnitPrice", {
            String newValue = it.newValue as String
            productUnitPriceField.value = newValue ?: productUnitPriceField.emptyValue
        })

        //bind combo boxes
        viewModel.addPropertyChangeListener("productUnit", {
            ProductUnit newValue = it.newValue as ProductUnit
            if (newValue) {
                productUnitComboBox.value = newValue
            } else {
                productUnitComboBox.value = productUnitComboBox.emptyValue
            }
        })
        productUnitComboBox.addSelectionListener({
            viewModel.setProductUnit(it.value as ProductUnit)
        })

        viewModel.addPropertyChangeListener("productCategory", {
            ProductCategory newValue = it.newValue as ProductCategory
            if (newValue) {
                productCategoryComboBox.value = newValue
            } else {
                productCategoryComboBox.value = productCategoryComboBox.emptyValue
            }
        })
        productCategoryComboBox.addSelectionListener({
            viewModel.setProductCategory(it.value as ProductCategory)
        })

        /*
       We listen to the valid properties. whenever the presenter resets values in the viewmodel
       and resets the valid properties the component error on the respective component is removed
       */
        viewModel.addPropertyChangeListener({
            switch (it.propertyName) {
                case "productNameValid":
                    if (it.newValue || it.newValue == null) {
                        productNameField.componentError = null
                    }
                    break
                case "productDescriptionValid":
                    if (it.newValue || it.newValue == null) {
                        productDescriptionField.componentError = null
                    }
                    break
                case "productUnitPriceValid":
                    if (it.newValue || it.newValue == null) {
                        productUnitPriceField.componentError = null
                    }
                    break
                case "productUnitValid":
                    if (it.newValue || it.newValue == null) {
                        productUnitComboBox.componentError = null
                    }
                    break
                case "productCategoryValid":
                    if (it.newValue || it.newValue == null) {
                        productCategoryComboBox.componentError = null
                    }
                    break
                default:
                    break
            }
            createProductButton.enabled = allValuesValid()
        })
    }

    /**
     * This method adds validation to the fields of this view
     */
    private void setupFieldValidators() {

        Validator<String> nameValidator =  Validator.from({String value -> (value && !value.trim().empty)}, "Please provide a valid name.")
        Validator<String> numberValidator = new RegexpValidator("This is not a number!", "[-]?[0-9]*\\.?[0-9]+")
        Validator<? extends Object> selectionValidator = Validator.from({o -> o != null}, "Please make a selection.")

        //Add Listeners to all Fields in the Form layout
        this.productNameField.addValueChangeListener({ event ->
            ValidationResult result = nameValidator.apply(event.getValue(), new ValueContext(this.productNameField))
            if (result.isError()) {
                viewModel.productNameValid = false
                UserError error = new UserError(result.getErrorMessage())
                productNameField.setComponentError(error)
            } else {
                viewModel.productNameValid = true
            }
        })
        this.productDescriptionField.addValueChangeListener({ event ->
            ValidationResult result = nameValidator.apply(event.getValue(), new ValueContext(this.productDescriptionField))
            if (result.isError()) {
                viewModel.productDescriptionValid = false
                UserError error = new UserError(result.getErrorMessage())
                productDescriptionField.setComponentError(error)
            } else {
                viewModel.productDescriptionValid = true
            }
        })
        this.productUnitPriceField.addValueChangeListener({ event ->
            ValidationResult result = numberValidator.apply(event.getValue(), new ValueContext(this.productUnitPriceField))
            if (result.isError()) {
                viewModel.productUnitPriceValid = false
                UserError error = new UserError(result.getErrorMessage())
                productUnitPriceField.setComponentError(error)
            } else {
                viewModel.productUnitPriceValid = true
            }
        })
        this.productUnitComboBox.addSelectionListener({selection ->
            ValidationResult result = selectionValidator.apply(selection.getValue(), new ValueContext(this.productUnitComboBox))
            if (result.isError()) {
                viewModel.productUnitValid = false
                UserError error = new UserError(result.getErrorMessage())
                productUnitComboBox.setComponentError(error)
            } else {
                viewModel.productUnitValid = true
            }
        })
        this.productCategoryComboBox.addSelectionListener({ selection ->
            ValidationResult result = selectionValidator.apply(selection.getValue(), new ValueContext(this.productCategoryComboBox))
            if (result.isError()) {
                viewModel.productCategoryValid = false
                UserError error = new UserError(result.getErrorMessage())
                productCategoryComboBox.setComponentError(error)
            } else {
                viewModel.productCategoryValid = true
            }
        })
    }
    /**
     * This is used to indicate whether all fields of this view are filled correctly.
     * It relies on the separate fields for validation.
     * @return
     */
    protected boolean allValuesValid() {
        return viewModel.productNameValid \
            && viewModel.productDescriptionValid \
            && viewModel.productUnitValid \
            && viewModel.productUnitPriceValid \
            && viewModel.productCategoryValid
    }

    private void setupListeners(){

        abortButton.addClickListener({clearAllFields() })
        registration = this.createProductButton.addClickListener({
            controller.createNewProduct(viewModel.productCategory, viewModel.productDescription,viewModel.productName, Double.parseDouble(viewModel.productUnitPrice),viewModel.productUnit)
        })

    }

    /**
     *  Clears User Input from all fields in the Create Products View and reset validation status of all Fields
     */
    protected void clearAllFields() {

        productNameField.clear()
        productDescriptionField.clear()
        productUnitPriceField.clear()
        productCategoryComboBox.selectedItem = productCategoryComboBox.clear()
        productUnitComboBox.selectedItem = productUnitComboBox.clear()

        viewModel.productNameValid = null
        viewModel.productDescriptionValid = null
        viewModel.productUnitPriceValid = null
        viewModel.productCategoryValid = null
        viewModel.productUnitValid = null
    }

}
