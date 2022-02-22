package life.qbic.business.offers

import groovy.transform.EqualsAndHashCode

import java.math.RoundingMode

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
    final double discountPerUnit
    final double discountPercentage
    final String serviceProvider
    final String unit
    final double itemTotal
    final String category

    static class Builder {
        double quantity
        String productDescription
        String productName
        double unitPrice
        double quantityDiscount
        double discountPerUnit
        double discountPercentage
        String serviceProvider
        String unit
        double itemTotal
        String category

        /**
         * <p>Beware that for all currency values, we explicitly round them, by applying
         * {@link java.math.BigDecimal#setScale(int, java.math.RoundingMode)} with
         * a scale of 2 and {@link java.math.RoundingMode#HALF_UP} explicitly.</p>
         * For example <code>2.356</code> becomes <code>2.36</code>.
         * @param quantity
         * @param productDescription
         * @param productName
         * @param unitPrice
         * @param quantityDiscount
         * @param discountPerUnit
         * @param discountPercentage
         * @param serviceProvider
         * @param unit
         * @param itemTotal
         */
        Builder(double quantity, String productDescription, String productName, double unitPrice, double quantityDiscount, double discountPerUnit, double discountPercentage, String serviceProvider, String unit, double itemTotal) {
            this.quantity =  Objects.requireNonNull(quantity, "Quantity must not be null")
            this.productDescription =  Objects.requireNonNull(productDescription, "Product description must not be null")
            this.productName =  Objects.requireNonNull(productName, "Product name must not be null")
            this.unitPrice =  Objects.requireNonNull(formatCurrency(unitPrice), "Unit price must not be null")
            this.quantityDiscount =  Objects.requireNonNull(formatCurrency(quantityDiscount), "Quantity discount must not be null")
            this.discountPerUnit = Objects.requireNonNull(formatCurrency(discountPerUnit), "Discount per unit must not be null")
            this.discountPercentage = Objects.requireNonNull(discountPercentage, "Discount percentage must not be null")
            this.serviceProvider =  Objects.requireNonNull(serviceProvider, "Service provider must not be null")
            this.unit =  Objects.requireNonNull(unit, "Unit must not be null")
            this.itemTotal =  Objects.requireNonNull(formatCurrency(itemTotal), "Item total must not be null")

        }

        Builder setCategory(String category) {
            this.category = category
            return this;
        }

        OfferItem build() {
            return new OfferItem(this)
        }

        private static double formatCurrency(Double value) {
            return BigDecimal.valueOf(value).setScale(2, RoundingMode.HALF_UP).doubleValue()
        }
    }

    private OfferItem(Builder builder) {
        this.quantity = builder.quantity
        this.productDescription = builder.productDescription
        this.productName = builder.productName
        this.unitPrice = builder.unitPrice
        this.quantityDiscount = builder.quantityDiscount
        this.discountPerUnit = builder.discountPerUnit
        this.discountPercentage = builder.discountPercentage
        this.serviceProvider = builder.serviceProvider
        this.unit = builder.unit
        this.itemTotal = builder.itemTotal
        this.category = builder.category ? builder.category : ""
    }
}
