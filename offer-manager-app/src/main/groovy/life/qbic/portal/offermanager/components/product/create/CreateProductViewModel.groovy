package life.qbic.portal.offermanager.components.product.create


import groovy.beans.Bindable
import life.qbic.datamodel.dtos.business.ProductCategory
import life.qbic.datamodel.dtos.business.facilities.Facility
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
    @Bindable String internalUnitPrice
    @Bindable String externalUnitPrice
    @Bindable Boolean internalUnitPriceValid
    @Bindable Boolean externalUnitPriceValid
    @Bindable ProductUnit productUnit
    @Bindable Boolean productUnitValid
    @Bindable ProductCategory productCategory
    @Bindable Boolean productCategoryValid
    @Bindable Facility productFacility
    @Bindable Boolean productFacilityValid

}
