package life.qbic.portal.offermanager.components.affiliation

import com.vaadin.data.ValidationResult
import com.vaadin.data.Validator
import com.vaadin.data.ValueContext
import com.vaadin.server.UserError
import com.vaadin.shared.ui.ContentMode
import com.vaadin.ui.ComboBox
import com.vaadin.ui.HorizontalLayout
import com.vaadin.ui.TextField
import com.vaadin.ui.VerticalLayout
import life.qbic.business.persons.affiliation.Country
import life.qbic.datamodel.dtos.business.Affiliation
import life.qbic.portal.offermanager.components.Resettable
import life.qbic.portal.offermanager.components.affiliation.create.CreateAffiliationViewModel

import java.util.stream.Collectors

class AffiliationFormView extends VerticalLayout implements Resettable {

  private final CreateAffiliationViewModel createAffiliationViewModel

  private ComboBox<String> organisationBox
  private TextField addressAdditionField
  private TextField streetField
  private TextField postalCodeField
  private TextField cityField
  private ComboBox<String> countryBox
  private ComboBox<String> affiliationCategoryField


  AffiliationFormView(CreateAffiliationViewModel createAffiliationViewModel) {
    this.createAffiliationViewModel = createAffiliationViewModel
    initLayout()
    fillAffiliationBox()
    bindViewModel()
    setupFieldValidators()
  }

  private void initLayout() {
    this.organisationBox = new ComboBox<>("Organisation Name")
    organisationBox.setPlaceholder("Name of the organisation")
    organisationBox.setDescription("Select or enter new name of the organisation e.g. Universität Tübingen.")

    this.addressAdditionField = new TextField("Address Addition")
    addressAdditionField.setPlaceholder("Department, Faculty, or other specification of affiliation name")
    addressAdditionField.setDescription("In case the affiliation differs from the organisation you can further specify that here.")

    this.streetField = new TextField("Street")
    streetField.setPlaceholder("Street name and street number ")

    this.postalCodeField = new TextField("Postal Code")
    postalCodeField.setPlaceholder("Customer postal code")

    this.cityField = new TextField("City")
    cityField.setPlaceholder("Name of the city")

    this.countryBox = new ComboBox<>("Country")
    countryBox.setPlaceholder("Name of the country")
    countryBox.setDescription("Select the name of the country e.g. Germany")
    countryBox.setItems(Country.availableCountryNames())

    this.affiliationCategoryField = generateAffiliationCategorySelect(createAffiliationViewModel.affiliationCategories) as ComboBox<String>

    organisationBox.setRequiredIndicatorVisible(true)
    addressAdditionField.setRequiredIndicatorVisible(false)
    streetField.setRequiredIndicatorVisible(true)
    postalCodeField.setRequiredIndicatorVisible(true)
    cityField.setRequiredIndicatorVisible(true)
    countryBox.setRequiredIndicatorVisible(true)
    affiliationCategoryField.setRequiredIndicatorVisible(true)

    HorizontalLayout row1 = new HorizontalLayout(organisationBox, addressAdditionField)
    row1.setSizeFull()
    HorizontalLayout row2 = new HorizontalLayout(streetField)
    row2.setSizeFull()
    HorizontalLayout row3 = new HorizontalLayout(postalCodeField, cityField)
    row3.setSizeFull()
    row3.setExpandRatio(postalCodeField, 1)
    row3.setExpandRatio(cityField, 3) // leads to it being 3/4 of the width
    HorizontalLayout row4 = new HorizontalLayout(countryBox)
    row4.setSizeFull()

    HorizontalLayout row5 = new HorizontalLayout(affiliationCategoryField)

    organisationBox.setSizeFull()
    addressAdditionField.setSizeFull()
    streetField.setSizeFull()
    postalCodeField.setSizeFull()
    cityField.setSizeFull()
    countryBox.setWidth(50, Unit.PERCENTAGE)
    affiliationCategoryField.setSizeFull()

    this.addComponents(row1, row2, row3, row4, row5)
    this.setMargin(false)
  }

