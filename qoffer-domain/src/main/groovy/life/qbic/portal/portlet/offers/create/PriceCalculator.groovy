package life.qbic.portal.portlet.offers.create

import life.qbic.datamodel.dtos.business.AffiliationCategory
import life.qbic.datamodel.dtos.business.ProductItem

/**
 * Calculates the price of a list of ProductItems
 *
 * The price of a list of product items is the basis of the price for an offer (or a cost estimate).
 *
 * @since: 0.1.0
 *
 */

class PriceCalculator {

    /**
     * Method to calculate the price form the offer items
     * @param items
     * @return
     */
    static double calculateOfferPrice(List<ProductItem> items, AffiliationCategory affiliationCategory){
        //todo exchange with the correct calculation
        //1. sum up item price
        double offerPrice = 0
        items.each {item ->
            offerPrice += item.quantity * item.product.unitPrice
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
        //todo add the correct overheads
        switch (category){
            case AffiliationCategory.INTERNAL:
                return 1.0
            case AffiliationCategory.EXTERNAL_ACADEMIC:
                return 1.0
            case AffiliationCategory.EXTERNAL:
                return 1.0
        }
    }
}
