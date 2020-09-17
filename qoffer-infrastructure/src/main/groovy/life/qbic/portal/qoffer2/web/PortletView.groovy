package life.qbic.portal.qoffer2.web

import com.vaadin.ui.VerticalLayout
import life.qbic.portal.qoffer2.web.views.CreateCustomerView

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

    final private ViewModel portletViewModel

    private CreateCustomerView createCustomer

    PortletView(ViewModel portletViewModel,
                CreateCustomerView createCustomer) {
        super()
        this.portletViewModel = portletViewModel
        this.createCustomer = createCustomer
        initLayout()
    }

    /**
     * Initializes the layout of the view
     */
    private void initLayout() {
        this.setMargin(false)
        this.setSpacing(false)
        this.addComponentsAndExpand(this.createCustomer)
        this.setSizeFull()
    }

}
