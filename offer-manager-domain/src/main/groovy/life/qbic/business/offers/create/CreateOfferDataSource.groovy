package life.qbic.business.offers.create

import life.qbic.datamodel.dtos.business.Offer
import life.qbic.business.exceptions.DatabaseQueryException


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
     */
    void store(Offer offer) throws DatabaseQueryException
}
