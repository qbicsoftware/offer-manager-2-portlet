package life.qbic.business.persons.search

import life.qbic.business.UseCaseFailure
import life.qbic.business.persons.Person

/**
 * Output interface for the {@link SearchPerson} use
 * case.
 *
 * @since: 1.0.0
 *
 */
interface SearchPersonOutput extends UseCaseFailure {

  /**
   * This method is called by the use case on success.
   *
   * It passes the search result for a given search query.
   *
   * @param foundPerson A list of {@link Person}.
   * @since 1.0.0
   */
  void successNotification(List<Person> foundCustomers)
}
