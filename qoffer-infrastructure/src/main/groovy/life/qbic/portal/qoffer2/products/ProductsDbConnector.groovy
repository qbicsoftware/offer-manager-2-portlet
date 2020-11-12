package life.qbic.portal.qoffer2.products

import life.qbic.portal.portlet.exceptions.DatabaseQueryException
import life.qbic.portal.portlet.packages.ListPackagesDataSource

/**
 * <class short description - 1 Line!>
 *
 * <More detailed description - When to use, what it solves, etc.>
 *
 * @since 1.0.0
 */
class ProductsDbConnector implements ListPackagesDataSource {
  @Override
  List<Package> findAllAvailablePackages() throws DatabaseQueryException {
    return null
  }
}
