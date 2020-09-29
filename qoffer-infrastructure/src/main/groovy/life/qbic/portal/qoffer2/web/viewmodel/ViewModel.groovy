package life.qbic.portal.qoffer2.web.viewmodel

import life.qbic.datamodel.dtos.business.Affiliation


/**
 * A simple DTO for data displayed in the PortletView
 *
 * This class holds information and data to be displayed in the view.
 * It can contain JavaBean objects to enable views to listen to changes in the values.
 *
 * @since: 1.0.0
 * @author: Tobias Koch
 */
class ViewModel {
    final ObservableList affiliations
    final ObservableList academicTitles
    final ObservableList successNotifications
    final ObservableList failureNotifications

    ViewModel() {
        this(new ArrayList<Affiliation>(), new ArrayList<String>(), new ArrayList<String>(), new ArrayList<String>())
    }

    private ViewModel(List<Affiliation> affiliations, List<String> academicTitles, List<String> successNotifications,
              List<String> failureNotifications) {
        this.affiliations = new ObservableList(affiliations)
        this.academicTitles = new ObservableList(academicTitles)
        this.successNotifications = new ObservableList(successNotifications)
        this.failureNotifications = new ObservableList(failureNotifications)
    }
}
