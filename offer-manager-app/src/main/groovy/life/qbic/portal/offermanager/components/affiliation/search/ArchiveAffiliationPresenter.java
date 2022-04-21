package life.qbic.portal.offermanager.components.affiliation.search;

import life.qbic.business.RefactorConverter;
import life.qbic.business.persons.affiliation.Affiliation;
import life.qbic.business.persons.affiliation.archive.ArchiveAffiliationOutput;
import life.qbic.portal.offermanager.components.AppViewModel;
import life.qbic.portal.offermanager.dataresources.ResourcesService;

/**
 * <b><class short description - 1 Line!></b>
 *
 * <p><More detailed description - When to use, what it solves, etc.></p>
 *
 * @since <version tag>
 */
public class ArchiveAffiliationPresenter implements ArchiveAffiliationOutput {

  private final AppViewModel sharedViewModel;

  private final SearchAffiliationViewModel searchAffiliationViewModel;

  private final ResourcesService<life.qbic.datamodel.dtos.business.Affiliation> affiliationResourcesService;

  public ArchiveAffiliationPresenter(AppViewModel sharedViewModel,
      SearchAffiliationViewModel searchAffiliationViewModel,
      ResourcesService<life.qbic.datamodel.dtos.business.Affiliation> affiliationResourcesService) {
    this.sharedViewModel = sharedViewModel;
    this.searchAffiliationViewModel = searchAffiliationViewModel;
    this.affiliationResourcesService = affiliationResourcesService;
  }

  @Override
  public void failNotification(String notification) {
    sharedViewModel.getFailureNotifications().add(notification);
  }

  @Override
  public void affiliationArchived(Affiliation affiliation) {
    life.qbic.datamodel.dtos.business.Affiliation affiliationDto = RefactorConverter.toAffiliationDto(
        affiliation);
    affiliationResourcesService.removeFromResource(affiliationDto);
    searchAffiliationViewModel.getAffiliations().remove(affiliationDto);
    sharedViewModel.getSuccessNotifications().add("Archived affiliation!");
  }
}
