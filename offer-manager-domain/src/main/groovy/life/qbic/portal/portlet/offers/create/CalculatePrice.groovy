package life.qbic.portal.portlet.offers.create

import life.qbic.datamodel.dtos.business.Affiliation
import life.qbic.datamodel.dtos.business.AffiliationCategory
import life.qbic.datamodel.dtos.business.ProductItem

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
     * @deprecated Please use {@link #calculatePrice(List<ProductItem>, Affiliation)}
     */
    @Deprecated
    void calculatePrice(List<ProductItem> items, AffiliationCategory category)

    /**
     * This method calculates the price of a list of items based on an affiliation
     * @param items The items describe which product and what quantity is desired
     * @param affiliation The affiliation for which the prices are calculated
     */
    void calculatePrice(List<ProductItem> items, Affiliation affiliation)

}
