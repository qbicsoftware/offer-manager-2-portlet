package life.qbic.business.products


import life.qbic.business.offers.OfferV2
import life.qbic.business.persons.affiliation.Affiliation
import life.qbic.business.persons.affiliation.AffiliationCategory
import spock.lang.Specification

/**
 * <b>Unit tests for the {@link ProductItem} class</b>
 *
 * @since 1.3.0
 */
class ProductItemSpec extends Specification {

    private Affiliation internalAffiliation

    private ProductItem testItem

    void setup() {
        createTestAffiliations()
        createItem()
    }

    def "Given an internal affiliation, an item shall reflect the product's unit price for internal customers"() {
        given: "An offer with selected internal affiliation"
        OfferV2 offer = Stub(OfferV2.class)
        offer.getSelectedCustomerAffiliation() >> internalAffiliation

        and: "The product item belongs to this offer"
        testItem.setOffer(offer)

        when: "We access the unit price"
        double unitPrice = testItem.getUnitPrice()

        then: "We receive the internal unit price"
        unitPrice == testItem.getProduct().internalUnitPrice
    }

    def "Given an external academic affiliation, an item shall reflect the product's unit price for external customers"() {

    }

    def "Given an external affiliation, an item shall reflect the product's unit price for external customers"() {

    }

    def "Given a product is a data analysis service, apply quantity discount to the unit price" () {

    }

    def "Given a product is a data analysis service, apply no quantity discount to the unit price" () {

    }

    def "Given a product is a data storage service and the customer is internal, apply 100% discount to the unit price"() {

    }

    def "Given a product is a data storage service and the customer is external or external academic, apply 0% discount to the unit price"() {

    }

