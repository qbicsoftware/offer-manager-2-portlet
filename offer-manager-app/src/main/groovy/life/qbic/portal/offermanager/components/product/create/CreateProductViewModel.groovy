package life.qbic.portal.offermanager.components.product.create

import groovy.beans.Bindable
import life.qbic.datamodel.dtos.business.ProductCategory
import life.qbic.datamodel.dtos.business.services.ProductUnit


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
    @Bindable String productUnitPrice //todo make sure to cast the string to a double in the use case
    @Bindable Boolean productUnitPriceValid
    @Bindable ProductUnit productUnit
    @Bindable Boolean productUnitValid
    @Bindable ProductCategory productCategory
    @Bindable Boolean productCategoryValid

}
