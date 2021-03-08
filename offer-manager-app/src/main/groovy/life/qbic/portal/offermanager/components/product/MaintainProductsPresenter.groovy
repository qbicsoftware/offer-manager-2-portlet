package life.qbic.portal.offermanager.components.product

import life.qbic.business.products.archive.ArchiveProductOutput
import life.qbic.business.products.copy.CopyProductOutput
import life.qbic.business.products.create.CreateProductOutput
import life.qbic.datamodel.dtos.business.services.Product
import life.qbic.portal.offermanager.components.AppViewModel

/**
 * <h1>AppPresenter for the {@link MaintainProductsView}</h1>
 * <br>
 * <p>This presenter handles the output of the {@link life.qbic.business.products.create.CreateProduct}, {@link life.qbic.business.products.copy.CopyProduct}
 * and {@link life.qbic.business.products.archive.ArchiveProduct} use cases and prepares it for the {@link MaintainProductsView}.</p>
 *
 * @since 1.0.0
 *
 */
class MaintainProductsPresenter implements CreateProductOutput, CopyProductOutput, ArchiveProductOutput{

    private final MaintainProductsViewModel productsViewModel
    private final AppViewModel mainViewModel

    MaintainProductsPresenter(MaintainProductsViewModel productsViewModel, AppViewModel mainViewModel){
        this.productsViewModel = productsViewModel
        this.mainViewModel = mainViewModel
    }

    @Override
    void archived(Product product) {

    }

    @Override
    void copied(Product product) {

    }

    @Override
    void created(Product product) {
        mainViewModel.successNotifications << "Successfully added new product $product.productId - $product.productName."
    }

    @Override
    void foundDuplicate(Product product) {
        mainViewModel.successNotifications << "Found duplicate product for $product.productId - $product.productName."
        //todo triggers smth in the view-model to ask the user if he still wants to create the duplicate
    }

    @Override
    void failNotification(String notification) {
        mainViewModel.failureNotifications << notification
    }
}
