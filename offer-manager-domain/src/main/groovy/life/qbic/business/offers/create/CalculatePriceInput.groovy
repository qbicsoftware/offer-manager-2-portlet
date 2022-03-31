package life.qbic.business.offers.create

import life.qbic.datamodel.dtos.business.Affiliation
import life.qbic.datamodel.dtos.business.ProductItem

/**
 * An interface handling the calculation of a price for an offer
 *
 * Calculates the price for an offer
 *
 * @since: 0.1.0
 *
 */
interface CalculatePriceInput {

    /**
     * This method calculates the price of a list of items based on an affiliation
     * @param items The items describe which product and what quantity is desired
     * @param affiliation The affiliation for which the prices are calculated
     */
    void calculatePrice(List<ProductItem> items, Affiliation affiliation)

}
