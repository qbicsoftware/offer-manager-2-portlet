package life.qbic.business.persons.affiliation.archive;

import java.util.Objects;
import life.qbic.business.logging.Logger;
import life.qbic.business.logging.Logging;
import life.qbic.business.persons.affiliation.Affiliation;

public class ArchiveAffiliation implements ArchiveAffiliationInput {

  private final ArchiveAffiliationOutput archiveAffiliationOutput;
  private final ArchiveAffiliationDataSource archiveAffiliationDataSource;

  private final Logging log = Logger.getLogger(this.getClass());

  public ArchiveAffiliation(ArchiveAffiliationOutput archiveAffiliationOutput, ArchiveAffiliationDataSource archiveAffiliationDataSource) {
    this.archiveAffiliationOutput = Objects.requireNonNull(archiveAffiliationOutput);
    this.archiveAffiliationDataSource = Objects.requireNonNull(
        archiveAffiliationDataSource);
  }

  @Override
  public void archive(Affiliation affiliation) {
    if (Objects.isNull(affiliation)) {
      log.error("Affiliation must not be null");
      archiveAffiliationOutput.failNotification("Archiving failed");
      return;
    }
    try {
      affiliation.archive();
      archiveAffiliationDataSource.archiveAffiliation(affiliation);
      archiveAffiliationOutput.archivedAffiliation(affiliation);
    } catch (Exception e) {
      log.error("Archiving failed", e);
      archiveAffiliationOutput.failNotification("Archiving failed");
    }
  }
}
