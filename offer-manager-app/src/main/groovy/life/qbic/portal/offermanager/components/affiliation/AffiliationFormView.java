package life.qbic.portal.offermanager.components.affiliation;

import com.vaadin.data.HasValue.ValueChangeEvent;
import com.vaadin.data.ValidationResult;
import com.vaadin.data.Validator;
import com.vaadin.data.ValueContext;
import com.vaadin.server.UserError;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.ui.*;

import java.util.*;
import java.util.stream.Collectors;
import life.qbic.business.persons.affiliation.Country;
import life.qbic.datamodel.dtos.business.Affiliation;
import life.qbic.datamodel.dtos.business.AffiliationCategory;
import life.qbic.portal.offermanager.components.Resettable;
import life.qbic.portal.offermanager.components.Updatable;
import life.qbic.portal.offermanager.components.UserInput;

public class AffiliationFormView extends VerticalLayout implements Resettable, Updatable<Affiliation>, UserInput<Affiliation> {

  private ComboBox<String> organisationBox;
  private TextField addressAdditionField;
  private TextField streetField;
  private TextField postalCodeField;
  private TextField cityField;
  private ComboBox<String> countryBox;
  private ComboBox<String> affiliationCategoryBox;

  private final List<ViewChangeListener> listeners = new ArrayList<>();


  /*
  private final CreateAffiliationViewModel createAffiliationViewModel

  private ComboBox<String> organisationBox
  private TextField addressAdditionField
  private TextField streetField
  private TextField postalCodeField
  private TextField cityField
  private ComboBox<String> countryBox
  private ComboBox<String> affiliationCategoryBox


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

    this.affiliationCategoryBox = generateAffiliationCategorySelect(createAffiliationViewModel.affiliationCategories) as ComboBox<String>

    organisationBox.setRequiredIndicatorVisible(true)
    addressAdditionField.setRequiredIndicatorVisible(false)
    streetField.setRequiredIndicatorVisible(true)
    postalCodeField.setRequiredIndicatorVisible(true)
    cityField.setRequiredIndicatorVisible(true)
    countryBox.setRequiredIndicatorVisible(true)
    affiliationCategoryBox.setRequiredIndicatorVisible(true)

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

    HorizontalLayout row5 = new HorizontalLayout(affiliationCategoryBox)

    organisationBox.setSizeFull()
    addressAdditionField.setSizeFull()
    streetField.setSizeFull()
    postalCodeField.setSizeFull()
    cityField.setSizeFull()
    countryBox.setWidth(50, Unit.PERCENTAGE)
    affiliationCategoryBox.setSizeFull()

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
    presentAddressAdditionChange()
    changeAddressAddition()

    // bind affiliationCategory
    presentAffiliationCategoryChange()
    changeAffiliationCategory()

    // bind city
    presentCityChange()
    changeCity()

    // bind country
    presentCountryChange()
    changeCountry()

    // bind organisation
    presentOrganisationChange()
    changeOrganisation()

    // bind postalCode
    presentPostalCodeChange()
    changePostalCode()

    // bind street
    presentStreetChange()
    changeStreet()

    *//*
    we listen to the valid properties. whenever the presenter resets values in the viewmodel
    and resets the valid properties the component error on the respective component is removed
    *//*
    createAffiliationViewModel.addPropertyChangeListener({
      switch (it.propertyName) {
        case "addressAdditionValid":
          if (it.newValue || it.newValue == null) {
            addressAdditionField.componentError = null
          }
          break
        case "affiliationCategoryValid":
          if (it.newValue || it.newValue == null) {
            affiliationCategoryBox.componentError = null
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
  //<editor-fold desc="view model binding">
  private Registration changeStreet() {
    return streetField.addValueChangeListener({
      createAffiliationViewModel.setStreet(it.value)
    })
  }

  private presentStreetChange() {
    createAffiliationViewModel.addPropertyChangeListener("street", {
      String newValue = it.newValue as String
      streetField.value = newValue ?: streetField.emptyValue
    })
  }

  private Registration changePostalCode() {
    return postalCodeField.addValueChangeListener({
      createAffiliationViewModel.setPostalCode(it.value)
    })
  }

  private presentPostalCodeChange() {
    createAffiliationViewModel.addPropertyChangeListener("postalCode", {
      String newValue = it.newValue as String
      postalCodeField.value = newValue ?: postalCodeField.emptyValue
    })
  }

  private Registration changeOrganisation() {
    return organisationBox.addSelectionListener({
      createAffiliationViewModel.setOrganisation(it.value)
    })
  }

  private presentOrganisationChange() {
    createAffiliationViewModel.addPropertyChangeListener("organisation", {
      String newValue = it.newValue as String
      organisationBox.value = newValue ?: organisationBox.emptyValue
    })
  }

  private Registration changeCountry() {
    return countryBox.addSelectionListener({
      createAffiliationViewModel.setCountry(it.value)
    })
  }

  private presentCountryChange() {
    createAffiliationViewModel.addPropertyChangeListener("country", {
      String newValue = it.newValue as String
      countryBox.value = newValue ?: countryBox.emptyValue
    })
  }

  private Registration changeCity() {
    return cityField.addValueChangeListener({
      createAffiliationViewModel.setCity(it.value)
    })
  }

  private presentCityChange() {
    createAffiliationViewModel.addPropertyChangeListener("city", {
      String newValue = it.newValue as String
      cityField.value = newValue ?: cityField.emptyValue
    })
  }

  private Registration changeAffiliationCategory() {
    return affiliationCategoryBox.addValueChangeListener({ createAffiliationViewModel.setAffiliationCategory(it.value) })
  }

  private presentAffiliationCategoryChange() {
    createAffiliationViewModel.addPropertyChangeListener("affiliationCategory", {
      String newValue = it.newValue as String
      affiliationCategoryBox.selectedItem = newValue ?: affiliationCategoryBox.emptyValue
    })
  }

  private Registration changeAddressAddition() {
    return addressAdditionField.addValueChangeListener({
      createAffiliationViewModel.setAddressAddition(it.value)
    })
  }

  private presentAddressAdditionChange() {
    createAffiliationViewModel.addPropertyChangeListener("addressAddition", {
      String newValue = it.newValue as String
      addressAdditionField.value = newValue ?: addressAdditionField.emptyValue
    })
  }
  //</editor-fold>

  private void setupFieldValidators() {
    Validator<String> nonEmptyStringValidator = Validator.from({ String value -> (value && !value.trim().empty) }, "Empty input not supported.")
    Validator<? extends Object> selectionValidator = Validator.from({ o -> o != null }, "Please make a selection.")

    this.addressAdditionField.addValueChangeListener({ event ->
      // we do not require this field
      createAffiliationViewModel.addressAdditionValid = true
    })
    this.affiliationCategoryBox.addValueChangeListener({ event ->
      ValidationResult result = selectionValidator.apply(event.getValue(), new ValueContext(this.affiliationCategoryBox))
      if (result.isError()) {
        createAffiliationViewModel.affiliationCategoryValid = false
        UserError error = new UserError(result.getErrorMessage())
        affiliationCategoryBox.setComponentError(error)
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
    affiliationCategoryBox.clear()
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
  }*/


