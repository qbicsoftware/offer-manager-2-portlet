package life.qbic.business.persons.affiliation;

import java.util.Arrays;
import java.util.Optional;

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

  public static AffiliationCategory from(String label) {
    Optional<AffiliationCategory> affiliationCategory =
        Arrays.stream(AffiliationCategory.values())
            .filter(it -> it.getLabel().equals(label))
            .findAny();
    return affiliationCategory.orElseThrow(
        () ->
            new IllegalArgumentException(
                String.format("No AffiliationCategory with label %s", label)));
  }

  /**
   * Holds the value of the enum
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
   * @return the label for this academic title. For example 'Dr.'
   */
  public String getLabel() {
    return label;
  }

}
