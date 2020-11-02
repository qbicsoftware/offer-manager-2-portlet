package life.qbic.portal.portlet.offers.create

import life.qbic.datamodel.accounting.ProductItem
import life.qbic.datamodel.accounting.Quotation
import life.qbic.datamodel.dtos.business.Customer


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
     *
     * @param projectTitle
     * @param projectDescription
     * @param customer
     * @param projectManager
     * @param productItems
     * @since 1.0.0
     */
    void createNewOffer(Quotation quotation)
}