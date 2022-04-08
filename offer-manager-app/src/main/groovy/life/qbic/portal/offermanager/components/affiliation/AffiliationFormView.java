package life.qbic.portal.offermanager.components.affiliation;

import com.vaadin.data.HasValue.ValueChangeEvent;
import com.vaadin.data.ValidationResult;
import com.vaadin.data.Validator;
import com.vaadin.data.ValueContext;
import com.vaadin.server.UserError;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.ui.AbstractComponent;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import life.qbic.business.persons.affiliation.Country;
import life.qbic.datamodel.dtos.business.Affiliation;
import life.qbic.datamodel.dtos.business.AffiliationCategory;
import life.qbic.datamodel.dtos.business.AffiliationCategoryFactory;
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
    addressAdditionField.clear();
    affiliationCategoryBox.clear();
    cityField.clear();
    countryBox.clear();
    organisationBox.clear();
    postalCodeField.clear();
    streetField.clear();

    addressAdditionField.setComponentError(null);
    affiliationCategoryBox.setComponentError(null);
    cityField.setComponentError(null);
    countryBox.setComponentError(null);
    organisationBox.setComponentError(null);
    postalCodeField.setComponentError(null);
    streetField.setComponentError(null);
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
      throw new RuntimeException("Tried to get invalid user input for affiliation");
    }
    Affiliation.Builder affiliationBuilder = new Affiliation.Builder(organisationBox.getValue(), streetField.getValue(), postalCodeField.getValue(), cityField.getValue());
    affiliationBuilder.country(countryBox.getValue());

    if (!addressAdditionField.isEmpty()) {
      affiliationBuilder.addressAddition(addressAdditionField.getValue());
    }

    AffiliationCategoryFactory categoryFactory = new AffiliationCategoryFactory();

    AffiliationCategory affiliationCategory;
    affiliationCategory = categoryFactory.getForString(affiliationCategoryBox.getValue());
    affiliationBuilder.setCategory(affiliationCategory);

    return affiliationBuilder.build();
  }
}
