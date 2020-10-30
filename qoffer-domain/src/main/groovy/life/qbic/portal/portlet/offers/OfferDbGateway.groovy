package life.qbic.portal.portlet.offers

import life.qbic.datamodel.accounting.ProductItem
import life.qbic.datamodel.dtos.business.Customer
import life.qbic.datamodel.dtos.business.QuotationId


/**
 * A gateway to access information from an offer database
 *
 * This class specifies how the application can access external resources.
 * It is meant to be implemented outside the domain layer.
 *
 * @since: 1.0.0
 * @author: Tobias Koch
 */
interface OfferDbGateway {

    def getOfferByID(QuotationId id)
    def createOffer(QuotationId id, String projectTitle, String projectDescription, Customer customer, Customer projectManager, List<ProductItem> productItems)

}