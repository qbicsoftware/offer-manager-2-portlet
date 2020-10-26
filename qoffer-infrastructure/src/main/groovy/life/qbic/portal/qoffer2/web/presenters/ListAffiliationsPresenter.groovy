package life.qbic.portal.qoffer2.web.presenters

import life.qbic.datamodel.dtos.business.Affiliation
import life.qbic.portal.portlet.customers.affiliation.list.ListAffiliationsOutput
import life.qbic.portal.qoffer2.web.viewmodel.ViewModel

/**
 * <short description>
 *
 * <detailed description>
 *
 * @since: <versiontag>
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
