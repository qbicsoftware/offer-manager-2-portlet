package life.qbic.business.products.create

import life.qbic.business.exceptions.DatabaseQueryException
import life.qbic.datamodel.dtos.business.ProductId
import life.qbic.datamodel.dtos.business.services.Product

/**
 * <h1>Data source for {@link life.qbic.business.products.create.CreateProduct}</h1>
 *
 * @since 1.0.0
 */
interface CreateProductDataSource {

    /**
     * Fetches a product from the database
     * @param productId The product id of the product to be fetched
     * @return returns an optional that contains the product if it has been found
     * @since 1.0.0
     * @throws life.qbic.business.exceptions.DatabaseQueryException
     */
    Optional<Product> fetch(ProductId productId) throws DatabaseQueryException

    /**
     * Stores a product in the database
     * @param product The product that needs to be stored
     * @since 1.0.0
     * @throws DatabaseQueryException if any technical interaction with the data source fails
     * @throws ProductExistsException if the product already exists in the data source
     */
    void store(Product product) throws DatabaseQueryException, ProductExistsException
}