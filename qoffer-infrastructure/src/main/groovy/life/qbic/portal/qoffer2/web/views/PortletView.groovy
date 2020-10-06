package life.qbic.portal.qoffer2.web.views

import com.vaadin.server.Page
import com.vaadin.ui.GridLayout
import com.vaadin.ui.HorizontalLayout
import com.vaadin.ui.Notification
import com.vaadin.ui.VerticalLayout
import life.qbic.portal.qoffer2.web.StyledNotification
import life.qbic.portal.qoffer2.web.viewmodel.ViewModel

/**
 * Class which connects the view elements with the ViewModel and the Controller
 *
 * This class provides the initial listeners
 * and layout upon which the views are presented
 *
 * @since: 1.0.0
 * @author: Jennifer Bödker
 *
 */
class PortletView extends VerticalLayout {

    private final ViewModel portletViewModel

    private final CreateCustomerView createCustomerView
    private final CreateAffiliationView createAffiliationView

    PortletView(ViewModel portletViewModel,
                CreateCustomerView createCustomerView, CreateAffiliationView createAffiliationView) {
        super()
        this.portletViewModel = portletViewModel
        this.createCustomerView = createCustomerView
        this.createAffiliationView = createAffiliationView
        initLayout()
        registerListeners()
    }

    /**
     * Initializes the layout of the view
     */
    private void initLayout() {
        this.setMargin(false)
        this.setSpacing(false)

        this.createAffiliationView.setVisible(portletViewModel.createAffiliationVisible)

        GridLayout gridLayout = new GridLayout()
        gridLayout.setRows(2)
        gridLayout.setSizeFull()

        gridLayout.addComponent(this.createCustomerView)
        gridLayout.addComponent(this.createAffiliationView)

        this.setSizeFull()
        this.addComponent(gridLayout)
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

        this.portletViewModel.addPropertyChangeListener({
            switch (it.propertyName) {
                case "createAffiliationVisible":
                    this.createAffiliationView.setVisible(portletViewModel.createAffiliationVisible)
                    break
                default:
                    break
            }
        })
    }

    private static def showNotification(String message, Notification.Type type) {
        StyledNotification notification = new StyledNotification(message, type)
        notification.show(Page.getCurrent())
    }
}
