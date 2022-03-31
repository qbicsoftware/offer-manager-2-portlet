package life.qbic.business.products.archive

import life.qbic.business.exceptions.DatabaseQueryException
import life.qbic.business.products.Product

/**
 * <h1>Data souce interface for {@link life.qbic.business.products.archive.ArchiveProduct}</h1>
 *
 * @since 1.0.0
 */
interface ArchiveProductDataSource {

  /**
   * A product is archived by setting it inactive
   * @param product The product that needs to be archived
   * @since 1.0.0
   * @throws life.qbic.business.exceptions.DatabaseQueryException
   */
  void archive(Product product) throws DatabaseQueryException

  /**
   * Fetches a product from the database
   * @param productId The product id of the product to be fetched
   * @return returns an optional that contains the product if it has been found
   * @since 1.0.0
   * @throws life.qbic.business.exceptions.DatabaseQueryException is thrown when any technical interaction with the data source fails
   */
  Optional<Product> fetch(String productId) throws DatabaseQueryException

}
