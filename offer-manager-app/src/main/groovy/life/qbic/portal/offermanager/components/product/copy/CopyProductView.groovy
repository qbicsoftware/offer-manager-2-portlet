package life.qbic.portal.offermanager.components.product.copy

import life.qbic.datamodel.dtos.business.ProductCategory
import life.qbic.portal.offermanager.components.product.MaintainProductsController
import life.qbic.portal.offermanager.components.product.ProductClassToCategory
import life.qbic.portal.offermanager.components.product.create.CreateProductView

/**
 * This class represents the GUI for copying a product
 *
 * The view is similar to the {@link CreateProductView} and updates the view to fit the copy product use case
 *
 * @since 1.0.0
 */

class CopyProductView extends CreateProductView {

    CopyProductViewModel copyProductViewModel
    MaintainProductsController controller

    CopyProductView(CopyProductViewModel copyProductViewModel, MaintainProductsController controller) {
        super(copyProductViewModel, controller)
        this.copyProductViewModel = copyProductViewModel
        this.controller = controller
        adaptView()
        adaptListener()
    }

    private void adaptView() {
        createProductButton.setCaption("Copy Product")
        titleLabel.setValue("Copy Service Product")
    }

    private void adaptListener() {
        createProductButtonRegistration.remove()
        this.createProductButton.addClickListener({
            controller.createNewProduct(viewModel.productCategory, viewModel.productDescription, viewModel.productName, Double.parseDouble(viewModel.internalUnitPrice), Double.parseDouble(viewModel.externalUnitPrice), viewModel.productUnit, viewModel.productFacility)
            clearAllFields()
        })
    }

    @Override
    protected boolean allValuesValid() {
        boolean wasModified = false
        ProductCategory originalProductCategory = new ProductClassToCategory().apply(copyProductViewModel.originalProduct.getClass())

        if (super.allValuesValid()) {
            if (copyProductViewModel.productName != copyProductViewModel.originalProduct.productName) {
                wasModified = true
            } else if (copyProductViewModel.productDescription != copyProductViewModel.originalProduct.description) {
                wasModified = true
            } else if (copyProductViewModel.internalUnitPrice != copyProductViewModel.originalProduct.internalUnitPrice.toString()) {
                wasModified = true
            } else if (copyProductViewModel.externalUnitPrice != copyProductViewModel.originalProduct.externalUnitPrice.toString()) {
                wasModified = true
            } else if (copyProductViewModel.productUnit != copyProductViewModel.originalProduct.unit) {
                wasModified = true
            } else if (copyProductViewModel.productCategory != originalProductCategory) {
                wasModified = true
            } else {
                wasModified = copyProductViewModel.productFacility != copyProductViewModel.originalProduct.serviceProvider
            }
        }
        return super.allValuesValid() && wasModified
    }

}
