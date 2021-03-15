package life.qbic.portal.offermanager.components.product.create

import groovy.beans.Bindable


/**
 * <h1>Holds all values that the user specifies in the CreateProductView</h1>
 *
 * @since 1.0.0
 *
*/
class CreateProductViewModel {
    @Bindable String productName
    @Bindable Boolean productNameValid
    @Bindable String productDescription
    @Bindable Boolean productDescriptionValid
    @Bindable Double productUnitPrice
    @Bindable Boolean productUnitPriceValid
    @Bindable String productUnit
    @Bindable Boolean productUnitValid
    @Bindable String productCategories
    @Bindable Boolean productCategoriesValid

}
