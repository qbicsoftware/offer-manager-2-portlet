package life.qbic.portal.portlet.packages

import groovy.util.logging.Log4j2
import life.qbic.portal.portlet.exceptions.DatabaseQueryException

/**
 * <class short description - 1 Line!>
 *
 * <More detailed description - When to use, what it solves, etc.>
 *
 * @since <versiontag>
 */
@Log4j2
class ListPackages implements ListPackagesInput {

  private final ListPackagesOutput output

  private final ListPackagesDataSource source

  ListPackages(ListPackagesDataSource source, ListPackagesOutput output) {
    this.output = output
    this.source = source
  }

  @Override
  void listAvailablePackages() {
    try {
      List<Package> availablePackages = source.findAllAvailablePackages()
      output.showAvailablePackages(availablePackages)
    } catch (DatabaseQueryException e) {
      log.error(e)
      output.failNotification("Something went wrong during the request of available packages.")
    }
  }
}