  private void fillAffiliationBox() {
    // we don't need the whole affiliation object, just the unique organization names.
    List<String> organisationNames = createAffiliationViewModel
            .affiliationService.iterator().toList()
            .stream()
            .map(affiliation -> (affiliation as Affiliation).organisation)
            .distinct()
            .collect(Collectors.toList())

    organisationBox.setItems(organisationNames)
    organisationBox.setTextInputAllowed(true)

    // Check if the caption for new item already exists in the list of item
    // captions before approving it as a new item.
    ComboBox.NewItemProvider<String> itemHandler = (String newItemCaption) -> {
      boolean newItem = organisationNames.stream().noneMatch(data -> data.equalsIgnoreCase(newItemCaption))
      if (newItem) {
        // Adds new option
        organisationNames.add(newItemCaption)
        organisationBox.setItems(organisationNames)
        organisationBox.setSelectedItem(newItemCaption)
      }
      return Optional.ofNullable(newItemCaption)
    }
    organisationBox.setNewItemProvider(itemHandler)
  }

  private void bindViewModel() {

    // bind addressAddition
    createAffiliationViewModel.addPropertyChangeListener("addressAddition", {
      String newValue = it.newValue as String
      addressAdditionField.value = newValue ?: addressAdditionField.emptyValue
    })
    addressAdditionField.addValueChangeListener({
      createAffiliationViewModel.setAddressAddition(it.value)
    })

    // bind affiliationCategory
    createAffiliationViewModel.addPropertyChangeListener("affiliationCategory", {
      String newValue = it.newValue as String
      affiliationCategoryField.selectedItem = newValue ?: affiliationCategoryField.emptyValue
    })
    affiliationCategoryField.addValueChangeListener({ createAffiliationViewModel.setAffiliationCategory(it.value) })

    // bind city
    createAffiliationViewModel.addPropertyChangeListener("city", {
      String newValue = it.newValue as String
      cityField.value = newValue ?: cityField.emptyValue
    })
    cityField.addValueChangeListener({
      createAffiliationViewModel.setCity(it.value)
    })

    // bind country
    createAffiliationViewModel.addPropertyChangeListener("country", {
      String newValue = it.newValue as String
      countryBox.value = newValue ?: countryBox.emptyValue
    })
    countryBox.addSelectionListener({
      createAffiliationViewModel.setCountry(it.value)
    })

    // bind organisation
    createAffiliationViewModel.addPropertyChangeListener("organisation", {
      String newValue = it.newValue as String
      organisationBox.value = newValue ?: organisationBox.emptyValue
    })
    organisationBox.addSelectionListener({
      createAffiliationViewModel.setOrganisation(it.value)
    })

    // bind postalCode
    createAffiliationViewModel.addPropertyChangeListener("postalCode", {
      String newValue = it.newValue as String
      postalCodeField.value = newValue ?: postalCodeField.emptyValue
    })
    postalCodeField.addValueChangeListener({
      createAffiliationViewModel.setPostalCode(it.value)
    })

    // bind street
    createAffiliationViewModel.addPropertyChangeListener("street", {
      String newValue = it.newValue as String
      streetField.value = newValue ?: streetField.emptyValue
    })
    streetField.addValueChangeListener({
      createAffiliationViewModel.setStreet(it.value)
    })

    /*
    we listen to the valid properties. whenever the presenter resets values in the viewmodel
    and resets the valid properties the component error on the respective component is removed
    */
    createAffiliationViewModel.addPropertyChangeListener({
      switch (it.propertyName) {
        case "addressAdditionValid":
          if (it.newValue || it.newValue == null) {
            addressAdditionField.componentError = null
          }
          break
        case "affiliationCategoryValid":
          if (it.newValue || it.newValue == null) {
            affiliationCategoryField.componentError = null
          }
          break
        case "cityValid":
          if (it.newValue || it.newValue == null) {
            cityField.componentError = null
          }
          break
        case "countryValid":
          if (it.newValue || it.newValue == null) {
            countryBox.componentError = null
          }
          break
        case "organisationValid":
          if (it.newValue || it.newValue == null) {
            organisationBox.componentError = null
          }
          break
        case "postalCodeValid":
          if (it.newValue || it.newValue == null) {
            postalCodeField.componentError = null
          }
          break
        case "streetValid":
          if (it.newValue || it.newValue == null) {
            streetField.componentError = null
          }
          break
        default:
          break
      }
    })
  }

