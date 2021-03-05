package life.qbic.business.products.archive

import life.qbic.datamodel.dtos.business.ProductId

/**
 * Input interface for the {@link ArchiveProduct} use case
 *
 * @since: 1.0.0
 *
 */
interface ArchiveProductInput {

    /**
     * A product defined by its product id should be archived
     * @param productId the product id for the product that will be archived
     * @since 1.0.0
     */
    void archive(ProductId productId)

}
