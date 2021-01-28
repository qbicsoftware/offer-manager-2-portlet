package life.qbic.portal.offermanager.components

import com.vaadin.icons.VaadinIcons
import com.vaadin.server.Page
import com.vaadin.ui.*
import com.vaadin.ui.themes.ValoTheme
import life.qbic.portal.offermanager.components.affiliation.create.CreateAffiliationView
import life.qbic.portal.offermanager.components.offer.create.CreateOfferView
import life.qbic.portal.offermanager.components.person.create.CreatePersonView
import life.qbic.portal.offermanager.components.offer.overview.OfferOverviewView

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
class AppView extends VerticalLayout {

    private final AppViewModel portletViewModel

    private final CreatePersonView createCustomerView
    private final CreateAffiliationView createAffiliationView
    private final CreateOfferView createOfferView
    private final List<Component> featureViews
    private final OfferOverviewView overviewView

    private final CreateOfferView updateOfferView

    AppView(AppViewModel portletViewModel,
            CreatePersonView createCustomerView,
            CreateAffiliationView createAffiliationView,
            OfferOverviewView overviewView,
            CreateOfferView updateOfferView) {
        super()
        this.portletViewModel = portletViewModel
        this.createCustomerView = createCustomerView
        this.createAffiliationView = createAffiliationView
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
        StyledNotification notification = new StyledNotification(message, type)
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
