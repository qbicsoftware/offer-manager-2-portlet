package life.qbic.business.offers

import life.qbic.business.offers.content.CreateOfferContent
import life.qbic.business.offers.content.CreateOfferContentOutput
import life.qbic.business.offers.fetch.FetchOfferDataSource
import life.qbic.business.offers.identifier.ProjectPart
import life.qbic.business.offers.identifier.RandomPart
import life.qbic.business.offers.identifier.Version
import life.qbic.datamodel.dtos.business.*
import life.qbic.datamodel.dtos.business.facilities.Facility
import life.qbic.datamodel.dtos.business.services.PrimaryAnalysis
import life.qbic.datamodel.dtos.business.services.ProductUnit
import life.qbic.datamodel.dtos.business.services.ProjectManagement
import spock.lang.Specification
import spock.lang.Unroll

import java.text.SimpleDateFormat

/**
 * <h1>Tests for the offercontent dto</h1>
 *
 * @since 1.1.0
 *
*/
class OfferContentSpec extends Specification{

    static Customer simpleCustomer = new Customer.Builder("Tom", "Sawyer", "tom@sawy.er").build()
    static Affiliation internalAffiliation = new Affiliation.Builder("Uni Tübingen", "Auf der " +
            "Morgenstelle 10", "72076", "Tuebingen").category(AffiliationCategory.INTERNAL).build()
    static Affiliation externalAcademicAffiliation = new Affiliation.Builder("Uni Frankfurt",
            "Irgendwo im Nirgendwo 20", "12345", "Frankfurt").category(AffiliationCategory
            .EXTERNAL_ACADEMIC)
            .build()
    static Affiliation externalAffiliation = new Affiliation.Builder("Company Frankfurt",
            "Irgendwo " +
                    "im Nirgendwo 20", "12345", "Frankfurt").category(AffiliationCategory.EXTERNAL)
            .build()
    static Customer customerWithAllAffiliations = new Customer.Builder("Max", "Mustermann", "max" +
            ".mustermann@qbic.uni-tuebingen.de").affiliations([internalAffiliation, externalAcademicAffiliation]).build()
    static ProjectManager projectManager1 = new ProjectManager.Builder("Maxime", "Musterfrau", "max" +
            ".musterfrau@qbic.uni-tuebingen.de").affiliations([internalAffiliation]).build()
    static ProjectManager projectManager2 = new ProjectManager.Builder("Max", "Mustermann", "max" +
            ".mustermann@qbic.uni-tuebingen.de").affiliations([internalAffiliation]).build()

    static Date creationDate = new SimpleDateFormat("yyyy-MM-dd").parse("2021-10-11")
    static Date expirationDate = new SimpleDateFormat("yyyy-MM-dd").parse("2022-10-11")

    final static List<OfferItem> items = [
            new OfferItem.Builder(2, "Just an example", "Basic RNAsq", 1.0, 1, 0.2, 0.1,
                    "QBiC","Sample", 1).build(),
            new OfferItem.Builder(2, "Just an example", "Basic RNAsq", 1.0, 1, 0.2, 0.1,
                    "QBiC","Dataset", 1).build(),

    ]

    final static List<OfferItem> items2 = [
            new OfferItem.Builder(4, "Just an example", "Basic RNAsq", 1.0, 1, 0.2, 0.1,
                    "QBiC","Sample", 1).build(),
            new OfferItem.Builder(2, "Just an example", "Basic RNAsq", 1.0, 1, 0.2, 0.1,
                    "QBiC","Dataset", 6).build(),

    ]

