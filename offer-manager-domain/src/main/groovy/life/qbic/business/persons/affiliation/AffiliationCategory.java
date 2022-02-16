package life.qbic.business.persons.affiliation;

/**
 * We define three major business affiliation categories here, which
 * means that we decide between
 * <ul>
 *  <li><b>internal</b>: An affiliation we consider as within the University of Tübingen or University
 *  Hospital of Tübingen</li>
 *  <li><b>external academic</b>: An outside affiliation but an academic institution (public research
 *  institutions)</li>
 *  <li><b>external</b>: An outside affiliation but not academic (i.e. private sector, companies, etc)</li>
 * </ul>
 * @since 1.3.0
 */
enum AffiliationCategory {

  INTERNAL("internal"),
  EXTERNAL_ACADEMIC("external academic"),
  EXTERNAL("external");

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
