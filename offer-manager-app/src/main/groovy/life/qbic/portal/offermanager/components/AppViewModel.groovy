package life.qbic.portal.offermanager.components


import life.qbic.portal.offermanager.security.Role

/**
 * A simple DTO for data displayed in the PortletView
 *
 * This class holds information and data to be displayed in the view.
 * It can contain JavaBean objects to enable views to listen to changes in the values.
 *
 * @since 1.0.0
 * @author Tobias Koch
 */
class AppViewModel {
    final ObservableList successNotifications
    final ObservableList failureNotifications

    private final Role role

    boolean createOfferFeatureEnabled
    boolean createCustomerFeatureEnabled
    boolean searchCustomerFeatureEnabled
    boolean maintainProductsFeatureEnabled

    AppViewModel(Role role) {
        this(new ArrayList<String>(), new ArrayList<String>(), role)
    }

    private AppViewModel(List<String> successNotifications,
                         List<String> failureNotifications,
                         Role role) {
        this.successNotifications = new ObservableList(successNotifications)
        this.failureNotifications = new ObservableList(failureNotifications)
        this.role = role
        activateFeatures()
    }

    private void activateFeatures() {
        setBasicFeatures()
        if (role.equals(Role.OFFER_ADMIN)) {
            setAdminFeatures()
        }
    }

    private void setBasicFeatures() {
        createCustomerFeatureEnabled = true
        createOfferFeatureEnabled = true
        searchCustomerFeatureEnabled = true
        maintainProductsFeatureEnabled = false
    }

    private void setAdminFeatures() {
        maintainProductsFeatureEnabled = true
    }
}
