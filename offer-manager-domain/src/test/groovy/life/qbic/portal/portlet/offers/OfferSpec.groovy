package life.qbic.portal.portlet.offers

import life.qbic.business.offers.Offer
import life.qbic.business.offers.QuantityDiscount
import life.qbic.business.offers.identifier.OfferId
import life.qbic.business.offers.identifier.ProjectPart
import life.qbic.business.offers.identifier.RandomPart
import life.qbic.business.offers.identifier.Version
import life.qbic.datamodel.dtos.business.*
import life.qbic.datamodel.dtos.business.facilities.Facility
import life.qbic.datamodel.dtos.business.services.*
import spock.lang.Shared
import spock.lang.Specification

/**
 * Test for the business rules in offer calculus at QBiC.
 *
 * @since 1.0.0
 */
class OfferSpec extends Specification {

    @Shared
    Affiliation externalAcademicAffiliation
    @Shared
    Affiliation externalAffiliation
    @Shared
    Affiliation internalAffiliation
    @Shared
    Customer customerWithAllAffiliations
    @Shared
    ProjectManager projectManager
    @Shared
    ProjectManager projectManager2
    @Shared
    List<OfferId> availableTestVersions

    def setup() {
        internalAffiliation = new Affiliation.Builder("Uni Tübingen", "Auf der " +
                "Morgenstelle 10", "72076", "Tuebingen").category(AffiliationCategory.INTERNAL).build()
        externalAcademicAffiliation = new Affiliation.Builder("Uni Frankfurt",
                "Irgendwo im Nirgendwo 20", "12345", "Frankfurt").category(AffiliationCategory
                .EXTERNAL_ACADEMIC)
                .build()
        externalAffiliation = new Affiliation.Builder("Company Frankfurt",
                "Irgendwo " +
                        "im Nirgendwo 20", "12345", "Frankfurt").category(AffiliationCategory.EXTERNAL)
                .build()
        customerWithAllAffiliations = new Customer.Builder("Max", "Mustermann", "max" +
                ".mustermann@qbic.uni-tuebingen.de").affiliations([internalAffiliation, externalAffiliation]).build()
        projectManager  = new ProjectManager.Builder("Maxime", "Musterfrau", "max" +
                ".musterfrau@qbic.uni-tuebingen.de").build()
        projectManager2  = new ProjectManager.Builder("Max", "Mustermann", "max" +
                ".mustermann@qbic.uni-tuebingen.de").build()
        availableTestVersions = createExampleOfferId()
    }

    static List<OfferId> createExampleOfferId() {
        def projectConservedPart = new ProjectPart("test")
        def versions = [3,1,4,2]
        return versions.stream()
                .map( version -> new OfferId(new RandomPart(), projectConservedPart,
                new Version(version)))
                .collect()
    }

    def "An offer with multiple versions shall return the latest version on request"() {
        given: "An offer id that is not the latest version of the offer"
        OfferId offerId = new OfferId (new RandomPart(), new ProjectPart("test"), new Version(0))

        and: "some example product items"
        List<ProductItem> items = [
                new ProductItem(2, new PrimaryAnalysis("Basic RNAsq", "Just an" +
                        " example", 1.0, ProductUnit.PER_SAMPLE, "1")),
                new ProductItem(1, new ProjectManagement("Basic Management",
                        "Just an example", 10.0, ProductUnit.PER_DATASET, "1"))
        ]

        and: "some example versions, with 4 as the highest one"
        def versions = availableTestVersions

        when: "we create an offer with that id"
        Offer offer = new Offer.Builder(customerWithAllAffiliations, projectManager, "Awesome Project", "An " +
                "awesome project", items, internalAffiliation)
                .identifier(offerId)
                .experimentalDesign(Optional.of("this is a design"))
                .build()
        offer.addAllAvailableVersions(versions)

        then: "the latest version must be 4"
        offer.getLatestVersion().version == new Version(4)
    }

