package life.qbic.portal.portlet.customers.affiliation

import life.qbic.portal.portlet.customers.CustomerDbGateway

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

  private final CustomerDbGateway gateway

  /**
   * List Affiliations Use Case
   *
   * Lists the available affiliations for customers.
   * @param output Where the use case submits the result to
   * @param gateway The database gateway to make the query
   */
  ListAffiliations(ListAffiliationsOutput output, CustomerDbGateway gateway) {
    this.output = output
    this.gateway = gateway
  }

  @Override
  void listAffiliations() {
    def affiliations = gateway.getAllAffiliations()
    this.output.reportAvailableAffiliations(affiliations)
  }
}
