package life.qbic.business.products.copy

import life.qbic.datamodel.dtos.business.ProductId

/**
 * Input interface for the {@link CopyProduct} use case
 *
 * @since: 1.0.0
 *
 */
interface CopyProductInput {

    /**
     * The content of a product is going to be copied
     * @param productId The id of the product that should be copied
     */
    void copy(ProductId productId)
}
