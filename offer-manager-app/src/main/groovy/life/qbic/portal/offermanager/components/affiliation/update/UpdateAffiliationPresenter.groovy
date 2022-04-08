package life.qbic.portal.offermanager.components.affiliation.update

import life.qbic.business.persons.affiliation.update.UpdateAffiliationOutput
import life.qbic.datamodel.dtos.business.Affiliation
import life.qbic.portal.offermanager.dataresources.ResourcesService

class UpdateAffiliationPresenter implements UpdateAffiliationOutput {

  private final ResourcesService<Affiliation> affiliationResourcesService

  UpdateAffiliationPresenter(ResourcesService<Affiliation> affiliationResourcesService) {
    this.affiliationResourcesService = affiliationResourcesService
  }

  @Override
  void updatedAffiliation(life.qbic.business.persons.affiliation.Affiliation affiliation) {
    affiliationResourcesService.reloadResources()
  }

  @Override
  void failNotification(String notification) {

  }
}
