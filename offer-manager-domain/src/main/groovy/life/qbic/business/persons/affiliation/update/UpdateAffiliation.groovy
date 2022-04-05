package life.qbic.business.persons.affiliation.update

import life.qbic.business.exceptions.DatabaseQueryException
import life.qbic.business.logging.Logger
import life.qbic.business.logging.Logging
import life.qbic.business.persons.affiliation.Affiliation
import life.qbic.business.persons.affiliation.AffiliationNotFoundException

/**
 * Updates an affiliation entry 
 *
 * @since 1.3.0
 */
class UpdateAffiliation implements UpdateAffiliationInput {

  private UpdateAffiliationDataSource affiliationDataSource
  private UpdateAffiliationOutput affiliationOutput

  private final Logging log = Logger.getLogger(UpdateAffiliation.class)

  UpdateAffiliation(UpdateAffiliationOutput affiliationOutput, UpdateAffiliationDataSource affiliationDataSource) {
    this.affiliationDataSource = affiliationDataSource
    this.affiliationOutput = affiliationOutput
  }

  @Override
  void updateAffiliation(Affiliation affiliation) {
    try {
      affiliationDataSource.updateAffiliation(affiliation)
      affiliationOutput.updatedAffiliation(affiliation)
    } catch (AffiliationNotFoundException notFoundException) {
      String message = "Failed to update affiliation ${affiliation.getOrganization()} ${affiliation.getAddressAddition()}. \nAffiliation was not found. Please try again."
      log.error(message, notFoundException)
      affiliationOutput.failNotification(message)
    } catch (DatabaseQueryException databaseQueryException) {
      String message = "Could not update ${affiliation.getOrganization()} ${affiliation.getAddressAddition()}. Please try again."
      log.error(message, databaseQueryException)
      affiliationOutput.failNotification(message)
    } catch (Exception unexpected) {
      String message = "An unexpected error occurred during the update of the ${affiliation.getOrganization()} ${affiliation.getAddressAddition()} affiliation."
      log.error("$message : $unexpected.message", unexpected)
      affiliationOutput.failNotification(message)
    }
  }

}
