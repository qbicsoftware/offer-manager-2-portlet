package life.qbic.portal.qoffer2.products

import groovy.sql.GroovyRowResult
import groovy.util.logging.Log4j2
import life.qbic.datamodel.dtos.business.services.PrimaryAnalysis
import life.qbic.datamodel.dtos.business.services.Product
import life.qbic.datamodel.dtos.business.services.ProductUnit
import life.qbic.portal.portlet.exceptions.DatabaseQueryException
import life.qbic.portal.portlet.packages.ListProductsDataSource
import life.qbic.portal.qoffer2.database.ConnectionProvider
import org.apache.groovy.sql.extensions.SqlExtensions

import java.sql.ResultSet
import java.sql.SQLException

/**
 * Provides a MariaDB connector implementation for {@link ListProductsDataSource}.
 *
 * @since 1.0.0
 */
@Log4j2
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
  List<Product> findAllAvailablePackages() throws DatabaseQueryException {
    try {
      tryToFindAllProducts()
    } catch (SQLException e) {
      log.error(e.message)
      log.error(e.stackTrace.join("\n"))
      throw new DatabaseQueryException("Unable to list all available products.")
    }
  }

  private List<Product> tryToFindAllProducts() {
    def packages = []
    provider.connect().withCloseable {
      final def query = it.prepareStatement(Queries.SELECT_ALL_PRODUCTS)
      final ResultSet result = query.executeQuery()
      packages.addAll(convertResultSet(result))
    }
    return packages
  }

  private static List<Product> convertResultSet(ResultSet resultSet) {
    final def packages = []
    while (resultSet.next()) {
      packages.add(rowResultToProduct(SqlExtensions.toRowResult(resultSet)))
    }
    return packages
  }

  private static Product rowResultToProduct(GroovyRowResult row) {
    def productCategory = row.category
    Product product
    switch(productCategory) {
      case "Primary Bioinformatics":
        product = new PrimaryAnalysis(row.productName as String,
            row.description as String,
            row.unitPrice as Double,
            ProductUnit.PER_SAMPLE
        )
        break
    }
    if(product == null) {
      log.error("Product could not be parsed from database query.")
      log.error(row)
      throw new DatabaseQueryException("Cannot parse product")
    } else {
      return product
    }
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
