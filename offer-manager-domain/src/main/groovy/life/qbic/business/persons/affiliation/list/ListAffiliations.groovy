package life.qbic.business.persons.affiliation.list

import life.qbic.business.persons.affiliation.Affiliation

import java.util.stream.Collectors


/**
 * This class implements the List Affiliations use case.
 *
 * This use case does not require any input, but needs to be triggered.
 *
 * It will query the available customer affiliations from a connected data source
 * and submit the result back to the output interface.
 *
 * @author Sven Fillinger
 * @since 1.0.0
 */
class ListAffiliations implements ListAffiliationsInput {

  private final ListAffiliationsOutput output

  private final ListAffiliationsDataSource dataSource

  /**
   * List Affiliations Use Case
   *
   * Lists the available affiliations for persons.
   * @param output Where the use case submits the result to
   * @param dataSource The database dataSource to make the query
   */
  ListAffiliations(ListAffiliationsOutput output, ListAffiliationsDataSource dataSource) {
    this.output = output
    this.dataSource = dataSource
  }

  @Override
  void listAffiliations() {
    def activeAffiliations = dataSource.listAllAffiliations()
            .stream().filter( Affiliation::isActive )
            .collect(Collectors.toList())
    this.output.reportAvailableAffiliations(activeAffiliations)
  }
}
