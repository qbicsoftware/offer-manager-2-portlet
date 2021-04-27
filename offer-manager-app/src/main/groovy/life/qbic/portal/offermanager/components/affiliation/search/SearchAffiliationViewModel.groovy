package life.qbic.portal.offermanager.components.affiliation.search

import groovy.beans.Bindable
import life.qbic.datamodel.dtos.business.Affiliation
import life.qbic.portal.offermanager.dataresources.persons.AffiliationResourcesService

/**
 * <h1>ViewModel for SearchAffiliationView</h1>
 *
 * <p>Determines state of the view. Listens to data events.</p>
 *
 * @since 1.0.0
 */
class SearchAffiliationViewModel {

    /**
     * A list of available affiliations. All items are of class {@link Affiliation}
     */
    ObservableList affiliations
    @Bindable Optional<Affiliation> selectedAffiliation
    @Bindable boolean detailsVisible

    private final AffiliationResourcesService affiliationResourcesService

    SearchAffiliationViewModel(AffiliationResourcesService affiliationResourcesService) {
        this.affiliationResourcesService = affiliationResourcesService
        this.affiliations = new ObservableList(new ArrayList<Affiliation>())
        resetAffiliations()
        subscribeToResources()
        detailsVisible = selectedAffiliation.isPresent()
    }

    /**
     * Resets the list of affiliations to the state of the affiliation
     * resource service. Deselects selected affiliation.
     * @since 1.0.0
     * @see #affiliations
     */
    void resetAffiliations() {
        affiliations.clear()
        selectedAffiliation = Optional.empty()

        affiliations.addAll(affiliationResourcesService.iterator())
    }

    /**
     * As we can not distinguish on the type of event.
     * Therefore, we reset the whole list.
     * @see #resetAffiliations
     */
    private void subscribeToResources() {
        this.affiliationResourcesService.subscribe({
            resetAffiliations()
        })
    }
}
