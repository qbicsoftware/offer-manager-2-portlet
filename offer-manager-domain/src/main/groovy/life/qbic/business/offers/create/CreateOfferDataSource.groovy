package life.qbic.business.offers.create

import life.qbic.business.exceptions.DatabaseQueryException
import life.qbic.business.offers.OfferV2
import life.qbic.business.offers.identifier.OfferId


/**
 * Provides methods to store offers in a data-source
 *
 * @since: 1.0.0
 */
interface CreateOfferDataSource {

    /**
     * Saves an offer in a persistent data-source.
     *
     * Developers should execute this method, when they
     * want to store an offer in f.e. a database.
     *
     * If the passed offer is already in the database (offer
     * content with the same offer identifier already exists),
     * then this method will throw a DatabaseQueryException.
     *
     * If the passed offer cannot be saved successfully, this method
     * will also throw a DatabaseQueryException.
     *
     * @param offer
     * @throws DatabaseQueryException
     * @since 1.0.0
     */
    void store(OfferV2 offer) throws DatabaseQueryException


    /**
     * This method will be replaced with the one provided by{@link life.qbic.business.offers.fetch.FetchOfferDataSource}
     * Fetches all versions of one offer id that are stored in the database
     * @param id The id for which all versions should be found
     * @return a list of all different version identifiers of an id must be at least of size 1
     */
    List<OfferId> fetchAllVersionsForOfferId(OfferId id)

    //ToDo Deprecate this method once the FetchOffer Use Case is implemented
    /**
     * This method will be replaced with the one provided by{@link life.qbic.business.offers.fetch.FetchOfferDataSource}
     * Returns the offer content based on the given offer id
     * @param oldId specifying the offer for which the content shall be fetched
     * @return the offer content in form of the offer dto
     */
    Optional<OfferV2> getOffer(OfferId oldId)
}
