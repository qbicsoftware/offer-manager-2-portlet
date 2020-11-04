package life.qbic.portal.portlet.offers.create


import life.qbic.datamodel.accounting.Quotation
import life.qbic.datamodel.dtos.business.QuotationId
import life.qbic.datamodel.persons.Person

/**
 * This class implements logic to create new offers.
 *
 * A PM has received a new project request and uses the offer manager to create a new offer for the customer.
 * Alternatively a new offer is created from an existing offer.
 * @since: 1.0.0
 * @author: Tobias Koch
 */
class CreateOffer implements CreateOfferInput{

    private CreateOfferDataSource gateway
    private CreateOfferOutput output

    CreateOffer(CreateOfferDataSource gateway, CreateOfferOutput output){
        this.gateway = gateway
        this.output = output
    }

    @Override
    void createOffer(String tomatoId) {

    }

    @Override
    void createNewOffer(Quotation quotation) {
        gateway.createOffer(quotation.identifier,quotation.projectTitle,quotation.projectDescription, quotation.customer,quotation.projectManager,quotation.items)
    }

    //todo move this method to the place where the quotation is generated
    private static QuotationId generateQuotationID(Person customer){//do we want to have a person here? todo update the datamodellib
        String projectConservedPart = customer.lastName.toLowerCase()
        String randomPart = "abcd"
        int version = 1

        return new QuotationId(projectConservedPart,randomPart,version)
    }
}
