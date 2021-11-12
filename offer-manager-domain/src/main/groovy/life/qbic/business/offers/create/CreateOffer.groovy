package life.qbic.business.offers.create

import life.qbic.business.Constants
import life.qbic.business.exceptions.DatabaseQueryException
import life.qbic.business.logging.Logger
import life.qbic.business.logging.Logging
import life.qbic.business.offers.Converter
import life.qbic.business.offers.identifier.OfferId
import life.qbic.business.offers.update.UpdateOffer
import life.qbic.business.offers.update.UpdateOfferOutput
import life.qbic.datamodel.dtos.business.*

/**
 * This class implements logic to create new offers.
 *
 * A PM has received a new project request and uses the offer manager to create a new offer for the customer.
 * Alternatively a new offer is created from an existing offer.
 * @since: 1.0.0
 * @author: Tobias Koch
 */
class CreateOffer implements CreateOfferInput, CalculatePrice, UpdateOfferOutput{

    private static final Logging log = Logger.getLogger(this.class)

    private CreateOfferDataSource dataSource
    private CreateOfferOutput output
    private UpdateOffer updateOffer

    CreateOffer(CreateOfferDataSource dataSource, CreateOfferOutput output){
        this.dataSource = dataSource
        this.output = output
        updateOffer = new UpdateOffer(dataSource,this)
    }

    @Override
    void createOffer(Offer offerContent) {

        if(offerContent.identifier){
            updateOffer.updateOffer(offerContent)
        }else{
            createNewOffer(offerContent)
        }
    }

    private void createNewOffer(Offer offerContent){
        OfferId newOfferId = generateTomatoId(offerContent.customer)

        life.qbic.business.offers.Offer finalizedOffer = new life.qbic.business.offers.Offer.Builder(
                offerContent.customer,
                offerContent.projectManager,
                offerContent.projectTitle,
                offerContent.projectDescription,
                offerContent.items,
                offerContent.selectedCustomerAffiliation)
                .experimentalDesign(offerContent.experimentalDesign)
                .identifier(newOfferId)
                .build()

        storeOffer(finalizedOffer)
    }

    private void storeOffer(life.qbic.business.offers.Offer finalizedOffer) {
        try {
            final offer = Converter.convertOfferToDTO(finalizedOffer)
            dataSource.store(offer)
            output.createdNewOffer(offer)
        } catch (DatabaseQueryException e) {
            output.failNotification(e.message)
        } catch (Exception unexpected) {
            log.error(unexpected.message)
            log.debug(unexpected.message, unexpected)
            output.failNotification("An unexpected during the saving of your offer occurred. " +
                    "Please contact ${Constants.QBIC_HELPDESK_EMAIL}.")
        }
    }

    /**
     * Method to generate the identifier of an offer with the project conserved part, the random part and the version
     * @param customer which is required for the project conserved part
     * @return
     */
    private static OfferId generateTomatoId(Customer customer){
        String projectConservedPart = customer.lastName.toLowerCase()
        int version = 1

        return new OfferId(projectConservedPart, version)
    }

    @Override
    void calculatePrice(List<ProductItem> items, Affiliation affiliation) {
        life.qbic.business.offers.Offer offer = buildOfferForCostCalculation(items, affiliation)
        output.calculatedPrice(
                offer.getTotalNetPrice(),
                offer.getTaxCosts(),
                offer.getOverheadSum(),
                offer.getTotalCosts(),
                offer.getTotalDiscountAmount())
    }

    /**
     * Builds an offer entity with a dummy customer and a dummy project manager
     * @param items the offer items to be used
     * @param affiliation the affiliation to be used
     * @return an offer object with the given items and affiliation
     */
    private static life.qbic.business.offers.Offer buildOfferForCostCalculation(List<ProductItem> items,
                                                                        Affiliation affiliation) {
        final def dummyCustomer = new Customer.Builder("Nobody", "Nobody",
                "nobody@qbic.com").build()
        final def dummyProjectManager = new ProjectManager.Builder("Nobody", "Nobody",
                "nobody@qbic.com").build()
        new life.qbic.business.offers.Offer.Builder(
                dummyCustomer,
                dummyProjectManager,
                "",
                "",
                items,
                affiliation).build()
    }

    @Override
    void updatedOffer(Offer createdOffer) {
        output.createdNewOffer(createdOffer)
    }

    @Override
    void failNotification(String notification) {
        output.failNotification(notification)
    }
}
