package life.qbic.portal.offermanager.components.affiliation.search

import com.vaadin.ui.FormLayout
import com.vaadin.ui.Grid
import life.qbic.datamodel.dtos.business.Affiliation
import life.qbic.portal.offermanager.dataresources.persons.AffiliationResourcesService

/**
 * <h1>View allowing the user to search for an affiliation</h1>
 *
 * <p>This view provides functionality for searching for affiliations.</p>
 *
 * @since 1.0.0
 */
class SearchAffiliationView extends FormLayout{

    private final SearchAffiliationViewModel

    private Grid<Affiliation> affiliationGrid

    SearchAffiliationView(SearchAffiliationViewModel) {
        this.SearchAffiliationViewModel = SearchAffiliationViewModel
        initLayout()
        setupAffiliationGrid()
        listenToAffiliationSelection()
    }

    private void initLayout() {

    }

    private void setupAffiliationGrid() {

    }

    /**
     * Sets a listener to the affiliation grid.
     * @see #onAffiliationSelection
     */
    private void listenToAffiliationSelection() {

    }

    /**
     * This method performs actions on affiliation selection.
     *
     */
    private void onAffiliationSelection() {

    }



}