    def "A customer with an internal affiliation shall pay no overheads"() {
        given: "A list of product items"
        ProductItem primaryAnalysisItem = new ProductItem(2, new PrimaryAnalysis("Basic RNAsq", "Just an" +
                " example", 1.0, 1.0, ProductUnit.PER_SAMPLE, 1, Facility.IMGAG))
        List<ProductItem> items = [
                primaryAnalysisItem,
                new ProductItem(1, new ProjectManagement("Basic Management",
                        "Just an example", 10.0, 10.0, ProductUnit.PER_DATASET, 1, Facility.QBIC))
        ]

        and: "an internal offer containing these product items"
        Offer offer = new Offer.Builder(customerWithAllAffiliations, projectManager, "Awesome Project", "An " +
                "awesome project", items, internalAffiliation).build()

        when: "the Offer object is tasked with calculating the total costs and the total net price"
        double totalCosts = offer.getTotalCosts()
        double netPrice = offer.getTotalNetPrice()
        double totalDiscount = new QuantityDiscount().apply(primaryAnalysisItem.quantity as Integer,
                (primaryAnalysisItem.product.externalUnitPrice * primaryAnalysisItem.quantity) as BigDecimal)

        then:
        double expectedNetPrice = (double) 10.0 + 2 * 1.0
        double expectedTotalPrice = expectedNetPrice
        netPrice == expectedNetPrice
        totalCosts == expectedTotalPrice - totalDiscount
        totalDiscount == offer.getTotalDiscountAmount()


    }

    def "A customer with an external academic affiliation shall pay 20% overheads and 19% VAT"() {
        given:
        ProductItem primaryAnalysisItem = new ProductItem(2, new PrimaryAnalysis("Basic RNAsq", "Just an" +
                " example", 1.0, 1.0, ProductUnit.PER_SAMPLE, 1, Facility.IMGAG))
        List<ProductItem> items = [
                primaryAnalysisItem,
                new ProductItem(1, new ProjectManagement("Basic Management",
               "Just an example", 10.0, 10.0, ProductUnit.PER_DATASET, 1, Facility.IMGAG))
        ]

        Offer offer = new Offer.Builder(customerWithAllAffiliations, projectManager, "Awesome Project", "An " +
                "awesome project", items, externalAcademicAffiliation).build()

        when:
        double overhead = offer.getOverheadSum()
        double taxes = offer.getTaxCosts()
        double totalCosts = offer.getTotalCosts()
        double netSum = offer.getTotalNetPrice()

        then:
        double expectedNetSum = (10.0 + (2 * 1.0))
        double expectedOverhead = (10.0 + (2 * 1.0)) * 0.2
        double expectedTaxes = (expectedNetSum + expectedOverhead) * 0.19
        double totalDiscount = new QuantityDiscount().apply(primaryAnalysisItem.quantity as Integer,
                (primaryAnalysisItem.product.externalUnitPrice * primaryAnalysisItem.quantity) as BigDecimal)

        offer.items.size() == 2
        netSum == expectedNetSum
        overhead == expectedOverhead
        taxes == expectedTaxes
        totalDiscount == offer.totalDiscountAmount

        totalCosts == (double) expectedNetSum + expectedOverhead + expectedTaxes - totalDiscount
    }

    def "A customer with an external (non-academic) affiliation shall pay 40% overheads and 19% VAT"() {
        given:
        ProductItem primaryAnalysisItem = new ProductItem(2, new PrimaryAnalysis("Basic RNAsq", "Just an" +
                " example", 1.0, 1.0, ProductUnit.PER_SAMPLE, 1, Facility.IMGAG))
        List<ProductItem> items = [
                primaryAnalysisItem,
                new ProductItem(1, new ProjectManagement("Basic Management",
                        "Just an example", 10.0, 10.0, ProductUnit.PER_DATASET, 1, Facility.PCT))
        ]

        Offer offer = new Offer.Builder(customerWithAllAffiliations, projectManager, "Awesome Project", "An " +
                "awesome project", items, externalAffiliation).build()

        when:
        double overhead = offer.getOverheadSum()
        double taxes = offer.getTaxCosts()
        double totalCosts = offer.getTotalCosts()
        double netSum = offer.getTotalNetPrice()
        double totalDiscount = new QuantityDiscount().apply(primaryAnalysisItem.quantity as Integer,
                (primaryAnalysisItem.product.externalUnitPrice * primaryAnalysisItem.quantity) as BigDecimal)

        then:
        double expectedNetSum = (10.0 + (2 * 1.0))
        double expectedOverhead = (10.0 + (2 * 1.0)) * 0.4
        double expectedTaxes = (expectedNetSum + expectedOverhead) * 0.19
        offer.items.size() == 2
        netSum == expectedNetSum
        overhead == expectedOverhead
        taxes == expectedTaxes
        totalDiscount == offer.getTotalDiscountAmount()

        totalCosts == (double) expectedNetSum + expectedOverhead + expectedTaxes - totalDiscount
    }

