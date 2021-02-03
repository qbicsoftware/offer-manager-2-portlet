package life.qbic.business.offers.update

import life.qbic.business.Constants
import life.qbic.business.exceptions.DatabaseQueryException
import life.qbic.business.logging.Logger
import life.qbic.business.logging.Logging
import life.qbic.business.offers.Converter
import life.qbic.business.offers.Offer
import life.qbic.business.offers.identifier.OfferId
import life.qbic.business.offers.update.UpdateOfferDataSource
import life.qbic.business.offers.update.UpdateOfferInput
import life.qbic.business.offers.update.UpdateOfferOutput

/**
 * <h1>SRS - 4.2.2 Update Offer</h1>
 * <br>
 * <p> During the offer preparation, the customer might request changes for the offer items (number of samples, change in the technology used for analysis, etc.).
 * <br>
 * The offer manager provides an interface to update an existing offer and create a new version from it. </p>
 *
 * @since: 1.0.0
 */
class UpdateOffer implements UpdateOfferInput{

    private final UpdateOfferDataSource dataSource
    private final UpdateOfferOutput output
    private final Logging log = Logger.getLogger(this.class)


    UpdateOffer(UpdateOfferDataSource dataSource, UpdateOfferOutput output) {
        this.dataSource = dataSource
        this.output = output
    }

    @Override
    void updateExistingOffer(life.qbic.datamodel.dtos.business.Offer offerContent) {
        OfferId identifier = Converter.buildOfferId(offerContent.identifier)
        identifier.increaseVersion()

        Offer finalizedOffer = new Offer.Builder(
                offerContent.customer,
                offerContent.projectManager,
                offerContent.projectTitle,
                offerContent.projectDescription,
                offerContent.items,
                offerContent.selectedCustomerAffiliation)
                .identifier(identifier)
                .build()

        storeOffer(finalizedOffer)
    }

    @Override
    void updateExistingOffer(life.qbic.datamodel.dtos.business.Offer newOfferContent, life.qbic.datamodel.dtos.business.Offer oldOfferContent) {

        OfferId identifier = Converter.buildOfferId(newOfferContent.identifier)
        identifier.increaseVersion()

        Offer finalizedOffer = new Offer.Builder(
                newOfferContent.customer,
                newOfferContent.projectManager,
                newOfferContent.projectTitle,
                newOfferContent.projectDescription,
                newOfferContent.items,
                newOfferContent.selectedCustomerAffiliation)
                .identifier(identifier)
                .build()


        Offer oldOffer = new Offer.Builder(
                oldOfferContent.customer,
                oldOfferContent.projectManager,
                oldOfferContent.projectTitle,
                oldOfferContent.projectDescription,
                oldOfferContent.items,
                oldOfferContent.selectedCustomerAffiliation)
                .identifier(Converter.buildOfferId(oldOfferContent.identifier))
                .build()

        if(!oldOffer.equals(finalizedOffer)){
            storeOffer(finalizedOffer)
        }
    }

    private void storeOffer(Offer finalizedOffer) {
        try {
            final offer = Converter.convertOfferToDTO(finalizedOffer)
            dataSource.store(offer)
            output.updatedOffer(offer)
        } catch (DatabaseQueryException e) {
            output.failNotification(e.message)
        } catch (Exception unexpected) {
            log.error unexpected.message
            log.error unexpected.stackTrace.join("\n")
            output.failNotification("An unexpected during the saving of your offer occurred. " +
                    "Please contact ${Constants.QBIC_HELPDESK_EMAIL}.")
        }
    }

}
