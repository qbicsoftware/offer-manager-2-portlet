package life.qbic.portal.portlet.offers.create

import life.qbic.datamodel.dtos.business.ProductItem
import life.qbic.datamodel.dtos.business.AffiliationCategory
import life.qbic.datamodel.dtos.business.Offer
import life.qbic.datamodel.dtos.business.OfferId
import life.qbic.datamodel.dtos.general.Person

/**
 * This class implements logic to create new offers.
 *
 * A PM has received a new project request and uses the offer manager to create a new offer for the customer.
 * Alternatively a new offer is created from an existing offer.
 * @since: 1.0.0
 * @author: Tobias Koch
 */
class CreateOffer implements CreateOfferInput{

    private CreateOfferDataSource dataSource
    private CreateOfferOutput output

    CreateOffer(CreateOfferDataSource dataSource, CreateOfferOutput output){
        this.dataSource = dataSource
        this.output = output
    }

    @Override
    void createOffer(Offer offerContent) {
        OfferId identifier = generateQuotationID(offerContent.customer)

        Offer finalizedOffer = new Offer.Builder(new Date(),
                offerContent.expirationDate,
                offerContent.customer,
                offerContent.projectManager,
                offerContent.projectDescription,
                offerContent.projectTitle,
                offerContent.items,
                calculateOfferPrice(offerContent.items,offerContent.selectedCustomerAffiliation.category),
                identifier,
                offerContent.selectedCustomerAffiliation)
                .build()

        dataSource.store(finalizedOffer)
    }

    /**
     * Method to generate the identifier of an offer with the project conserved part, the random part and the version
     * @param customer which is required for the project conserved part
     * @return
     */
    private static OfferId generateQuotationID(Person customer){
    //todo: do we want to have a person here? 
    //todo: update the datamodellib
        String projectConservedPart = customer.lastName.toLowerCase()
        String randomPart = "abcd"
        //TODO make random ID part random
        int version = 1

        return new OfferId(projectConservedPart,randomPart,version)
    }

    /**
     * Method to calculate the price form the offer items
     * @param items
     * @return
     */
    private static double calculateOfferPrice(List<ProductItem> items, AffiliationCategory affiliationCategory){
        //1. sum up item price
        double offerPrice = 0
        items.each {item ->
            offerPrice += item.computeTotalCosts()
        }
        //2. add overheads if applicable
        double overhead = getOverhead(affiliationCategory)
        offerPrice = offerPrice + offerPrice * overhead
        //2. VAT?
        //todo

        return offerPrice
    }

    /**
     * This method returns the overhead for a given {@link AffiliationCategory}
     * @param category determines the overhead type of a customer
     * @return
     */
    private static double getOverhead(AffiliationCategory category){
        switch (category){
            case AffiliationCategory.INTERNAL:
                return 1.0
            case AffiliationCategory.EXTERNAL_ACADEMIC:
                return 1.0
            case AffiliationCategory.EXTERNAL:
                return 1.0
            case AffiliationCategory.UNKNOWN:
                return 1.0
        }

    }
}