  public AffiliationFormView() {
    initializeComponents();
    layoutComponents();
  }

  public void addChangeListener(ViewChangeListener viewChangeListener) {
    listeners.add(viewChangeListener);
  }

  private void fireViewChanged() {
    listeners.forEach(ViewChangeListener::onViewChanged);
  }

  private void layoutComponents() {
    HorizontalLayout row1 = new HorizontalLayout(organisationBox, addressAdditionField);
    row1.setSizeFull();
    HorizontalLayout row2 = new HorizontalLayout(streetField);
    row2.setSizeFull();
    HorizontalLayout row3 = new HorizontalLayout(postalCodeField, cityField);
    row3.setSizeFull();
    row3.setExpandRatio(postalCodeField, 1);
    row3.setExpandRatio(cityField, 3); // leads to it being 3/4 of the width

    HorizontalLayout row4 = new HorizontalLayout(countryBox);
    row4.setSizeFull();

    HorizontalLayout row5 = new HorizontalLayout(affiliationCategoryBox);
    row5.setSizeFull();


    this.addComponents(row1, row2, row3, row4, row5);
    this.setMargin(false);
  }

  private void initializeComponents() {
    organisationBox = createOrganisationBox();
    addressAdditionField = createAddressAdditionField();
    streetField = createStreetField();
    postalCodeField = createPostalCodeField();
    cityField = createCityField();
    countryBox = createCountryBox();
    affiliationCategoryBox = createAffiliationCategoryField();
  }

  private ComboBox<String> createAffiliationCategoryField() {
    ComboBox<String> affiliationCategoryBox = generateAffiliationCategorySelect(getPossibleCategories());
    requireSelection(affiliationCategoryBox);
    affiliationCategoryBox.setWidth(50, Unit.PERCENTAGE);

    affiliationCategoryBox.addValueChangeListener(it -> fireViewChanged());

    return affiliationCategoryBox;
  }

  private List<String> getPossibleCategories() {
    return Arrays.stream(AffiliationCategory.values())
        .map(AffiliationCategory::getValue)
        .collect(Collectors.toList());
  }

  private ComboBox<String> generateAffiliationCategorySelect(List<String> possibleCategories) {
    ComboBox<String> affiliationCategoryBox = new ComboBox<>("Affiliation Category");
    affiliationCategoryBox.setItems(possibleCategories);
    affiliationCategoryBox.setPlaceholder("Affiliation category");
    String description =
        "We define three major business affiliation categories here\n" +
               "- internal:\n" +
               "    An affiliation we consider as within the University of Tübingen or University Hospital of Tübingen\n"
               +
               "- external academic:\n" +
               "    An outside affiliation but an academic institution (public research institutions)\n"
               +
               "- external:\n" +
               "    An outside affiliation but not academic (i.e. private sector, companies, etc)";
    affiliationCategoryBox.setDescription(description, ContentMode.PREFORMATTED);

    return affiliationCategoryBox;
  }

