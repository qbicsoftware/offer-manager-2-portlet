package life.qbic.portal.offermanager.dataresources.offers

/**
 * Contains methods to work with offer overviews data
 * @since 1.0.0
 */
interface OfferOverviewDataSource {

    /**
     * Provides a list of offer overviews for all offers
     * @return a list of offer overviews for all offers
     * @since 1.0.0
     */
    List<OfferOverview> listOfferOverviews()

}