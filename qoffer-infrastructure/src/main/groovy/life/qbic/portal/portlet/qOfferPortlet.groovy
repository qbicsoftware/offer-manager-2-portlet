package life.qbic.portal.portlet
import com.vaadin.annotations.Theme
import com.vaadin.annotations.Widgetset
import com.vaadin.server.VaadinRequest
import com.vaadin.ui.HorizontalLayout
import com.vaadin.ui.Layout
import groovy.transform.CompileStatic
import groovy.util.logging.Log4j2
import life.qbic.ViewModel

/**
 * Entry point for the application. This class derives from {@link QBiCPortletUI}.
 *
 * @since: 1.0.0
 * @author: Jennifer Bödker
 * @see <a href=https://github.com/qbicsoftware/portal-utils-lib>portal-utils-lib</a>
 */
@Theme("mytheme")
@SuppressWarnings("serial")
@Widgetset("life.qbic.portal.portlet.AppWidgetSet")
@Log4j2
@CompileStatic
class qOfferPortlet extends QBiCPortletUI {
    ViewModel viewModel
    private CreateCustomerView createCustomerView
    @Override
    protected Layout getPortletContent(final VaadinRequest request) {
        HorizontalLayout layout = new HorizontalLayout()
        layout.setSpacing(true);
        layout.setMargin(true);
        layout.setWidth("100%");
        layout.setHeight("100%");

        def customview = new CreateCustomerView(viewModel)
        layout.addComponent(customview)
        return layout

    }
}

