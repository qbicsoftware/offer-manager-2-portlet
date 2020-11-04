package life.qbic.portal.portlet.offers.create

import life.qbic.datamodel.accounting.ProductItem
import life.qbic.datamodel.accounting.Quotation
import life.qbic.datamodel.dtos.business.AffiliationCategory
import life.qbic.datamodel.dtos.business.QuotationId
import life.qbic.datamodel.dtos.general.Person

import java.text.DateFormat
import java.text.SimpleDateFormat

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
    void createOffer(String tomatoId) {

    }

    @Override
    void createNewOffer(String projectTitle, String projectDescription, Person customer, Person projectManager, List<ProductItem> productItems) {
        QuotationId identifier = generateQuotationID(customer)

        Quotation quotation = new Quotation(new Date(),null,customer,projectManager,projectTitle,projectDescription,productItems,calculateOfferPrice(productItems),identifier)
        dataSource.saveOffer(quotation)
    }

    /**
     * Method to generate the identifier of an offer with the project conserved part, the random part and the version
     * @param customer which is required for the project conserved part
     * @return
     */
    private static QuotationId generateQuotationID(Person customer){
    //todo: do we want to have a person here? 
    //todo: update the datamodellib
        String projectConservedPart = customer.lastName.toLowerCase()
        String randomPart = "abcd"
        int version = 1

        return new QuotationId(projectConservedPart,randomPart,version)
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
        //2. add discount if available
        double discount = getDiscount(affiliationCategory)
        offerPrice *= discount
        //2. VAT?
        //todo

        return offerPrice
    }

    /**
     * This method returns the discount for a given {@link AffiliationCategory}
     * @param category determines the discount type of a customer
     * @return
     */
    private static double getDiscount(AffiliationCategory category){
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
