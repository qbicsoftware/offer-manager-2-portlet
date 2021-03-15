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
     * @since 1.0.0
     */
    void create(Product product)

}
