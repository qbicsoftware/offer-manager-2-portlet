package life.qbic.business.products.list

import life.qbic.business.exceptions.DatabaseQueryException
import life.qbic.datamodel.dtos.business.services.Product

/**
 * Provides functionality to list persons
 *
 * @since 1.0.0
 */
interface ListProductsDataSource {

    /**
     * <p>Queries a data source for all available service
     * product that have been defined by the organisation.</p>
     *
     * <p>Throws a {@link DatabaseQueryException} if the query
     * fails for some reason. An exception must NOT be thrown,
     * if no product can be found. The returned list needs to
     * be empty then.</p>
     *
     * @return A list of service {@link Product}.
     * @throws DatabaseQueryException
     */
    List<Product> listProducts() throws DatabaseQueryException

}