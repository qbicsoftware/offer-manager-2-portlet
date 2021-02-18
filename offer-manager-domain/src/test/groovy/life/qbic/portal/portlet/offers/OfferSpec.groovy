package life.qbic.portal.portlet.offers

import life.qbic.business.offers.Offer
import life.qbic.business.offers.identifier.OfferId
import life.qbic.business.offers.identifier.ProjectPart
import life.qbic.business.offers.identifier.RandomPart
import life.qbic.business.offers.identifier.Version
import life.qbic.datamodel.dtos.business.Affiliation
import life.qbic.datamodel.dtos.business.AffiliationCategory
import life.qbic.datamodel.dtos.business.Customer
import life.qbic.datamodel.dtos.business.ProductItem
import life.qbic.datamodel.dtos.business.ProjectManager
import life.qbic.datamodel.dtos.business.services.DataStorage
import life.qbic.datamodel.dtos.business.services.PrimaryAnalysis
import life.qbic.datamodel.dtos.business.services.ProductUnit
import life.qbic.datamodel.dtos.business.services.ProjectManagement
import life.qbic.datamodel.dtos.business.services.SecondaryAnalysis
import life.qbic.datamodel.dtos.business.services.Sequencing
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
                .identifier(offerId)build()
        offer.addAllAvailableVersions(versions)

        then: "the latest version must be 4"
        offer.getLatestVersion().version == new Version(4)
    }

    def "A customer with an internal affiliation shall pay no overheads"() {
        given: "A list of product items"
        List<ProductItem> items = [
                new ProductItem(2, new PrimaryAnalysis("Basic RNAsq", "Just an" +
                        " example", 1.0, ProductUnit.PER_SAMPLE, "1")),
                new ProductItem(1, new ProjectManagement("Basic Management",
                        "Just an example", 10.0, ProductUnit.PER_DATASET, "1"))
        ]

        and: "an internal offer containing these product items"
        Offer offer = new Offer.Builder(customerWithAllAffiliations, projectManager, "Awesome Project", "An " +
                "awesome project", items, internalAffiliation).build()

        when: "the Offer object is tasked with calculating the total costs and the total net price"
        double totalCosts = offer.getTotalCosts()
        double netPrice = offer.getTotalNetPrice()

        then:
        double expectedNetPrice = (double) 10.0 + 2 * 1.0
        double expectedTotalPrice = expectedNetPrice
        netPrice == expectedNetPrice
        totalCosts == expectedTotalPrice

    }

    def "A customer with an external academic affiliation shall pay 20% overheads and 19% VAT"() {
        given:
        List<ProductItem> items = [
                new ProductItem(2, new PrimaryAnalysis("Basic RNAsq", "Just an" +
                " example", 1.0, ProductUnit.PER_SAMPLE, "1")),
                new ProductItem(1, new ProjectManagement("Basic Management",
               "Just an example", 10.0, ProductUnit.PER_DATASET, "1"))
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
        double expectedOverhead = 2 * 1.0 * 0.2
        double expectedTaxes = (expectedNetSum + expectedOverhead) * 0.19
        offer.items.size() == 2
        netSum == expectedNetSum
        overhead == expectedOverhead
        taxes == expectedTaxes

        totalCosts == (double) expectedNetSum + expectedOverhead + expectedTaxes
    }

    def "A customer with an external affiliation shall pay 40% overheads and 19% VAT"() {
        given:
        List<ProductItem> items = [
                new ProductItem(2, new PrimaryAnalysis("Basic RNAsq", "Just an" +
                        " example", 1.0, ProductUnit.PER_SAMPLE, "1")),
                new ProductItem(1, new ProjectManagement("Basic Management",
                        "Just an example", 10.0, ProductUnit.PER_DATASET, "1"))
        ]

        Offer offer = new Offer.Builder(customerWithAllAffiliations, projectManager, "Awesome Project", "An " +
                "awesome project", items, externalAffiliation).build()

        when:
        double overhead = offer.getOverheadSum()
        double taxes = offer.getTaxCosts()
        double totalCosts = offer.getTotalCosts()
        double netSum = offer.getTotalNetPrice()

        then:
        double expectedNetSum = (10.0 + (2 * 1.0))
        double expectedOverhead = 2 * 1.0 * 0.4
        double expectedTaxes = (expectedNetSum + expectedOverhead) * 0.19
        offer.items.size() == 2
        netSum == expectedNetSum
        overhead == expectedOverhead
        taxes == expectedTaxes

        totalCosts == (double) expectedNetSum + expectedOverhead + expectedTaxes
    }

    def "On data storage and project management service, no overheads shall be calculated"() {
        given:
        List<ProductItem> items = [
                new ProductItem(1, new ProjectManagement("Basic Management",
                        "Just an example", 10.0, ProductUnit.PER_DATASET, "1")),
                new ProductItem(1, new DataStorage("Basic Management",
                        "Just an example", 10.0, ProductUnit.PER_GIGABYTE, "1"))

        ]

        Offer offer = new Offer.Builder(customerWithAllAffiliations, projectManager, "Awesome Project", "An " +
                "awesome project", items, externalAffiliation).build()

        when:
        double overhead = offer.getOverheadSum()

        then:
        overhead == 0
    }

    def "Different offer with updated item list can be differentiated"(){
        given:
        List<ProductItem> items = [
                new ProductItem(2, new PrimaryAnalysis("Basic RNAsq", "Just an" +
                        " example", 1.0, ProductUnit.PER_SAMPLE,"1")),
                new ProductItem(1, new ProjectManagement("Basic Management",
                        "Just an example", 10.0, ProductUnit.PER_DATASET,"1"))
        ]

        Offer offer = new Offer.Builder(customerWithAllAffiliations, projectManager, "Awesome Project", "An " +
                "awesome project", items, externalAffiliation).build()

        List<ProductItem> items2 = [
                new ProductItem(10, new PrimaryAnalysis("Basic RNAsq", "Just an" +
                        " example", 1.0, ProductUnit.PER_SAMPLE,"1")),
                new ProductItem(5, new ProjectManagement("Basic Management",
                        "Just an example", 10.0, ProductUnit.PER_DATASET,"1"))
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
                        " example", 1.0, ProductUnit.PER_SAMPLE,"1")),
                new ProductItem(1, new ProjectManagement("Basic Management",
                        "Just an example", 10.0, ProductUnit.PER_DATASET,"1"))
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
                        " example", 1.0, ProductUnit.PER_SAMPLE,"1")),
                new ProductItem(1, new ProjectManagement("Basic Management",
                        "Just an example", 10.0, ProductUnit.PER_DATASET,"1"))
        ]

        Offer offer = new Offer.Builder(customerWithAllAffiliations, projectManager, "Awesome Project", "An " +
                "awesome project", items, externalAffiliation).build()

        List<ProductItem> items2 = [
                new ProductItem(2, new PrimaryAnalysis("Basic RNAsq", "Just an" +
                        " example", 1.0, ProductUnit.PER_SAMPLE,"1")),
                new ProductItem(1, new ProjectManagement("Basic Management",
                        "Just an example", 10.0, ProductUnit.PER_DATASET,"1"))
        ]

        Offer offer2 = new Offer.Builder(customerWithAllAffiliations, projectManager, "Awesome Project", "An " +
                "awesome project", items2, internalAffiliation).build()

        when:
        def res = offer.equals(offer2)

        then:
        !res
    }

    def "An Offer will provide methods to distinct between ProductItems associated with overhead costs and calculate their net sum"() {
        given:
        ProductItem primaryAnalysis = new ProductItem(2, new PrimaryAnalysis("Basic RNAsq", "Just an" +
                " example primary analysis", 1.0, ProductUnit.PER_SAMPLE, "1"))
        ProductItem secondaryAnalysis = new ProductItem(1, new SecondaryAnalysis("Basic RNAsq", "Just an" +
                " example secondary analysis", 2.0, ProductUnit.PER_SAMPLE, "1"))
        ProductItem sequencing = new ProductItem(3, new Sequencing("Basic Sequencing", "Just an" +
                "example sequencing", 3.0, ProductUnit.PER_SAMPLE, "1"))
        ProductItem projectManagement = new ProductItem(1, new ProjectManagement("Basic Management",
                "Just an example", 10.0, ProductUnit.PER_DATASET, "1"))
        ProductItem dataStorage = new ProductItem(2, new DataStorage("Data Storage",
                "Just an example", 20.0, ProductUnit.PER_DATASET, "1"))

        List<ProductItem> items = [primaryAnalysis, projectManagement, sequencing, dataStorage, secondaryAnalysis]
        Offer offer = new Offer.Builder(customerWithAllAffiliations, projectManager, "Awesome Project", "An " +
                "awesome project", items, externalAffiliation).build()


        when:
        List<ProductItem> itemsWithoutOverhead = offer.getNoOverheadItems()
        List<ProductItem> itemsWithOverhead = offer.getOverheadItems()
        double itemsWithoutOverheadNetPrice = offer.getNoOverheadItemsNet()
        double itemsWithOverheadNetPrice = offer.getOverheadItemsNet()

        then:

        double expectedItemsWithOverheadNetPrice = 2 * 1.0 + 1 * 2.0 + 3 * 3.0
        double expectedItemsWithoutOverheadNetPrice = 1 * 10 + 2 * 20
        List<ProductItem> expectedItemsWithOverhead = [primaryAnalysis, sequencing, secondaryAnalysis]
        List<ProductItem> expectedItemsWithoutOverhead = [projectManagement, dataStorage]
        itemsWithOverhead == expectedItemsWithOverhead
        itemsWithoutOverhead == expectedItemsWithoutOverhead
        expectedItemsWithOverheadNetPrice == itemsWithOverheadNetPrice
        expectedItemsWithoutOverheadNetPrice == itemsWithoutOverheadNetPrice

    }
}
