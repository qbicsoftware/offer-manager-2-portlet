package life.qbic.portal.offermanager.components.affiliation.search;

import java.util.Objects;
import life.qbic.business.RefactorConverter;
import life.qbic.business.persons.affiliation.archive.ArchiveAffiliationOutput;
import life.qbic.datamodel.dtos.business.Affiliation;
import life.qbic.portal.offermanager.components.AppViewModel;
import life.qbic.portal.offermanager.dataresources.ResourcesService;

public class ArchiveAffiliationPresenter implements ArchiveAffiliationOutput {

  private final ResourcesService<Affiliation> affiliationResourcesService;

  private final AppViewModel sharedViewModel;

  public ArchiveAffiliationPresenter(ResourcesService<Affiliation> affiliationResourcesService,
      AppViewModel sharedViewModel) {
    this.affiliationResourcesService = Objects.requireNonNull(affiliationResourcesService);
    this.sharedViewModel = Objects.requireNonNull(sharedViewModel);
  }


  @Override
  public void failNotification(String notification) {
    sharedViewModel.getFailureNotifications().add(notification);
  }

  @Override
  public void archivedAffiliation(life.qbic.business.persons.affiliation.Affiliation affiliation) {
    affiliationResourcesService.reloadResources();
    affiliationResourcesService.removeFromResource(RefactorConverter.toAffiliationDto(affiliation));
    sharedViewModel.getSuccessNotifications().add("Archived affiliation successfully");
  }
}
