package life.qbic.portal.portlet.offers.create

import life.qbic.datamodel.accounting.ProductItem
import life.qbic.datamodel.dtos.business.AffiliationCategory

/**
 * An interface handling the calculation of a price for an offer
 *
 * Calculates the price for an offer
 *
 * @since: 0.1.0
 *
 */
interface CalculatePrice {

    /**
     * This method calculates the price of a list of items based on an affiliation category
     * @param items The items describe which product and what quantity is desired
     * @param category The category defines which overhead should be applied
     */
    void calculatePrice(List<ProductItem> items, AffiliationCategory category)

}