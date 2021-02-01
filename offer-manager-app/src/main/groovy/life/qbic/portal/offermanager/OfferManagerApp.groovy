package life.qbic.portal.offermanager

import com.vaadin.annotations.Theme
import com.vaadin.server.Page
import com.vaadin.server.VaadinRequest
import com.vaadin.ui.Layout
import com.vaadin.ui.VerticalLayout
import groovy.transform.CompileStatic
import groovy.util.logging.Log4j2
import life.qbic.portal.offermanager.components.StyledNotification

/**
 * Entry point for the application. This class derives from {@link life.qbic.portal.portlet.QBiCPortletUI}.
 *
 * @since: 1.0.0
 * @author: Jennifer Bödker
 * @see <a href=https://github.com/qbicsoftware/portal-utils-lib>portal-utils-lib</a>
 */
@Theme("mytheme")
@SuppressWarnings("serial")
@Log4j2
@CompileStatic
class OfferManagerApp extends QBiCPortletUI {

    private DependencyManager dependencyManager

    OfferManagerApp() {
        super()
        // The constructor MUST NOT fail since the user does not get any feedback otherwise.
        try {
            create()
        } catch (Exception e) {
            log.error("Could not initialize {}", OfferManagerApp.getCanonicalName(), e)
        } catch (Error error) {
            log.error("Unexpected runtime error.", error)
        }
    }

    private void create() {
        this.dependencyManager = new DependencyManager()
    }

    @Override
    protected Layout getPortletContent(final VaadinRequest request) {
        def layout
        log.info "Generating content for class {}", OfferManagerApp.getCanonicalName()
        try {
            layout = this.dependencyManager.getPortletView()
        } catch (Exception e) {
            log.error("Failed generating content for class {}", OfferManagerApp.getCanonicalName())
            log.error(e)
            String errorCaption = "Application not available"
            String errorMessage = "We apologize for any inconveniences. Please inform us via email to support@qbic.zendesk.com."
            StyledNotification initializationErrorNotification = new StyledNotification(errorCaption, errorMessage)
            initializationErrorNotification.show(Page.getCurrent())
            layout = new VerticalLayout()
        }
        return layout
    }

}