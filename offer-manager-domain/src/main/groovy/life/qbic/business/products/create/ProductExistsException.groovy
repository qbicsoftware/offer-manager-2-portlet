package life.qbic.business.products.create
import life.qbic.datamodel.dtos.business.ProductId

/**
 * <h1>Signals that an attempt to store a product has failed</h1>
 * <p>This exception will be thrown by the {@link CreateProductDataSource} when a product already exists. </p>
 *
 * @since 1.0.0
 */
class ProductExistsException extends RuntimeException {

    ProductExistsException(ProductId productId) {
        super()
    }

    ProductExistsException(ProductId productId, String message) {
        super(message)
    }

    ProductExistsException(ProductId productId, String message, Throwable cause) {
        super(message, cause)
    }

    ProductExistsException(ProductId productId, Throwable cause) {
        super(cause)
    }
}
