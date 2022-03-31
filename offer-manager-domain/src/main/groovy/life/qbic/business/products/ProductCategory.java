package life.qbic.business.products;

import java.util.Arrays;

/**
 * Lists all packages that bundle the offer items
 * <p>
 * A package is of different types, they are differentiated by the items they contain.
 */
public enum ProductCategory {

  SEQUENCING("Sequencing", "SE"),
  PROJECT_MANAGEMENT("Project Management", "PM"),
  PRIMARY_BIOINFO("Primary Bioinformatics", "PB"),
  SECONDARY_BIOINFO("Secondary Bioinformatics", "SB"),
  DATA_STORAGE("Data Storage", "DS"),
  PROTEOMICS("Proteomics", "PR"),
  METABOLOMIC("Metabolomics", "ME"),
  EXTERNAL_SERVICE("External Service", "EXT");

  /**
   * Label describing the enum type with a string
   */
  private final String label;

  /**
   * Abbreviation for the enum type name
   */
  private final String abbreviation;

  ProductCategory(String label, String abbreviation) {
    this.label = label;
    this.abbreviation = abbreviation;
  }

  /**
   * Returns the value associated to the given enum This is <em>NOT</em> the same as {@link
   * #toString}
   *
   * @return a user-friendly string value
   */
  public String getLabel() {
    return label;
  }

  /**
   * Returns the abbreviation associated to the given enum This is <em>NOT</em> the same as {@link
   * #toString}
   *
   * @return a user-friendly string value
   */
  public String getAbbreviation() {
    return abbreviation;
  }

  public static ProductCategory forLabel(String label) {
    return Arrays.stream(values()).filter(it -> it.getLabel().equals(label)).findAny().orElseThrow(
        () -> new IllegalArgumentException("No ProductCategory exists for label: " + label));
  }
}
