package life.qbic.portal.qoffer2.web.viewmodel.create.offer

import groovy.beans.Bindable
import life.qbic.datamodel.dtos.business.ProjectManager

/**
 * The view model for the {@link life.qbic.portal.qoffer2.web.views.create.offer.ProjectManagerSelectionView}
 *
 * Describes the view components of the ProjectManagerSelectionView
 *
 * @since: 0.1.0
 *
 */
class ProjectManagerSelectionViewModel {

    @Bindable ProjectManager projectManager 
    List<ProjectManager> projectManagers = []
}
