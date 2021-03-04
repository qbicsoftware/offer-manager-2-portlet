package life.qbic.business.products.create

import life.qbic.datamodel.dtos.business.services.Product

/**
 * Input interface for the {@link CreateProduct} use case
 *
 * @since: 1.0.0
 *
 */
interface CreateProductInput {

    /**
     * A product is created in the database
     * @param product The product that is added to the database
     */
    void create(Product product)

    /**
     * Even though a duplicate product in the database exist a new product should be added.
     * The duplicate product is required to have a different product id!
     * @param product The product that should be added
     */
    void createDuplicate(Product product)
}
