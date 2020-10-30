package life.qbic.portal.portlet.offers.create

import life.qbic.datamodel.accounting.ProductItem
import life.qbic.datamodel.dtos.business.Customer
import life.qbic.datamodel.dtos.business.QuotationId
import life.qbic.portal.portlet.offers.OfferDbGateway

/**
 * This class implements logic to create new offers.
 *
 * A PM has received a new project request and uses the offer manager to create a new offer for the customer.
 * Alternatively a new offer is created from an existing offer.
 * @since: 1.0.0
 * @author: Tobias Koch
 */
class CreateOffer implements CreateOfferInput{

    private OfferDbGateway gateway
    private CreateOfferOutput output

    CreateOffer(OfferDbGateway gateway, CreateOfferOutput output){
        this.gateway = gateway
        this.output = output
    }

    @Override
    void createOffer(String tomatoId) {

    }

    @Override
    void createNewOffer(String projectTitle, String projectDescription, Customer customer, Customer projectManager, List<ProductItem> productItems) {
        //create id

        //create offer in database
        gateway.createOffer(new QuotationId("conserved","random",1),projectTitle,projectDescription,customer,projectManager,productItems)
    }
}
