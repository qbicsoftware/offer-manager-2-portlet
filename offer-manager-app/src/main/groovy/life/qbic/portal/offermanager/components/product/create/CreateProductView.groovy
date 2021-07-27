package life.qbic.portal.offermanager.components.product.create

import com.vaadin.data.ValidationResult
import com.vaadin.data.Validator
import com.vaadin.data.ValueContext
import com.vaadin.data.validator.RegexpValidator
import com.vaadin.icons.VaadinIcons
import com.vaadin.server.UserError
import com.vaadin.shared.Registration
import com.vaadin.ui.*
import com.vaadin.ui.themes.ValoTheme
import life.qbic.datamodel.dtos.business.ProductCategory
import life.qbic.datamodel.dtos.business.facilities.Facility
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
class CreateProductView extends VerticalLayout{

    protected final CreateProductViewModel viewModel
    protected final MaintainProductsController controller

    TextField productNameField
    TextField productDescriptionField
    TextField internalUnitPriceField
    TextField externalUnitPriceField

    ComboBox<String> productUnitComboBox
    ComboBox<String> productCategoryComboBox
    ComboBox<String> productFacilityComboBox
    Button abortButton

    Button createProductButton
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
        titleLabel = new Label("Create Service Product")
        titleLabel.setStyleName(ValoTheme.LABEL_HUGE)
        this.addComponent(titleLabel)

        //add textfields and boxes
        HorizontalLayout sharedLayout = new HorizontalLayout(internalUnitPriceField, externalUnitPriceField, productUnitComboBox)
        sharedLayout.setWidthFull()
        sharedLayout.setMargin(false)
        HorizontalLayout buttons = new HorizontalLayout(abortButton,createProductButton)

        VerticalLayout sideLayout = new VerticalLayout(titleLabel,productNameField,productDescriptionField,sharedLayout,productCategoryComboBox,buttons)
        sideLayout.setSizeFull()
        sideLayout.setMargin(false)
        sideLayout.setComponentAlignment(buttons, Alignment.BOTTOM_RIGHT)

        this.setMargin(false)
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

        internalUnitPriceField = new TextField("Internal Unit Price")
        internalUnitPriceField.setPlaceholder("00.00")
        internalUnitPriceField.setRequiredIndicatorVisible(true)
        internalUnitPriceField.setWidthFull()

        externalUnitPriceField = new TextField("External Unit Price")
        externalUnitPriceField.setPlaceholder("00.00")
        externalUnitPriceField.setRequiredIndicatorVisible(true)
        externalUnitPriceField.setWidthFull()
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

        productFacilityComboBox = new ComboBox<>("Facility")
        productFacilityComboBox.setRequiredIndicatorVisible(true)
        productFacilityComboBox.setPlaceholder("Select facility that provides the product")
        productFacilityComboBox.setEmptySelectionAllowed(false)
        productCategoryComboBox.setItems(Arrays.asList(Facility.values()) as List<String>)
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

        this.internalUnitPriceField.addValueChangeListener({this.viewModel.internalUnitPrice = it.value})

        viewModel.addPropertyChangeListener("internalUnitPrice", {
            String newValue = it.newValue as String
            internalUnitPriceField.value = newValue ?: internalUnitPriceField.emptyValue
        })

        this.externalUnitPriceField.addValueChangeListener({this.viewModel.externalUnitPrice = it.value})

        viewModel.addPropertyChangeListener("externalUnitPrice", {
            String newValue = it.newValue as String
            externalUnitPriceField.value = newValue ?: externalUnitPriceField.emptyValue
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


        viewModel.addPropertyChangeListener("productFacility", {
            Facility newValue = it.newValue as Facility
            if (newValue) {
                productFacilityComboBox.value = newValue
            } else {
                productFacilityComboBox.value = productFacilityComboBox.emptyValue
            }
        })
        productFacilityComboBox.addSelectionListener({
            viewModel.setProductFacility(it.value as Facility)
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
                case "internalUnitPriceValid":
                    if (it.newValue || it.newValue == null) {
                        internalUnitPriceField.componentError = null
                    }
                    break
                case "externalUnitPriceValid":
                    if (it.newValue || it.newValue == null) {
                        externalUnitPriceField.componentError = null
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
                case "productFacilityValid":
                    if (it.newValue || it.newValue == null) {
                        productFacilityComboBox.componentError = null
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
        this.internalUnitPriceField.addValueChangeListener({ event ->
            ValidationResult result = numberValidator.apply(event.getValue(), new ValueContext(this.internalUnitPriceField))
            if (result.isError()) {
                viewModel.internalUnitPriceValid = false
                UserError error = new UserError(result.getErrorMessage())
                internalUnitPriceField.setComponentError(error)
            } else {
                viewModel.internalUnitPriceValid = true
            }
        })
        this.externalUnitPriceField.addValueChangeListener({ event ->
            ValidationResult result = numberValidator.apply(event.getValue(), new ValueContext(this.externalUnitPriceField))
            if (result.isError()) {
                viewModel.externalUnitPriceValid = false
                UserError error = new UserError(result.getErrorMessage())
                externalUnitPriceField.setComponentError(error)
            } else {
                viewModel.externalUnitPriceValid = true
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
        this.productFacilityComboBox.addSelectionListener({selection ->
            ValidationResult result = selectionValidator.apply(selection.getValue(), new ValueContext(this.productFacilityComboBox))
            if (result.isError()) {
                viewModel.productFacilityValid = false
                UserError error = new UserError(result.getErrorMessage())
                productCategoryComboBox.setComponentError(error)
            } else {
                viewModel.productFacilityValid = true
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
            && viewModel.internalUnitPriceValid \
            && viewModel.externalUnitPriceValid \
            && viewModel.productCategoryValid \
            && viewModel.productFacilityValid
    }

    private void setupListeners(){

        abortButton.addClickListener({clearAllFields() })
        createProductButtonRegistration = this.createProductButton.addClickListener({
            controller.createNewProduct(viewModel.productCategory, viewModel.productDescription,viewModel.productName, Double.parseDouble(viewModel.internalUnitPrice), Double.parseDouble(viewModel.externalUnitPrice), viewModel.productUnit, viewModel.productFacility)
            clearAllFields()
        })

    }

    /**
     *  Clears User Input from all fields in the Create Products View and reset validation status of all Fields
     */
    protected void clearAllFields() {

        productNameField.clear()
        productDescriptionField.clear()
        internalUnitPriceField.clear()
        externalUnitPriceField.clear()
        productCategoryComboBox.selectedItem = productCategoryComboBox.clear()
        productUnitComboBox.selectedItem = productUnitComboBox.clear()

        viewModel.productNameValid = null
        viewModel.productDescriptionValid = null
        viewModel.internalUnitPriceValid = null
        viewModel.externalUnitPriceValid = null
        viewModel.productCategoryValid = null
        viewModel.productUnitValid = null
    }

}
