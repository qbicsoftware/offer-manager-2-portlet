package life.qbic.business.products.create

import life.qbic.business.exceptions.DatabaseQueryException
import life.qbic.business.products.Product
import life.qbic.business.products.ProductDraft

/**
 * <h1>Data source for {@link life.qbic.business.products.create.CreateProduct}</h1>
 *
 * @since 1.0.0
 */
interface CreateProductDataSource {

  /**
   * Stores a product in the database
   * @param product The product that needs to be stored
   * @return The product identifier of the stored product
   * @since 1.0.0
   * @throws DatabaseQueryException if any technical interaction with the data source fails
   */
  Product store(ProductDraft productDraft) throws DatabaseQueryException

  /**
   * Finds all duplicates stored in the database for a given productDraft
   * @param product The productDraft for which the database is checked for duplicate entries
   * @return List of products containing the same properties as the productDraft
   * @since 1.2.0
   * @throws DatabaseQueryException if any technical interaction with the data source fails
   */
  List<Product> findDuplicateProducts(ProductDraft productDraft) throws DatabaseQueryException

}
