package life.qbic.portal.offermanager.dataresources.projects

import life.qbic.datamodel.dtos.projectmanagement.ProjectIdentifier
import life.qbic.portal.offermanager.communication.EventEmitter
import life.qbic.portal.offermanager.communication.Subscription
import life.qbic.portal.offermanager.dataresources.ResourcesService

/**
 * Service that holds resources about available projects
 *
 * This service holds resources about project identifiers and can be used to subscribe to any
 * update event of the underlying resource data.
 *
 * @since 1.0.0
 */
class ProjectResourceService implements ResourcesService<ProjectIdentifier>{

    private final ProjectMainConnector projectMainConnector

    private final List<ProjectIdentifier> existingProjects

    private final EventEmitter<ProjectIdentifier> eventEmitter

    ProjectResourceService(ProjectMainConnector projectMainConnector) {
        this.projectMainConnector = Objects.requireNonNull(projectMainConnector, "Connector " +
                "must not be null.")
        this.existingProjects = projectMainConnector.fetchProjects()
        this.eventEmitter = new EventEmitter<>()
    }

    @Override
    void reloadResources() {

    }

    @Override
    void subscribe(Subscription<ProjectIdentifier> subscription) {
        this.eventEmitter.register(subscription)
    }

    @Override
    void unsubscribe(Subscription<ProjectIdentifier> subscription) {
        this.eventEmitter.unregister(subscription)
    }

    @Override
    void addToResource(ProjectIdentifier resourceItem) {
        this.existingProjects.add(resourceItem)
        this.eventEmitter.emit(resourceItem)
    }

    @Override
    void removeFromResource(ProjectIdentifier resourceItem) {
        this.existingProjects.remove(resourceItem)
        this.eventEmitter.emit(resourceItem)
    }

    @Override
    Iterator<ProjectIdentifier> iterator() {
        return new ArrayList<>(this.existingProjects).iterator()
    }
}