package life.qbic.portal.qoffer2.web

import com.vaadin.server.Page
import com.vaadin.ui.Notification
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

    private CreateCustomerView createCustomerView

    PortletView(ViewModel portletViewModel,
                CreateCustomerView createCustomerView) {
        super()
        this.portletViewModel = portletViewModel
        this.createCustomerView = createCustomerView
        initLayout()
        registerListeners()
    }

    /**
     * Initializes the layout of the view
     */
    private void initLayout() {
        this.setMargin(false)
        this.setSpacing(false)
        this.addComponentsAndExpand(this.createCustomerView)
        this.setSizeFull()
    }

    private def registerListeners() {

        this.portletViewModel.successNotifications.addPropertyChangeListener { evt ->
            if (evt instanceof ObservableList.ElementAddedEvent) {
                // show notification
                showNotification(evt.newValue.toString(), Notification.Type.HUMANIZED_MESSAGE)
                // remove displayed message
                portletViewModel.successNotifications.remove(evt.newValue)
            }
        }

        this.portletViewModel.failureNotifications.addPropertyChangeListener { evt ->
            if (evt instanceof ObservableList.ElementAddedEvent) {
                // show notification
                showNotification(evt.newValue.toString(), Notification.Type.ERROR_MESSAGE)
                // remove displayed message
                portletViewModel.failureNotifications.remove(evt.newValue)
            }
        }
    }

    private static def showNotification(String message, Notification.Type type) {
        StyledNotification notification = new StyledNotification(message, type)
        notification.show(Page.getCurrent())
    }
}
