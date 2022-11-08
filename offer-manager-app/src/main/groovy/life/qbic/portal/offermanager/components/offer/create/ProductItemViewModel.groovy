package life.qbic.portal.offermanager.components.offer.create

import groovy.transform.EqualsAndHashCode
import life.qbic.datamodel.dtos.business.services.Product

/**
 * An item describing the quantity and a product
 *
 * A product has a quantity when added to a
 *
 * @since: 0.1.0
 *
 */
@EqualsAndHashCode
class ProductItemViewModel {

    double quantity
    Product product

    ProductItemViewModel(double quantity, Product product){
        this.quantity = quantity
        this.product = product
    }

    void setQuantity(double quantity) {
        this.quantity = quantity
    }
}
