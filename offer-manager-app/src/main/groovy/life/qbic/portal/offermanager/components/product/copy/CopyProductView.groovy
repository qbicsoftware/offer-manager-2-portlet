package life.qbic.portal.offermanager.components.product.copy

import com.vaadin.event.MouseEvents
import com.vaadin.ui.Button
import life.qbic.datamodel.dtos.business.ProductId
import life.qbic.datamodel.dtos.business.services.Product
import life.qbic.portal.offermanager.components.product.MaintainProductsController
import life.qbic.portal.offermanager.components.product.create.CreateProductView


/**
 * This class represents the GUI for copying a product
 *
 * The view is similar to the {@link CreateProductView} and updates the view to fit the copy product use case
 *
 * @since: 1.0.0
 *
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
            controller.copyProduct(viewModel.productCategory, viewModel.productDescription, viewModel.productName, Double.parseDouble(viewModel.productUnitPrice), viewModel.productUnit, copyProductViewModel.productId)
            clearAllFields()
        })
    }

}
