package life.qbic.business.products.create

import life.qbic.business.UseCaseFailure
import life.qbic.business.products.Product

/**
 * Output interface for the {@link CreateProduct} use case
 *
 * @since: 1.0.0
 *
 */
interface CreateProductOutput extends UseCaseFailure {

  /**
   * A product has been created in the database
   * @param product The product that has been created
   * @since 1.0.0
   */
  void created(Product product)

  /**
   * Multiple instances of the product are already stored in the database
   * @param product The product for which multiple duplicates have been found
   * @since 1.2.0
   */
  void foundDuplicates(List<Product> product)


}
