package life.qbic.business.products.archive

import life.qbic.business.UseCaseFailure
import life.qbic.datamodel.dtos.business.services.Product

/**
 * Output interface for the {@link ArchiveProduct} use case
 *
 * @since: 1.0.0
 *
 */
interface ArchiveProductOutput extends UseCaseFailure{

    /**
     * A product has been archived in the database
     * @param product The product that has been archived
     */
    void archived(Product product)
}
