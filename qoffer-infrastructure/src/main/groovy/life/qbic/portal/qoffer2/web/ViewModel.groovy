package life.qbic.portal.qoffer2.web

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
    final ObservableList successNotifications
    final ObservableList failureNotifications

    ViewModel() {
        this(new ArrayList<Affiliation>(), new ArrayList<String>(), new ArrayList<String>())
    }

    ViewModel(List<Affiliation> affiliations, List<String> successNotifications,
              List<String> failureNotifications) {
        //this.affiliations = new ObservableList(affiliations)
        Affiliation affiliation = new Affiliation.Builder("Universität Tübingen",
                "Auf der Morgenstelle 10",
                "72076",
                "Tübingen").build()

        Affiliation affiliation2 = new Affiliation.Builder("None",
                "Gartenstrasse 20",
                "72070",
                "Tübingen").build()

        this.affiliations = new ObservableList([affiliation,affiliation2])
        this.successNotifications = new ObservableList(successNotifications)
        this.failureNotifications = new ObservableList(failureNotifications)
    }

    /**
     * Notifies the user about a successful operation
     *
     * @param notification describing what has been successfully finished
     */
    void showSuccessNotification(String notification){
        successNotifications.add(notification)
    }
}
