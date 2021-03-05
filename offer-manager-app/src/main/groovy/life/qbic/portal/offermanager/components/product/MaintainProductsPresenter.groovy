package life.qbic.portal.offermanager.components.product

import life.qbic.business.products.archive.ArchiveProductOutput
import life.qbic.business.products.copy.CopyProductOutput
import life.qbic.business.products.create.CreateProductOutput
import life.qbic.datamodel.dtos.business.services.Product

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

    private final MaintainProductsViewModel viewModel

    MaintainProductsPresenter(MaintainProductsViewModel viewModel){
        this.viewModel = viewModel
    }

    @Override
    void archived(Product product) {

    }

    @Override
    void copied(Product product) {

    }

    @Override
    void created(Product product) {

    }

    @Override
    void foundDuplicate(Product product) {

    }

    @Override
    void failNotification(String notification) {

    }
}
