package life.qbic.portal.qoffer2.products

import groovy.transform.CompileStatic
import groovy.util.logging.Log4j2
import life.qbic.portal.portlet.exceptions.DatabaseQueryException
import life.qbic.portal.portlet.packages.ListProductsDataSource
import life.qbic.portal.qoffer2.database.ConnectionProvider

import java.sql.SQLException

/**
 * Provides a MariaDB connector implementation for {@link ListProductsDataSource}.
 *
 * @since 1.0.0
 */
@Log4j2
@CompileStatic
class ProductsDbConnector implements ListProductsDataSource {

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
    try {
      tryToFindAllProducts()
    } catch (SQLException e) {
      log.error(e.message)
      log.error(e.stackTrace.join("\n"))
      throw new DatabaseQueryException("Unable to list all available products.")
    }
  }

  private List<Package> tryToFindAllProducts() {
    provider.connect().withCloseable {
      final def query = it.prepareStatement(Queries.SELECT_ALL_PRODUCTS)
      query.executeQuery()
    }
    return []
  }

  /**
   * Class that encapsulates the available SQL queries.
   */
  private static class Queries {

    /**
     * Query for all available products.
     */
    final static String SELECT_ALL_PRODUCTS = "SELECT * FROM products"

  }
}
