package life.qbic.business.offers

import groovy.transform.EqualsAndHashCode
import life.qbic.business.offers.identifier.OfferId
import life.qbic.datamodel.dtos.business.Affiliation
import life.qbic.datamodel.dtos.business.Customer
import life.qbic.datamodel.dtos.business.ProductItem
import life.qbic.datamodel.dtos.business.ProjectManager
import life.qbic.datamodel.dtos.projectmanagement.ProjectIdentifier

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
    private double quantity
    private String productDescription
    private String productName
    private double unitPrice
    private String serviceProvider
    private String unit
    private double itemTotal

    static class Builder {
        double quantity
        String productDescription
        String productName
        double unitPrice
        String serviceProvider
        String unit
        double itemTotal

        Builder(double quantity, String productDescription, String productName, double unitPrice, String serviceProvider, String unit, double itemTotal) {
            this.quantity =  Objects.requireNonNull(quantity, "Customer must not be null")
            this.productDescription =  Objects.requireNonNull(productDescription, "Customer must not be null")
            this.productName =  Objects.requireNonNull(productName, "Customer must not be null")
            this.unitPrice =  Objects.requireNonNull(unitPrice, "Customer must not be null")
            this.serviceProvider =  Objects.requireNonNull(serviceProvider, "Customer must not be null")
            this.unit =  Objects.requireNonNull(unit, "Customer must not be null")
            this.itemTotal =  Objects.requireNonNull(itemTotal, "Customer must not be null")

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
        this.serviceProvider = builder.serviceProvider
        this.unit = builder.unit
        this.itemTotal = builder.itemTotal
    }
}