    def "Discounted product unit price multiplied with quantity obtains the correct total discount price" () {
        given: "CreateOfferContent use case"
        FetchOfferDataSource fetchOfferDataSource = Stub()
        CreateOfferContentOutput output = Mock()
        CreateOfferContent createOfferContent = new CreateOfferContent(output, fetchOfferDataSource)

        and: "An offer id that is not the latest version of the offer"
        life.qbic.business.offers.identifier.OfferId offerId = new life.qbic.business.offers.identifier.OfferId (new RandomPart(), new ProjectPart("test"), new Version(0))

        and: "some example product items"
        List<ProductItem> items = [
                new ProductItem(42, new PrimaryAnalysis("Basic RNAsq", "Just an" +
                        " example", 83.33, 83.33,ProductUnit.PER_SAMPLE, 1, Facility.QBIC)),
                new ProductItem(1, new ProjectManagement("Basic Management",
                        "Just an example", 10.0, 10.0, ProductUnit.PER_DATASET, 1, Facility.QBIC))
        ]

        and: "an offer"
        life.qbic.datamodel.dtos.business.Offer offertest = new life.qbic.datamodel.dtos.business.Offer.Builder(customerWithAllAffiliations,projectManager1,"Awesome Project", "An " +
                "awesome project", internalAffiliation)
                .items(items).identifier(Converter.convertIdToDTO(offerId))
                .experimentalDesign("This is a test")
                .expirationDate(new Date()).modificationDate(new Date())
                .netPrice(5)
                .taxes(5)
                .overheads(5)
                .totalPrice(5)
                .totalDiscountPrice(5666)
                .overheadRatio(0.2)
                .build()

        fetchOfferDataSource.getOffer(_ as OfferId) >> {Optional.of(offertest)}

        when: "the offer content is created"
        createOfferContent.createOfferContent(offertest.identifier)

        then: "the total discount can be calculated from the unit price discount and the other way around"
        output.createdOfferContent(_ as OfferContent) >> { arguments ->
                final OfferContent offerContent = arguments.get(0)
                OfferItem item = offerContent.dataAnalysisItems.get(0)
                assert item.quantityDiscount == BigDecimal.valueOf(item.quantity * item.discountPerUnit).round(2)
                assert item.discountPerUnit.toBigDecimal().round(4) == (item.quantityDiscount / item.quantity.toBigDecimal()).round(4)
        }
    }

    def "An OfferContent with equal content is equal"(){
        when: "two offercontents with the exact same content"
        OfferContent offerContent1 = new OfferContent.Builder(customerWithAllAffiliations,externalAcademicAffiliation,projectManager1,
                creationDate, expirationDate,"title","description",
                "experimental design","O_greiner_ksma_1", 777)
        .totalVat(3333)
        .netCost(222)
        .totalCost(2222)
        .netDataAnalysis(111)
        .netDataGeneration(222)
        .netProjectManagementAndDataStorage(2)
        .overheadsProjectManagementAndDataStorage(33)
        .overheadsDataGeneration(444)
        .overheadsDataAnalysis(33)
        .overheadTotal(333)
        .overheadRatio(0.2)
        .dataManagementItems(items)
        .dataAnalysisItems([])
        .dataGenerationItems(items)
        .vatRatio(19)
        .totalDiscountAmount(0)
        .build()

        OfferContent offerContent2 = new OfferContent.Builder(customerWithAllAffiliations,externalAcademicAffiliation,projectManager1,
                creationDate, expirationDate,"title","description",
                "experimental design","O_greiner_ksma_1", 777)
                .totalVat(3333)
                .netCost(222)
                .totalCost(2222)
                .netDataAnalysis(111)
                .netDataGeneration(222)
                .netProjectManagementAndDataStorage(2)
                .overheadsProjectManagementAndDataStorage(33)
                .overheadsDataGeneration(444)
                .overheadsDataAnalysis(33)
                .overheadTotal(333)
                .overheadRatio(0.2)
                .dataManagementItems(items)
                .dataAnalysisItems([])
                .dataGenerationItems(items)
                .vatRatio(19)
                .totalDiscountAmount(0)
                .build()

        then: "both are equal"
        offerContent1 == offerContent2
    }

