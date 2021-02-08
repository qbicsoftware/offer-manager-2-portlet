package life.qbic.portal.offermanager.components.offer.create

import groovy.util.logging.Log4j2
import life.qbic.datamodel.dtos.business.Affiliation
import life.qbic.datamodel.dtos.business.Customer
import life.qbic.datamodel.dtos.business.Offer
import life.qbic.datamodel.dtos.business.OfferId
import life.qbic.datamodel.dtos.business.ProductItem
import life.qbic.datamodel.dtos.business.ProjectManager

import life.qbic.business.offers.create.CalculatePrice
import life.qbic.business.offers.create.CreateOfferInput

/**
 * Controller class adapter from view information into use case input interface
 *
 * This class translates the information that was received from the view into method calls to the use case
 *
 * @since: 0.1.0
 *
 */
@Log4j2
class CreateOfferController {

    private final CreateOfferInput input
    private final CalculatePrice calculatePrice

    CreateOfferController(CreateOfferInput input, CalculatePrice calculatePrice){
        this.input = input
        this.calculatePrice = calculatePrice
    }

    /**
     * This method creates an offer based on the information provided from the view
     *
     * @param offerId The current offer id, if available
     * @param projectTitle The title of the project
     * @param projectDescription The description of the project title
     * @param customer The customer for whom the offer is created
     * @param manager The project manager who is managing this project
     * @param items The product items listed on the offer
     * @param customerAffiliation The affiliation of the customer for this specific offer
     */
    void createOffer(
            OfferId offerId,
            String projectTitle,
            String projectDescription,
            Customer customer,
            ProjectManager manager,
            List<ProductItem> items,
            Affiliation customerAffiliation){

        Offer offer = new Offer.Builder(
                    customer,
                    manager,
                    projectTitle,
                    projectDescription,
                    customerAffiliation)
                    .items(items)
                    .identifier(offerId)
                    .build()
        //if the offer id is exists no identifier for the offer then it is a new offer that needs to be created
        if(offerId == null){
            this.input.createOffer(offer)
        }else{
            this.input.createOffer(offer)
        }
    }

    /**
     * Method to trigger the calculation of the price based on a list of items and a category
     * @param items A list of product items with a quantity and product
     * @param category defining the category of the affiliation
     */
    void calculatePriceForItems(List<ProductItem> items, Affiliation affiliation){
        try {
            this.calculatePrice.calculatePrice(items, affiliation)
        } catch(Exception ignored) {
            log.error(ignored.message)
            log.error(ignored.stackTrace.join("\n"))
            throw new IllegalArgumentException("Could not calculate price from provided arguments.")
        }
    }
}