    def "On data storage and project management service, overhead costs are includes since 1.0.4"() {
        given:
        List<ProductItem> items = [
                new ProductItem(1, new ProjectManagement("Basic Management",
                        "Just an example", 10.0, 10.0, ProductUnit.PER_DATASET, 1, Facility.QBIC)),
                new ProductItem(1, new DataStorage("Basic Management",
                        "Just an example", 10.0, 10.0, ProductUnit.PER_GIGABYTE, 1, Facility.QBIC))

        ]

        Offer offer = new Offer.Builder(customerWithAllAffiliations, projectManager, "Awesome Project", "An " +
                "awesome project", items, externalAffiliation).build()

        when:
        double overhead = offer.getOverheadSum()
        double expectedOverhead = (10.0 + 10.0) * 0.4

        then:
        overhead == expectedOverhead
    }

    def "Different offer with updated item list can be differentiated"(){
        given:
        List<ProductItem> items = [
                new ProductItem(2, new PrimaryAnalysis("Basic RNAsq", "Just an" +
                        " example", 1.0, 1.0, ProductUnit.PER_SAMPLE,1, Facility.QBIC)),
                new ProductItem(1, new ProjectManagement("Basic Management",
                        "Just an example", 10.0, 10.0, ProductUnit.PER_DATASET,1, Facility.QBIC))
        ]

        Offer offer = new Offer.Builder(customerWithAllAffiliations, projectManager, "Awesome Project", "An " +
                "awesome project", items, externalAffiliation).build()

        List<ProductItem> items2 = [
                new ProductItem(10, new PrimaryAnalysis("Basic RNAsq", "Just an" +
                        " example", 1.0, 1.0, ProductUnit.PER_SAMPLE,1, Facility.QBIC)),
                new ProductItem(5, new ProjectManagement("Basic Management",
                        "Just an example", 10.0, 10.0, ProductUnit.PER_DATASET,1, Facility.QBIC))
        ]

        Offer offer2 = new Offer.Builder(customerWithAllAffiliations, projectManager, "Awesome Project", "An " +
                "awesome project", items2, externalAffiliation).build()

        when:
        def res = offer.equals(offer2)

        then:
        !res
    }

    def "Different offer with updated project manager can be differentiated"(){
        given:
        List<ProductItem> items = [
                new ProductItem(2, new PrimaryAnalysis("Basic RNAsq", "Just an" +
                        " example", 1.0, 1.0,ProductUnit.PER_SAMPLE,1, Facility.QBIC)),
                new ProductItem(1, new ProjectManagement("Basic Management",
                        "Just an example", 10.0, 10.0, ProductUnit.PER_DATASET,1, Facility.QBIC))
        ]

        Offer offer = new Offer.Builder(customerWithAllAffiliations, projectManager, "Awesome Project", "An " +
                "awesome project", items, externalAffiliation).build()

        Offer offer2 = new Offer.Builder(customerWithAllAffiliations, projectManager2, "Awesome Project", "An " +
                "awesome project", items, externalAffiliation).build()

        when:
        def res = offer.equals(offer2)

        then:
        !res
    }

