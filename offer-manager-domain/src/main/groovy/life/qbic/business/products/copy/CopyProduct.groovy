package life.qbic.business.products.copy

import life.qbic.business.exceptions.DatabaseQueryException
import life.qbic.business.products.create.CreateProductOutput
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
class CopyProduct implements CopyProductInput, CreateProductOutput {

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
/**
 * Sends failure notifications that have been
 * recorded during the use case.
 * @param notification containing a failure message
 * @since 1.0.0
 */

    @Override
    void failNotification(String notification) {

    }

    /**
     * A product has been created in the database
     * @param product The product that has been created
     * @since 1.0.0
     */
    @Override
    void created(Product product) {
        output.copied(product)
    }

    /**
     * The product is already stored in the database
     * @param product The product for which a duplicate has been found
     * @since 1.0.0
     */
    @Override
    void foundDuplicate(Product product) {
        //todo change product id and create
    }
}
