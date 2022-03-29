package life.qbic.portal.offermanager

import com.vaadin.annotations.Theme
import com.vaadin.server.Page
import com.vaadin.server.VaadinRequest
import com.vaadin.server.VaadinService
import com.vaadin.ui.Layout
import com.vaadin.ui.Notification
import com.vaadin.ui.VerticalLayout
import groovy.transform.CompileStatic
import groovy.util.logging.Log4j2
import life.qbic.portal.offermanager.components.StyledNotification
import life.qbic.portal.offermanager.security.Role
import life.qbic.portal.offermanager.security.RoleService
import life.qbic.portal.offermanager.security.liferay.LiferayRoleService
import life.qbic.portal.offermanager.security.local.LocalAdminRoleService

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
        final Role userRole = determineUserRole()
        this.dependencyManager = new DependencyManager(userRole)
    }

    private static Role determineUserRole() {
        RoleService roleService
        String userId
        if(System.getProperty("environment") == "testing") {
            roleService = new LocalAdminRoleService()
            userId = "test"
            log.info("Running test environment configuration.")
        } else {
            roleService = new LiferayRoleService()
            userId = VaadinService.getCurrentRequest().getRemoteUser()
        }
        return loadAppRole(roleService, userId)
    }

    private static Role loadAppRole(RoleService roleService, String userId) {
        Optional<Role> userRole = roleService.getRoleForUser(userId)
        if (!userRole.isPresent()) {
            throw new RuntimeException("Security issue: Can not determine user role.")
        }
        return userRole.get()
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
            StyledNotification initializationErrorNotification = new StyledNotification
                    (errorCaption, errorMessage, Notification.Type.ERROR_MESSAGE)
            initializationErrorNotification.show(Page.getCurrent())
            layout = new VerticalLayout()
        }
        return layout
    }

}
