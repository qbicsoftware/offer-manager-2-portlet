package life.qbic.portal.offermanager.web.viewmodel


import life.qbic.datamodel.dtos.business.Affiliation
import life.qbic.portal.offermanager.customers.AffiliationResourcesService


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

    private final AffiliationResourcesService service

    ViewModel(AffiliationResourcesService service) {
        this(new ArrayList<Affiliation>(),
                new ArrayList<String>(),
                new ArrayList<String>(),
                new ArrayList<String>(),
                service)
    }

    private ViewModel(List<Affiliation> affiliations,
                      List<String> academicTitles,
                      List<String> successNotifications,
                      List<String> failureNotifications,
                      AffiliationResourcesService service) {
        this.affiliations = new ObservableList(affiliations)
        this.successNotifications = new ObservableList(successNotifications)
        this.failureNotifications = new ObservableList(failureNotifications)
        this.service = service
        /*
        We register a subscription that will receive a list of affiliations, when
        the affiliation service emits an update event.
         */
        this.service.eventEmitter.register((List<Affiliation> updatedAffiliations) -> {
            this.affiliations.clear()
            this.affiliations.addAll(updatedAffiliations)
        })
    }
}
