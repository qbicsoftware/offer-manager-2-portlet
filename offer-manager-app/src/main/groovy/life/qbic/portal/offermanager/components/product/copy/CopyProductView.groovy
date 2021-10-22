package life.qbic.portal.offermanager.components.product.copy

import life.qbic.business.products.Converter
import life.qbic.business.products.dtos.ProductDraft
import life.qbic.portal.offermanager.components.product.MaintainProductsController
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
            controller.copyProduct(viewModel.productCategory, viewModel.productDescription, viewModel.productName, Double.parseDouble(viewModel.internalUnitPrice), Double.parseDouble(viewModel.externalUnitPrice), viewModel.productUnit, copyProductViewModel.productId, viewModel.productFacility)
            clearAllFields()
        })
    }

    @Override
    protected boolean allValuesValid() {
        boolean wasModified = false
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
            } else if (copyProductViewModel.productCategory != Converter.getCategory(copyProductViewModel.originalProduct)) {
                wasModified = true
            }  else if (copyProductViewModel.productFacility != copyProductViewModel.originalProduct.serviceProvider) {
                wasModified = true
            }else {
                wasModified = false
            }
        }
        return super.allValuesValid() && wasModified
    }

}
