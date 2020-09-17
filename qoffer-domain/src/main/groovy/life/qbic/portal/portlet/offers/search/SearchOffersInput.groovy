package life.qbic.portal.portlet.offers.search

import life.qbic.portal.portlet.SearchCriteria


/**
 * Input interface for the {@link life.qbic.portal.portlet.offers.search.SearchOffers} use case
 *
 * @since: 1.0.0
 * @author: Tobias Koch
 */
interface SearchOffersInput {

    /**
     * Triggers the search for an offer based on the given {@link SearchCriteria}
     *
     * @param searchCriteria defines characteristics of the offer like customer name or the group name of the users affiliation
     */
    void searchOffers(SearchCriteria searchCriteria)

    /**
     * Triggers the download of the given offer as an PDF to the users system
     *
     * @param offerId specifies the offer that should be downloaded
     */
    void downloadOfferAsPdf(String offerId)

}