package life.qbic.portal.offermanager.dataresources.persons

import life.qbic.business.RefactorConverter
import life.qbic.business.persons.list.ListPersonsDataSource
import life.qbic.datamodel.dtos.business.Affiliation
import life.qbic.datamodel.dtos.business.ProjectManager
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

    private final RefactorConverter refactorConverter = new RefactorConverter()

    ProjectManagerResourceService(ListPersonsDataSource listPersonsDataSource, ResourcesService<Affiliation> affiliationResourcesService) {
        availableProjectManagers = listPersonsDataSource.listPersons().stream().map(refactorConverter::toProjectManagerDto).collect()
        this.listPersonsDataSource = listPersonsDataSource
        resourceUpdateEvent = new EventEmitter<>()
        affiliationResourcesService.subscribe(it -> reloadResources())
    }

    @Override
    void reloadResources() {
        availableProjectManagers.clear()

        List<ProjectManager> updatedEntries = listPersonsDataSource.listPersons().stream().map(refactorConverter::toProjectManagerDto).collect()
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
