package life.qbic.portal.portlet.packages

import life.qbic.portal.portlet.UseCaseFailure

/**
 * Interface to pass all available service packages.
 *
 * This interface shall be implemented by classes, that
 * want to receive the output of the use case <emph>List Packages</emph>.
 *
 * @since 1.0.0
 */
interface ListPackagesOutput extends UseCaseFailure {

  /**
   * Will pass all available service packages found.
   *
   * If no available service package can be found, the list
   * will be empty.
   *
   * @param availablePackages
   */
  void showAvailablePackages(List<Package> availablePackages)

}
