package life.qbic.portal.offermanager.components.product.create

import com.vaadin.icons.VaadinIcons
import com.vaadin.ui.Button
import com.vaadin.ui.ComboBox
import com.vaadin.ui.HorizontalLayout
import com.vaadin.ui.Label
import com.vaadin.ui.TextField
import com.vaadin.ui.VerticalLayout
import com.vaadin.ui.themes.ValoTheme
import life.qbic.datamodel.dtos.business.ProductCategory
import life.qbic.datamodel.dtos.business.services.ProductUnit
import life.qbic.portal.offermanager.components.product.MaintainProductsViewModel

/**
 * <h1>This view serves the user to create a new service product</h1>
 * <br>
 * <p>The view contains several text input fields and combo boxes in order to fully describe the new service products</p>
 *
 * @since 1.0.0
 *
*/
class CreateProductView extends HorizontalLayout{

    private final MaintainProductsViewModel viewModel

    TextField productName
    TextField productDescription
    TextField productUnitPrice

    ComboBox<String> productUnit
    ComboBox<String> productCategories

    Button createProduct
    Button abort

    CreateProductView(MaintainProductsViewModel viewModel){

        this.viewModel = viewModel

        initTextFields()
        initComboBoxes()
        initButtons()
        initLayout()
        addListeners()
        setupFieldValidator()
    }

    private void initLayout(){
        Label label = new Label("Create Service Product")
        label.setStyleName(ValoTheme.LABEL_HUGE)
        this.addComponent(label)

        //add textfields and boxes
        HorizontalLayout sharedLayout = new HorizontalLayout(productUnitPrice,productUnit)
        HorizontalLayout buttons = new HorizontalLayout(abort,createProduct)
        VerticalLayout sideLayout = new VerticalLayout(label,productName,productDescription,sharedLayout,productCategories,buttons)

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
        productName.setWidthFull()

        productUnitPrice = new TextField("Product Unit Price")
        productDescription.setPlaceholder("00.00")
        productDescription.setRequiredIndicatorVisible(true)
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
        /**this.titleField.addValueChangeListener({this.createPersonViewModel.academicTitle = it.value })
        createPersonViewModel.addPropertyChangeListener("academicTitle", {
            String newValue = it.newValue as String
            titleField.value = newValue ?: titleField.emptyValue
        })

        createPersonViewModel.addPropertyChangeListener("affiliation", {
            Affiliation newValue = it.newValue as Affiliation
            if (newValue) {
                affiliationComboBox.value = newValue
                refreshAddressAdditions()
                addressAdditionComboBox.value = newValue
            } else {
                affiliationComboBox.value = affiliationComboBox.emptyValue
                addressAdditionComboBox.value = addressAdditionComboBox.emptyValue
            }
        })
        /*
        we listen to the valid properties. whenever the presenter resets values in the viewmodel
        and resets the valid properties the component error on the respective component is removed
        */
        /**
        createPersonViewModel.addPropertyChangeListener({it ->
            switch (it.propertyName) {
                case "academicTitleValid":
                    if (it.newValue || it.newValue == null) {
                        titleField.componentError = null
                    }
                    break
                case "firstNameValid":
                    if (it.newValue || it.newValue == null) {
                        firstNameField.componentError = null
                    }
                    break
                case "lastNameValid":
                    if (it.newValue || it.newValue == null) {
                        lastNameField.componentError = null
                    }
                    break
                case "emailValid":
                    if (it.newValue || it.newValue == null) {
                        emailField.componentError = null
                    }
                    break
                case "affiliationValid":
                    if (it.newValue || it.newValue == null) {
                        affiliationComboBox.componentError = null
                        addressAdditionComboBox.componentError = null
                    }
                    break
                default:
                    break
            }
            submitButton.enabled = allValuesValid()
            addressAdditionComboBox.enabled = !Objects.isNull(createPersonViewModel.affiliation)
        })**/
    }

    private void setupFieldValidator(){
        /**this.emailField.addValueChangeListener({ event ->
            ValidationResult result = emailValidator.apply(event.getValue(), new ValueContext(this.emailField))
            if (result.isError()) {
                createPersonViewModel.emailValid = false
                UserError error = new UserError(result.getErrorMessage())
                emailField.setComponentError(error)
            } else {
                createPersonViewModel.emailValid = true
            }
        })**/
    }

    private void addListeners(){

    }

}
