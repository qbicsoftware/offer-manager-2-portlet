package life.qbic.portal.offermanager.components;

import com.vaadin.data.ValidationResult;
import com.vaadin.data.Validator;
import com.vaadin.data.ValueContext;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <b>short description</b>
 *
 * <p>detailed description</p>
 *
 * @since <version tag>
 */
public class ValidatorCombination<T> implements Validator<T> {

  private final List<Validator<T>> validators;

  public ValidatorCombination() {
    this.validators = new ArrayList<>();
  }

  public void addValidator(Validator<T> validator) {
    validators.add(validator);
  }

  public void removeValidator(Validator<T> validator) {
    validators.remove(validator);
  }

  @Override
  public ValidationResult apply(T value, ValueContext valueContext) {
    List<ValidationResult> validationResults = validators.stream()
        .map(it -> it.apply(value, valueContext))
        .collect(Collectors.toList());
    for (ValidationResult validationResult : validationResults) {
      if (validationResult.isError()) {
        return validationResult;
      }
    }
    return ValidationResult.ok();
  }
}
