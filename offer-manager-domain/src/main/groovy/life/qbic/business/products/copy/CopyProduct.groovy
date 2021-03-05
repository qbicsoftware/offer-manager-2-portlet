package life.qbic.business.products.copy

import life.qbic.datamodel.dtos.business.ProductId

/**
 * <h1>4.3.2 Copy Service Product</h1>
 * <br>
 * <p> Offer Administrators are allowed to create a new permutation of an existing product.
 * <br> New permutations can include changes in unit price, sequencing technology and other attributes of service products.
 * </p>
 *
 * @since: 1.0.0
 *
 */
class CopyProduct implements CopyProductInput {
    @Override
    void copy(ProductId productId) {

    }
}
