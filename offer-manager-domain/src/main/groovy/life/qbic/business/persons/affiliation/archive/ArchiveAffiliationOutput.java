package life.qbic.business.persons.affiliation.archive;

import life.qbic.business.UseCaseFailure;
import life.qbic.business.persons.affiliation.Affiliation;


public interface ArchiveAffiliationOutput extends UseCaseFailure {

  void archivedAffiliation(Affiliation affiliation);

}