    def "Different offer with updated customer affiliation can be differentiated"(){
        given:
        List<ProductItem> items = [
                new ProductItem(2, new PrimaryAnalysis("Basic RNAsq", "Just an" +
                        " example", 1.0, 1.0, ProductUnit.PER_SAMPLE,1, Facility.QBIC)),
                new ProductItem(1, new ProjectManagement("Basic Management",
                        "Just an example", 10.0, 10.0, ProductUnit.PER_DATASET,1, Facility.QBIC))
        ]

        Offer offer = new Offer.Builder(customerWithAllAffiliations, projectManager, "Awesome Project", "An " +
                "awesome project", items, externalAffiliation).build()

        List<ProductItem> items2 = [
                new ProductItem(2, new PrimaryAnalysis("Basic RNAsq", "Just an" +
                        " example", 1.0, 1.0, ProductUnit.PER_SAMPLE,1, Facility.QBIC)),
                new ProductItem(1, new ProjectManagement("Basic Management",
                        "Just an example", 10.0, 10.0, ProductUnit.PER_DATASET,1, Facility.QBIC))
        ]

        Offer offer2 = new Offer.Builder(customerWithAllAffiliations, projectManager, "Awesome Project", "An " +
                "awesome project", items2, internalAffiliation).build()

        when:
        def res = offer.equals(offer2)

        then:
        !res
    }

    /**
     * @since 1.1.0
     */
    def "the total net costs are computed with the correct internal prices"() {
        given: "a list of product items with internal and external base prices"
        ProductItem primaryAnalysis = new ProductItem(2, new PrimaryAnalysis("Basic RNAsq", "Just an" +
                " example primary analysis", 2.5, 3.5, ProductUnit.PER_SAMPLE, 1, Facility.QBIC))
        ProductItem secondaryAnalysis = new ProductItem(1, new SecondaryAnalysis("Basic RNAsq", "Just an" +
                " example secondary analysis", 2.4, 42.56, ProductUnit.PER_SAMPLE, 1, Facility.QBIC))
        ProductItem sequencing = new ProductItem(3, new Sequencing("Basic Sequencing", "Just an" +
                "example sequencing", 3.0, 4.6, ProductUnit.PER_SAMPLE, 1, Facility.QBIC))
        ProductItem projectManagement = new ProductItem(1, new ProjectManagement("Basic Management",
                "Just an example", 10.0, 11.26, ProductUnit.PER_DATASET, 1, Facility.QBIC))
        ProductItem dataStorage = new ProductItem(2, new DataStorage("Data Storage",
                "Just an example", 20.0, 23, ProductUnit.PER_DATASET, 1, Facility.QBIC))
        and: "an offer with these items"
        List<ProductItem> items = [primaryAnalysis, projectManagement, sequencing, dataStorage, secondaryAnalysis]
        Offer offer = new Offer.Builder(customerWithAllAffiliations, projectManager, "Awesome Project", "An " +
                "awesome project", items, affiliation).build()
        double netPrice

        when: "the net price is calculated"
        netPrice = offer.getTotalNetPrice()

        then: "the correct prices are taken into account"
        assert offer.selectedCustomerAffiliation == affiliation
        netPrice == items.sum {(it.quantity * it.product.internalUnitPrice) as double}

        where: "the affiliation is"
        affiliation << [internalAffiliation]

    }

    /**
     * @since 1.1.0
     */
    def "the total net costs are computed with the correct external prices"() {
        given: "a list of product items with internal and external base prices"
        ProductItem primaryAnalysis = new ProductItem(2, new PrimaryAnalysis("Basic RNAsq", "Just an" +
                " example primary analysis", 2.5, 3.5, ProductUnit.PER_SAMPLE, 1, Facility.QBIC))
        ProductItem secondaryAnalysis = new ProductItem(1, new SecondaryAnalysis("Basic RNAsq", "Just an" +
                " example secondary analysis", 2.4, 42.56, ProductUnit.PER_SAMPLE, 1, Facility.QBIC))
        ProductItem sequencing = new ProductItem(3, new Sequencing("Basic Sequencing", "Just an" +
                "example sequencing", 3.0, 4.6, ProductUnit.PER_SAMPLE, 1, Facility.QBIC))
        ProductItem projectManagement = new ProductItem(1, new ProjectManagement("Basic Management",
                "Just an example", 10.0, 11.26, ProductUnit.PER_DATASET, 1, Facility.QBIC))
        ProductItem dataStorage = new ProductItem(2, new DataStorage("Data Storage",
                "Just an example", 20.0, 23, ProductUnit.PER_DATASET, 1, Facility.QBIC))
        and: "an offer with these items"
        List<ProductItem> items = [primaryAnalysis, projectManagement, sequencing, dataStorage, secondaryAnalysis]
        Offer offer = new Offer.Builder(customerWithAllAffiliations, projectManager, "Awesome Project", "An " +
                "awesome project", items, affiliation).build()
        double netPrice

        when: "the net price is calculated"
        netPrice = offer.getTotalNetPrice()

        then: "the correct prices are taken into account"
        netPrice == items.sum {(it.quantity * it.product.externalUnitPrice) as double}

        where: "the affiliation is"
        affiliation << [externalAffiliation, externalAcademicAffiliation]
    }

