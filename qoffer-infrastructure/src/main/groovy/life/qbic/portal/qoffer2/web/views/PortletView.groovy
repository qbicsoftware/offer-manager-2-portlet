package life.qbic.portal.qoffer2.web.views

import com.vaadin.server.Page
import com.vaadin.ui.GridLayout
import com.vaadin.ui.Notification
import com.vaadin.ui.VerticalLayout
import life.qbic.datamodel.dtos.business.Affiliation
import life.qbic.portal.qoffer2.web.StyledNotification
import life.qbic.portal.qoffer2.web.viewmodel.CreateAffiliationViewModel
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
class PortletView extends VerticalLayout implements AffiliationSelectionListener{

    private final ViewModel portletViewModel

    private final CreateCustomerView createCustomerView
    private final CreateAffiliationView createAffiliationView
    private final SearchCustomerView searchCustomerView
    private final CreateOfferView createOfferView

    PortletView(ViewModel portletViewModel,
                CreateCustomerView createCustomerView, CreateAffiliationView createAffiliationView, SearchCustomerView searchCustomerView,
                CreateOfferView createOfferView) {
        super()
        this.portletViewModel = portletViewModel
        this.createCustomerView = createCustomerView
        this.createAffiliationView = createAffiliationView
        this.searchCustomerView = searchCustomerView
        this.createOfferView = createOfferView
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
        //ToDo Find solution on how to best host different views in the portlet
        //gridLayout.addComponent(this.searchCustomerView)
        gridLayout.addComponent(this.createCustomerView)
        //gridLayout.addComponent(this.createAffiliationView)
        //gridLayout.addComponent(this.createOfferView)

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

    /**
     * {@inheritdoc}
     * This method informs the CreateAffiliationView of a new selection and
     * updates the view accordingly.
     */
    @Override
    void affiliationSelected(AffiliationSelectionEvent event) {
        Affiliation affiliation = event.getValue()
        CreateAffiliationViewModel viewModel = createAffiliationView.createAffiliationViewModel
        viewModel.affiliationCategory = affiliation?.getCategory()
        viewModel.country = affiliation?.getCountry()
        viewModel.city = affiliation?.getCity()
        viewModel.postalCode = affiliation?.getPostalCode()
        viewModel.street = affiliation?.getStreet()
        viewModel.addressAddition = affiliation?.addressAddition
        viewModel.organisation = affiliation?.organisation
    }
}
