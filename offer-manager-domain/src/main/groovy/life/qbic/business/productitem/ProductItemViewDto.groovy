package life.qbic.business.productitem

import life.qbic.datamodel.dtos.business.ProductCategory

/**
 * <b>Information about a offer position that should be visible to the application</b>
 * <p>This item holds all information necessary for listing the position in an offer</p>
 *
 * @since 1.1.0
 */
class ProductItemViewDto {

    /**
     * Describes the category of the product
     */
    final ProductCategory productCategory

    /**
     * Describes the currency under which the product is charged
     */
    final Currency currency

    /**
     * Provides a more detailed description of the product
     */
    final String description

    /**
     * The name of the product
     */
    final String productName

    /**
     * The price of one unit of the product
     */
    final Double unitPrice

    /**
     * The unit in which the product is offered
     */
    final String unit

    /**
     * The quantity of the product in this item
     */
    final double quantity

    /**
     * The charged amount (without discounts, overheads, ...)
     */
    final double amount

    /**
     * The quantity discount amount applied to this item
     */
    final double quantityDiscount

    ProductItemViewDto(ProductCategory productCategory, Currency currency, String description, String productName, Double unitPrice, String unit, double quantity, double amount, double quantityDiscount) {
        this.productCategory = productCategory
        this.currency = currency
        this.description = description
        this.productName = productName
        this.unitPrice = unitPrice
        this.unit = unit
        this.quantity = quantity
        this.amount = amount
        this.quantityDiscount = quantityDiscount
    }
}
