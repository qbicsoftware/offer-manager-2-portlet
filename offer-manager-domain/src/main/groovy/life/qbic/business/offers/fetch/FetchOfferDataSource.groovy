package life.qbic.business.offers.fetch

import life.qbic.business.exceptions.DatabaseQueryException
import life.qbic.business.offers.OfferV2
import life.qbic.business.offers.identifier.OfferId

/**
 * Provides methods to retrieve offers in a data-source
 *
 * @since: 1.0.0
 */
interface FetchOfferDataSource {

    /**
     * Returns the offer content based on the given offer id
     * @param Id specifying the offer for which the content shall be fetched
     * @throws DatabaseQueryException
     * @return the offer content in form of an Offer Dto
     */
    Optional<OfferV2> getOffer(OfferId Id) throws DatabaseQueryException

}
