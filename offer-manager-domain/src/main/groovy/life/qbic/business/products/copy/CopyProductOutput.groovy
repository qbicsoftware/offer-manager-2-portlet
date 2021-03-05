package life.qbic.business.products.copy

import life.qbic.business.UseCaseFailure
import life.qbic.datamodel.dtos.business.services.Product

/**
 * Output interface for the {@link CopyProduct} use case
 *
 * @since: 1.0.0
 *
 */
interface CopyProductOutput extends UseCaseFailure {

    /**
     * A copy of a product has been created. This method is called after the copied product has been stored in the database.
     * @param product The product that has been copied
     * @since 1.0.0
     */
    void copied(Product product)
}
