package life.qbic.business.products.archive

import life.qbic.business.exceptions.DatabaseQueryException
import life.qbic.datamodel.dtos.business.ProductId
import life.qbic.datamodel.dtos.business.services.Product

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

    private final ArchiveProductDataSource dataSource
    private final ArchiveProductOutput output

    ArchiveProduct(ArchiveProductDataSource dataSource, ArchiveProductOutput output) {
        this.dataSource = dataSource
        this.output = output
    }

    @Override
    void archive(ProductId productId) {
        try {
            Optional<Product> searchResult = this.dataSource.fetch(productId)
            if (searchResult.isPresent()) {
                dataSource.archive(searchResult.get())
                output.archived(searchResult.get())
            } else {
                output.failNotification("Could not find a product with identifier ${productId.toString()}")
            }
        } catch (DatabaseQueryException ignored) {
            output.failNotification("Could not archive product ${productId.toString()}")
        }

    }
}
