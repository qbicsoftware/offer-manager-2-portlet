package life.qbic.business.persons.affiliation;

import java.util.Arrays;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Stream;

/**
 * We define three major business affiliation categories here, which means that we decide between
 *
 * <ul>
 *   <li><b>internal</b>: An affiliation we consider as within the University of Tübingen or
 *       University Hospital of Tübingen
 *   <li><b>external academic</b>: An outside affiliation but an academic institution (public
 *       research institutions)
 *   <li><b>external</b>: An outside affiliation but not academic (i.e. private sector, companies,
 *       etc)
 * </ul>
 *
 * @since 1.3.0
 */
public enum AffiliationCategory {
  INTERNAL("internal"),
  EXTERNAL_ACADEMIC("external academic"),
  EXTERNAL("external");

  /**
   * Factory method returning the corresponding AffiliationCategory given a label.
   * This method scans through all available enum values and returns a value with matching label.
   * @param label the label for which the enum value is requested
   * @return an {@link AffiliationCategory} if any has a matching label, otherwise {@link IllegalArgumentException}
   * @since 1.3.0
   * @throws IllegalArgumentException in case no enum value has the provided label
   */
  public static AffiliationCategory forLabel(String label) throws IllegalArgumentException {
    Stream<AffiliationCategory> availableCategories = Arrays.stream(AffiliationCategory.values());
    Predicate<AffiliationCategory> hasProvidedLabel = it -> it.getLabel().equals(label);
    Supplier<IllegalArgumentException> noCategoryFound = () -> new IllegalArgumentException(
        String.format("No AffiliationCategory with label %s exists", label));
    return availableCategories
        .filter(hasProvidedLabel)
        .findAny()
        .orElseThrow(noCategoryFound);
  }

  /**
   * Holds the label of this enum value
   */
  private final String label;

  /**
   * A private constructor to create different AffiliationCategory enum items
   * @param label the textual representation of this enum
   */
  AffiliationCategory(String label) {
    this.label = label;
  }

  /**
   * @return the label for this affiliation category. For example 'internal'.
   */
  public String getLabel() {
    return label;
  }

  /**
   * {@inheritDoc} <b>This method might change. Please consider using
   * {@link life.qbic.business.persons.affiliation.AffiliationCategory#getLabel()} instead</b>
   */
  @Override
  public String toString() {
    return label;
  }
}
