package life.qbic.business.products.archive

import life.qbic.business.UseCaseFailure
import life.qbic.business.products.Product

/**
 * Output interface for the {@link ArchiveProduct} use case
 *
 * @since: 1.0.0
 *
 */
interface ArchiveProductOutput extends UseCaseFailure {

  /**
   * A product has been archived in the database
   * @param product The product that has been archived
   * @since 1.0.0
   */
  void archived(Product product)
}
