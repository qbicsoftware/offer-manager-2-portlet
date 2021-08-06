package life.qbic.business.offers

import groovy.transform.EqualsAndHashCode

/**
 * <h1>Item that contains the information about a product item on the offer</h1>
 *
 * <p>This DTO should protect the business logic from being exposed to the external layers</p>
 *
 * @since 1.1.0
 *
*/
@EqualsAndHashCode
class OfferItem {
    final double quantity
    final String productDescription
    final String productName
    final double unitPrice
    final double quantityDiscount
    final String serviceProvider
    final String unit
    final double itemTotal

    static class Builder {
        double quantity
        String productDescription
        String productName
        double unitPrice
        double quantityDiscount
        String serviceProvider
        String unit
        double itemTotal

        Builder(double quantity, String productDescription, String productName, double unitPrice, double quantityDiscount, String serviceProvider, String unit, double itemTotal) {
            this.quantity =  Objects.requireNonNull(quantity, "Quantity must not be null")
            this.productDescription =  Objects.requireNonNull(productDescription, "Product description must not be null")
            this.productName =  Objects.requireNonNull(productName, "Product name must not be null")
            this.unitPrice =  Objects.requireNonNull(unitPrice, "Unit price must not be null")
            this.quantityDiscount =  Objects.requireNonNull(quantityDiscount, "Quantity discount must not be null")
            this.serviceProvider =  Objects.requireNonNull(serviceProvider, "Service provider must not be null")
            this.unit =  Objects.requireNonNull(unit, "Unit must not be null")
            this.itemTotal =  Objects.requireNonNull(itemTotal, "Item total must not be null")

        }

        OfferItem build() {
            return new OfferItem(this)
        }
    }

    private OfferItem(Builder builder) {
        this.quantity = builder.quantity
        this.productDescription = builder.productDescription
        this.productName = builder.productName
        this.unitPrice = builder.unitPrice
        this.quantityDiscount = builder.quantityDiscount
        this.serviceProvider = builder.serviceProvider
        this.unit = builder.unit
        this.itemTotal = builder.itemTotal
    }
}