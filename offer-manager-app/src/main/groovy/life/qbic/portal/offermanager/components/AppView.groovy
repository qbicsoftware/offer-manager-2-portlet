package life.qbic.portal.offermanager.components

import com.vaadin.icons.VaadinIcons
import com.vaadin.server.Page
import com.vaadin.ui.*
import com.vaadin.ui.MenuBar.MenuItem
import com.vaadin.ui.themes.ValoTheme
import groovy.util.logging.Log4j2
import life.qbic.portal.offermanager.components.affiliation.create.CreateAffiliationView
import life.qbic.portal.offermanager.components.affiliation.search.SearchAffiliationView
import life.qbic.portal.offermanager.components.offer.create.CreateOfferView
import life.qbic.portal.offermanager.components.offer.overview.OfferOverviewView
import life.qbic.portal.offermanager.components.person.create.CreatePersonView
import life.qbic.portal.offermanager.components.person.search.SearchPersonView
import life.qbic.portal.offermanager.components.product.MaintainProductsView

/**
 * Class which connects the view elements with the ViewModel and the Controller
 *
 * This class provides the initial listeners
 * and layout upon which the views are presented
 *
 * @since 1.0.0
 */
@Log4j2
class AppView extends VerticalLayout {

    private final AppViewModel portletViewModel

    private final CreatePersonView createPersonView
    private final CreateAffiliationView createAffiliationView
    private final SearchAffiliationView searchAffiliationView
    private final CreateOfferView createOfferView
    private final List<Component> featureViews
    private final OfferOverviewView overviewView
    private final SearchPersonView searchPersonView
    private final MaintainProductsView maintainProductsView
    private final CreateOfferView updateOfferView

    AppView(AppViewModel portletViewModel,
            CreatePersonView createPersonView,
            CreateAffiliationView createAffiliationView,
            SearchAffiliationView searchAffiliationView,
            CreateOfferView createOfferView,
            OfferOverviewView overviewView,
            CreateOfferView updateOfferView,
            SearchPersonView searchPersonView,
            MaintainProductsView maintainProductsView) {
        super()
        this.portletViewModel = portletViewModel
        this.createPersonView = createPersonView
        this.createAffiliationView = createAffiliationView
        this.searchAffiliationView = searchAffiliationView
        this.createOfferView = createOfferView
        this.featureViews = []
        this.overviewView = overviewView
        this.updateOfferView = updateOfferView
        this.searchPersonView = searchPersonView
        this.maintainProductsView = maintainProductsView

        initLayout()
        registerListeners()
        setupFeatureViews()
        hideAllFeatureViews()
        makeAppViewScrollable()

        /*
         We set the offer overview view as first active view.
         You can set any other view to visible here for the startup
         screen.
         */
        this.overviewView.setVisible(true)
    }

    private void setupFeatureViews() {
        featureViews.addAll([
                createPersonView,
                createAffiliationView,
                searchAffiliationView,
                createOfferView,
                overviewView,
                updateOfferView,
                searchPersonView,
                maintainProductsView
        ])
    }

    private void hideAllFeatureViews() {
        featureViews.each { it.setVisible(false) }
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
        verticalLayout.addComponent(this.createPersonView)
        verticalLayout.addComponent(this.createOfferView)
        verticalLayout.addComponents(this.createAffiliationView)
        verticalLayout.addComponent(this.searchAffiliationView)
        verticalLayout.addComponent(this.overviewView)
        verticalLayout.addComponent(this.updateOfferView)
        verticalLayout.addComponent(this.searchPersonView)
        verticalLayout.addComponent(this.maintainProductsView)

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

        this.updateOfferView.viewModel.addPropertyChangeListener("offerCreatedSuccessfully", {
            if (it.newValue as Boolean) {
                this.updateOfferView.setVisible(false)
                this.overviewView.setVisible(true)
            }
        })

        this.createOfferView.viewModel.addPropertyChangeListener("offerCreatedSuccessfully", {
            if (it.newValue as Boolean) {
                this.createOfferView.setVisible(false)
                this.overviewView.setVisible(true)
            }
        })
    }

    private static def showNotification(String message, Notification.Type type) {
        StyledNotification notification = new StyledNotification(message, type)
        notification.show(Page.getCurrent())
    }

    private static void showNotification(String title, String message, Notification.Type type) {
        StyledNotification notification = new StyledNotification(title, message, type)
        Page page = Page.getCurrent()
        if (page) {
            notification.show(page)
        } else {
            log.warn("Page ${page}: Tried to show ${type}: $title - $message")
        }
    }

    private void makeAppViewScrollable () {
        this.setWidth("100%");
        this.setHeight("100%");
        this.addStyleName("scrollable-layout")
    }

    private class TomatoFeatures extends HorizontalLayout {

        TomatoFeatures() {
            createMenuBar()
        }

        private void createMenuBar() {
            this.addComponent(dropDownButton("Offers",
                    [
                            "New Offer"     : { toggleView(createOfferView) },
                            "Offer Overview": { toggleView(overviewView) }
                    ]))
            this.addComponent(dropDownButton("Persons",
                    [
                            "New Person": { toggleView(createPersonView) },
                            "Search"    : { toggleView(searchPersonView) }
                    ]))
            this.addComponent(dropDownButton("Affiliations",
                    [
                            "New Affiliation": { toggleView(createAffiliationView) },
                            "Search"         : { toggleView(searchAffiliationView) }
                    ]))
            Button maintainProducts = new Button("Service Products", VaadinIcons.GRID_BIG)
            maintainProducts.setStyleName(ValoTheme.BUTTON_BORDERLESS_COLORED)
            maintainProducts.addClickListener({
                toggleView(maintainProductsView)
            })
            this.addComponent(maintainProducts)
        }

        private void toggleView(Component component) {
            hideAllFeatureViews()
            component.setVisible(true)
        }

        MenuBar dropDownButton(String caption, Map<String, Closure> items) {
            MenuBar dropDownButton = new MenuBar()
            MenuItem content = dropDownButton.addItem(caption)
            content.setIcon(VaadinIcons.GRID_BIG)
            for (String item : items.keySet()) {
                content.addItem(item, items.get(item))
            }
            dropDownButton.addStyleName(ValoTheme.MENUBAR_BORDERLESS)
            return dropDownButton
        }

      }

}
