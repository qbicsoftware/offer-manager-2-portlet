package life.qbic.portal.offermanager.components.affiliation.update

import life.qbic.business.persons.affiliation.update.UpdateAffiliationOutput
import life.qbic.datamodel.dtos.business.Affiliation
import life.qbic.portal.offermanager.components.AppViewModel
import life.qbic.portal.offermanager.dataresources.ResourcesService

class UpdateAffiliationPresenter implements UpdateAffiliationOutput {

  private final ResourcesService<Affiliation> affiliationResourcesService
  private final AppViewModel sharedViewModel

  UpdateAffiliationPresenter(ResourcesService<Affiliation> affiliationResourcesService, AppViewModel sharedViewModel) {
    this.affiliationResourcesService = affiliationResourcesService
    this.sharedViewModel = sharedViewModel
  }

  @Override
  void updatedAffiliation(life.qbic.business.persons.affiliation.Affiliation affiliation) {
    affiliationResourcesService.reloadResources()
    def affiliationString = "${affiliation.getOrganization()}${affiliation.getAddressAddition() != null && !affiliation.getAddressAddition().isEmpty() ? " - ${affiliation.getAddressAddition()}" : ""}"
    sharedViewModel.successNotifications.add("Successfully updated affiliation " + affiliationString)
  }

  @Override
  void failNotification(String notification) {
    sharedViewModel.failureNotifications.add(notification)
  }
}
