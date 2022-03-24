package life.qbic.portal.offermanager.dataresources.projects

import life.qbic.business.RefactorConverter
import life.qbic.business.projects.spaces.list.ListProjectSpacesDataSource
import life.qbic.datamodel.dtos.projectmanagement.ProjectSpace
import life.qbic.portal.offermanager.communication.EventEmitter
import life.qbic.portal.offermanager.communication.Subscription
import life.qbic.portal.offermanager.dataresources.ResourcesService

/**
 * Service that holds resources about available spaces
 *
 * This service holds resources about space information and can be used to subscribe to any
 * update event of the underlying resource data.
 *
 * @since 1.0.0
 */
class ProjectSpaceResourceService implements ResourcesService<ProjectSpace>{

    private final ListProjectSpacesDataSource listProjectSpacesDataSource

    private final List<ProjectSpace> availableSpaces

    private final EventEmitter<ProjectSpace> eventEmitter

    private final RefactorConverter refactorConverter = new RefactorConverter()

    ProjectSpaceResourceService(ListProjectSpacesDataSource listProjectSpacesDataSource) {
        this.listProjectSpacesDataSource = Objects.requireNonNull(listProjectSpacesDataSource,
                "Connector must not be null.")
        this.availableSpaces = listProjectSpacesDataSource.listSpaces()
        this.eventEmitter = new EventEmitter<>()
    }

    @Override
    void reloadResources() {

    }

    @Override
    void subscribe(Subscription<ProjectSpace> subscription) {
        this.eventEmitter.register(subscription)
    }

    @Override
    void unsubscribe(Subscription<ProjectSpace> subscription) {
        this.eventEmitter.unregister(subscription)
    }

    @Override
    void addToResource(ProjectSpace resourceItem) {
        this.availableSpaces.add(resourceItem)
        this.eventEmitter.emit(resourceItem)
    }

    @Override
    void removeFromResource(ProjectSpace resourceItem) {
        this.availableSpaces.remove(resourceItem)
        this.eventEmitter.emit(resourceItem)
    }

    @Override
    Iterator<ProjectSpace> iterator() {
        return new ArrayList<>(this.availableSpaces).iterator()
    }
}
