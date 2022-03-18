package life.qbic.business.products

import life.qbic.business.offers.OfferV2
import life.qbic.business.offers.identifier.OfferId
import life.qbic.business.persons.affiliation.Affiliation
import life.qbic.business.persons.affiliation.AffiliationCategory
import spock.lang.Shared
import spock.lang.Specification

/**
 * <b>Unit tests for the {@link ProductItem} class</b>
 *
 * @since 1.3.0
 */
class ProductItemSpec extends Specification {

    @Shared
    private Affiliation internalAffiliation
    @Shared
    private ProductItem testItem
    @Shared
    Affiliation externalAcademicAffiliation
    @Shared
    Affiliation externalAffiliation

    void setupSpec() {
        createTestAffiliations()
        createItem()
        createProducts()
    }

    // Unit price determination
    def "Given an internal affiliation, an item shall reflect the product's unit price for internal customers"() {
        given: "An offer with selected internal affiliation"
        def offer = new OfferV2(internalAffiliation, new OfferId("test", "abcd", 1))

        and: "The product item belongs to this offer"
        testItem.setOffer(offer)

        when: "We access the unit price"
        BigDecimal unitPrice = testItem.getUnitListPrice()

        then: "We receive the internal unit price"
        unitPrice == testItem.getProduct().internalUnitPrice
    }

    def "Given an external academic affiliation, an item shall reflect the product's unit price for external customers"() {
        given: "An offer with selected external academic affiliation"
        def offer = new OfferV2(externalAcademicAffiliation, new OfferId("test", "abcd", 1))

        and: "The product item belongs to this offer"
        testItem.setOffer(offer)

        when: "We access the unit price"
        double unitPrice = testItem.getUnitListPrice()

        then: "We receive the internal unit price"
        unitPrice == testItem.getProduct().externalUnitPrice
    }

    def "Given an external affiliation, an item shall reflect the product's unit price for external customers"() {
        given: "An offer with selected external academic affiliation"
        def offer = new OfferV2(externalAffiliation, new OfferId("test", "abcd", 1))

        and: "The product item belongs to this offer"
        testItem.setOffer(offer)

        when: "We access the unit price"
        double unitPrice = testItem.getUnitListPrice()

        then: "We receive the internal unit price"
        unitPrice == testItem.getProduct().externalUnitPrice
    }

    // Discounting
    def "Given a product is a primary bioinformatics service, apply quantity discount to the unit price for every affiliation" () {
        given: "An offer for an internal customer"
        def product = new Product()
        product.setInternalUnitPrice(20.0)
        product.setExternalUnitPrice(40.0)
        product.setCategory("Primary Bioinformatics")
        def item = new ProductItem(product, 20.0)

        and: "A product item with a data analysis product"
        def offer = new OfferV2(affiliation as Affiliation, new OfferId("test", "abcd", 1))
        item.setOffer(offer)

        when: "We request the discount"
        double discountedUnitPrice = item.getUnitPriceDiscount()

        then: "The discounted unit price shall be smaller that the unit list price"
        discountedUnitPrice < item.getUnitListPrice()
        item.hasDiscount()

        where:
        affiliation << [internalAffiliation, externalAcademicAffiliation, externalAffiliation]
    }

    def "Given a product is neither a primary bioinformatics analysis service nor data storage , apply no discount to the unit price" () {
        given: "A product item with a product that is not a Primary Bioinformatics or Data Storage service"
        def product = new Product()
        product.setInternalUnitPrice(20.0)
        product.setExternalUnitPrice(40.0)
        product.setCategory(productCategory as String)
        def item = new ProductItem(product, 20.0)

        and: "A product item"
        def offer = new OfferV2(affiliation as Affiliation, new OfferId("test", "abcd", 1))
        item.setOffer(offer)

        when:
        BigDecimal discountedUnitPrice = item.getUnitPriceDiscount()

        then:
        discountedUnitPrice == 0
        !item.hasDiscount()

        where:
        [productCategory, affiliation] << [["Metabolomics", "Proteomics", "Sequencing"],
                                           [internalAffiliation, externalAcademicAffiliation, externalAffiliation]].combinations()

    }

    def "Given any discount has been applied to a product item, the request for the discount percentage returns the relative unit price discount"() {
        given: "A product with quantity discount"
        def product = new Product()
        product.internalUnitPrice = 10.0
        product.externalUnitPrice = 20.0
        product.category = "Primary Bioinformatics"
        def item = new ProductItem(product, 10.0)
        def offer = new OfferV2(internalAffiliation, new OfferId("test", "abc", 1))
        item.setOffer(offer)

        expect:
        item.getRelativeDiscount() > 0.0

    }

