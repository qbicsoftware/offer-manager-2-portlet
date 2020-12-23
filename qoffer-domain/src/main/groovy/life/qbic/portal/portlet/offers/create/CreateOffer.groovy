package life.qbic.portal.portlet.offers.create

import life.qbic.datamodel.dtos.business.Affiliation
import life.qbic.datamodel.dtos.business.AffiliationCategory
import life.qbic.datamodel.dtos.business.Customer
import life.qbic.datamodel.dtos.business.OfferId
import life.qbic.datamodel.dtos.business.ProductItem
import life.qbic.datamodel.dtos.business.ProjectManager
import life.qbic.portal.portlet.offers.Offer

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

        dataSource.store(Converter.convertOfferToDTO(finalizedOffer))
    }

    /**
     * Method to generate the identifier of an offer with the project conserved part, the random part and the version
     * @param customer which is required for the project conserved part
     * @return
     */
    private static OfferId generateQuotationID(Customer customer){
    //todo: do we want to have a person here? 
    //todo: update the datamodellib
        String projectConservedPart = customer.lastName.toLowerCase()
        String randomPart = "abcd"
        //TODO make random ID part random
        int version = 1

        return new OfferId(projectConservedPart,randomPart,version)
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

    private static class Converter {
        static life.qbic.datamodel.dtos.business.Offer convertOfferToDTO(Offer offer) {
            new life.qbic.datamodel.dtos.business.Offer.Builder(
                    offer.customer,
                    offer.projectManager,
                    offer.projectTitle,
                    offer.projectDescription,
                    offer.selectedCustomerAffiliation)
                    .items(offer.getItems())
                    .netPrice(offer.getTotalNetPrice())
                    .taxes(offer.getTaxCosts())
                    .overheads(offer.getOverheadSum())
                    .totalPrice(offer.getTotalCosts())
                    .modificationDate(offer.modificationDate)
                    .expirationDate(offer.expirationDate)
                    .build()
        }

        static Offer buildOfferForCostCalculation(List<ProductItem> items,
                                                  Affiliation affiliation) {
            final def dummyCustomer = new Customer.Builder("Nobody", "Nobody",
                    "nobody@qbic.com").build()
            final def dummyProjectManager = new ProjectManager.Builder("Nobody", "Nobody",
                    "nobody@qbic.com").build()
            new Offer.Builder(
                    dummyCustomer,
                    dummyProjectManager,
                    "",
                    "",
                    items,
                    affiliation).build()
        }

    }
}
