package life.qbic.portal.offermanager.components

import com.vaadin.icons.VaadinIcons
import com.vaadin.server.Page
import com.vaadin.ui.*
import com.vaadin.ui.themes.ValoTheme
import life.qbic.portal.offermanager.components.affiliation.create.CreateAffiliationView
import life.qbic.portal.offermanager.components.offer.create.CreateOfferView
import life.qbic.portal.offermanager.components.person.create.CreatePersonView
import life.qbic.portal.offermanager.components.offer.overview.OfferOverviewView
import life.qbic.portal.offermanager.components.person.search.SearchPersonView
import life.qbic.portal.offermanager.components.projectcreation.CreateProjectView
import life.qbic.portal.offermanager.security.Role
import life.qbic.portal.offermanager.security.RoleService

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
    private final SearchPersonView searchPersonView
    private final CreateProjectView createProjectView
    private final CreateOfferView updateOfferView

    AppView(AppViewModel portletViewModel,
            CreatePersonView createCustomerView,
            CreateAffiliationView createAffiliationView,
            CreateOfferView createOfferView,
            OfferOverviewView overviewView,
            CreateOfferView updateOfferView,
            SearchPersonView searchPersonView,
            CreateProjectView createProjectView) {
        super()
        this.portletViewModel = portletViewModel
        this.createCustomerView = createCustomerView
        this.createAffiliationView = createAffiliationView
        this.createOfferView = createOfferView
        this.featureViews = []
        this.overviewView = overviewView
        this.updateOfferView = updateOfferView
        this.searchPersonView = searchPersonView
        this.createProjectView = createProjectView

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
                updateOfferView,
                searchPersonView,
                createProjectView
        ])
    }

    private void hideAllFeatureViews() {
        featureViews.each {it.setVisible(false)}
    }

    /**
     * Initializes the layout of the view
     */
    private void initLayout() {
        this.setMargin(true)

        VerticalLayout verticalLayout = new VerticalLayout()
        verticalLayout.setMargin(false)

        verticalLayout.setSizeFull()
        //ToDo Find solution on how to best host different views in the portlet
        //gridLayout.addComponent(this.searchCustomerView)
        verticalLayout.addComponent(new TomatoFeatures())
        verticalLayout.addComponent(this.createCustomerView)
        verticalLayout.addComponent(this.createOfferView)
        verticalLayout.addComponents(this.createAffiliationView)
        verticalLayout.addComponent(this.overviewView)
        verticalLayout.addComponent(this.updateOfferView)
        verticalLayout.addComponent(this.searchPersonView)
        verticalLayout.addComponent(this.createProjectView)

        this.setSizeFull()
        this.addComponent(verticalLayout)
    }

    private def registerListeners() {

        this.portletViewModel.successNotifications.addPropertyChangeListener { evt ->
            if (evt instanceof ObservableList.ElementAddedEvent) {
                // show notification
                showNotification("Success!", evt.newValue.toString(), Notification.Type
                        .HUMANIZED_MESSAGE)
                // remove displayed message
                portletViewModel.successNotifications.remove(evt.newValue)
            }
        }

        this.portletViewModel.failureNotifications.addPropertyChangeListener { evt ->
            if (evt instanceof ObservableList.ElementAddedEvent) {
                // show notification
                showNotification("Something went wrong.", evt.newValue.toString(), Notification.Type
                        .ERROR_MESSAGE)
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

    private static void showNotification(String title, String message, Notification.Type type) {
        StyledNotification notification = new StyledNotification(title, message, type)
        notification.show(Page.getCurrent())
    }

    private class TomatoFeatures extends HorizontalLayout {

        Button createOfferBtn

        Button createCustomerBtn

        Button createAffiliationBtn

        Button overviewBtn

        Button searchPersonBtn

        Button createProjectBtn

        TomatoFeatures() {
            this.createOfferBtn = new Button("New Offer")
            this.createCustomerBtn = new Button("New Customer")
            this.createAffiliationBtn = new Button("New Affiliation")
            this.overviewBtn = new Button("Offer Overview")
            this.searchPersonBtn = new Button("Search Customer")
            this.createProjectBtn = new Button("Create Project")

            this.addComponents(
                    overviewBtn,
                    createOfferBtn,
                    createCustomerBtn,
                    createAffiliationBtn,
                    searchPersonBtn,
                    createProjectBtn
            )
            setStyles()
            setupListeners()
            setIcons()
            setDefault()
            enableFeatures()
        }

        private void enableFeatures() {
            createOfferBtn.setEnabled(portletViewModel.createOfferFeatureEnabled)
            createCustomerBtn.setEnabled(portletViewModel.createCustomerFeatureEnabled)
            searchPersonBtn.setEnabled(portletViewModel.searchCustomerFeatureEnabled)
        }

        private void setDefault() {
            setButtonActive(overviewBtn)
        }

        private void setStyles() {
            overviewBtn.addStyleName(ValoTheme.BUTTON_BORDERLESS_COLORED)
            createOfferBtn.addStyleName(ValoTheme.BUTTON_BORDERLESS_COLORED)
            createCustomerBtn.addStyleName(ValoTheme.BUTTON_BORDERLESS_COLORED)
            createAffiliationBtn.addStyleName(ValoTheme.BUTTON_BORDERLESS_COLORED)
            searchPersonBtn.addStyleName(ValoTheme.BUTTON_BORDERLESS_COLORED)
            createProjectBtn.addStyleName(ValoTheme.BUTTON_BORDERLESS_COLORED)
        }

        private void setIcons() {
            overviewBtn.setIcon(VaadinIcons.GRID_BIG_O)
            createOfferBtn.setIcon(VaadinIcons.GRID_BIG_O)
            createCustomerBtn.setIcon(VaadinIcons.GRID_BIG_O)
            createAffiliationBtn.setIcon(VaadinIcons.GRID_BIG_O)
            searchPersonBtn.setIcon(VaadinIcons.GRID_BIG_O)
            createProjectBtn.setIcon(VaadinIcons.GRID_BIG_O)
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
            this.searchPersonBtn.addClickListener(listener -> {
                hideAllFeatureViews()
                setIcons()
                searchPersonView.setVisible(true)
                setButtonActive(this.searchPersonBtn)
            })
            this.createProjectBtn.addClickListener(listener -> {
                hideAllFeatureViews()
                setIcons()
                createProjectView.setVisible(true)
                setButtonActive(this.createProjectBtn)
            })
        }

    }
}
