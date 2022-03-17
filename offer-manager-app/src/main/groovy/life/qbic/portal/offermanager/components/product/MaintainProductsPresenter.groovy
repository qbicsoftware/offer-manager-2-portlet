package life.qbic.portal.offermanager.components.product

import life.qbic.business.RefactorConverter
import life.qbic.business.products.archive.ArchiveProductOutput
import life.qbic.business.products.create.CreateProductOutput
import life.qbic.datamodel.dtos.business.ProductId
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
class MaintainProductsPresenter implements CreateProductOutput, ArchiveProductOutput {

    private final MaintainProductsViewModel productsViewModel
    private final AppViewModel mainViewModel

    private static final RefactorConverter refactorConverter = new RefactorConverter()

    MaintainProductsPresenter(MaintainProductsViewModel productsViewModel, AppViewModel mainViewModel) {
        this.productsViewModel = productsViewModel
        this.mainViewModel = mainViewModel
    }

    @Override
    void failNotification(String notification) {
        mainViewModel.failureNotifications << notification
    }

    @Override
    void archived(life.qbic.business.products.Product product) {
        mainViewModel.successNotifications << "Successfully archived product $product.productId - $product.productName."
        productsViewModel.productsResourcesService.removeFromResource(refactorConverter.toProductDto(product))
    }

    @Override
    void created(life.qbic.business.products.Product product) {
        mainViewModel.successNotifications << "Successfully added new product $product.productId - $product.productName."
        productsViewModel.productsResourcesService.addToResource(refactorConverter.toProductDto(product))
        productsViewModel.productCreatedSuccessfully = true
    }

    @Override
    void foundDuplicates(List<life.qbic.business.products.Product> products) {
        life.qbic.business.products.Product duplicateProduct = products.first()
        List<ProductId> duplicateProductIds = []
        products.forEach { Product product ->
            duplicateProductIds << product.getProductId()
        }
        String productIds = duplicateProductIds.join(", ")
        mainViewModel.failureNotifications << "Found multiple products for ${duplicateProduct.productName} : ${productIds}"
    }
}
