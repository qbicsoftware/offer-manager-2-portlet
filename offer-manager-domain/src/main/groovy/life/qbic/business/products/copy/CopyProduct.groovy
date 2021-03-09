package life.qbic.business.products.copy

import life.qbic.business.exceptions.DatabaseQueryException
import life.qbic.datamodel.dtos.business.ProductId
import life.qbic.datamodel.dtos.business.services.Product

/**
 * <h1>4.3.2 Copy Service Product</h1>
 * <br>
 * <p> Offer Administrators are allowed to create a new permutation of an existing product.
 * <br> New permutations can include changes in unit price, sequencing technology and other attributes of service products.
 * </p>
 *
 * @since: 1.0.0
 *
 */
class CopyProduct implements CopyProductInput {

    private final CopyProductDataSource dataSource
    private final CopyProductOutput output

    CopyProduct(CopyProductDataSource dataSource, CopyProductOutput output) {
        this.dataSource = dataSource
        this.output = output
    }

    @Override
    void copy(ProductId productId) {
        try {
            Optional<Product> searchResult = dataSource.fetch(productId)
            if (searchResult.isPresent()) {
                output.copied(searchResult.get())
            } else {
                output.failNotification("Could not find any product for $productId")
            }
        } catch (DatabaseQueryException ignored) {
            output.failNotification("Could not copy product $productId")
        }
    }
}
