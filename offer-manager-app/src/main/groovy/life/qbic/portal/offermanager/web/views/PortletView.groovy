package life.qbic.portal.offermanager.web.views

import com.vaadin.icons.VaadinIcons
import com.vaadin.server.Page
import com.vaadin.ui.*
import com.vaadin.ui.themes.ValoTheme

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

    private final life.qbic.portal.offermanager.web.viewmodel.ViewModel portletViewModel

    private final CreateCustomerView createCustomerView
    private final CreateAffiliationView createAffiliationView
    private final SearchCustomerView searchCustomerView
    private final CreateOfferView createOfferView
    private final List<Component> featureViews
    private final OverviewView overviewView

    private final CreateOfferView updateOfferView

    PortletView(life.qbic.portal.offermanager.web.viewmodel.ViewModel portletViewModel,
                CreateCustomerView createCustomerView,
                CreateAffiliationView createAffiliationView,
                SearchCustomerView searchCustomerView,
                CreateOfferView createOfferView,
                OverviewView overviewView,
                CreateOfferView updateOfferView) {
        super()
        this.portletViewModel = portletViewModel
        this.createCustomerView = createCustomerView
        this.createAffiliationView = createAffiliationView
        this.searchCustomerView = searchCustomerView
        this.createOfferView = createOfferView
        this.featureViews = []
        this.overviewView = overviewView
        this.updateOfferView = updateOfferView

        initLayout()
        registerListeners()
        setupFeatureViews()
        hideAllFeatureViews()
        /*
         We set the offer overview view as first active view.
         You can set any other view to visible here for the startup
         screen.
         */
        this.overviewView.setVisible(true)
    }

    private void setupFeatureViews() {
        featureViews.addAll([
                createCustomerView,
                createAffiliationView,
                createOfferView,
                overviewView,
                updateOfferView
        ])
    }

    private void hideAllFeatureViews() {
        featureViews.each {it.setVisible(false)}
    }

    /**
     * Initializes the layout of the view
     */
    private void initLayout() {
        this.setMargin(false)
        this.setSpacing(false)

        VerticalLayout verticalLayout = new VerticalLayout()


        verticalLayout.setSizeFull()
        //ToDo Find solution on how to best host different views in the portlet
        //gridLayout.addComponent(this.searchCustomerView)
        verticalLayout.addComponent(new TomatoFeatures())
        verticalLayout.addComponent(this.createCustomerView)
        verticalLayout.addComponent(this.createOfferView)
        verticalLayout.addComponents(this.createAffiliationView)
        verticalLayout.addComponent(this.overviewView)
        verticalLayout.addComponent(this.updateOfferView)

        this.setSizeFull()
        this.addComponent(verticalLayout)
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

        this.overviewView.updateOfferBtn.addClickListener({
            hideAllFeatureViews()
            this.updateOfferView.setVisible(true)
        })
    }

    private static def showNotification(String message, Notification.Type type) {
        life.qbic.portal.offermanager.web.StyledNotification notification = new life.qbic.portal.offermanager.web.StyledNotification(message, type)
        notification.show(Page.getCurrent())
    }

    private class TomatoFeatures extends HorizontalLayout {

        Button createOfferBtn

        Button createCustomerBtn

        Button createAffiliationBtn

        Button overviewBtn

        TomatoFeatures() {
            this.createOfferBtn = new Button("New Offer")
            this.createCustomerBtn = new Button("New Customer")
            this.createAffiliationBtn = new Button("New Affiliation")
            this.overviewBtn = new Button("Offer overview")
            this.addComponents(
                    overviewBtn,
                    createOfferBtn,
                    createCustomerBtn,
                    createAffiliationBtn
            )
            setStyles()
            setupListeners()
            setIcons()
            setDefault()
        }

        private void setDefault() {
            setButtonActive(overviewBtn)
        }

        private void setStyles() {
            overviewBtn.addStyleName(ValoTheme.BUTTON_BORDERLESS_COLORED)
            createOfferBtn.addStyleName(ValoTheme.BUTTON_BORDERLESS_COLORED)
            createCustomerBtn.addStyleName(ValoTheme.BUTTON_BORDERLESS_COLORED)
            createAffiliationBtn.addStyleName(ValoTheme.BUTTON_BORDERLESS_COLORED)
        }

        private void setIcons() {
            overviewBtn.setIcon(VaadinIcons.GRID_BIG_O)
            createOfferBtn.setIcon(VaadinIcons.GRID_BIG_O)
            createCustomerBtn.setIcon(VaadinIcons.GRID_BIG_O)
            createAffiliationBtn.setIcon(VaadinIcons.GRID_BIG_O)
        }

        private void setButtonActive(Button b) {
            b.setIcon(VaadinIcons.GRID_BIG)
        }

        private void setupListeners() {
            this.overviewBtn.addClickListener(listener -> {
                hideAllFeatureViews()
                setIcons()
                overviewView.setVisible(true)
                setButtonActive(this.overviewBtn)
            })
            this.createOfferBtn.addClickListener(listener -> {
                hideAllFeatureViews()
                setIcons()
                createOfferView.setVisible(true)
                setButtonActive(this.createOfferBtn)
            })
            this.createCustomerBtn.addClickListener(listener -> {
                hideAllFeatureViews()
                setIcons()
                createCustomerView.setVisible(true)
                setButtonActive(this.createCustomerBtn)
            })
            this.createAffiliationBtn.addClickListener(listener -> {
                hideAllFeatureViews()
                setIcons()
                createAffiliationView.setVisible(true)
                setButtonActive(this.createAffiliationBtn)
            })

        }

    }
}
