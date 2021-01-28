package life.qbic.business.offers.create


import life.qbic.datamodel.dtos.business.Affiliation
import life.qbic.datamodel.dtos.business.AffiliationCategory
import life.qbic.datamodel.dtos.business.Customer
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
class CreateOffer implements CreateOfferInput, CalculatePrice{

    private CreateOfferDataSource dataSource
    private CreateOfferOutput output

    CreateOffer(CreateOfferDataSource dataSource, CreateOfferOutput output){
        this.dataSource = dataSource
        this.output = output
    }

    @Override
    void createOffer(life.qbic.datamodel.dtos.business.Offer offerContent) {
        /*
        If no identifier is provided, we create a new identifier. Otherwise
        we assume, that the customer updated an existing offer.
         */
        if (offerContent.identifier) {
            updateExistingOffer(offerContent)
        } else {
            createNewOffer(offerContent)
        }
    }

    private void updateExistingOffer(life.qbic.datamodel.dtos.business.Offer offerContent) {
        life.qbic.business.offers.identifier.OfferId identifier = life.qbic.business.offers.Converter.buildOfferId(offerContent.identifier)
        identifier.increaseVersion()

        life.qbic.business.offers.Offer finalizedOffer = new life.qbic.business.offers.Offer.Builder(
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

    private void createNewOffer(life.qbic.datamodel.dtos.business.Offer offerContent) {
        life.qbic.business.offers.identifier.OfferId newOfferId = generateQuotationID(offerContent.customer)

        life.qbic.business.offers.Offer finalizedOffer = new life.qbic.business.offers.Offer.Builder(
                offerContent.customer,
                offerContent.projectManager,
                offerContent.projectTitle,
                offerContent.projectDescription,
                offerContent.items,
                offerContent.selectedCustomerAffiliation)
                .identifier(newOfferId)
                .build()

        storeOffer(finalizedOffer)
    }

    private void storeOffer(life.qbic.business.offers.Offer finalizedOffer) {
        try {
            final offer = life.qbic.business.offers.Converter.convertOfferToDTO(finalizedOffer)
            dataSource.store(offer)
            output.createdNewOffer(offer)
        } catch (DatabaseQueryException e) {
            output.failNotification(e.message)
        } catch (Exception ignored) {
            println ignored.message
            println ignored.stackTrace.join("\n")
            output.failNotification("An unexpected during the saving of your offer occurred. " +
                    "Please contact ${Constants.QBIC_HELPDESK_EMAIL}.")
        }
    }

    /**
     * Method to generate the identifier of an offer with the project conserved part, the random part and the version
     * @param customer which is required for the project conserved part
     * @return
     */
    private static life.qbic.business.offers.identifier.OfferId generateQuotationID(Customer customer){
        life.qbic.business.offers.identifier.RandomPart randomPart = new life.qbic.business.offers.identifier.RandomPart()
        life.qbic.business.offers.identifier.ProjectPart projectConservedPart = new life.qbic.business.offers.identifier.ProjectPart(customer.lastName.toLowerCase())
        life.qbic.business.offers.identifier.Version version = new life.qbic.business.offers.identifier.Version(1)

        return new life.qbic.business.offers.identifier.OfferId(randomPart, projectConservedPart, version)
    }

    @Override
    void calculatePrice(List<ProductItem> items, AffiliationCategory category) {
        throw new RuntimeException("Method not implemented.")
    }

    @Override
    void calculatePrice(List<ProductItem> items, Affiliation affiliation) {
        life.qbic.business.offers.Offer offer = life.qbic.business.offers.Converter.buildOfferForCostCalculation(items, affiliation)
        output.calculatedPrice(
                offer.getTotalNetPrice(),
                offer.getTaxCosts(),
                offer.getOverheadSum(),
                offer.getTotalCosts())
    }
}
