package life.qbic.portal.offermanager.components.affiliation.update


import life.qbic.business.persons.affiliation.Affiliation
import life.qbic.business.persons.affiliation.update.UpdateAffiliationInput

class UpdateAffiliationController {

  private UpdateAffiliationInput updateAffiliationInput

  void updateAffiliation(Affiliation affiliation) {
    if (updateAffiliationInput == null) {
      throw new RuntimeException("$this is missing the use case input.")
    }
    updateAffiliationInput.updateAffiliation(affiliation)
  }

  void setUpdateAffiliationInput(UpdateAffiliationInput updateAffiliationInput) {
    this.updateAffiliationInput = updateAffiliationInput
  }
}
