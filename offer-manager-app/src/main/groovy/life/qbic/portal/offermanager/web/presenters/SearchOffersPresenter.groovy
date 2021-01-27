package life.qbic.portal.offermanager.web.presenters

import life.qbic.datamodel.dtos.business.Offer
import life.qbic.business.offers.search.SearchOffersOutput

/**
 * AppPresenter for the SearchOffers
 *
 * This presenter handles the output of the Search Offers use case and prepares it for an appropriate view.
 *
 * @since: 1.0.0
 */
class SearchOffersPresenter implements SearchOffersOutput {
    @Override
    void matchingOffers(List<Offer> offers) {
        //TODO implement
    }
}
