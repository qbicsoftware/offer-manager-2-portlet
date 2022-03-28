package life.qbic.business.products.create


import life.qbic.business.products.ProductDraft

/**
 * Input interface for the {@link CreateProduct} use case
 *
 * @since: 1.0.0
 *
 */
interface CreateProductInput {

  /**
   * A product is created in the database
   * @param productDraft The productDraft containing information about the product to be added the database
   * @since 1.0.0
   */
  void create(ProductDraft productDraft)

}
