package life.qbic.business.products.dtos

import life.qbic.datamodel.dtos.business.ProductCategory
import life.qbic.datamodel.dtos.business.facilities.Facility
import life.qbic.datamodel.dtos.business.services.ProductUnit

/**
 * <b>A draft for a product</b>
 *
 * <p>This data transfer object contains information necessary to create a product.</p>
 *
 * @since 1.2.1
 */
class ProductDraft {
    final ProductCategory category
    final String name
    final String description
    final double internalUnitPrice
    final double externalUnitPrice
    final ProductUnit unit
    final Facility serviceProvider

    private ProductDraft(ProductCategory category, String name, String description, double internalUnitPrice, double externalUnitPrice, ProductUnit unit, Facility serviceProvider) {
        this.category = category
        this.name = name
        this.description = description
        this.internalUnitPrice = internalUnitPrice
        this.externalUnitPrice = externalUnitPrice
        this.unit = unit
        this.serviceProvider = serviceProvider
    }

    static ProductDraft create(ProductCategory category, String name, String description, double internalUnitPrice, double externalUnitPrice, ProductUnit unit, Facility serviceProvider) {
        return new ProductDraft(category,
                name,
                description,
                internalUnitPrice,
                externalUnitPrice,
                unit,
                serviceProvider)
    }
}
