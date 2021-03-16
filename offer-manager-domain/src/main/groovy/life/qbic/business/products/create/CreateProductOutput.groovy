package life.qbic.business.products.create

import life.qbic.business.UseCaseFailure
import life.qbic.datamodel.dtos.business.services.Product

/**
 * Output interface for the {@link CreateProduct} use case
 *
 * @since: 1.0.0
 *
 */
interface CreateProductOutput extends UseCaseFailure{

    /**
     * A product has been created in the database
     * @param product The product that has been created
     * @since 1.0.0
     */
    void created(Product product)

    /**
     * The product is already stored in the database
     * @param product The product for which a duplicate has been found
     * @since 1.0.0
     */
    void foundDuplicate(Product product)


}
