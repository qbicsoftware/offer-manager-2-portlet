package life.qbic.portal.offermanager.components.offer.create


import life.qbic.datamodel.dtos.business.services.Product

/**
 * An item describing the quantity and a product
 *
 * A product has a quantity when added to a
 *
 * @since: 0.1.0
 *
 */

class ProductItemViewModel {

    double quantity
    Product product

    ProductItemViewModel(double quantity, Product product) {
        this.quantity = quantity
        this.product = product
    }

    void setQuantity(double quantity) {
        this.quantity = quantity
    }

    @Override
    boolean equals(o) {
        if (this.is(o)) return true
        if (o == null || getClass() != o.class) return false
        ProductItemViewModel that = (ProductItemViewModel) o
        if (product != that.product) return false
        return true
    }

    @Override
    int hashCode() {
        return product.hashCode()
    }
}
