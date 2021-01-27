package life.qbic.portal.offermanager.services

/**
 * Services can be triggered to reload their resources they present.
 * @since 1.0.0
 */
interface ResourcesService {

    /**
     * Triggers a service to reload its resources.
     */
    void reloadResources()
}
