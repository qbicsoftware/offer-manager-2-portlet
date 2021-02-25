package life.qbic.business.offers.fetch

import life.qbic.datamodel.dtos.business.Offer
import life.qbic.datamodel.dtos.business.OfferId

/**
 * Provides methods to retrieve offers in a data-source
 *
 * @since: 1.0.0
 */
interface FetchOfferDataSource {

    /**
     * Retrieves an offer from a persistent data-source given the associated OfferId
     *
     * Developers should execute this method, when they
     * want to fetch an offer from f.e. a database.
     *
     */

    /**
     * Returns the offer content based on the given offer id
     * @param Id specifying the offer for which the content shall be fetched
     * @return the offer content in form of an Offer Dto
     */
    Optional<Offer> getOffer(OfferId Id)

    /**
     * Fetches all versions of one offer id that are stored in the database
     *
     * @param id The id for which all versions should be found
     * @return a list of all different version identifiers of an id must be at least of size 1
     */
    List<OfferId> fetchAllVersionsForOfferId(OfferId id)

}
