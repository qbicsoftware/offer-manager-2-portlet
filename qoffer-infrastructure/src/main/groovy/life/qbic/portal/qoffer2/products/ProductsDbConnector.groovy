package life.qbic.portal.qoffer2.products

import life.qbic.portal.portlet.exceptions.DatabaseQueryException
import life.qbic.portal.portlet.packages.ListPackagesDataSource
import life.qbic.portal.qoffer2.database.ConnectionProvider

/**
 * Provides a MariaDB connector implementation for {@link ListPackagesDataSource}.
 *
 * @since 1.0.0
 */
class ProductsDbConnector implements ListPackagesDataSource {

  private final ConnectionProvider provider

  /**
   * Creates a connector for a MariaDB instance.
   *
   * The class instantiation will fail, if the passed provider is null.
   *
   * @param provider A connection provider
   */
  ProductsDbConnector(ConnectionProvider provider) {
    this.provider = Objects.requireNonNull(provider, "Provider must not be null.")
  }

  @Override
  List<Package> findAllAvailablePackages() throws DatabaseQueryException {
    return null
  }
}
