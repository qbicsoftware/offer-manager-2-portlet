package life.qbic.portal.portlet.packages

import groovy.util.logging.Log4j2
import life.qbic.portal.portlet.exceptions.DatabaseQueryException

/**
 * Implementation of the use case <emph>List Packages</emph>.
 *
 * This use case returns all available service packages of QBiC.
 *
 * @since 1.0.0
 */
@Log4j2
class ListProducts implements ListProductsInput {

  private final ListProductsOutput output

  private final ListProductsDataSource source

  ListProducts(ListProductsDataSource source, ListProductsOutput output) {
    this.output = output
    this.source = source
  }

  @Override
  void listAvailableProducts() {
    try {
      List<Package> availablePackages = source.findAllAvailableProducts()
      output.showAvailableProducts(availablePackages)
    } catch (DatabaseQueryException e) {
      log.error(e)
      output.failNotification("Something went wrong during the request of available packages.")
    }
  }
}
