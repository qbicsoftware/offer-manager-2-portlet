package life.qbic.portal.portlet.offers.update

import life.qbic.portal.portlet.Constants
import life.qbic.portal.portlet.exceptions.DatabaseQueryException
import life.qbic.portal.portlet.offers.Converter
import life.qbic.portal.portlet.offers.Offer
import life.qbic.portal.portlet.offers.identifier.OfferId

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

    UpdateOffer(UpdateOfferDataSource dataSource, UpdateOfferOutput output) {
        this.dataSource = dataSource
        this.output = output
    }

    @Override
    void updateExistingOffer(life.qbic.datamodel.dtos.business.Offer offerContent) {
        //TODO implement
        throw new RuntimeException("Method not implemented.")
        /*OfferId identifier = Converter.buildOfferId(offerContent.identifier)
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

        storeOffer(finalizedOffer)*/
    }

    private void storeOffer(Offer finalizedOffer) {
        try {
            final offer = Converter.convertOfferToDTO(finalizedOffer)
            dataSource.store(offer)
            output.updatedOffer(offer)
        } catch (DatabaseQueryException e) {
            output.failNotification(e.message)
        } catch (Exception unexpected) {
            //TODO use logger facade instead of println
            println unexpected.message
            println unexpected.stackTrace.join("\n")
            output.failNotification("An unexpected during the saving of your offer occurred. " +
                    "Please contact ${Constants.QBIC_HELPDESK_EMAIL}.")
        }
    }
}
