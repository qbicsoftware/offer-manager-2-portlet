package life.qbic.portal.offermanager.components.product.create

import com.vaadin.data.ValidationResult
import com.vaadin.data.Validator
import com.vaadin.data.ValueContext
import com.vaadin.data.validator.RegexpValidator
import com.vaadin.icons.VaadinIcons
import com.vaadin.server.UserError
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

/**
 * <h1>This view serves the user to create a new service product</h1>
 * <br>
 * <p>The view contains several text input fields and combo boxes in order to fully describe the new service products</p>
 *
 * @since 1.0.0
 *
*/
class CreateProductView extends HorizontalLayout{

    private final CreateProductViewModel createProductViewModel

    TextField productNameField
    TextField productDescriptionField
    TextField productUnitPriceField

    ComboBox<String> productUnitComboBox
    ComboBox<String> productCategoriesComboBox

    Button createProductButton
    Button abortButton

    CreateProductView(CreateProductViewModel createProductViewModel){

        this.createProductViewModel = createProductViewModel
        initTextFields()
        initComboBoxes()
        initButtons()
        initLayout()
        bindViewModel()
        setupFieldValidators()
    }

    private void initLayout(){
        Label label = new Label("Create Service Product")
        label.setStyleName(ValoTheme.LABEL_HUGE)
        this.addComponent(label)

        //add textfields and boxes
        HorizontalLayout sharedLayout = new HorizontalLayout(productUnitPriceField,productUnitComboBox)
        sharedLayout.setWidthFull()
        HorizontalLayout buttons = new HorizontalLayout(abortButton,createProductButton)

        VerticalLayout sideLayout = new VerticalLayout(label,productNameField,productDescriptionField,sharedLayout,productCategoriesComboBox,buttons)
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

        productCategoriesComboBox = new ComboBox<>("Product Category")
        productCategoriesComboBox.setRequiredIndicatorVisible(true)
        productCategoriesComboBox.setPlaceholder("Select Product Category")
        productCategoriesComboBox.setEmptySelectionAllowed(false)
        productCategoriesComboBox.setItems(Arrays.asList(ProductCategory.values()) as List<String>)
        productCategoriesComboBox.setWidthFull()
    }

    private void initButtons(){
        abortButton = new Button("Abort", VaadinIcons.CLOSE)
        createProductButton = new Button("Create", VaadinIcons.CHECK)
        createProductButton.setEnabled(allValuesValid())
    }

    private void bindViewModel(){
        //bind all textfields
        this.productNameField.addValueChangeListener({this.createProductViewModel.productName = it.value })

        createProductViewModel.addPropertyChangeListener("productName", {
            String newValue = it.newValue as String
            productNameField.value = newValue ?: productNameField.emptyValue
        })

        this.productDescriptionField.addValueChangeListener({this.createProductViewModel.productDescription = it.value })

        createProductViewModel.addPropertyChangeListener("productDescription", {
            String newValue = it.newValue as String
            productDescriptionField.value = newValue ?: productDescriptionField.emptyValue
        })

        this.productUnitPriceField.addValueChangeListener({this.createProductViewModel.productUnitPrice = it.value})

        createProductViewModel.addPropertyChangeListener("productUnitPrice", {
            String newValue = it.newValue as String
            productUnitPriceField.value = newValue ?: productUnitPriceField.emptyValue
        })

        //bind combo boxes
        createProductViewModel.addPropertyChangeListener("productUnit", {
            ProductUnit newValue = it.newValue as ProductUnit
            if (newValue) {
                productUnitComboBox.value = newValue
            } else {
                productUnitComboBox.value = productUnitComboBox.emptyValue
            }
        })
        productUnitComboBox.addSelectionListener({
            createProductViewModel.setProductUnit(it.value as ProductUnit)
        })

        createProductViewModel.addPropertyChangeListener("productCategories", {
            ProductCategory newValue = it.newValue as ProductCategory
            if (newValue) {
                productCategoriesComboBox.value = newValue
            } else {
                productCategoriesComboBox.value = productCategoriesComboBox.emptyValue
            }
        })
        productCategoriesComboBox.addSelectionListener({
            createProductViewModel.setProductCategories(it.value as ProductCategory)
        })

        /*
       We listen to the valid properties. whenever the presenter resets values in the viewmodel
       and resets the valid properties the component error on the respective component is removed
       */
        createProductViewModel.addPropertyChangeListener({
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
                        productCategoriesComboBox.componentError = null
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
        Validator<String> numberValidator = new RegexpValidator("This is not a number!", "[-]?[0-9]*\\.?,?[0-9]+")
        Validator<? extends Object> selectionValidator = Validator.from({o -> o != null}, "Please make a selection.")

        //Add Listeners to all Fields in the Form layout
        this.productNameField.addValueChangeListener({ event ->
            ValidationResult result = nameValidator.apply(event.getValue(), new ValueContext(this.productNameField))
            if (result.isError()) {
                createProductViewModel.productNameValid = false
                UserError error = new UserError(result.getErrorMessage())
                productNameField.setComponentError(error)
            } else {
                createProductViewModel.productNameValid = true
            }
        })
        this.productDescriptionField.addValueChangeListener({ event ->
            ValidationResult result = nameValidator.apply(event.getValue(), new ValueContext(this.productDescriptionField))
            if (result.isError()) {
                createProductViewModel.productDescriptionValid = false
                UserError error = new UserError(result.getErrorMessage())
                productDescriptionField.setComponentError(error)
            } else {
                createProductViewModel.productDescriptionValid = true
            }
        })
        this.productUnitPriceField.addValueChangeListener({ event ->
            ValidationResult result = numberValidator.apply(event.getValue(), new ValueContext(this.productUnitPriceField))
            if (result.isError()) {
                createProductViewModel.productUnitPriceValid = false
                UserError error = new UserError(result.getErrorMessage())
                productUnitPriceField.setComponentError(error)
            } else {
                createProductViewModel.productUnitPriceValid = true
            }
        })
        this.productUnitComboBox.addSelectionListener({selection ->
            ValidationResult result = selectionValidator.apply(selection.getValue(), new ValueContext(this.productUnitComboBox))
            if (result.isError()) {
                createProductViewModel.productUnitValid = false
                UserError error = new UserError(result.getErrorMessage())
                productUnitComboBox.setComponentError(error)
            } else {
                createProductViewModel.productUnitValid = true
            }
        })
        this.productCategoriesComboBox.addSelectionListener({selection ->
            ValidationResult result = selectionValidator.apply(selection.getValue(), new ValueContext(this.productCategoriesComboBox))
            if (result.isError()) {
                createProductViewModel.productCategoriesValid = false
                UserError error = new UserError(result.getErrorMessage())
                productCategoriesComboBox.setComponentError(error)
            } else {
                createProductViewModel.productCategoriesValid = true
            }
        })
    }
    /**
     * This is used to indicate whether all fields of this view are filled correctly.
     * It relies on the separate fields for validation.
     * @return
     */
    private boolean allValuesValid() {
        return createProductViewModel.productNameValid \
            && createProductViewModel.productDescriptionValid \
            && createProductViewModel.productUnitValid \
            && createProductViewModel.productUnitPriceValid \
            && createProductViewModel.productCategoriesValid
    }

}
