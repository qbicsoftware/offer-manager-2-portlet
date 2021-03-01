package life.qbic.portal.offermanager.components


import life.qbic.datamodel.dtos.business.Affiliation
import life.qbic.portal.offermanager.dataresources.persons.AffiliationResourcesService
import life.qbic.portal.offermanager.security.Role
import life.qbic.portal.offermanager.security.RoleService


/**
 * A simple DTO for data displayed in the PortletView
 *
 * This class holds information and data to be displayed in the view.
 * It can contain JavaBean objects to enable views to listen to changes in the values.
 *
 * @since: 1.0.0
 * @author: Tobias Koch
 */
class AppViewModel {
    final ObservableList successNotifications
    final ObservableList failureNotifications

    private final AffiliationResourcesService service

    private final Role role

    boolean createOfferFeatureEnabled

    boolean createCustomerFeatureEnabled

    boolean searchCustomerFeatureEnabled

    AppViewModel(AffiliationResourcesService service,
                 Role role) {
        this(new ArrayList<Affiliation>(),
                new ArrayList<String>(),
                new ArrayList<String>(),
                new ArrayList<String>(),
                service,
                role)
    }

    private AppViewModel(List<Affiliation> affiliations,
                         List<String> academicTitles,
                         List<String> successNotifications,
                         List<String> failureNotifications,
                         AffiliationResourcesService service,
                         Role role) {
        this.successNotifications = new ObservableList(successNotifications)
        this.failureNotifications = new ObservableList(failureNotifications)
        this.service = service
        this.role = role
        activateFeatures()
    }

    private void activateFeatures(){
        setBasicFeatures()
        if (role.equals(Role.OFFER_ADMIN)) {
            setAdminFeatures()
        }
    }

    private void setBasicFeatures() {
        createCustomerFeatureEnabled = true
        createOfferFeatureEnabled = true
        searchCustomerFeatureEnabled = false
    }

    private void setAdminFeatures() {
        searchCustomerFeatureEnabled = true
    }
}
