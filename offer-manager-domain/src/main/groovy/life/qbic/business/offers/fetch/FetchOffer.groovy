package life.qbic.business.offers.fetch

import groovy.transform.CompileStatic
import life.qbic.business.logging.Logger
import life.qbic.business.logging.Logging
import life.qbic.business.offers.OfferV2
import life.qbic.business.offers.identifier.OfferId

/**
 * This class implements logic to fetch an Offer from the database
 *
 * This Use Case is responsible for correctly returning an Offer from the database.
 * It also triggers all the internal calculation methods associated with the offer entity
 * and returns the filled Offer DTO for further usage
 *
 * @since: 1.0.0
 */
@CompileStatic
class FetchOffer implements FetchOfferInput {

    private FetchOfferDataSource dataSource
    private FetchOfferOutput output
    private final Logging log = Logger.getLogger(this.class)

    FetchOffer(FetchOfferDataSource dataSource, FetchOfferOutput output){
        this.dataSource = dataSource
        this.output = output
    }

    /**
     * Retrieves an offer from a (persistent) datasource.
     *
     * Developers shall call this method to pass an OfferId
     * provided from the user in order to trigger the completion
     * of the business use case `Fetch Offer`,
     * which will apply business policies for offer retrieval
     * in a pre-configured, optimally persistent data-source.
     *
     * @param offerId {@link life.qbic.datamodel.dtos.business.OfferId}
     * @since 1.0.0
     *
     */
    @Override
    void fetchOffer(OfferId offerId) {

        Optional<OfferV2> fetchedOffer
        try {
            fetchedOffer = dataSource.getOffer(offerId)
        } catch (RuntimeException e) {
            String message = "Database query unsuccessful for offer ${offerId.toString()}"
            log.error(message, e)
            output.failNotification(message)
            return
        }
        if (!fetchedOffer.isPresent()) {
            String message = "Failed to find an offer for $offerId.projectPart $offerId.randomPart $offerId.version"
            log.info(message)
            output.failNotification(message)
        }
        fetchedOffer.ifPresent({
            output.fetchedOffer(it)
        })
    }
}
