package life.qbic.business.offers.create

import life.qbic.business.exceptions.DatabaseQueryException
import life.qbic.business.logging.Logger
import life.qbic.business.logging.Logging
import life.qbic.business.offers.OfferCalculus
import life.qbic.business.offers.OfferExistsException
import life.qbic.business.offers.OfferV2

/**
 * This class implements logic to create new offers.
 *
 * A PM has received a new project request and uses the offer manager to create a new offer for the customer.
 * Alternatively a new offer is created from an existing offer.
 * @since 1.0.0
 * @author Tobias Koch
 */
class CreateOffer implements CreateOfferInput {



    private static final Logging log = Logger.getLogger(this.class)

    private CreateOfferDataSource dataSource
    private CreateOfferOutput output

    CreateOffer(CreateOfferDataSource dataSource, CreateOfferOutput output){
        this.dataSource = dataSource
        this.output = output
    }

    @Override
    void createOffer(OfferV2 offer) {
        OfferV2 processedOffer = OfferCalculus.process(offer)
        try {
            dataSource.store(processedOffer)
        } catch (OfferExistsException offerExistsException) {
            String message = "Offer $offer already exists in the database."
            log.error(message, offerExistsException)
            output.failNotification(message)
            return
        } catch (DatabaseQueryException databaseQueryException) {
            String message = "Offer $offer could not be stored."
            log.error(message, databaseQueryException)
            output.failNotification(message)
            return
        }
        output.createdNewOffer(processedOffer)
    }

    @Override
    void updateOffer(OfferV2 offer) {

    }
}
