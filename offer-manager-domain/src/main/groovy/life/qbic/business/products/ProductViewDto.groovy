package life.qbic.business.products

import life.qbic.datamodel.dtos.business.ProductCategory
import life.qbic.datamodel.dtos.business.ProductId
import life.qbic.datamodel.dtos.business.services.ProductUnit

/**
 * <b>A data-transfer object holding date to be transferred to the view</b>
 *
 * <p>This class holds information relevant to the view.</p>
 *
 * @since 1.1.0
 */
class ProductViewDto {
    final ProductCategory category
    final String name
    final String description
    final double unitPrice
    final ProductUnit unit
    final ProductId id
    final double quantity


}
