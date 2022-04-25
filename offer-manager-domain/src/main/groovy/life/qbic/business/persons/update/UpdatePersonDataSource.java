package life.qbic.business.persons.update;

/**
 * Interface for updating person entities.
 *
 * @since 1.5.0
 */
public interface UpdatePersonDataSource {

  /**
   * Removes an affiliation relation from all persons.
   *
   * @param affiliationId the affiliations unique id
   * @since 1.5.0
   */
  void removeAffiliationFromAllPersons(int affiliationId);

}
