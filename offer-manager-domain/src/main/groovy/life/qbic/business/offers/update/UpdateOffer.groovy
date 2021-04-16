package life.qbic.business.offers.update

import life.qbic.business.Constants
import life.qbic.business.exceptions.DatabaseQueryException
import life.qbic.business.logging.Logger
import life.qbic.business.logging.Logging
import life.qbic.business.offers.Converter
import life.qbic.business.offers.Offer
import life.qbic.business.offers.create.CreateOfferDataSource
import life.qbic.business.offers.create.CreateOfferOutput
import life.qbic.business.offers.identifier.OfferId

/**
 * <h1>SRS - 4.2.2 Update Offer</h1>
 * <br>
 * <p> During the offer preparation, the customer might request changes for the offer items (number of samples, change in the technology used for analysis, etc.).
 * <br>
 * The offer manager provides an interface to update an existing offer and create a new version from it. </p>
 *
 * @since: 1.0.0
 */
class UpdateOffer{

    private final CreateOfferDataSource dataSource
    private final UpdateOfferOutput output
    private final Logging log = Logger.getLogger(this.class)
    private Offer offerToUpdate


    UpdateOffer(CreateOfferDataSource dataSource, UpdateOfferOutput output) {
        this.dataSource = dataSource
        this.output = output
    }

    void updateOffer(life.qbic.datamodel.dtos.business.Offer offerContent) {
        offerToUpdate = createBusinessOffer(offerContent)
        //fetch old offer by id
        Offer existingOffer
        try {
            existingOffer = getOfferById(offerToUpdate.getIdentifier())
        } catch (NullPointerException e) {
            log.debug(e.stackTrace.join("\n"))
            output.failNotification("No offer was found for the ID ${offerContent.identifier.toString()}")
            return
        } catch (Exception e) {
            log.debug(e.message)
            log.debug(e.stackTrace.join("\n"))
            output.failNotification("An unexpected exception occurred during the offer update: " +
                    "${offerContent.identifier.toString()}")
            return
        }

        if (theOfferHasChanged(existingOffer, offerToUpdate)) {
            updateVersion()
            storeOffer()
        } else {
            log.error "An unchanged offer cannot be updated"
            output.failNotification("An unchanged offer cannot be updated")
        }
    }

    private void storeOffer() {
        try {
            final offer = Converter.convertOfferToDTO(offerToUpdate)
            println offer.experimentalDesign
            dataSource.store(offer)
            output.updatedOffer(offer)
        } catch (Exception e) {
            log.error(e.message)
            log.error(e.stackTrace.join("\n"))
            output.failNotification("An unexpected error during the saving of your offer " +
                    "occurred. " +
                    "Please contact ${Constants.QBIC_HELPDESK_EMAIL}.")
        }
    }

    private void updateVersion(){
        // We get all available versions first
        fetchAllAvailableVersions()
        // Then we update the version
        offerToUpdate.increaseVersion()
    }

    private void fetchAllAvailableVersions() {
        def versions = dataSource.fetchAllVersionsForOfferId(Converter.convertIdToDTO(offerToUpdate
                .identifier))
        offerToUpdate.addAllAvailableVersions(
                versions.stream()
                        .map(version -> Converter.buildOfferId(version))
                        .collect())
    }

    private static Offer createBusinessOffer(life.qbic.datamodel.dtos.business.Offer offer){
        return new Offer.Builder(
                offer.customer,
                offer.projectManager,
                offer.projectTitle,
                offer.projectDescription,
                offer.items,
                offer.selectedCustomerAffiliation)
                .experimentalDesign(offer.experimentalDesign)
                .identifier(Converter.buildOfferId(offer.identifier))
                .build()
    }

    private Offer getOfferById(offerId){
        // Will throw a NullPointer Exception, when the offer is not present
        def offerDTO = dataSource.getOffer(Converter.convertIdToDTO(offerId)).get()
        return createBusinessOffer(offerDTO)
    }

    private OfferId createNewVersionTag(){

        //search for all ids in the database
        List<OfferId> allVersionIds = dataSource
                .fetchAllVersionsForOfferId(Converter.convertIdToDTO(offerToUpdate.identifier))
                .stream().map(offerId -> Converter.buildOfferId(offerId))
                .collect()

        offerToUpdate.addAllAvailableVersions(allVersionIds)

        OfferId convertedId = offerToUpdate.getLatestVersion()
        convertedId.increaseVersion()

        return convertedId
    }

    private static boolean theOfferHasChanged(Offer oldOffer, Offer newOffer) {
        return oldOffer.checksum() != newOffer.checksum()
    }

    private static Offer buildOffer(life.qbic.datamodel.dtos.business.Offer offer, OfferId identifier){
        return new Offer.Builder(
                        offer.customer,
                        offer.projectManager,
                        offer.projectTitle,
                        offer.projectDescription,
                        offer.items,
                        offer.selectedCustomerAffiliation)
                        .identifier(identifier)
                        .build()
    }

}