  private void setupFieldValidators() {
    Validator<String> nonEmptyStringValidator = Validator.from({ String value -> (value && !value.trim().empty) }, "Empty input not supported.")
    Validator<? extends Object> selectionValidator = Validator.from({ o -> o != null }, "Please make a selection.")

    this.addressAdditionField.addValueChangeListener({ event ->
      // we do not require this field
      createAffiliationViewModel.addressAdditionValid = true
    })
    this.affiliationCategoryField.addValueChangeListener({ event ->
      ValidationResult result = selectionValidator.apply(event.getValue(), new ValueContext(this.affiliationCategoryField))
      if (result.isError()) {
        createAffiliationViewModel.affiliationCategoryValid = false
        UserError error = new UserError(result.getErrorMessage())
        affiliationCategoryField.setComponentError(error)
      } else {
        createAffiliationViewModel.affiliationCategoryValid = true
      }
    })
    this.cityField.addValueChangeListener({ event ->
      ValidationResult result = nonEmptyStringValidator.apply(event.getValue(), new ValueContext(this.cityField))
      if (result.isError()) {
        createAffiliationViewModel.cityValid = false
        UserError error = new UserError(result.getErrorMessage())
        cityField.setComponentError(error)
      } else {
        createAffiliationViewModel.cityValid = true
      }
    })
    this.countryBox.addValueChangeListener({ event ->
      ValidationResult result = nonEmptyStringValidator.apply(event.getValue(), new ValueContext(this.countryBox))
      if (result.isError()) {
        createAffiliationViewModel.countryValid = false
        UserError error = new UserError(result.getErrorMessage())
        countryBox.setComponentError(error)
      } else {
        createAffiliationViewModel.countryValid = true
      }
    })
    this.organisationBox.addValueChangeListener({ event ->
      ValidationResult result = nonEmptyStringValidator.apply(event.getValue(), new ValueContext(this.organisationBox))
      if (result.isError()) {
        createAffiliationViewModel.organisationValid = false
        UserError error = new UserError(result.getErrorMessage())
        organisationBox.setComponentError(error)
      } else {
        createAffiliationViewModel.organisationValid = true
      }
    })
    this.postalCodeField.addValueChangeListener({ event ->
      ValidationResult result = nonEmptyStringValidator.apply(event.getValue(), new ValueContext(this.postalCodeField))
      if (result.isError()) {
        createAffiliationViewModel.postalCodeValid = false
        UserError error = new UserError(result.getErrorMessage())
        postalCodeField.setComponentError(error)
      } else {
        createAffiliationViewModel.postalCodeValid = true
      }
    })
    this.streetField.addValueChangeListener({ event ->
      ValidationResult result = nonEmptyStringValidator.apply(event.getValue(), new ValueContext(this.streetField))
      if (result.isError()) {
        createAffiliationViewModel.streetValid = false
        UserError error = new UserError(result.getErrorMessage())
        streetField.setComponentError(error)
      } else {
        createAffiliationViewModel.streetValid = true
      }
    })
  }

  private static ComboBox generateAffiliationCategorySelect(List<String> possibleCategories) {
    ComboBox comboBox = new ComboBox<>("Affiliation Category")
    comboBox.setItems(possibleCategories)
    comboBox.setEmptySelectionAllowed(false)
    comboBox.setPlaceholder("Affiliation category")
    comboBox.setDescription("""We define three major business affiliation categories here
        - internal: 
            An affiliation we consider as within the University of Tübingen or University Hospital of Tübingen
        - external academic: 
            An outside affiliation but an academic institution (public research institutions)
        - external:
            An outside affiliation but not academic (i.e. private sector, companies, etc)""".stripIndent(), ContentMode.PREFORMATTED)
    return comboBox
  }


  @Override
  void reset() {
    addressAdditionField.clear()
    affiliationCategoryField.clear()
    cityField.clear()
    countryBox.clear()
    organisationBox.clear()
    postalCodeField.clear()
    streetField.clear()

    createAffiliationViewModel.addressAdditionValid = null
    createAffiliationViewModel.affiliationCategoryValid = null
    createAffiliationViewModel.cityValid = null
    createAffiliationViewModel.countryValid = null
    createAffiliationViewModel.organisationValid = null
    createAffiliationViewModel.postalCodeValid = null
    createAffiliationViewModel.streetValid = null
  }
}