    /**
     * @since 1.1.0
     */
    def "the total overhead costs are computed with the correct internal prices"() {
        given: "a list of product items with internal and external base prices"
        ProductItem primaryAnalysis = new ProductItem(2, new PrimaryAnalysis("Basic RNAsq", "Just an" +
                " example primary analysis", 2.5, 3.5, ProductUnit.PER_SAMPLE, 1, Facility.QBIC))
        ProductItem secondaryAnalysis = new ProductItem(1, new SecondaryAnalysis("Basic RNAsq", "Just an" +
                " example secondary analysis", 2.4, 42.56, ProductUnit.PER_SAMPLE, 1, Facility.QBIC))
        ProductItem sequencing = new ProductItem(3, new Sequencing("Basic Sequencing", "Just an" +
                "example sequencing", 3.0, 4.6, ProductUnit.PER_SAMPLE, 1, Facility.QBIC))
        ProductItem projectManagement = new ProductItem(1, new ProjectManagement("Basic Management",
                "Just an example", 10.0, 11.26, ProductUnit.PER_DATASET, 1, Facility.QBIC))
        ProductItem dataStorage = new ProductItem(2, new DataStorage("Data Storage",
                "Just an example", 20.0, 23, ProductUnit.PER_DATASET, 1, Facility.QBIC))
        List<ProductItem> items = [primaryAnalysis, projectManagement, sequencing, dataStorage, secondaryAnalysis]
        Offer offer = new Offer.Builder(customerWithAllAffiliations, projectManager, "Awesome Project", "An " +
                "awesome project", items, affiliation).build()
        def overheadSum = 0

        when: "the net price is calculated"
        overheadSum = offer.getOverheadSum()

        then: "the correct prices are taken into account"
        assert offer.selectedCustomerAffiliation.category == AffiliationCategory.INTERNAL
        overheadSum == items.collect {return it.quantity * it.product.internalUnitPrice}.sum() * overheadRatio

        where: "the affiliation is"
        affiliation << [internalAffiliation]
        overheadRatio = 0.0

    }

    /**
     * @since 1.1.0
     */
    def "the total overhead costs are computed with the correct external prices"() {
        given: "a list of product items with internal and external base prices"
        ProductItem primaryAnalysis = new ProductItem(2, new PrimaryAnalysis("Basic RNAsq", "Just an" +
                " example primary analysis", 2.5, 3.5, ProductUnit.PER_SAMPLE, 1, Facility.QBIC))
        ProductItem secondaryAnalysis = new ProductItem(1, new SecondaryAnalysis("Basic RNAsq", "Just an" +
                " example secondary analysis", 2.4, 42.56, ProductUnit.PER_SAMPLE, 1, Facility.QBIC))
        ProductItem sequencing = new ProductItem(3, new Sequencing("Basic Sequencing", "Just an" +
                "example sequencing", 3.0, 4.6, ProductUnit.PER_SAMPLE, 1, Facility.QBIC))
        ProductItem projectManagement = new ProductItem(1, new ProjectManagement("Basic Management",
                "Just an example", 10.0, 11.26, ProductUnit.PER_DATASET, 1, Facility.QBIC))
        ProductItem dataStorage = new ProductItem(2, new DataStorage("Data Storage",
                "Just an example", 20.0, 23, ProductUnit.PER_DATASET, 1, Facility.QBIC))
        and: "an offer with these items"
        List<ProductItem> items = [primaryAnalysis, projectManagement, sequencing, dataStorage, secondaryAnalysis]
        Offer offer = new Offer.Builder(customerWithAllAffiliations, projectManager, "Awesome Project", "An " +
                "awesome project", items, affiliation).build()
        def overheadSum = 0

        when: "the net price is calculated"
        overheadSum = offer.getOverheadSum()

        then: "the correct prices are taken into account"
        assert offer.selectedCustomerAffiliation.category == AffiliationCategory.EXTERNAL || offer.selectedCustomerAffiliation.category == AffiliationCategory.EXTERNAL_ACADEMIC
        overheadSum == items.collect {return it.quantity * it.product.externalUnitPrice}.sum() * overheadRatio

        where: "the affiliation is"
        affiliation | overheadRatio
        externalAffiliation | 0.4
        externalAcademicAffiliation | 0.2

    }

