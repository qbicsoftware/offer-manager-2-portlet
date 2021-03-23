package life.qbic.business.products.copy

import life.qbic.datamodel.dtos.business.ProductId
import life.qbic.datamodel.dtos.business.services.Product

/**
 * Input interface for the {@link CopyProduct} use case
 *
 * @since: 1.0.0
 *
 */
interface CopyProductInput {

    /**
     * Creates a product and populates it with provided information
     * @param product The modified product information. The identifier should already be present.
     * @since 1.0.0
     */
    void copyModified(Product product)
}
