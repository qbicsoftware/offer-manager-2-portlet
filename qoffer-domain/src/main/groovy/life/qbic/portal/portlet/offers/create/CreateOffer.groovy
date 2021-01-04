package life.qbic.portal.portlet.offers.create

import life.qbic.datamodel.dtos.business.Affiliation
import life.qbic.datamodel.dtos.business.AffiliationCategory
import life.qbic.datamodel.dtos.business.Customer
import life.qbic.datamodel.dtos.business.ProductItem
import life.qbic.portal.portlet.Constants
import life.qbic.portal.portlet.exceptions.DatabaseQueryException
import life.qbic.portal.portlet.offers.Converter
import life.qbic.portal.portlet.offers.Offer
import life.qbic.portal.portlet.offers.identifier.OfferId
import life.qbic.portal.portlet.offers.identifier.ProjectPart
import life.qbic.portal.portlet.offers.identifier.RandomPart
import life.qbic.portal.portlet.offers.identifier.Version

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
        OfferId identifier = generateQuotationID(offerContent.customer)

        Offer finalizedOffer = new Offer.Builder(
                offerContent.customer,
                offerContent.projectManager,
                offerContent.projectTitle,
                offerContent.projectDescription,
                offerContent.items,
                offerContent.selectedCustomerAffiliation)
                .identifier(identifier)
                .expirationDate(new Date(2030,12,24)) //todo how to determine this?
                .modificationDate(new Date())
                .build()

        try {
            final offer = Converter.convertOfferToDTO(finalizedOffer)
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
    private static OfferId generateQuotationID(Customer customer){
        //todo: do we want to have a person here?
        //todo: update the datamodellib
        def randomPart = new RandomPart()
        def projectConservedPart = new ProjectPart(customer.lastName.toLowerCase())
        def version = new Version(1)
        //TODO make random ID part random

        return new OfferId(randomPart, projectConservedPart, version)
    }

    @Override
    void calculatePrice(List<ProductItem> items, AffiliationCategory category) {
        throw new RuntimeException("Method not implemented.")
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
