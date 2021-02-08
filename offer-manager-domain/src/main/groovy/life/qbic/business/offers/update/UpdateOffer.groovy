package life.qbic.business.offers.update

import life.qbic.business.Constants
import life.qbic.business.exceptions.DatabaseQueryException
import life.qbic.business.logging.Logger
import life.qbic.business.logging.Logging
import life.qbic.business.offers.Converter
import life.qbic.business.offers.Offer
import life.qbic.business.offers.create.CalculatePrice
import life.qbic.business.offers.create.CreateOfferInput
import life.qbic.business.offers.create.CreateOfferOutput
import life.qbic.business.offers.identifier.OfferId
import life.qbic.datamodel.dtos.business.Affiliation
import life.qbic.datamodel.dtos.business.ProductItem

/**
 * <h1>SRS - 4.2.2 Update Offer</h1>
 * <br>
 * <p> During the offer preparation, the customer might request changes for the offer items (number of samples, change in the technology used for analysis, etc.).
 * <br>
 * The offer manager provides an interface to update an existing offer and create a new version from it. </p>
 *
 * @since: 1.0.0
 */
class UpdateOffer implements CreateOfferInput, CalculatePrice{

    private final UpdateOfferDataSource dataSource
    private final CreateOfferOutput output
    private final Logging log = Logger.getLogger(this.class)


    UpdateOffer(UpdateOfferDataSource dataSource, CreateOfferOutput output) {
        this.dataSource = dataSource
        this.output = output
    }

    @Override
    void createOffer(life.qbic.datamodel.dtos.business.Offer offerContent) {

        OfferId oldId = Converter.buildOfferId(offerContent.identifier)
        //fetch old offer by id
        life.qbic.datamodel.dtos.business.Offer offer = dataSource.getOfferById(offerContent.identifier)

        Offer oldOffer = new Offer.Builder(
                offer.customer,
                offer.projectManager,
                offer.projectTitle,
                offer.projectDescription,
                offer.items,
                offer.selectedCustomerAffiliation)
                .identifier(oldId)
                .build()

        OfferId identifier = increaseOfferIdentifier(offerContent.identifier)

        Offer finalizedOffer = new Offer.Builder(
                offerContent.customer,
                offerContent.projectManager,
                offerContent.projectTitle,
                offerContent.projectDescription,
                offerContent.items,
                offerContent.selectedCustomerAffiliation)
                .identifier(identifier)
                .build()

        if(oldOffer.checksum() != finalizedOffer.checksum()){
            storeOffer(finalizedOffer)
        }else{
            output.failNotification("An unchanged offer cannot be updated")
        }
    }


    private void storeOffer(Offer finalizedOffer) {
        try {
            final offer = Converter.convertOfferToDTO(finalizedOffer)
            dataSource.store(offer)
            output.createdNewOffer(offer)
        } catch (DatabaseQueryException e) {
            output.failNotification(e.message)
        } catch (Exception unexpected) {
            log.error unexpected.message
            log.error unexpected.stackTrace.join("\n")
            output.failNotification("An unexpected during the saving of your offer occurred. " +
                    "Please contact ${Constants.QBIC_HELPDESK_EMAIL}.")
        }
    }

    private OfferId increaseOfferIdentifier(life.qbic.datamodel.dtos.business.OfferId oldOfferId){
        OfferId convertedId = null

        try{
            //search for all ids in the database
            List<life.qbic.datamodel.dtos.business.OfferId> allVersionIds = dataSource.fetchAllVersionsForOfferId(oldOfferId)
            //take the latest one and increase it
            life.qbic.datamodel.dtos.business.OfferId latestVersion = getLatestVersion(allVersionIds)

            convertedId = Converter.buildOfferId(latestVersion)
            convertedId.increaseVersion()
        }catch(DatabaseQueryException e){
            output.failNotification(e.message)
        }

        return convertedId
    }

    private static life.qbic.datamodel.dtos.business.OfferId getLatestVersion(List<life.qbic.datamodel.dtos.business.OfferId> ids){
        life.qbic.datamodel.dtos.business.OfferId maxID = null
        int maxVersion = -1

        ids.each {id ->
            int currentVersion = Integer.parseInt(id.getVersion())
            if ( currentVersion > maxVersion){
                maxVersion = currentVersion
                maxID = id
            }
        }
        return maxID
    }

    @Override
    void calculatePrice(List<ProductItem> items, Affiliation affiliation) {
        Offer offer = Converter.buildOfferForCostCalculation(items, affiliation)
        output.calculatedPrice(
                offer.getTotalNetPrice(),
                offer.getTaxCosts(),
                offer.getOverheadSum(),
                offer.getTotalCosts())
    }

}
