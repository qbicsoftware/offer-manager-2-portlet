package life.qbic.portal.offermanager.components.product.copy


import life.qbic.business.products.Converter
import life.qbic.datamodel.dtos.business.ProductId
import life.qbic.datamodel.dtos.business.services.Product
import life.qbic.portal.offermanager.communication.EventEmitter
import life.qbic.portal.offermanager.components.product.create.CreateProductViewModel

/**
 * <h1>Holds all values that the user specifies in the CreateProductView</h1>
 *
 * @since 1.0.0
 */
class CopyProductViewModel extends CreateProductViewModel {

    EventEmitter<Product> productUpdate
    ProductId productId

    Product originalProduct

    CopyProductViewModel(EventEmitter<Product> productUpdate) {
        super()
        this.productUpdate = productUpdate
        this.productUpdate.register((Product product) -> {
            loadData(product)
        })
    }

    private void loadData(Product product) {
        setOriginalProduct(product)
        productName = product.productName
        productDescription = product.description
        productUnit = product.unit
        internalUnitPrice = product.unitPrice
        productCategory = Converter.getCategory(product)
        productId = product.productId
    }
}
