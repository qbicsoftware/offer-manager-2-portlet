package life.qbic.portal.qoffer2

import groovy.util.logging.Log4j2
import life.qbic.portal.qoffer2.web.PortletView

/**
 * Class that manages all the dependency injections and class instance creations
 *
 * This class has access to all classes that are instantiated at setup. It is responsible to construct
 * and provide every instance with it's dependencies injected. The class should only be accessed once upon
 * portlet creation and shall not be used later on in the control flow.
 *
 * @since: 1.0.0
 */

@Log4j2
class DependencyManager {
    private PortletView portletView;

    /**
     * Public constructor.
     *
     * This constructor creates a dependency manager with all the instances of required classes.
     * It ensures that the {@link #portletView} field is set.
     */
    public DependencyManager() {
        initializeDependencies()
    }

    private void initializeDependencies() {
        //TODO implement
    }

    public PortletView getPortletView(){
        return this.getPortletView();
    }



}
