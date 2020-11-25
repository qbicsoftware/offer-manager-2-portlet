package life.qbic.portal.qoffer2.offers

import life.qbic.datamodel.dtos.business.ProductItem

import java.sql.Connection

/**
 * An interface to allow to retrieve information from the Product Database
 *
 * Allows to interact with the product database through predefined methods
 *
 * @since: 0.1.0
 *
 */
interface OfferToProductGateway {

    /**
     * Returns the ids of the items in a list
     *
     * @param connection A connection on which the database statement is executed
     * @param items A list of items for which the id needs to be retrieved
     * @param offerId the Id of the respective offer
     */
    createOfferItems(List<ProductItem> items, int offerId)

}