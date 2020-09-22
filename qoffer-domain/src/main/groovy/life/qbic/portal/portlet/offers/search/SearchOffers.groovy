package life.qbic.portal.portlet.offers.search

import life.qbic.portal.portlet.SearchCriteria
import life.qbic.portal.portlet.offers.OfferExporter

/**
 * This use case searches for offers matching provided criteria.
 *
 * Project managers need to check the offer items during a project in order to check the delivery of promised items.
 * The offer manager provides a search interface, where the user can set one or more search criteria to filter the offers.
 *
 * @since: 1.0.0
 * @author: Tobias Koch
 */
class SearchOffers implements SearchOffersInput{

    private final OfferExporter exporter
    private final SearchOffersOutput output

    SearchOffers(SearchOffersOutput output, OfferExporter exporter){
        this.output = output
        this.exporter = exporter
    }

    @Override
    void searchOffers(SearchCriteria searchCriteria) {

    }

    @Override
    void downloadOfferAsPdf(String offerId) {

    }
}
