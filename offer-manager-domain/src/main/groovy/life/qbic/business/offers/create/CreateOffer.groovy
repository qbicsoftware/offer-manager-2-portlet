package life.qbic.business.offers.create

import life.qbic.business.offers.Converter
import life.qbic.business.offers.update.UpdateOffer
import life.qbic.business.offers.identifier.OfferId
import life.qbic.business.offers.identifier.ProjectPart
import life.qbic.business.offers.identifier.RandomPart
import life.qbic.business.offers.identifier.Version
import life.qbic.business.offers.update.UpdateOfferOutput
import life.qbic.datamodel.dtos.business.Affiliation
import life.qbic.datamodel.dtos.business.Customer
import life.qbic.datamodel.dtos.business.Offer
import life.qbic.datamodel.dtos.business.ProductItem
import life.qbic.business.Constants
import life.qbic.business.exceptions.DatabaseQueryException

/**
 * This class implements logic to create new offers.
 *
 * A PM has received a new project request and uses the offer manager to create a new offer for the customer.
 * Alternatively a new offer is created from an existing offer.
 * @since: 1.0.0
 * @author: Tobias Koch
 */
class CreateOffer implements CreateOfferInput, CalculatePrice, UpdateOfferOutput{

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
        OfferId newOfferId = generateQuotationID(offerContent.customer)

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

        println offerContent.experimentalDesign

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
            //TODO use logger facade instead of println
            println unexpected.message
            println unexpected.stackTrace.join("\n")
            output.failNotification("An unexpected during the saving of your offer occurred. " +
                    "Please contact ${Constants.QBIC_HELPDESK_EMAIL}.")
        }
    }

    /**
     * Method to generate the identifier of an offer with the project conserved part, the random part and the version
     * @param customer which is required for the project conserved part
     * @return
     */
    private static OfferId generateQuotationID(Customer customer){
        RandomPart randomPart = new RandomPart()
        ProjectPart projectConservedPart = new ProjectPart(customer.lastName.toLowerCase())
        Version version = new Version(1)

        return new OfferId(randomPart, projectConservedPart, version)
    }

    @Override
    void calculatePrice(List<ProductItem> items, Affiliation affiliation) {
        life.qbic.business.offers.Offer offer = Converter.buildOfferForCostCalculation(items, affiliation)
        output.calculatedPrice(
                offer.getTotalNetPrice(),
                offer.getTaxCosts(),
                offer.getOverheadSum(),
                offer.getTotalCosts())
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
