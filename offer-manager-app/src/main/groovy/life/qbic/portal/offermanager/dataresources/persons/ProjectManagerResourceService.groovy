package life.qbic.portal.offermanager.dataresources.persons

import life.qbic.business.persons.Person
import life.qbic.business.persons.list.ListPersonsDataSource
import life.qbic.portal.offermanager.communication.EventEmitter
import life.qbic.portal.offermanager.communication.Subscription
import life.qbic.portal.offermanager.dataresources.ResourcesService

/**
 * Project manager service that holds resources about available managers
 *
 * This service holds resources about project manager information and can be used to subscribe to
 * any update event of the underlying resource data.
 *
 * @since 1.0.0
 */
class ProjectManagerResourceService implements ResourcesService<Person>{

    private final List<Person> availableProjectManagers
    private final ListPersonsDataSource listPersonsDataSource

    private final EventEmitter<Person> resourceUpdateEvent

    ProjectManagerResourceService(ListPersonsDataSource listPersonsDataSource) {
        availableProjectManagers = listPersonsDataSource.listPersons()
        this.listPersonsDataSource = listPersonsDataSource
        resourceUpdateEvent = new EventEmitter<>()
    }

    @Override
    void reloadResources() {
        availableProjectManagers.clear()

        List<Person> updatedEntries = listPersonsDataSource.listPersons()
        updatedEntries.each {
            addToResource(it)
        }
    }

    @Override
    void subscribe(Subscription<Person> subscription) {
        resourceUpdateEvent.register(subscription)
    }

    @Override
    void unsubscribe(Subscription<Person> subscription) {
        resourceUpdateEvent.unregister(subscription)
    }

    @Override
    void addToResource(Person resourceItem) {
        availableProjectManagers.add(resourceItem)
        resourceUpdateEvent.emit(resourceItem)
    }

    @Override
    void removeFromResource(Person resourceItem) {
        availableProjectManagers.remove(resourceItem)
        resourceUpdateEvent.emit(resourceItem)

    }

    @Override
    Iterator<Person> iterator() {
        return new ArrayList(availableProjectManagers).iterator()
    }
}
