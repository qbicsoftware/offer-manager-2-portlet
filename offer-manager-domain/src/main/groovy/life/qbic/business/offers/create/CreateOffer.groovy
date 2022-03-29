package life.qbic.business.offers.create

import life.qbic.business.exceptions.DatabaseQueryException
import life.qbic.business.logging.Logger
import life.qbic.business.logging.Logging
import life.qbic.business.offers.OfferExistsException
import life.qbic.business.offers.OfferV2
import life.qbic.business.offers.identifier.OfferId

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
        try {
            offer.setIdentifier(new OfferId(offer.getCustomer().getLastName().toLowerCase(), 1))
            dataSource.store(offer)
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
        output.createdNewOffer(offer)
    }



    @Override
    void updateOffer(OfferV2 offer) {
        Optional<OfferV2> persistentOffer = dataSource.getOffer(offer.getIdentifier())
        if (!persistentOffer.isPresent()) {
            output.failNotification("The provided offer (${offer.getIdentifier().toString()} is unknown to the system.")
            return
        }
        OfferV2 processedOffer = updateIdToLatestVersion(offer)
        try {
            dataSource.store(processedOffer)
            output.createdNewOffer(processedOffer)

        } catch (OfferExistsException offerExistsException) {
            log.error("Failed to update offer $offer.identifier", offerExistsException)
            output.failNotification("The provided offer is the same as the stored.")
        }
    }

    private OfferV2 updateIdToLatestVersion(OfferV2 offer) {
        OfferV2 workingCopy = OfferV2.copyOf(offer)
        Optional<OfferId> latestOfferId = dataSource.fetchAllVersionsForOfferId(offer.getIdentifier()).stream()
                .max(OfferId::compareTo)
                .map(this::increaseVersion)
        latestOfferId.ifPresent({ workingCopy.setIdentifier(it) })
        return workingCopy
    }

    private static OfferId increaseVersion(OfferId identifier) {
        def copy = new OfferId(identifier.getProjectPart(), identifier.getRandomPart(), identifier.getVersion())
        copy.increaseVersion()
        return copy
    }
}
