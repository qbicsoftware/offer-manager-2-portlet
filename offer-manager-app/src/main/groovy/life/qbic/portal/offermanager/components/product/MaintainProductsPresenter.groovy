package life.qbic.portal.offermanager.components.product

import life.qbic.business.products.archive.ArchiveProductOutput
import life.qbic.business.products.create.CreateProductOutput
import life.qbic.datamodel.dtos.business.services.Product
import life.qbic.portal.offermanager.components.AppViewModel

/**
 * <h1>AppPresenter for the {@link MaintainProductsView}</h1>
 * <br>
 * <p>This presenter handles the output of the {@link life.qbic.business.products.create.CreateProduct} and {@link life.qbic.business.products.archive.ArchiveProduct} use cases and prepares it for the {@link MaintainProductsView}.</p>
 *
 * @since 1.0.0
 *
 */
class MaintainProductsPresenter implements CreateProductOutput, ArchiveProductOutput{

    private final MaintainProductsViewModel productsViewModel
    private final AppViewModel mainViewModel

    MaintainProductsPresenter(MaintainProductsViewModel productsViewModel, AppViewModel mainViewModel){
        this.productsViewModel = productsViewModel
        this.mainViewModel = mainViewModel
    }

    @Override
    void archived(Product product) {
        mainViewModel.successNotifications << "Successfully archived product $product.productId - $product.productName."
        productsViewModel.productsResourcesService.removeFromResource(product)
    }

    @Override
    void created(Product product) {
        mainViewModel.successNotifications << "Successfully added new product $product.productId - $product.productName."
        productsViewModel.productsResourcesService.addToResource(product)
        productsViewModel.productCreatedSuccessfully = true
    }

    @Override
    void foundDuplicates(List<Product> duplicateProducts) {
        String duplicateProductName = duplicateProducts.get(0).productName
        mainViewModel.failureNotifications << "Found duplicate products for ${duplicateProductName} with Id ${duplicateProducts.productId}"
    }

    @Override
    void failNotification(String notification) {
        mainViewModel.failureNotifications << notification
    }

}
