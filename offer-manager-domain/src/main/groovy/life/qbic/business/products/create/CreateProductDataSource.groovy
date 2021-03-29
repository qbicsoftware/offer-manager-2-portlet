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
     * Stores a product in the database
     * @param product The product that needs to be stored
     * @return The product identifier of the stored product
     * @since 1.0.0
     * @throws DatabaseQueryException if any technical interaction with the data source fails
     * @throws ProductExistsException if the product already exists in the data source
     */
    ProductId store(Product product) throws DatabaseQueryException, ProductExistsException

}
