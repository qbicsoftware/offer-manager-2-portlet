package life.qbic.portal.offermanager.components.product.copy

import life.qbic.business.products.Converter
import life.qbic.datamodel.dtos.business.Offer
import life.qbic.datamodel.dtos.business.ProductCategory
import life.qbic.datamodel.dtos.business.ProductId
import life.qbic.datamodel.dtos.business.services.DataStorage
import life.qbic.datamodel.dtos.business.services.MetabolomicAnalysis
import life.qbic.datamodel.dtos.business.services.PrimaryAnalysis
import life.qbic.datamodel.dtos.business.services.Product
import life.qbic.datamodel.dtos.business.services.ProjectManagement
import life.qbic.datamodel.dtos.business.services.ProteomicAnalysis
import life.qbic.datamodel.dtos.business.services.SecondaryAnalysis
import life.qbic.datamodel.dtos.business.services.Sequencing
import life.qbic.datamodel.dtos.general.Person
import life.qbic.portal.offermanager.communication.EventEmitter
import life.qbic.portal.offermanager.components.offer.create.ProductItemViewModel
import life.qbic.portal.offermanager.components.product.MaintainProductsViewModel
import life.qbic.portal.offermanager.components.product.create.CreateProductViewModel
import life.qbic.portal.offermanager.dataresources.persons.CustomerResourceService
import life.qbic.portal.offermanager.dataresources.persons.ProjectManagerResourceService
import life.qbic.portal.offermanager.dataresources.products.ProductsResourcesService


/**
 * <h1>Holds all values that the user specifies in the CreateProductView</h1>
 *
 * @since 1.0.0
 *
*/
class CopyProductViewModel extends CreateProductViewModel{

    EventEmitter<Product> productUpdate
    ProductId productId
    CopyProductViewModel(EventEmitter<Product> productUpdate) {
        super()
        this.productUpdate = productUpdate
        this.productUpdate.register((Product product) -> {
            loadData(product)
        })
    }

    private void loadData(Product product) {
        productName = product.productName
        productDescription = product.description
        productUnit = product.unit
        productUnitPrice = product.unitPrice
        productCategory = Converter.getCategory(product)
        productId = product.productId
    }
}
