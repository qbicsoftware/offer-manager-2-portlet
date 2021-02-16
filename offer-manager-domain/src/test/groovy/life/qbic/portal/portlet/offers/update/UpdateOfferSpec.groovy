package life.qbic.portal.portlet.offers.update

import life.qbic.business.offers.create.CreateOfferDataSource
import life.qbic.business.offers.create.CreateOfferOutput
import life.qbic.business.offers.update.UpdateOffer

import life.qbic.datamodel.dtos.business.Affiliation
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
class UpdateOfferSpec extends Specification {
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

    def "Updating an offer is successful"(){
        given:
        CreateOfferOutput output = Mock(CreateOfferOutput.class)
        CreateOfferDataSource ds = Stub(CreateOfferDataSource.class)
        UpdateOffer updateOffer = new UpdateOffer(ds,output)


        and:
        List<ProductItem> items = [
                new ProductItem(2, new PrimaryAnalysis("Basic RNAsq", "Just an" +
                        " example", 1.0, ProductUnit.PER_SAMPLE, "1")),
                new ProductItem(1, new ProjectManagement("Basic Management",
                        "Just an example", 10.0, ProductUnit.PER_DATASET, "1"))
        ]

        OfferId oldOfferId = new OfferId("Conserved","abcd","2")

        Offer newOffer = new Offer.Builder(customer, projectManager, projectTitle, projectDescription, selectedAffiliation)
                .modificationDate(date).expirationDate(date).items(items).identifier(oldOfferId)
                .build()

        and: "Db returns that there is already one version of the offer"
        ds.fetchAllVersionsForOfferId(_ as OfferId) >> [new OfferId("Conserved","abcd","1")]
        ds.getOffer(_ as OfferId) >> Optional.of(new Offer.Builder(customer, projectManager, projectTitle, projectDescription, selectedAffiliation)
                .modificationDate(date).expirationDate(date).items([items[0]]).identifier(oldOfferId)
                .build())

        when:
        updateOffer.updateOffer(newOffer)

        then:
        1* output.createdNewOffer(_)
    }

    def "Unchanged offer does not lead to a database entry"(){
        given:
        CreateOfferOutput output = Mock(CreateOfferOutput.class)
        CreateOfferDataSource ds = Stub(CreateOfferDataSource.class)
        UpdateOffer updateOffer = new UpdateOffer(ds,output)

        and:
        List<ProductItem> items = [
                new ProductItem(2, new PrimaryAnalysis("Basic RNAsq", "Just an" +
                        " example", 1.0, ProductUnit.PER_SAMPLE, "1")),
                new ProductItem(1, new ProjectManagement("Basic Management",
                        "Just an example", 10.0, ProductUnit.PER_DATASET, "1"))
        ]

        Offer oldOffer = new Offer.Builder(customer, projectManager, projectTitle, projectDescription, selectedAffiliation)
                .modificationDate(date).expirationDate(date).items([items[0]]).identifier(new OfferId("Conserved","abcd","1"))
                .build()
        OfferId oldOfferId = new OfferId("Conserved","abcd","2")

        and: "Db returns that there is already one version of the offer"
        ds.fetchAllVersionsForOfferId(_ as OfferId) >> [new OfferId("Conserved","abcd","1")]
        ds.getOffer(_ as OfferId) >> Optional.of(new Offer.Builder(customer, projectManager, projectTitle, projectDescription, selectedAffiliation)
                .modificationDate(date).expirationDate(date).items([items[0]]).identifier(oldOfferId)
                .build())

        when:
        updateOffer.updateOffer(oldOffer)

        then:
        0* output.createdNewOffer(_)
        1* output.failNotification("An unchanged offer cannot be updated")
    }

    def "Increase version the latest version of an offer"(){
        given:
        CreateOfferOutput output = Mock(CreateOfferOutput)
        CreateOfferDataSource ds = Mock(CreateOfferDataSource)
        UpdateOffer updateOffer = new UpdateOffer(ds,output)

        and: "given offers and items"
        List<ProductItem> items = [new ProductItem(1,new Sequencing("This is a sequencing package", "a short description",1.4, ProductUnit.PER_SAMPLE, "1")),
                                   new ProductItem(1,new Sequencing("This is a sequencing package", "a short description",1.4, ProductUnit.PER_SAMPLE, "1"))]

        OfferId offer1 = new OfferId("Conserved","abcd","1")
        OfferId offer2 = new OfferId("Conserved","abcd","2")
        OfferId offer3 = new OfferId("Conserved","abcd","3")

        OfferId oldOffer = new OfferId("Conserved","abcd","2")

        Offer newOffer = new Offer.Builder(customer, projectManager, projectTitle, projectDescription, selectedAffiliation)
                .modificationDate(date).expirationDate(date).items(items).identifier(oldOffer)
                .build()

        and: "Db returns that there are already 3 versions of the offer"
        ds.fetchAllVersionsForOfferId(_ as OfferId) >> [offer3,offer1,offer2]
        ds.getOffer(_ as OfferId) >> Optional.of(new Offer.Builder(customer, projectManager, projectTitle, projectDescription, selectedAffiliation)
                .modificationDate(date).expirationDate(date).items([items[0]]).identifier(oldOffer)
                .build())

        when:
        updateOffer.updateOffer(newOffer)

        then:
        1* output.createdNewOffer(_) >> {arguments ->
            final Offer offer = arguments.get(0)
            assert offer.identifier.version.toString() == "4"
        }


    }
}