    @Unroll
    def "An OfferContent with different content are not equal for different #argumentName"(){
        given: "a reference offer content"
        OfferContent reference = new OfferContent.Builder(
                customerWithAllAffiliations,
                internalAffiliation,
                projectManager1,
                creationDate,
                expirationDate,
                "title",
                "description",
                "experimental design",
                "oabcd1", 777)
                .dataGenerationItems(items)
                .dataAnalysisItems(items)
                .dataManagementItems(items)
                .overheadTotal(333)
                .overheadRatio(0.2)
                .overheadsDataGeneration(444)
                .overheadsDataAnalysis(444)
                .overheadsProjectManagementAndDataStorage(444)
                .netDataGeneration(444)
                .netDataAnalysis(444)
                .netProjectManagementAndDataStorage(444)
                .totalCost(444)
                .netCost(444)
                .totalVat(444)
                .vatRatio(19)
                .totalDiscountAmount(0)
                .build()

        when: "two offercontents different"
        OfferContent differentContent = new OfferContent.Builder(
                customer as Customer,
                affiliation as Affiliation,
                projectManager as ProjectManager,
                creationDate_ as Date,
                expirationDate_ as Date,
                projectTitle,
                projectObjective,
                experimentalDesign,
                offerIdentifier,
                netCostsWithOverheads)
                .dataGenerationItems(dataGenerationItems)
                .dataAnalysisItems(dataAnalysisItems)
                .dataManagementItems(dataManagementItems)
                .overheadTotal(overheadTotal)
                .overheadRatio(overheadRatio)
                .overheadsDataGeneration(overheadsDataGeneration)
                .overheadsDataAnalysis(overheadsDataAnalysis)
                .overheadsProjectManagementAndDataStorage(overheadsPMandDS)
                .netDataGeneration(netDataGeneration)
                .netDataAnalysis(netDataAnalysis)
                .netProjectManagementAndDataStorage(netPMandDS)
                .totalCost(totalCost)
                .netCost(netCost)
                .totalVat(totalVat)
                .vatRatio(vatRatio)
                .totalDiscountAmount(totalDiscountAmount)
                .build()

        then: "both are not equal"
        differentContent != reference

        where:
        argumentName | customer | affiliation | projectManager | creationDate_ | expirationDate_ | projectTitle | projectObjective | experimentalDesign | offerIdentifier | dataGenerationItems |dataAnalysisItems | dataManagementItems | overheadTotal | overheadsDataGeneration | overheadsDataAnalysis | overheadsPMandDS | netDataGeneration | netDataAnalysis | netPMandDS | totalCost | netCost | totalVat | vatRatio | totalDiscountAmount | netCostsWithOverheads
        "customer" | simpleCustomer | internalAffiliation | projectManager1 | creationDate | expirationDate | "title" | "description" | "experimental design" | "oabcd1" | items |items | items | 333  | 444   | 444   | 444  | 444  | 444  | 444 | 444 | 444 | 444 | 19 | 20 | 777
        "affiliation" | customerWithAllAffiliations | externalAffiliation | projectManager1 | creationDate | expirationDate | "title" | "description" | "experimental design" | "oabcd1" | items |items | items | 333  | 444   | 444   | 444  | 444  | 444  | 444 | 444 | 444 | 444 | 19 | 20 | 777
        "projectManager" | customerWithAllAffiliations | internalAffiliation | projectManager2 | creationDate | expirationDate | "title" | "description" | "experimental design" | "oabcd1" | items |items | items | 333  | 444   | 444   | 444  | 444  | 444  | 444 | 444 | 444 | 444 | 19 | 20 | 777
        "creationDate" | customerWithAllAffiliations | internalAffiliation | projectManager1 | expirationDate | expirationDate | "title" | "description" | "experimental design" | "oabcd1" | items |items | items | 333 | 444 | 444 | 444 | 444 | 444 | 444 | 444 | 444 | 444 | 19 | 20 | 777
        "expirationDate" | customerWithAllAffiliations | internalAffiliation | projectManager1 | creationDate | creationDate | "title" | "description" | "experimental design" | "oabcd1" | items |items | items | 333  | 444   | 444   | 444  | 444  | 444  | 444 | 444 | 444 | 444 | 19 | 20 | 777
        "projectTitle" | customerWithAllAffiliations | internalAffiliation | projectManager1 | creationDate | expirationDate | "OFFER" | "description" | "experimental design" | "oabcd1" | items |items | items | 333  | 444   | 444   | 444  | 444  | 444  | 444 | 444 | 444 | 444 | 19 | 20 | 777
        "projectObjective" | customerWithAllAffiliations | internalAffiliation | projectManager1 | creationDate | expirationDate | "title" | "SOME TEXT  " | "experimental design" | "oabcd1" | items |items | items | 333  | 444   | 444   | 444  | 444  | 444  | 444 | 444 | 444 | 444 | 19 | 20 | 777
        "experimentalDesign" | customerWithAllAffiliations | internalAffiliation | projectManager1 | creationDate | expirationDate | "title" | "description" | "THERE IS NO DESIGN!" | "oabcd1" | items |items | items | 333  | 444   | 444   | 444  | 444  | 444  | 444 | 444 | 444 | 444 | 19 | 20 | 777
        "offerIdentifier" | customerWithAllAffiliations | internalAffiliation | projectManager1 | creationDate | expirationDate | "title" | "description" | "experimental design" | "ODCBA2" | items |items | items | 333  | 444   | 444   | 444  | 444  | 444  | 444 | 444 | 444 | 444 | 19 | 20 | 777
        "dataGenerationItems" | customerWithAllAffiliations | internalAffiliation | projectManager1 | creationDate | expirationDate | "title" | "description" | "experimental design" | "oabcd1" | [] |items | items | 333  | 444   | 444   | 444  | 444  | 444  | 444 | 444 | 444 | 444 | 19 | 20 | 777
        "dataAnalysisItems"| customerWithAllAffiliations | internalAffiliation | projectManager1 | creationDate | expirationDate | "title" | "description" | "experimental design" | "oabcd1" | items |[] | items | 333  | 444   | 444   | 444  | 444  | 444  | 444 | 444 | 444 | 444 | 19 | 20 | 777
        "dataManagementItems" | customerWithAllAffiliations | internalAffiliation | projectManager1 | creationDate | expirationDate | "title" | "description" | "experimental design" | "oabcd1" | items |items | [] | 333  | 444   | 444   | 444  | 444  | 444  | 444 | 444 | 444 | 444 | 19 | 20 | 777
        "overheadTotal" | customerWithAllAffiliations | internalAffiliation | projectManager1 | creationDate | expirationDate | "title" | "description" | "experimental design" | "oabcd1" | items |items | items |  0  | 444   | 444   | 444  | 444  | 444  | 444 | 444 | 444 | 444 | 19 | 20 | 777
        "overheadsDataGeneration" | customerWithAllAffiliations | internalAffiliation | projectManager1 | creationDate | expirationDate | "title" | "description" | "experimental design" | "oabcd1" | items |items | items | 333  |  0   | 444   | 444  | 444  | 444  | 444 | 444 | 444 | 444 | 19 | 20 | 777
        "overheadsDataAnalysis" | customerWithAllAffiliations | internalAffiliation | projectManager1 | creationDate | expirationDate | "title" | "description" | "experimental design" | "oabcd1" | items |items | items | 333  | 444   |  0   | 444  | 444  | 444  | 444 | 444 | 444 | 444 | 19 | 20 | 777
        "overheadsProjectManagementAndDataStorage" | customerWithAllAffiliations | internalAffiliation | projectManager1 | creationDate | expirationDate | "title" | "description" | "experimental design" | "oabcd1" | items |items | items | 333  | 444   | 444   |  0  | 444  | 444  | 444 | 444 | 444 | 444 | 19 | 20 | 777
        "netDataGeneration" | customerWithAllAffiliations | internalAffiliation | projectManager1 | creationDate | expirationDate | "title" | "description" | "experimental design" | "oabcd1" | items |items | items | 333  | 444   | 444   | 444  |  0  | 444  | 444 | 444 | 444 | 444 | 19 | 20 | 777
        "netDataAnalysis" | customerWithAllAffiliations | internalAffiliation | projectManager1 | creationDate | expirationDate | "title" | "description" | "experimental design" | "oabcd1" | items |items | items | 333  | 444   | 444   | 444  | 444  |  0  | 444 | 444 | 444 | 444 | 19 | 20 | 777
        "netPMandDS" | customerWithAllAffiliations | internalAffiliation | projectManager1 | creationDate | expirationDate | "title" | "description" | "experimental design" | "oabcd1" | items |items | items | 333  | 444   | 444   | 444  | 444  | 444  |  0 | 444 | 444 | 444 | 19 | 20 | 777
        "totalCost" | customerWithAllAffiliations | internalAffiliation | projectManager1 | creationDate | expirationDate | "title" | "description" | "experimental design" | "oabcd1" | items |items | items | 333  | 444   | 444   | 444  | 444  | 444  | 444 |  0 | 444 | 444 | 19 | 20 | 777
        "netCost" | customerWithAllAffiliations | internalAffiliation | projectManager1 | creationDate | expirationDate | "title" | "description" | "experimental design" | "oabcd1" | items |items | items | 333  | 444   | 444   | 444  | 444  | 444  | 444 | 444 |  0 | 444 | 19 | 20 | 777
        "totalVat" | customerWithAllAffiliations | internalAffiliation | projectManager1 | creationDate | expirationDate | "title" | "description" | "experimental design" | "oabcd1" | items |items | items | 333  | 444   | 444   | 444  | 444  | 444  | 444 | 444 | 444 |  0 | 19 | 20 | 777
        "vatRatio" | customerWithAllAffiliations | internalAffiliation | projectManager1 | creationDate | expirationDate | "title" | "description" | "experimental design" | "oabcd1" | items |items | items | 333  | 444   | 444   | 444  | 444  | 444  | 444 | 444 | 444 |  444 | 0 | 20 | 777
        "totalDiscountAmount" | customerWithAllAffiliations | internalAffiliation | projectManager1 | creationDate | expirationDate | "title" | "description" | "experimental design" | "oabcd1" | items |items | items | 333  | 444   | 444   | 444  | 444  | 444  | 444 | 444 | 444 |  444 | 19 | 0 | 777
        "netCostsWithOverheads" | customerWithAllAffiliations | internalAffiliation | projectManager1 | creationDate | expirationDate | "title" | "description" | "experimental design" | "oabcd1" | items |items | items | 333  | 444   | 444   | 444  | 444  | 444  | 444 | 444 | 444 |  444 | 19 | 20 | 007
        /* this row produces an OfferContent that should be equal to the reference letting the test fail
         * It can be used to test that the reference was created as expected
        "NONE" | customerWithAllAffiliations | internalAffiliation | projectManager1 | "2021-10-11" | "2022-10-11" | "title" | "description" | "experimental design" | "oabcd1" | items |items | items | 333  | 444   | 444   | 444  | 444  | 444  | 444 | 444 | 444 | 444 | 19
        */
        and: "the overhead ratio is correct for the selected affiliation"
        overheadRatio = determineOverheadRatio(affiliation.getCategory())

    }

    private static def determineOverheadRatio(AffiliationCategory category) {
        if (category == AffiliationCategory.INTERNAL) {
            return 0.0
        } else if (category == AffiliationCategory.EXTERNAL_ACADEMIC) {
            return 0.2
        } else if (category == AffiliationCategory.EXTERNAL) {
            return 0.4
        }
    }
}