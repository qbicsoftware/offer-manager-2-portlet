package life.qbic.portal.offermanager.components.offer.update

import life.qbic.business.logging.Logger
import life.qbic.business.logging.Logging
import life.qbic.business.offers.create.CalculatePrice
import life.qbic.business.offers.update.UpdateOfferInput
import life.qbic.datamodel.dtos.business.Affiliation
import life.qbic.datamodel.dtos.business.Offer
import life.qbic.datamodel.dtos.business.ProductItem

/**
 * AppPresenter for the UpdateOffer
 *
 * This presenter handles the output of the UpdateOffer use case and prepares it for a view.
 *
 * @since: 1.0.0
 */
class UpdateOfferController {

    private final UpdateOfferInput input
    private final CalculatePrice calculatePrice
    private final Logging logger = Logger.getLogger(UpdateOfferController.class)

    UpdateOfferController(UpdateOfferInput input, CalculatePrice calculatePrice){
        this.input = input
        this.calculatePrice = calculatePrice
    }

    /**
     * This method updates an offer based on the information provided from the view
     *
     * @param offerId The current offer id, if available
     * @param projectTitle The title of the project
     * @param projectDescription The description of the project title
     * @param customer The customer for whom the offer is created
     * @param manager The project manager who is managing this project
     * @param items The product items listed on the offer
     * @param customerAffiliation The affiliation of the customer for this specific offer
     */
    void updateOffer(Offer oldOffer, Offer newOffer){
        //todo needs the method specification from domain pr
        this.input.updateExistingOffer(oldOffer, newOffer)
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
            logger.error(ignored.message)
            logger.error(ignored.stackTrace.join("\n"))
            throw new IllegalArgumentException("Could not calculate price from provided arguments.")
        }
    }
}
