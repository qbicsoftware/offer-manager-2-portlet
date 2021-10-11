package life.qbic.portal.offermanager.dataresources.persons

import life.qbic.business.persons.list.ListPersonsDataSource
import life.qbic.datamodel.dtos.business.ProjectManager
import life.qbic.datamodel.people.Person
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
class ProjectManagerResourceService implements ResourcesService<ProjectManager>{

    private final List<ProjectManager> availableProjectManagers
    private final ListPersonsDataSource listPersonsDataSource

    private final EventEmitter<ProjectManager> resourceUpdateEvent

    ProjectManagerResourceService(ListPersonsDataSource listPersonsDataSource) {
        availableProjectManagers = listPersonsDataSource.listAllProjectManagers()
        this.listPersonsDataSource = listPersonsDataSource
        resourceUpdateEvent = new EventEmitter<>()
    }

    @Override
    void reloadResources() {
        //do it explicitly to trigger the service
        availableProjectManagers.each {
            removeFromResource(it)
        }
        List updatedEntries = listPersonsDataSource.listAllProjectManagers()
        updatedEntries.each {
            addToResource(it)
        }
    }

    @Override
    void subscribe(Subscription<ProjectManager> subscription) {
        resourceUpdateEvent.register(subscription)
    }

    @Override
    void unsubscribe(Subscription<ProjectManager> subscription) {
        resourceUpdateEvent.unregister(subscription)
    }

    @Override
    void addToResource(ProjectManager resourceItem) {
        availableProjectManagers.add(resourceItem)
        resourceUpdateEvent.emit(resourceItem)
    }

    @Override
    void removeFromResource(ProjectManager resourceItem) {
        availableProjectManagers.remove(resourceItem)
        resourceUpdateEvent.emit(resourceItem)

    }

    @Override
    Iterator<ProjectManager> iterator() {
        return new ArrayList(availableProjectManagers).iterator()
    }
}
