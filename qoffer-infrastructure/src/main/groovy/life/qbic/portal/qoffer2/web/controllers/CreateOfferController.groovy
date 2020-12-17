package life.qbic.portal.qoffer2.web.controllers

import life.qbic.datamodel.dtos.business.Affiliation
import life.qbic.datamodel.dtos.business.AffiliationCategory
import life.qbic.datamodel.dtos.business.Customer
import life.qbic.datamodel.dtos.business.Offer
import life.qbic.datamodel.dtos.business.ProductItem
import life.qbic.datamodel.dtos.business.ProjectManager

import life.qbic.portal.portlet.offers.create.CalculatePrice
import life.qbic.portal.portlet.offers.create.CreateOfferInput

/**
 * Controller class adapter from view information into use case input interface
 *
 * This class translates the information that was received from the view into method calls to the use case
 *
 * @since: 0.1.0
 *
 */
class CreateOfferController {

    private final CreateOfferInput input
    private final CalculatePrice calculatePrice

    CreateOfferController(CreateOfferInput input,CalculatePrice calculatePrice){
        this.input = input
        this.calculatePrice = calculatePrice
    }

    /**
     * This method creates an offer based on the information provided from the view
     *
     * @param projectTitle The title of the project
     * @param projectDescription The description of the project title
     * @param customer The customer for whom the offer is created
     * @param manager The project manager who is managing this project
     * @param items The product items listed on the offer
     * @param totalPrice The total price for the offer
     * @param customerAffiliation The affiliation of the customer for this specific offer
     */
    void createOffer(String projectTitle, String projectDescription, Customer customer, ProjectManager manager, List<ProductItem> items, double totalPrice, Affiliation customerAffiliation){
        try {
            Offer offer = new Offer.Builder(customer,manager,projectDescription,projectTitle,items,customerAffiliation).totalPrice(totalPrice).build()
            this.input.createOffer(offer)
        } catch(Exception ignored) {
            throw new IllegalArgumentException("Could not create offer from provided arguments.")
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
            throw new IllegalArgumentException("Could not calculate price from provided arguments.")
        }
    }
}
