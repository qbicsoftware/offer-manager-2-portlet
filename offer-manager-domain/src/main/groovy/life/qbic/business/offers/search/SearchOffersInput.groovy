package life.qbic.business.offers.search


/**
 * Input interface for the {@link SearchOffers} use case
 *
 * @since: 1.0.0
 * @author: Tobias Koch
 */
interface SearchOffersInput {

    /**
     * Searches for offers containing the passed character sequence.
     *
     * This method triggers the business use case `Search Offer` and shall
     * be called, whenever a user wants to search for offers containing the passed
     * character sequence in any of the offer's content properties.
     *
     * This type of search is known as free-text search.
     *
     * @param offerContent A character sequence to search for in the stored offers datasource.
     */
    void searchOffer(String offerContent)
}
