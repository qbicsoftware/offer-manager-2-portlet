package life.qbic.business.products.copy

import life.qbic.datamodel.dtos.business.ProductId
import life.qbic.datamodel.dtos.business.services.Product

/**
 * Input interface for the {@link CopyProduct} use case
 *
 * @since: 1.0.0
 *
 */
interface CopyProductInput {

    /**
     * creates a copy of an existing product
     * @param productId The id of the product that should be copied
     * @since 1.0.0
     */
    void copy(ProductId productId)

    /**
     * creates a copy of an existing product
     * @param product The modified product information
     * @since 1.0.0
     */
    void copy(Product productId)


/*    *//**
     * Even though a duplicate product in the database exist a new product should be added.
     * The new product will receive a new id that allows to differentiate it from the old product. The old id will be ignored.
     * @param product The product that should be added
     * @since 1.0.0
     *//*
    void createDuplicate(Product product)*/
}
