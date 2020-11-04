package life.qbic.portal.portlet.offers.create

import life.qbic.datamodel.accounting.ProductItem
import life.qbic.datamodel.accounting.Quotation
import life.qbic.datamodel.dtos.business.Customer
import life.qbic.datamodel.dtos.business.QuotationId
import life.qbic.datamodel.dtos.general.Person


/**
 * A gateway to access information from an offer database
 *
 * This class specifies how the application can access external resources.
 * It is meant to be implemented outside the domain layer.
 *
 * @since: 1.0.0
 * @author: Tobias Koch
 */
interface CreateOfferDataSource {

    /**
     * This method retrieves a Quotation by an ID
     * @param id of the quotation
     * @return Quotation for the given ID
     * @since 1.0.0
     */
    Quotation getOfferByID(QuotationId id)

    /**
     * This method creates a quotation for the given quotation information
     * @param id of the quotation
     * @param projectTitle of the quotation
     * @param projectDescription of the quotation
     * @param customer of the quotation
     * @param projectManager of the quotation
     * @param productItems of the quotation
     * @since 1.0.0
     */
    void createOffer(QuotationId id, String projectTitle, String projectDescription, Person customer, Person projectManager, List<ProductItem> productItems)

}