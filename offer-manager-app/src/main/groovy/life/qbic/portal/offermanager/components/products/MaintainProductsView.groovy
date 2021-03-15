package life.qbic.portal.offermanager.components.products

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
class MaintainProductsView {

    private final MaintainProductsController controller
    private final MaintainProductsViewModel viewModel

    MaintainProductsView(MaintainProductsController controller, MaintainProductsViewModel viewModel){
        this.controller = controller
        this.viewModel = viewModel
    }
}
