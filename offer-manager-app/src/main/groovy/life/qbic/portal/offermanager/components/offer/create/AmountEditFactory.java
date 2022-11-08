package life.qbic.portal.offermanager.components.offer.create;

import com.vaadin.data.HasValue;
import com.vaadin.data.ValidationResult;
import com.vaadin.data.Validator;
import com.vaadin.data.ValueContext;
import com.vaadin.server.UserError;
import com.vaadin.shared.Registration;
import com.vaadin.ui.Composite;
import com.vaadin.ui.TextField;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * <b>short description</b>
 *
 * <p>detailed description</p>
 *
 * @since <version tag>
 */
public class AmountEditFactory {

  public enum InputPattern {
    ATOMIC(Pattern.compile("^(-?)([0-9]+)$")),
    PARTIAL(Pattern.compile("^(-?)([0-9]*)(.[0-9]+)?$"));

    private final Pattern pattern;

    InputPattern(Pattern pattern) {
      this.pattern = pattern;
    }

    public boolean test(String s) {
      return pattern.asPredicate().test(s);
    }
  }
  private static final Validator<String> NON_EMPTY_STRING_VALIDATOR = Validator.from(
      value -> Objects.nonNull(value) && !value.trim().isEmpty(),
      "Please provide a number as input.");
  private static final Validator<String> ATOMIC_VALIDATOR = Validator.from(
      InputPattern.ATOMIC::test,
      "Please provide an integer input");

  private static final Validator<String> PARTIAL_VALIDATOR = Validator.from(
      InputPattern.PARTIAL::test,
      "Please provide a decimal input");

  public static class AmountEdit extends Composite implements HasValue<String> {
    private final TextField amountTextField;
    private List<Validator<String>> validators;

    public AmountEdit() {
      validators = new ArrayList<>();
      amountTextField = new TextField();
      validateUserInput();
      setCompositionRoot(amountTextField);
    }

    private void validateUserInput() {
      amountTextField.addValueChangeListener(valueChangeEvent -> {
        if (!validateUserInput(valueChangeEvent)) {
          amountTextField.setValue(valueChangeEvent.getOldValue());
        }
      });
    }

    private boolean validateUserInput(ValueChangeEvent<String> valueChangeEvent) {
      String value = valueChangeEvent.getValue();
      return validateInput(value);
    }

    private boolean validateInput(String value) {
      List<ValidationResult> validationResults = validators.stream()
          .map(validator -> validator
              .apply(value, new ValueContext(amountTextField)))
          .collect(Collectors.toList());
      for (ValidationResult validationResult : validationResults) {
        if (validationResult.isError()) {
          UserError error = new UserError(validationResult.getErrorMessage());
          amountTextField.setComponentError(error);
          return false;
        } else {
          amountTextField.setComponentError(null);
        }
      }
      return true;
    }

    public void setInputValidators(List<Validator<String>> validators) {
      if (Objects.isNull(validators)) {
        throw new IllegalArgumentException("Validator must not be null");
      }
      this.validators = validators;
    }

    @Override
    public void setValue(String value) {
      if (validateInput(value)) {
        amountTextField.setValue(value);
      }
    }

    @Override
    public String getValue() {
      return amountTextField.getValue();
    }

    @Override
    public void setRequiredIndicatorVisible(boolean b) {
      amountTextField.setRequiredIndicatorVisible(b);
    }

    @Override
    public boolean isRequiredIndicatorVisible() {
      return amountTextField.isRequiredIndicatorVisible();
    }

    @Override
    public void setReadOnly(boolean b) {
      amountTextField.setReadOnly(b);
    }

    @Override
    public boolean isReadOnly() {
      return amountTextField.isReadOnly();
    }

    @Override
    public Registration addValueChangeListener(ValueChangeListener<String> valueChangeListener) {
      return amountTextField.addValueChangeListener(valueChangeListener);
    }
  }

  public static AmountEdit forAtomicItem(ProductItemViewModel productItemViewModel) {
    AmountEdit component = new AmountEdit();
    ArrayList<Validator<String>> validators = new ArrayList<>();
    validators.add(NON_EMPTY_STRING_VALIDATOR);
    validators.add(ATOMIC_VALIDATOR);
    component.setInputValidators(validators);
    return component;
  }
  public static AmountEdit forPartialItem(ProductItemViewModel productItemViewModel) {
    AmountEdit component = new AmountEdit();
    ArrayList<Validator<String>> validators = new ArrayList<>();
    validators.add(NON_EMPTY_STRING_VALIDATOR);
    validators.add(PARTIAL_VALIDATOR);
    component.setInputValidators(validators);    return component;
  }

}
