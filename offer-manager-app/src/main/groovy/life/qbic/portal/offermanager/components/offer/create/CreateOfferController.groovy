package life.qbic.portal.offermanager.components.offer.create

import groovy.util.logging.Log4j2
import life.qbic.business.RefactorConverter
import life.qbic.business.offers.create.CreateOfferInput
import life.qbic.business.offers.fetch.FetchOfferInput
import life.qbic.datamodel.dtos.business.*

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
    private final FetchOfferInput fetchOfferInput

    private static final RefactorConverter refactorConverter = new RefactorConverter()

    CreateOfferController(CreateOfferInput input, FetchOfferInput fetchOfferInput) {
        this.input = input
        this.fetchOfferInput = fetchOfferInput
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
            Affiliation customerAffiliation,
            String experimentalDesign) {

        Offer.Builder offerBuilder = new Offer.Builder(
                customer,
                manager,
                projectTitle,
                projectDescription,
                customerAffiliation)
                .items(items)
                .identifier(offerId)
        if (experimentalDesign) {
            offerBuilder.experimentalDesign(experimentalDesign)
        }
        Offer offer = offerBuilder.build()

        this.input.createOffer(refactorConverter.toOffer(offer))
    }

    /**
     * Method to trigger the calculation of the price based on a list of items and a category
     * @param items A list of product items with a quantity and product
     * @param category defining the category of the affiliation
     */
    void calculatePriceForItems(List<ProductItem> items, Affiliation affiliation) {
        try {
            this.calculatePrice.calculatePrice(items, affiliation)
        } catch (Exception ignored) {
            log.error(ignored.message)
            log.error(ignored.stackTrace.join("\n"))
            throw new IllegalArgumentException("Could not calculate price from provided arguments.")
        }
    }
    /**
     * Triggers the FetchOffer use case on execution
     *
     * @param offerId The identifier of the offer to be fetched
     */
    void fetchOffer(OfferId offerId) {
        this.fetchOfferInput.fetchOffer(refactorConverter.toOfferId(offerId))
    }

}
