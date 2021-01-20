package life.qbic.portal.portlet.products

import groovy.util.logging.Log4j2
import life.qbic.datamodel.dtos.business.services.Product
import life.qbic.portal.portlet.exceptions.DatabaseQueryException

/**
 * Implementation of the use case <emph>List Products</emph>.
 *
 * This use case returns all available service products of QBiC.
 *
 * @since 1.0.0
 */
@Log4j2
class ListProducts {

  private final ListProductsDataSource source

    ListProducts(ListProductsDataSource source) {
    this.source = source
  }
}
