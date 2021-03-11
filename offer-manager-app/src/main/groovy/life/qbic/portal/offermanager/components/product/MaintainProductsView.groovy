package life.qbic.portal.offermanager.components.product

import com.vaadin.icons.VaadinIcons
import com.vaadin.ui.Button
import com.vaadin.ui.FormLayout
import com.vaadin.ui.Grid
import com.vaadin.ui.Label
import com.vaadin.ui.themes.ValoTheme
import life.qbic.datamodel.dtos.business.services.Product
import life.qbic.portal.offermanager.components.product.archive.ArchiveProductView
import life.qbic.portal.offermanager.components.product.create.CreateProductView

/**
 *
 * <h1>This class generates a Form Layout in which the user can maintain the service products</h1>
 *
 * <p>{@link MaintainProductsViewModel} will be integrated into the qOffer 2.0 Portlet and provides an User Interface
 * with the intention of enabling an {@value life.qbic.portal.offermanager.security.Role#OFFER_ADMIN} to create, archive and copy products.</p>
 *
 * @since 1.0.0
 *
 */
class MaintainProductsView extends FormLayout{

    private final MaintainProductsViewModel viewModel

    Grid<Product> productGrid

    Button addProduct
    Button copyProduct
    Button archiveProduct

    CreateProductView createProductView
    ArchiveProductView archiveProductView
    CreateProductView copyProductView

    MaintainProductsView(MaintainProductsViewModel viewModel, CreateProductView createProductView
                         , ArchiveProductView archiveProductView, CreateProductView copyProductView){
        //todo add the controller
        this.viewModel = viewModel
        this.createProductView = createProductView
        this.copyProductView = copyProductView
        this.archiveProductView = archiveProductView

        setUpTitle()
        createButtons()
    }

    private void setUpTitle(){
        Label label = new Label("Service Product Maintenance")
        label.setStyleName(ValoTheme.LABEL_HUGE)
        this.addComponent(label)
    }

    private void createButtons(){
        addProduct = new Button("Add Product", VaadinIcons.PLUS)
        copyProduct = new Button ("Copy Product", VaadinIcons.COPY)
        archiveProduct = new Button("Archive Product", VaadinIcons.ARCHIVE)
    }

    private void setUpListeners(){
        addProduct.addClickListener({

        })
    }

}