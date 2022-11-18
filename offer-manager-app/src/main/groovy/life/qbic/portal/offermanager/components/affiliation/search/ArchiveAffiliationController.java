package life.qbic.portal.offermanager.components.affiliation.search;

import java.util.Objects;
import life.qbic.business.RefactorConverter;
import life.qbic.business.persons.affiliation.archive.ArchiveAffiliationInput;
import life.qbic.datamodel.dtos.business.Affiliation;

public class ArchiveAffiliationController {

  private final ArchiveAffiliationInput archiveAffiliationInput;

  public ArchiveAffiliationController(ArchiveAffiliationInput archiveAffiliationInput) {
    this.archiveAffiliationInput = Objects.requireNonNull(archiveAffiliationInput);
  }

  public void archive(Affiliation affiliation) {
    life.qbic.business.persons.affiliation.Affiliation convertedAffiliation = RefactorConverter.toAffiliation(affiliation);
    archiveAffiliationInput.archive(convertedAffiliation);
  }




}
