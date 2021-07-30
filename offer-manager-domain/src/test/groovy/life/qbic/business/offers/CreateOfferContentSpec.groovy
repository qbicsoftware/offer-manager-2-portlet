package life.qbic.business.offers

import life.qbic.business.offers.content.CreateOfferContent
import life.qbic.business.offers.content.CreateOfferContentOutput
import life.qbic.business.offers.fetch.FetchOffer
import life.qbic.business.offers.fetch.FetchOfferDataSource
import life.qbic.business.offers.fetch.FetchOfferInput
import life.qbic.business.offers.fetch.FetchOfferOutput
import life.qbic.datamodel.dtos.business.Affiliation
import life.qbic.datamodel.dtos.business.AffiliationCategory
import life.qbic.datamodel.dtos.business.Customer
import life.qbic.datamodel.dtos.business.OfferId
import life.qbic.datamodel.dtos.business.ProductItem
import life.qbic.datamodel.dtos.business.ProjectManager
import life.qbic.datamodel.dtos.business.facilities.Facility
import life.qbic.datamodel.dtos.business.services.PrimaryAnalysis
import life.qbic.datamodel.dtos.business.services.ProductUnit
import life.qbic.datamodel.dtos.business.services.ProjectManagement
import spock.lang.Shared
import spock.lang.Specification

/**
 * <h1>Tests the CreateOfferContent use case</h1>
 *
 * @since 1.1.0
 *
*/
class CreateOfferContentSpec extends Specification{

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
    @Shared
    List<ProductItem> items
    @Shared
    OfferId offerId

    def setup() {
        date = new Date(1000, 10, 10)
        customer = new Customer.Builder("Max", "Mustermann", "").build()
        projectManager = new ProjectManager.Builder("Max", "Mustermann", "").build()
        selectedAffiliation = new Affiliation.Builder("Universität Tübingen",
                "Auf der Morgenstelle 10",
                "72076",
                "Tübingen")
                .category(AffiliationCategory.EXTERNAL)
                .build()
        projectTitle = "Archer"
        projectDescription = "Cartoon Series"
        items = [
                new ProductItem(2, new PrimaryAnalysis("Basic RNAsq", "Just an" +
                        " example", 1.0, 1.0, ProductUnit.PER_SAMPLE, 1, Facility.QBIC)),
                new ProductItem(1, new ProjectManagement("Basic Management",
                        "Just an example", 10.0, 10.0, ProductUnit.PER_DATASET, 1, Facility.QBIC))
        ]
        offerId = new OfferId("Conserved", "abcd", "2")
    }

    def "Overhead costs for product groups are correctly set"(){
        given: "the create offer content use case"
        FetchOfferDataSource ds = Stub(FetchOfferDataSource.class)

        CreateOfferContentOutput output = Stub()
        CreateOfferContent createOfferContent = new CreateOfferContent(output,ds)

        and: "a mocked offer, that is returned upon the FetchOffer use case"
        ds.getOffer(_ as OfferId) >> Optional.of(new life.qbic.datamodel.dtos.business.Offer.Builder(customer, projectManager, projectTitle, projectDescription, selectedAffiliation)
                .modificationDate(date).expirationDate(date).items(items).identifier(offerId)
                .build())

        when: "the offer content creation is triggered"
        createOfferContent.createOfferContent(offerId)

        then: "the overheads for the product groups are set correctly"
        output.createdOfferContent(_ as OfferContent) >> { arguments ->
            final OfferContent offerContent = arguments.get(0)
            assert offerContent.overheadsDataAnalysis  == (2 * 1 * 0.4) as double
            assert offerContent.overheadsDataGeneration == 0
            assert offerContent.overheadsProjectManagementAndDataStorage == (1 * 10 * 0.4) as double
        }
    }

    def "Net costs for product groups are correctly set"(){
        given: "the create offer content use case"
        FetchOfferDataSource ds = Stub(FetchOfferDataSource.class)

        CreateOfferContentOutput output = Stub()
        CreateOfferContent createOfferContent = new CreateOfferContent(output,ds)

        and: "a mocked offer, that is returned upon the FetchOffer use case"
        ds.getOffer(_ as OfferId) >> Optional.of(new life.qbic.datamodel.dtos.business.Offer.Builder(customer, projectManager, projectTitle, projectDescription, selectedAffiliation)
                .modificationDate(date).expirationDate(date).items(items).identifier(offerId)
                .build())

        when: "the offer content creation is triggered"
        createOfferContent.createOfferContent(offerId)

        then: "the overheads for the product groups are set correctly"
        output.createdOfferContent(_ as OfferContent) >> { arguments ->
            final OfferContent offerContent = arguments.get(0)
            assert offerContent.netDataAnalysis  == (2 * 1) as double
            assert offerContent.netDataGeneration == 0
            assert offerContent.netPMandDS == (1 * 10) as double
        }
    }

    def "Unknown OfferId triggers the failedNotification() method"(){
        given: "the create offer content use case"
        FetchOfferDataSource ds = Stub(FetchOfferDataSource.class)

        CreateOfferContentOutput output = Mock()
        CreateOfferContent createOfferContent = new CreateOfferContent(output,ds)

        and: "no offer is found for the given id"
        ds.getOffer(_ as OfferId) >> Optional.empty()

        when: "the offer content creation is triggered"
        createOfferContent.createOfferContent(offerId)

        then: "failed use case"
        1 * output.failNotification(_)
    }

    def "A successful use case results in an OfferContent DTO"(){
        given: "the create offer content use case"
        FetchOfferDataSource ds = Stub(FetchOfferDataSource.class)

        CreateOfferContentOutput output = Mock()
        CreateOfferContent createOfferContent = new CreateOfferContent(output,ds)

        and: "a mocked offer, that is returned upon the FetchOffer use case"
        ds.getOffer(_ as OfferId) >> Optional.of(new life.qbic.datamodel.dtos.business.Offer.Builder(customer, projectManager, projectTitle, projectDescription, selectedAffiliation)
                .modificationDate(date).expirationDate(date).items([items[0]]).identifier(offerId)
                .build())

        when: "the offer content creation is triggered"
        createOfferContent.createOfferContent(offerId)

        then: "the overheads for the product groups are set correctly"
        1 * output.createdOfferContent(_ as OfferContent)
    }

}