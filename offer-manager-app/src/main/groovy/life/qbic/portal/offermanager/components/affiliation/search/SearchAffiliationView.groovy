package life.qbic.portal.offermanager.components.affiliation.search

import com.vaadin.ui.FormLayout
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

    SearchAffiliationView(SearchAffiliationViewModel) {
        this.SearchAffiliationViewModel = SearchAffiliationViewModel
    }

}
