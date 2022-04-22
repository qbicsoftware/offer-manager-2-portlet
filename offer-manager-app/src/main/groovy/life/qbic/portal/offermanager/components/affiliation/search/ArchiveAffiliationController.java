package life.qbic.portal.offermanager.components.affiliation.search;

import life.qbic.business.RefactorConverter;
import life.qbic.business.persons.affiliation.archive.ArchiveAffiliationInput;
import life.qbic.datamodel.dtos.business.Affiliation;

/**
 * <b>Holds a reference to the archive affiliation use case</b>
 *
 * @since 1.5.0
 */
public class ArchiveAffiliationController {

  private ArchiveAffiliationInput archiveAffiliationInput;

  public ArchiveAffiliationController(){}

  public ArchiveAffiliationController(ArchiveAffiliationInput archiveAffiliationInput) {
    this.archiveAffiliationInput = archiveAffiliationInput;
  }

  public void setUseCaseInput(ArchiveAffiliationInput archiveAffiliationInput) {
    this.archiveAffiliationInput = archiveAffiliationInput;
  }

  /**
   * Executes the archive affiliation use case
   * @param affiliation the affiliation to archive
   * @since 1.5.0
   */
  public void archiveAffiliation(Affiliation affiliation) {
    life.qbic.business.persons.affiliation.Affiliation affiliationConverted = RefactorConverter.toAffiliation(affiliation);
    archiveAffiliationInput.archiveAffiliation(affiliationConverted);
  }

}
