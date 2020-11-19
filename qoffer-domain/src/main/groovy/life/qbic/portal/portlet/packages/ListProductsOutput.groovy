package life.qbic.portal.portlet.packages

import life.qbic.datamodel.dtos.business.services.Product
import life.qbic.portal.portlet.UseCaseFailure

/**
 * Interface to pass all available service products.
 *
 * This interface shall be implemented by classes, that
 * want to receive the output of the use case <emph>List Products</emph>.
 *
 * @since 1.0.0
 */
interface ListProductsOutput extends UseCaseFailure {

  /**
   * Will pass all available service products found.
   *
   * If no available service product can be found, the list
   * will be empty.
   *
   * @param availableProducts
   */
  void showAvailableProducts(List<Product> availableProducts)

}
