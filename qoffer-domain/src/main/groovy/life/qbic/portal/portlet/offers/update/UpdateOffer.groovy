package life.qbic.portal.portlet.offers.update

import life.qbic.portal.portlet.Constants
import life.qbic.portal.portlet.exceptions.DatabaseQueryException
import life.qbic.portal.portlet.offers.Converter
import life.qbic.portal.portlet.offers.Offer
import life.qbic.portal.portlet.offers.create.CreateOfferDataSource
import life.qbic.portal.portlet.offers.identifier.OfferId

/**
 * Update offer use case.
 *
 * During the offer preparation, the customer might request changes for the offer items (number of
 * samples, change in the technology used for analysis, etc.).
 * The offer manager provides an interface to update an existing offer and create a new version
 * from it.
 *
 * In the process of updating an existing offer, the updated one has the same offer identifier
 * as the original one, with the one exception that the version modifier has been increased.
 *
 * @since: 1.0.0
 * @author: Tobias Koch
 */
class UpdateOffer implements UpdateOfferInput {

    final private UpdateOfferOutput output

    final private CreateOfferDataSource dataSource

    private Offer updatedOffer

    UpdateOffer(UpdateOfferOutput output, CreateOfferDataSource dataSource) {
        this.output = output
        this.dataSource = dataSource
    }

    /**
     * {@inheritDoc}
     */
    @Override
    void update(life.qbic.datamodel.dtos.business.Offer updatedContent) {
        createOffer(updatedContent)
        try {
            applyBusinessRules()
            storeOffer()
        } catch (DatabaseQueryException ignored){
            output.failNotification("Could not update offer with ID ${updatedContent.identifier}.")
        } catch (Exception e) {
            println e.stackTrace.join("\n")
            output.failNotification("An unexpected error occured. Please contact ${Constants.QBIC_HELPDESK_EMAIL}")
        }
        output.onOfferUpdated(Converter.convertOfferToDTO(updatedOffer))
    }

    private void storeOffer() {
        dataSource.store(Converter.convertOfferToDTO(updatedOffer))
    }

    private void applyBusinessRules() {
        updatedOffer.increaseVersion()
    }

    private void createOffer(life.qbic.datamodel.dtos.business.Offer offerContent) {
        OfferId offerId = Converter.buildOfferId(offerContent.identifier)
        updatedOffer = new Offer.Builder(
                offerContent.customer,
                offerContent.projectManager,
                offerContent.projectTitle,
                offerContent.projectDescription,
                offerContent.items,
                offerContent.selectedCustomerAffiliation)
                .identifier(offerId)
                .expirationDate(offerContent.expirationDate)
                .modificationDate(offerContent.modificationDate)
                .build()
    }
}
