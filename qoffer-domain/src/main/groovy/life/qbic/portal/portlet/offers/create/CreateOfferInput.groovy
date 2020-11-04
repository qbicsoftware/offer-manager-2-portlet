package life.qbic.portal.portlet.offers.create

import life.qbic.datamodel.accounting.ProductItem
import life.qbic.datamodel.accounting.Quotation
import life.qbic.datamodel.dtos.business.Customer
import life.qbic.datamodel.dtos.business.QuotationId
import life.qbic.datamodel.dtos.general.Person


/**
 * Input interface for the {@link life.qbic.portal.portlet.offers.create.CreateOffer} use case
 *
 * @since: 1.0.0
 * @author: Tobias Koch
 */
interface CreateOfferInput {


    /**
     * Creates a new quotation based on the given TOMATOID
     * @param tomatoId makes it possible to identify the quotation
     * @since 1.0.0
     */
    void createOffer(String tomatoId)

    /**
     * Method to create a new offer for the provided offer information
     * @param projectTitle describing the title of the project
     * @param projectDescription a description of the project
     * @param customer as the customer of the project
     * @param projectManager as the manager of the project
     * @param productItems which are requested by the customer
     */
    void createNewOffer(String projectTitle, String projectDescription, Person customer, Person projectManager, List<ProductItem> productItems)
}