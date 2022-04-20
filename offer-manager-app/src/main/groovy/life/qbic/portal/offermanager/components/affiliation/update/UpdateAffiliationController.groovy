package life.qbic.portal.offermanager.components.affiliation.update


import life.qbic.business.persons.affiliation.Affiliation
import life.qbic.business.persons.affiliation.update.UpdateAffiliationInput

class UpdateAffiliationController {

  private UpdateAffiliationInput useCaseInput

  void updateAffiliation(Affiliation affiliation) {
    if (useCaseInput == null) {
      throw new RuntimeException("$this is missing the use case input.")
    }
    useCaseInput.updateAffiliation(affiliation)
  }

  void setUseCaseInput(UpdateAffiliationInput updateAffiliationInput) {
    this.useCaseInput = updateAffiliationInput
  }
}
