package life.qbic.business.products.archive

import life.qbic.datamodel.dtos.business.ProductId

/**
 * <h1>4.3.2 Archive Service Product</h1>
 * <br>
 * <p> Offer Administrators are allowed to archive existing products.
 * <br> The archived products should be still available in old offers but not selectable for new offers.
 * </p>
 *
 * @since: 1.0.0
 *
 */
class ArchiveProduct implements ArchiveProductInput {
    @Override
    void archive(ProductId productId) {

    }
}
