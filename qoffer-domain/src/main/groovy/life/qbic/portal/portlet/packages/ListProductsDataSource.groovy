package life.qbic.portal.portlet.packages

import life.qbic.datamodel.dtos.business.services.Product
import life.qbic.portal.portlet.exceptions.DatabaseQueryException

/**
 * Interface can be implemented to create access
 * to an underlying data-source.
 *
 * It provides access to available service packages.
 *
 * @since 1.0.0
 */
interface ListProductsDataSource {

  /**
   * Queries a data source for all available service
   * packages that have been defined by the organisation.
   *
   * Throws a {@link DatabaseQueryException} if the query
   * fails for some reason. An exception must NOT be thrown,
   * if no packages can be found. The returned list needs to
   * be empty then.
   *
   * @return A list of service {@link Product}.
   * @throws  DatabaseQueryException
   */
  List<Product> findAllAvailableProducts() throws DatabaseQueryException

}
