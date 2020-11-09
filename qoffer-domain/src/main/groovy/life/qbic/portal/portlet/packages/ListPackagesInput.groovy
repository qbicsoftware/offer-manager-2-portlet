package life.qbic.portal.portlet.packages

/**
 * Interface to ask for available service packages.
 *
 * This input interface should be implemented by use cases
 * that want to display that they will list
 * all available service packages.
 *
 * @author Sven Fillinger
 * @since 1.0.0
 */
interface ListPackagesInput {

  /**
   * This method triggers the use case <emph>List Packages</emph>.
   */
  void listAvailablePackages()

}
