package life.qbic.portal.offermanager.components.affiliation.search;

import life.qbic.business.RefactorConverter;
import life.qbic.business.persons.affiliation.Affiliation;
import life.qbic.business.persons.affiliation.archive.ArchiveAffiliationOutput;
import life.qbic.portal.offermanager.components.AppViewModel;
import life.qbic.portal.offermanager.dataresources.ResourcesService;

/**
 * <b>Archive Affiliation Presenter</b>
 * <p>
 * Callback implementation for the
 * {@link life.qbic.business.persons.affiliation.archive.ArchiveAffiliation} use case.
 *
 * @since 1.5.0
 */
public class ArchiveAffiliationPresenter implements ArchiveAffiliationOutput {

  private final AppViewModel sharedViewModel;

  private final SearchAffiliationViewModel searchAffiliationViewModel;
  private final SearchAffiliationView searchAffiliationView;

  private final ResourcesService<life.qbic.datamodel.dtos.business.Affiliation> affiliationResourcesService;

  public ArchiveAffiliationPresenter(AppViewModel sharedViewModel,
      SearchAffiliationViewModel searchAffiliationViewModel,
      SearchAffiliationView searchAffiliationView,
      ResourcesService<life.qbic.datamodel.dtos.business.Affiliation> affiliationResourcesService) {
    this.sharedViewModel = sharedViewModel;
    this.searchAffiliationViewModel = searchAffiliationViewModel;
    this.searchAffiliationView = searchAffiliationView;
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
    searchAffiliationViewModel.resetAffiliations();
    searchAffiliationView.refresh();
    sharedViewModel.getSuccessNotifications().add("Archived affiliation!");
  }
}
