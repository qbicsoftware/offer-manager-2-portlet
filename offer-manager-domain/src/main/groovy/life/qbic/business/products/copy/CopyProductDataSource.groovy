package life.qbic.business.products.copy

import life.qbic.business.exceptions.DatabaseQueryException
import life.qbic.datamodel.dtos.business.ProductId
import life.qbic.datamodel.dtos.business.services.Product

/**
 * <h1>Data source for the {@link life.qbic.business.products.copy.CopyProduct} use case</h1>
 *
 * @since 1.0.0
 */
interface CopyProductDataSource {

    /**
     * Fetches a product from the database
     * @param productId The product id of the product to be fetched
     * @return returns an optional that contains the product if it has been found
     * @since 1.0.0
     * @throws life.qbic.business.exceptions.DatabaseQueryException
     */
    Optional<Product> fetch(ProductId productId) throws DatabaseQueryException

    /**
     * Fetches the latest version of the provided product id
     * @param productId
     * @return the latest version of the product id if found at all - Optional.empty otherwise
     * @throws DatabaseQueryException in case of failure due to technical reasons
     */
    Optional<Long> latestVersion(ProductId productId) throws DatabaseQueryException

}
