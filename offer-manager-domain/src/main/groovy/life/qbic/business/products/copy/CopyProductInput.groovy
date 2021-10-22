package life.qbic.business.products.copy

import life.qbic.business.products.dtos.ProductDraft
import life.qbic.datamodel.dtos.business.ProductId

/**
 * Input interface for the {@link CopyProduct} use case
 *
 * @since: 1.0.0
 *
 */
interface CopyProductInput {

    /**
     * Creates a product and populates it with provided information
     * @param productDraft The modified product information
     * @param originalProductId the id of the origin Product
     * @since 1.0.0
     */
    void copyModified(ProductDraft productDraft, ProductId originalProductId)
}
