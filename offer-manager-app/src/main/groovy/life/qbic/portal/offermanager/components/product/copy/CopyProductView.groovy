package life.qbic.portal.offermanager.components.product.copy

import com.vaadin.event.MouseEvents
import com.vaadin.ui.Button
import life.qbic.datamodel.dtos.business.ProductId
import life.qbic.datamodel.dtos.business.services.Product
import life.qbic.portal.offermanager.components.product.MaintainProductsController
import life.qbic.portal.offermanager.components.product.create.CreateProductView


/**
 * <class short description - One Line!>
 *
 * <More detailed description - When to use, what it solves, etc.>
 *
 * @since: <versiontag>
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
    }

    private void adaptView() {
        createProductButton.setCaption("Copy Product")
        label.setValue("Copy Service Product")
        registration.remove()
        this.createProductButton.addClickListener({
            controller.copyProduct(viewModel.productCategory, viewModel.productDescription, viewModel.productName, Double.parseDouble(viewModel.productUnitPrice), viewModel.productUnit, copyProductViewModel.productId)
        })
    }

}
