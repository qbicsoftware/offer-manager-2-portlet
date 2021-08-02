package life.qbic.business.offers

import life.qbic.business.ProductFactory
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
import life.qbic.datamodel.dtos.business.services.DataStorage
import life.qbic.datamodel.dtos.business.services.PrimaryAnalysis
import life.qbic.datamodel.dtos.business.services.Product
import life.qbic.datamodel.dtos.business.services.ProductUnit
import life.qbic.datamodel.dtos.business.services.ProjectManagement
import life.qbic.datamodel.dtos.business.services.Sequencing
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
        selectedAffiliation = new Affiliation.Builder("Universität Tübingen",
                "Auf der Morgenstelle 10",
                "72076",
                "Tübingen")
                .category(AffiliationCategory.EXTERNAL)
                .build()
        customer = new Customer.Builder("Max", "Mustermann", "").affiliation(selectedAffiliation).build()
        projectManager = new ProjectManager.Builder("Max", "Mustermann", "").affiliation(selectedAffiliation).build()
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

    def "Overhead costs of #overheadPercent for product groups are correctly set for #categoryString affiliation"(){
        given: "the create offer content use case"
        Product product1 = ProductFactory.createProduct(Sequencing, internalPrice1, externalPrice1)
        Product product2 = ProductFactory.createProduct(Sequencing, internalPrice2, externalPrice2)
        Product product3 = ProductFactory.createProduct(Sequencing, internalPrice3, externalPrice3)
        List<ProductItem> productItems = [
                new ProductItem(1, product1),
                new ProductItem(2, product2),
                new ProductItem(3, product3)
        ]
        Affiliation affiliation = new Affiliation.Builder("Test orga", "Test street", "1234", "Tübingen").category(affiliationCategory).build()

        and: "a data stubbed data source"
        FetchOfferDataSource ds = Stub(FetchOfferDataSource.class)

        CreateOfferContentOutput output = Stub()
        CreateOfferContent createOfferContent = new CreateOfferContent(output,ds)

        and: "a mocked offer, that is returned upon the FetchOffer use case"
        ds.getOffer(_ as OfferId) >> Optional.of(new life.qbic.datamodel.dtos.business.Offer.Builder(customer, projectManager, projectTitle, projectDescription, affiliation)
                .modificationDate(date).expirationDate(date).items(productItems).identifier(offerId)
                .build())

        when: "the offer content creation is triggered"
        createOfferContent.createOfferContent(offerId)

        then: "the overheads for the product groups are set correctly"
        output.createdOfferContent(_ as OfferContent) >> { arguments ->
            final OfferContent offerContent = arguments.get(0)
            assert offerContent.overheadsDataAnalysis  == offerContent.getNetDataAnalysis() * overheadRatio
            assert offerContent.overheadsDataGeneration == offerContent.getNetDataGeneration() * overheadRatio
            assert offerContent.overheadsProjectManagementAndDataStorage == offerContent.getNetPMandDS() * overheadRatio
        }

        where:
        affiliationCategory | overheadRatio
        AffiliationCategory.INTERNAL | 0.0
        AffiliationCategory.EXTERNAL_ACADEMIC | 0.2
        AffiliationCategory.EXTERNAL | 0.4

        and:
        overheadPercent = "${overheadRatio * 100}%"
        categoryString = affiliationCategory.toString()

        and:
        internalPrice1 = 4.0
        internalPrice2 = 5.9
        internalPrice3 = 3.3

        and:
        externalPrice1 = internalPrice1 + 0.5
        externalPrice2 = internalPrice2 + 0.5
        externalPrice3 = internalPrice3 + 0.5
    }

    def "Net costs for product groups are correctly set"(){
        given: "the create offer content use case"
        ProductItem productItem1 = new ProductItem(2, ProductFactory.createProduct(PrimaryAnalysis, internalPrice, externalPrice))
        ProductItem productItem2 = new ProductItem(3, ProductFactory.createProduct(Sequencing, internalPrice, externalPrice))
        ProductItem productItem3 = new ProductItem(4, ProductFactory.createProduct(DataStorage, internalPrice, externalPrice))
        List<ProductItem> productItems = [
                productItem1,
                productItem2,
                productItem3
        ]
        Affiliation affiliation = new Affiliation.Builder("Test orga", "Test street", "1234", "Tübingen").category(affiliationCategory).build()

        and: "a data stubbed data source"
        FetchOfferDataSource ds = Stub(FetchOfferDataSource.class)

        CreateOfferContentOutput output = Stub()
        CreateOfferContent createOfferContent = new CreateOfferContent(output,ds)
        and:
        QuantityDiscount quantityDiscount = new QuantityDiscount()

        and: "an offer to be returned"
        life.qbic.datamodel.dtos.business.Offer offerDto = new life.qbic.datamodel.dtos.business.Offer.Builder(customer, projectManager, projectTitle, projectDescription, affiliation)
                .modificationDate(date).expirationDate(date).items(productItems).identifier(offerId)
                .build()

        and: "a mocked offer, that is returned upon the FetchOffer use case"
        ds.getOffer(_ as OfferId) >> Optional.of(offerDto)

        when: "the offer content creation is triggered"
        createOfferContent.createOfferContent(offerId)

        then: "the overheads for the product groups are set correctly"
        output.createdOfferContent(_ as OfferContent) >> { arguments ->
            final OfferContent offerContent = arguments.get(0)
            assert offerContent.netDataAnalysis  == 2 * unitPrice - quantityDiscount.apply(2, 2*unitPrice)
            assert offerContent.netDataGeneration == 3 * unitPrice - quantityDiscount.apply(2, 3*unitPrice)
            assert offerContent.netPMandDS == 4 * unitPrice - quantityDiscount.apply(2, 4*unitPrice)
        }

        where:
        internalPrice = 4.0
        externalPrice = 4.5

        and:
        affiliationCategory << [AffiliationCategory.INTERNAL, AffiliationCategory.EXTERNAL_ACADEMIC, AffiliationCategory.EXTERNAL]

        and:
        unitPrice = affiliationCategory == AffiliationCategory.INTERNAL ? internalPrice : externalPrice
    }

    def "Unknown OfferId triggers the failedNotification() method"(){
        given: "the create offer content use case"
        FetchOfferDataSource ds = Stub(FetchOfferDataSource.class)

        CreateOfferContentOutput output = Mock()
        CreateOfferContent createOfferContent = new CreateOfferContent(output,ds)

        and: "no offer is found for the given id"
        ds.getOffer(_ as OfferId) >> Optional.empty()

        when: "the offer content creation is triggered"
        def result = createOfferContent.createOfferContent(offerId)

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
        def result = createOfferContent.createOfferContent(offerId)

        then: "the overheads for the product groups are set correctly"
        1 * output.createdOfferContent(_ as OfferContent)

    }

}