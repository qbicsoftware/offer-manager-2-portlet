package life.qbic.business.products

import life.qbic.business.exceptions.DatabaseQueryException
import life.qbic.datamodel.dtos.business.ProductId
import life.qbic.datamodel.dtos.business.services.Product

/**
 * Defines the methods of the Datasource implementation
 *
 * @since: 1.0.0
 *
 */
interface ProductDataSource {

    /**
     * Fetches a product from the database
     * @param productId The product id of the product to be fetched
     * @return returns an optional that contains the product if it has been found
     * @throws DatabaseQueryException
     */
    Optional<Product> fetch(ProductId productId) throws DatabaseQueryException

    /**
     * Stores a product in the database
     * @param product The product that needs to be stored
     * @throws DatabaseQueryException
     */
    void store(Product product) throws DatabaseQueryException

    /**
     * A product is archived by setting it inactive
     * @param product The product that needs to be archived
     * @throws DatabaseQueryException
     */
    void archive(Product product) throws DatabaseQueryException
}