    def "Given #samples data analysis service items, calculate the correct amount of sample-dependent discount with factor #discountFactor"() {
        given: "an offer with one primary analysis service product of N samples"
        PrimaryAnalysis primaryAnalysis1 = new PrimaryAnalysis("Test Bioinformatics", "Testing", 10, 10, ProductUnit.PER_SAMPLE, 1L, Facility.QBIC)
        PrimaryAnalysis primaryAnalysis2 = new PrimaryAnalysis("Test Bioinformatics 2", "Testing", 10, 10, ProductUnit.PER_SAMPLE, 1L, Facility.QBIC)

        ProductItem item1 = new ProductItem(samples as Double, primaryAnalysis1)
        ProductItem item2 = new ProductItem(samples as Double, primaryAnalysis2)

        Offer offer = new Offer.Builder(customerWithAllAffiliations, projectManager,  "", "", [item1, item2], internalAffiliation).build()

        when: "we request the total discount amount"
        double totalDiscountAmount = offer.getTotalDiscountAmount()

        then: "the total discount amount has to increase with the number of samples the service is applied to"
        double expectedTotalDiscount = 10 * samples * (1-discountFactor) + 10 * samples * (1-discountFactor)
        totalDiscountAmount == expectedTotalDiscount

        where:
        samples | discountFactor
        1 | 1
        2 | 0.98
        10 | 0.67
        100 | 0.3

    }

    def "Given #samples data analysis service items and data generation items, calculate the correct amount of sample-dependent discount with factor #discountFactor"() {

        given: "an offer with one primary analysis service product of N samples"
        PrimaryAnalysis primaryAnalysis1 = new PrimaryAnalysis("Test Bioinformatics", "Testing", 10, 10, ProductUnit.PER_SAMPLE, 1L, Facility.QBIC)
        PrimaryAnalysis primaryAnalysis2 = new PrimaryAnalysis("Test Bioinformatics 2", "Testing", 10, 10, ProductUnit.PER_SAMPLE, 1L, Facility.QBIC)
        Sequencing dataGeneration = new Sequencing("Test Sequencing 2", "Testing", 10, 10, ProductUnit.PER_SAMPLE, 1L, Facility.QBIC)

        ProductItem item1 = new ProductItem(samples as Double, primaryAnalysis1)
        ProductItem item2 = new ProductItem(samples as Double, primaryAnalysis2)
        ProductItem item3 = new ProductItem(samples as Double, dataGeneration)

        Offer offer = new Offer.Builder(customerWithAllAffiliations, projectManager,  "", "", [item1, item2, item3], internalAffiliation).build()

        when: "we request the total discount amount"
        double totalDiscountAmount = offer.getTotalDiscountAmount()

        then: "the total discount amount has to increase with the number of samples the service is applied to"
        double expectedTotalDiscount = 10 * samples * (1-discountFactor) + 10 * samples * (1-discountFactor)
        totalDiscountAmount == expectedTotalDiscount

        where:
        samples | discountFactor
        1 | 1
        2 | 0.98
        10 | 0.67
        100 | 0.3

    }
}
