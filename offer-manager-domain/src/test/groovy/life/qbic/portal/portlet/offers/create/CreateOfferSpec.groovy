package life.qbic.portal.portlet.offers.create

import life.qbic.business.offers.create.CreateOffer
import life.qbic.business.offers.create.CreateOfferDataSource
import life.qbic.business.offers.create.CreateOfferOutput
import life.qbic.datamodel.dtos.business.Affiliation
import life.qbic.datamodel.dtos.business.AffiliationCategory
import life.qbic.datamodel.dtos.business.Customer
import life.qbic.datamodel.dtos.business.Offer
import life.qbic.datamodel.dtos.business.OfferId
import life.qbic.datamodel.dtos.business.ProductItem
import life.qbic.datamodel.dtos.business.ProjectManager
import life.qbic.datamodel.dtos.business.services.PrimaryAnalysis
import life.qbic.datamodel.dtos.business.services.ProductUnit
import life.qbic.datamodel.dtos.business.services.ProjectManagement
import life.qbic.datamodel.dtos.business.services.Sequencing
import spock.lang.Shared
import spock.lang.Specification

/**
 * <short description>
 *
 * <detailed description>
 *
 * @since: 1.0.0
 * @author: Tobias Koch
 */
class CreateOfferSpec extends Specification {
    @Shared
    Date date
    @Shared
    Customer customer
    @Shared
    ProjectManager projectManager
    @Shared
    Affiliation selectedAffiliation
    @Shared
    String projectTitle
    @Shared
    String projectDescription


    def setup(){
        date = new Date(1000, 10, 10)
        customer = new Customer.Builder("Max", "Mustermann", "").build()
        projectManager = new ProjectManager.Builder("Max", "Mustermann", "").build()
        selectedAffiliation = new Affiliation.Builder("Universität Tübingen",
                "Auf der Morgenstelle 10",
                "72076",
                "Tübingen")
                .build()
        projectTitle = "Archer"
        projectDescription = "Cartoon Series"
    }

    def "Creating an offer is successful"(){
        given:
        CreateOfferOutput output = Mock(CreateOfferOutput.class)
        CreateOfferDataSource ds = Stub(CreateOfferDataSource.class)
        CreateOffer createOffer = new CreateOffer(ds,output)

        and:
        List<ProductItem> items = [
                new ProductItem(2, new PrimaryAnalysis("Basic RNAsq", "Just an" +
                        " example", 1.0, ProductUnit.PER_SAMPLE, "1")),
                new ProductItem(1, new ProjectManagement("Basic Management",
                        "Just an example", 10.0, ProductUnit.PER_DATASET, "1"))
        ]

        Offer offer = new Offer.Builder(customer, projectManager, projectTitle, projectDescription, selectedAffiliation)
                .modificationDate(date).expirationDate(date).items([items[0]]).identifier(new OfferId("Conserved","abcd","1"))
                .build()

        when:
        createOffer.createOffer(offer)

        then:
        1* output.createdNewOffer(_)
    }

    def "calculate offer price correctly"(){
        given:
        CreateOfferOutput output = Mock(CreateOfferOutput)
        CreateOffer createOffer = new CreateOffer(Stub(CreateOfferDataSource),output)

        and:
        List<ProductItem> items = [new ProductItem(1,new Sequencing("This is a sequencing package", "a short description",1.4, ProductUnit.PER_SAMPLE, "1")),
                                   new ProductItem(1,new Sequencing("This is a sequencing package", "a short description",1.4, ProductUnit.PER_SAMPLE, "1"))]
        when:
        createOffer.calculatePrice(items, new Affiliation.Builder("Test", "", "", "").category
        (AffiliationCategory.INTERNAL).build())

        then:
        1 * output.calculatedPrice(2.8, 0, 0, 2.8)
    }
}