  private ComboBox<String> createCountryBox() {
    ComboBox<String> countryBox = new ComboBox<>("Country");
    countryBox.setPlaceholder("Name of the country");
    countryBox.setDescription("Select the name of the country e.g. Germany");
    countryBox.setItems(Country.availableCountryNames());
    countryBox.setWidth(50, Unit.PERCENTAGE);
    requireSelection(countryBox);
    countryBox.addValueChangeListener(it -> fireViewChanged());

    return countryBox;
  }

  private TextField createCityField() {
    TextField cityField = new TextField("City");
    cityField.setPlaceholder("Name of the city");
    cityField.setSizeFull();
    requireTextInput(cityField);
    cityField.addValueChangeListener(it -> fireViewChanged());

    return cityField;
  }

  private TextField createPostalCodeField() {
    TextField postalCodeField = new TextField("Postal Code");
    postalCodeField.setPlaceholder("Customer postal code");
    postalCodeField.setSizeFull();
    requireTextInput(postalCodeField);
    postalCodeField.addValueChangeListener(it -> fireViewChanged());

    return postalCodeField;
  }

  private TextField createStreetField() {
    TextField streetField = new TextField("Street");
    streetField.setPlaceholder("Street name and street number ");
    streetField.setSizeFull();
    requireTextInput(streetField);
    streetField.addValueChangeListener(it -> fireViewChanged());

    return streetField;
  }

  private TextField createAddressAdditionField() {
    TextField addressAdditionField = new TextField("Address Addition");
    addressAdditionField.setPlaceholder(
        "Department, Faculty, or other specification of affiliation name");
    addressAdditionField.setDescription(
        "In case the affiliation differs from the organisation you can further specify that here.");
    addressAdditionField.setSizeFull();
    addressAdditionField.addValueChangeListener(it -> fireViewChanged());

    return addressAdditionField;
  }

  private ComboBox<String> createOrganisationBox() {
    ComboBox<String> organisationBox = new ComboBox<>("Organisation Name");
    organisationBox.setPlaceholder("Name of the organisation");
    organisationBox.setDescription(
        "Select or enter new name of the organisation e.g. Universität Tübingen.");
    organisationBox.setSizeFull();
    organisationBox.setNewItemProvider(Optional::of);
    requireTextInput(organisationBox);
    organisationBox.addValueChangeListener(it -> fireViewChanged());

    return organisationBox;
  }

  private void requireTextInput(TextField textField) {
    textField.setRequiredIndicatorVisible(true);
    textField.addValueChangeListener(event -> addNotEmptyValidation(textField, event));
  }

  private void requireTextInput(ComboBox<String> comboBox) {
    comboBox.setRequiredIndicatorVisible(true);
    comboBox.addValueChangeListener(event -> addNotEmptyValidation(comboBox, event));
  }

  private <T> void requireSelection(ComboBox<T> comboBox) {
    comboBox.setEmptySelectionAllowed(false);
    comboBox.setRequiredIndicatorVisible(true);
    comboBox.addValueChangeListener(event -> addSelectionPresentValidation(comboBox, event));
  }

  private <T> void addSelectionPresentValidation(ComboBox<T> comboBox, ValueChangeEvent<T> event) {
    Validator<T> selectionValidator = Validator.from(Objects::nonNull, "Please make a selection.");
    ValidationResult result = selectionValidator.apply(event.getValue(),
        new ValueContext(comboBox));
    if (result.isError()) {
      UserError error = new UserError(result.getErrorMessage());
      comboBox.setComponentError(error);
    } else {
      comboBox.setComponentError(null);
    }
  }

  private void addNotEmptyValidation(AbstractComponent component, ValueChangeEvent<String> event) {
    Validator<String> nonEmptyStringValidator = Validator.from( value -> (value != null && !value.trim().isEmpty()), "Empty input not supported.");
    ValidationResult result = nonEmptyStringValidator.apply(event.getValue(),
        new ValueContext(component));
    if (result.isError()) {
      UserError error = new UserError(result.getErrorMessage());
      component.setComponentError(error);
    } else {
      component.setComponentError(null);
    }
  }

  @Override
  public void reset() {

  }

  @Override
  public void update(Affiliation value) {

  }

  @Override
  public boolean isValid() {
    return requiredFieldsFilled() && hasNoComponentError();
  }

  private boolean requiredFieldsFilled() {
    return affiliationCategoryBox.getValue() != null &&
            organisationBox.getComponentError() == null &&
            countryBox.getComponentError() == null &&
            streetField.getComponentError() == null &&
            postalCodeField.getComponentError() == null &&
            cityField.getComponentError() == null;
  }

  private boolean hasNoComponentError() {
    return affiliationCategoryBox.getComponentError() == null &&
            organisationBox.getComponentError() == null &&
            countryBox.getComponentError() == null &&
            streetField.getComponentError() == null &&
            postalCodeField.getComponentError() == null &&
            cityField.getComponentError() == null;
  }


  @Override
  public Affiliation get() {
    if (!isValid()) {
      throw new RuntimeException("Shit");
    }
    return null;
  }
}
