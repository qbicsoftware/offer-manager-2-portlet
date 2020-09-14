package life.qbic.portal.qoffer2.web
import com.vaadin.ui.VerticalLayout
import life.qbic.portal.portlet.CreateCustomerView

/**
 * Class which connects the view elements with the ViewModel and the Controller
 *
 * This class acts as connector between the individual view elements, the ViewModel {@link ViewModel}
 * and the PortletController {@link Controller} and provides the initial listeners
 * and layout upon which the views are presented
 *
 * @since: 1.0.0
 * @author: Jennifer Bödker
 *
 */
class PortletView extends VerticalLayout {

    final private Controller controller
    final private ViewModel portletViewModel

    private CreateCustomerView createCustomer

    PortletView(Controller portletController, ViewModel portletViewModel,
                CreateCustomerView createCustomer) {
        super()
        this.controller = portletController
        this.portletViewModel = portletViewModel
        this.createCustomer = createCustomer
        initLayout()
        registerListeners()
    }

    /**
     *
     * @return
     */
    private def initLayout() {
        this.setMargin(false)
        this.setSpacing(false)
        this.addComponentsAndExpand(this.createCustomer)
        this.setSizeFull()
    }
    private def registerListeners() {}
}
