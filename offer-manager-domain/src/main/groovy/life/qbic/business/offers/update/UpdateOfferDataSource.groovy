package life.qbic.business.offers.update

import life.qbic.business.exceptions.DatabaseQueryException
import life.qbic.datamodel.dtos.business.Offer
import life.qbic.datamodel.dtos.business.OfferId

/**
 * Provides methods to store offers in a data-source
 *
 * @since: 1.0.0
 */
interface UpdateOfferDataSource {

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
     */
    void store(Offer offer) throws DatabaseQueryException


    /**
     * Fetches all versions of one offer id that are stored in the database
     *
     * @param id The id for which all versions should be found
     * @return a list of all different version identifiers of an id must be at least of size 1
     */
    List<OfferId> fetchAllVersionsForOfferId(String id)


    /**
     * Returns the offer content based on the given offer id
     * @param oldId specifying the offer for which the content shall be fetched
     * @return the offer content in form of the offer dto
     */
    Offer getOfferById(OfferId oldId)
}
