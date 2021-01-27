package life.qbic.portal.offermanager.web.presenters

import life.qbic.datamodel.dtos.business.Affiliation
import life.qbic.business.customers.affiliation.list.ListAffiliationsOutput
import life.qbic.portal.offermanager.web.viewmodel.ViewModel

/**
 * This class acts as a presenter for the ListAffiliation use case. It takes care of reflecting the updated information in the view model
 *
 * @since: 1.0.0
 */
class ListAffiliationsPresenter implements ListAffiliationsOutput{

    private final ViewModel viewModel

    ListAffiliationsPresenter(ViewModel viewModel) {
        this.viewModel = viewModel
    }

    @Override
    void reportAvailableAffiliations(List<Affiliation> affiliations) {
        viewModel.affiliations.clear()
        viewModel.affiliations.addAll(affiliations)
    }
}
