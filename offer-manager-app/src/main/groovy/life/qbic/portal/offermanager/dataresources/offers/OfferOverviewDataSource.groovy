package life.qbic.portal.offermanager.dataresources.offers

import com.vaadin.shared.data.sort.SortOrder
import life.qbic.datamodel.dtos.business.OfferId

/**
 * Contains methods to work with offer overviews data.
 *
 * <p>Provides paged access to the latest version of each offer, supporting server-side
 * filtering and sorting, so that only the currently visible rows are loaded from the
 * database.</p>
 *
 * @since 1.0.0
 */
interface OfferOverviewDataSource {

    /**
     * Fetches a page of offer overviews containing only the latest version of each offer.
     *
     * @param offset the index of the first row to load
     * @param limit the maximum number of rows to load
     * @param filter the server-side filter criteria (may be {@code null})
     * @param sortOrders the server-side sort order to apply (may be empty)
     * @return a list of at most {@code limit} offer overviews
     * @since 1.0.0
     */
    List<OfferOverview> fetchLatestOverviews(int offset, int limit, OfferFilter filter,
                                             List<SortOrder<String>> sortOrders)

    /**
     * Counts the number of latest offer versions that match the given filter.
     *
     * @param filter the server-side filter criteria (may be {@code null})
     * @return the number of matching latest offer overviews
     * @since 1.0.0
     */
    int countLatestOverviews(OfferFilter filter)

    /**
     * Fetches all offer versions that belong to the same offer family as the given offer id.
     *
     * @param familyId an offer id of the family whose versions shall be loaded
     * @return all overviews of the versions belonging to that offer family
     * @since 1.0.0
     */
    List<OfferOverview> fetchVersionsOfOffer(OfferId familyId)

}