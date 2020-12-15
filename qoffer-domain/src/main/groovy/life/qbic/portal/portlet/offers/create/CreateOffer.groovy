package life.qbic.portal.portlet.offers.create

import life.qbic.datamodel.dtos.business.AffiliationCategory
import life.qbic.datamodel.dtos.business.Customer
import life.qbic.datamodel.dtos.business.Offer
import life.qbic.datamodel.dtos.business.OfferId
import life.qbic.datamodel.dtos.business.ProductItem

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
    void createOffer(Offer offerContent) {
        OfferId identifier = generateQuotationID(offerContent.customer)
        double offerPrice = PriceCalculator.calculateOfferPrice(offerContent.items,offerContent.selectedCustomerAffiliation.category)

        Offer finalizedOffer = new Offer.Builder(
                offerContent.customer,
                offerContent.projectManager,
                offerContent.projectTitle,
                offerContent.projectDescription,
                offerContent.selectedCustomerAffiliation)
                .items(offerContent.items)
                .identifier(identifier)
                .expirationDate(new Date(2030,12,24)) //todo how to determine this?
                .modificationDate(new Date())
                .totalPrice(offerPrice)
                .build()

        dataSource.store(finalizedOffer)
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
        double offerPrice = PriceCalculator.calculateOfferPrice(items,category)
        output.calculatedPrice(offerPrice)
    }
}