    // DATA STORAGE DISCOUNT
    /*def "expect the data storage discount is 0 for external and external academic affiliations"() {
        expect: "the data storage discount is 0 for external and external academic"
        calculateDataStorageDiscount(AffiliationCategory.EXTERNAL_ACADEMIC,
                "Data Storage",
                unitPrice as BigDecimal) == BigDecimal.ZERO

        calculateDataStorageDiscount(AffiliationCategory.EXTERNAL,
                "Data Storage",
                unitPrice as BigDecimal) == BigDecimal.ZERO

        where: "the product category, unit price and quantity are as follows"
        unitPrice | quantity
        10        | 10
        20        | 30.33
        0         | 0
        0.33      | 100
    }

    def "expect the data storage discount is 100% for internal affiliation and data storage product"() {
        expect: "the data storage discount is 0 for external and external academic"
        calculateDataStorageDiscount(AffiliationCategory.INTERNAL,
                "Data Storage",
                unitPrice as BigDecimal) == expectedPrice

        where: "the product category, unit price and quantity are as follows"
        unitPrice | quantity | expectedPrice
        10        | 10       | 10.0
        20        | 30.33    | 20.0
        0         | 0        | 0.00
        0.33      | 100      | 0.33
    }

    def "expect the data storage discount is 0 for #productCategory products"() {
        expect:
        calculateDataStorageDiscount(AffiliationCategory.INTERNAL,
                productCategory,
                unitPrice as BigDecimal) == BigDecimal.ZERO

        where: "the unit price and quantity are as follows"
        unitPrice = 30.33
        quantity = 94
        and: "the product category is one of"
        productCategory << ["Sequencing",
                            "Proteomics",
                            "Metabolomics",
                            "Primary Bioinformatics",
                            "Secondary Bioinformatics",
                            "External Service"]
    }*/
    // QUANTITY DISCOUNT
    /*def "expect the quantity discount is 0 for #productCategory products"() {
        expect:
        calculateQuantityDiscount(unitPrice, quantity, productCategory) == BigDecimal.ZERO
        where:
        unitPrice = 33.34
        quantity = 44.0

        and:
        productCategory << ["Sequencing",
                            "Proteomics",
                            "Metabolomics",
                            "Data Storage",
                            "External Service"]
    }

    def "expect the quantity discount is #expectedResult for primary and secondary bioinformatics products"() {
        expect:
        calculateQuantityDiscount(unitPrice, quantity, "Primary Bioinformatics") == expectedResult
        calculateQuantityDiscount(unitPrice, quantity, "Secondary Bioinformatics") == expectedResult
        where:
        unitPrice | quantity | expectedResult
        100.33    | 33.0     | 57.19 //57.1881
        33.34     | 44.0     | 20.34 // 20.3374
        10.0      | 10000.0  | 8.20 // 8.20
    }

    def "expect the quantity discount to be rounded up 2 digits after the comma"() {
        expect: "the quantity discount is computed"
        calculateQuantityDiscount(unitPrice, quantity, "Primary Bioinformatics")
                == expectedResult
        where:
        unitPrice | quantity | expectedResult
        100.33    | 33.0     | 57.19 //57.1881
        33.34     | 44.0     | 20.34 // 20.3374
        10.0      | 10000.0  | 8.20 // 8.20
    }*/
    // ITEM DISCOUNT
    /*def "expect the item discount is the maximum of quantity and data storage discount for data storage products"() {
        expect:
        calculateUnitDiscount(productCategory as String,
                affiliationCategory as AffiliationCategory,
                unitPrice as BigDecimal,
                quantity as BigDecimal) == expectedResult
        where:
        [productCategory, affiliationCategory, unitPrice, quantity] << GroovyCollections.combinations(
                ["Data Storage", "Primary Bioinformatics", "Project Management"],
                AffiliationCategory.values(),
                [0, 33.33, 44, 100],
                [0.1, 0.5, 1, 7, 33, 100, 1000])
        and:
        dataStorageDiscount = calculateDataStorageDiscount(affiliationCategory as AffiliationCategory,
                productCategory as String,
                unitPrice as BigDecimal)
        quantityDiscount = calculateQuantityDiscount(unitPrice as BigDecimal,
                quantity as BigDecimal,
                productCategory as String)
        expectedResult = dataStorageDiscount.max(quantityDiscount)
    }*/
    // ITEM DISCOUNT USING OFFERv2
    /*def "when an item is accounted for, then the correct discount is applied: #productCategory: #quantity * #unitPrice = #expectedDiscount"() {
        given: "an offer with at least one item"
        def offer = new OfferV2()
        offer.setSelectedCustomerAffiliation(setupInternalAffiliation())
        offer.setItems([item])
        when: "the offer is processed"
        def processedOffer = withGroupedOfferItems(offer)
        OfferItem offerItem = processedOffer.getDataAnalysisItems().first()
        then: "the discount per unit of that item is equal to the expected discount"
        offerItem.unitDiscount == expectedDiscount as double
        and: "the total discount for that item is equal to the product of unit discount and quantity"
        offerItem.itemDiscount == expectedTotalDiscount as double

        where: "the quantity and unit price are as follows"
        quantity | unitPrice
        10.0     | 10.0
        20.0     | 0.34
        10.0     | 0.67
        4.53     | 1004.45

        item = createDataAnalysisProductItem(quantity, unitPrice)
        productCategory = item.getProduct().getCategory()

        and: "the expected discount is the item discount for that item"
        expectedDiscount = calculateUnitDiscount(
                item.getProduct().getCategory(),
                AffiliationCategory.INTERNAL,
                unitPrice,
                quantity)
        expectedTotalDiscount = (BigDecimal.valueOf(expectedDiscount) * BigDecimal.valueOf(quantity))
                .setScale(2, RoundingMode.HALF_UP)

    }*/
    // ITEM NET
    /*def "expect the item net to be (#unitPrice - #unitDiscount) * #quantity = #expectedItemNet"() {
        expect:
        itemNetPrice(unitPrice, quantity, unitDiscount) == expectedItemNet

        where:
        unitPrice | quantity | unitDiscount
        100.33    | 33.0     | 57.19
        33.34     | 44.0     | 20.34
        10.0      | 10000.0  | 8.20
        100.33    | 33.5     | 100.33

        expectedItemNet = (unitPrice - unitDiscount) * quantity
    }*/

    void createTestAffiliations() {
        internalAffiliation = new Affiliation("Test orga", "", "Funnystreet", "72070", "Tuebingen", "Germany", AffiliationCategory.INTERNAL)
    }

    void createItem() {
        Product product = new Product()
        product.internalUnitPrice = 20.0
        product.externalUnitPrice = 40.0
        testItem = new ProductItem(product, 10)
    }
}