    // DATA STORAGE DISCOUNT
    def "Given a product is a data storage service and the customer is internal, apply 100% discount to the unit price"() {
        given: "A data storage service product item"
        def product = new Product()
        product.setCategory("Data Storage")
        product.setInternalUnitPrice(20.0)
        def item = new ProductItem(product, 20.0)

        and: "An offer for an internal customer"
        def offer = new OfferV2(internalAffiliation, new OfferId("test", "abcd", 1))
        item.setOffer(offer)

        expect: "a 100% discount shall be applied"
        item.unitPriceDiscount == item.unitListPrice
        item.hasDiscount()
    }

    def "Given a product is a data storage service and the customer is external or external academic, apply 0% discount to the unit price"() {
        given: "A data storage service product item"
        def product = new Product()
        product.setCategory("Data Storage")
        product.setExternalUnitPrice(40.0)
        def item = new ProductItem(product, 20.0)

        and: "An offer for an external customer"
        def offer = new OfferV2(affiliation, new OfferId("test", "abcd", 1))
        item.setOffer(offer)

        expect: "No discount shall be applied"
        item.unitPriceDiscount == 0
        !item.hasDiscount()

        where:
        affiliation << [externalAcademicAffiliation, externalAffiliation]
    }

    def "expect the data storage discount is 0 for external and external academic affiliations"() {
        expect: "the data storage discount is 0 for external and external academic"
        ProductItem.calculateDataStorageDiscount(AffiliationCategory.EXTERNAL_ACADEMIC,
                "Data Storage",
                unitPrice as BigDecimal) == BigDecimal.ZERO

        ProductItem.calculateDataStorageDiscount(AffiliationCategory.EXTERNAL,
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
        ProductItem.calculateDataStorageDiscount(AffiliationCategory.INTERNAL,
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
        ProductItem.calculateDataStorageDiscount(AffiliationCategory.INTERNAL,
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
    }
    // QUANTITY DISCOUNT
    def "expect the quantity discount is 0 for #productCategory products"() {
        expect:
        ProductItem.calculateQuantityDiscount(unitPrice, quantity, productCategory) == BigDecimal.ZERO
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
        ProductItem.calculateQuantityDiscount(unitPrice, quantity, "Primary Bioinformatics") == expectedResult
        ProductItem.calculateQuantityDiscount(unitPrice, quantity, "Secondary Bioinformatics") == expectedResult
        where:
        unitPrice | quantity | expectedResult
        100.33    | 33.0     | 57.19 //57.1881
        33.34     | 44.0     | 20.34 // 20.3374
        10.0      | 10000.0  | 8.20 // 8.20
    }

    def "expect the quantity discount to be rounded up 2 digits after the comma"() {
        expect: "the quantity discount is computed"
        ProductItem.calculateQuantityDiscount(unitPrice, quantity, "Primary Bioinformatics")
                == expectedResult
        where:
        unitPrice | quantity | expectedResult
        100.33    | 33.0     | 57.19 //57.1881
        33.34     | 44.0     | 20.34 // 20.3374
        10.0      | 10000.0  | 8.20 // 8.20
    }

    // ITEM NET
    def "expect the item net to be (#unitPrice - #unitDiscount) * #quantity = #expectedItemNet"() {
        given:
        def product = new Product()
        product.externalUnitPrice = unitPrice.doubleValue()
        product.internalUnitPrice = unitPrice.doubleValue()
        product.category = "Primary Bioinformatics"
        def item = new ProductItem(product, quantity)

        and:
        def offer = new OfferV2(affiliation, new OfferId("test", "abc", 1))
        item.setOffer(offer)

        expect:
        item.getItemTotalPrice() == expectedItemNet

        where:
        unitPrice | quantity | unitDiscount | affiliation
        100.33    | 33.0     | 57.19        | internalAffiliation
        33.34     | 44.0     | 20.34        | externalAcademicAffiliation
        10.0      | 10000.0  | 8.20         | externalAffiliation

        expectedItemNet = (unitPrice - unitDiscount) * quantity

    }

    void createTestAffiliations() {
        internalAffiliation = new Affiliation("Test orga", "", "Funnystreet", "72070", "Tuebingen", "Germany", AffiliationCategory.INTERNAL)
        externalAcademicAffiliation = new Affiliation("Test orga", "", "Funnystreet", "72070", "Tuebingen", "Germany", AffiliationCategory.EXTERNAL_ACADEMIC)
        externalAffiliation = new Affiliation("Test orga", "", "Funnystreet", "72070", "Tuebingen", "Germany", AffiliationCategory.EXTERNAL)
    }

    void createItem() {
        Product product = new Product()
        product.internalUnitPrice = 20.0
        product.externalUnitPrice = 40.0
        product.setCategory("Primary Bioinformatics")
        testItem = new ProductItem(product, 10)
    }

    void createProducts() {
        Product product = new Product()
        product.internalUnitPrice = 20.0
        product.externalUnitPrice = 40.0
        product.setCategory("Primary Bioinformatics")
    }
}
