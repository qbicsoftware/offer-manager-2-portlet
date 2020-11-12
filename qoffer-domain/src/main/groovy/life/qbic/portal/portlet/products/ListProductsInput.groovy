package life.qbic.portal.portlet.products

/**
 * Interface to ask for available service products.
 *
 * This input interface should be implemented by use cases
 * that want to display that they will list
 * all available service products.
 *
 * @since 1.0.0
 */
interface ListProductsInput {

  /**
   * This method triggers the use case <emph>List Products</emph>.
   */
  void listAvailableProducts()

}
