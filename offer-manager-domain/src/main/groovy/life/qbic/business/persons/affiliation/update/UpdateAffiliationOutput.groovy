package life.qbic.business.persons.affiliation.update

import life.qbic.business.UseCaseFailure
import life.qbic.business.exceptions.DatabaseQueryException
import life.qbic.business.persons.affiliation.Affiliation

/**
 * The output interface for the Update Affiliation use case.
 *
 * @since 1.3.0
 */
interface UpdateAffiliationOutput extends UseCaseFailure {

  /**
   * Updates an affiliation entry
   * @param affiliation the affiliation after the update
   */
  void updatedAffiliation(Affiliation affiliation)

  /**
   * To be called if an affiliation entry was not found in the database
   * @param missingAffiliation The affiliation that was searched for but not found
   * @param message The message the should be propagated to the view
   */
  void affiliationNotFound(Affiliation notFoundAffiliation, String message)
}
