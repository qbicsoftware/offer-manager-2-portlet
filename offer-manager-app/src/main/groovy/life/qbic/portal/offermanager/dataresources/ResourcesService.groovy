package life.qbic.portal.offermanager.dataresources

import life.qbic.portal.offermanager.communication.Subscription

/**
 * Services can be triggered to reload their resources they present.
 * @since 1.0.0
 */
interface ResourcesService<T> {

    /**
     * Triggers a service to reload its resources.
     * This clears the current list and reloads the data from the connected datasource
     *
     */
    void reloadResources()

    /**
     * Subscribes to service update events. Update events are emitted by the service when new
     * resource items are added, removed or the resource has refreshed.
     *
     * @param subscription The subscription to register for update events
     */
    void subscribe(Subscription<T> subscription)

    /**
     * Unsubscribe from the service events.
     *
     * @param subscription The subscription to remove
     */
    void unsubscribe(Subscription<T> subscription)

    /**
     * Adds a resource item to a resource of the service.
     *
     * @param resourceItem The resource item to add
     */
    void addToResource(T resourceItem)

    /**
     * Removes a resource item from the resource of the service.
     *
     * @param resourceItem
     */
    void removeFromResource(T resourceItem)

    /**
     * Returns an iterator that provides access to all resource items of the service.
     *
     * @return An iterator of type resource type T
     */
    Iterator<T> iterator()
}
