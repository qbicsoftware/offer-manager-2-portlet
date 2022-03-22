package life.qbic.business.products

import life.qbic.business.offers.OfferV2
import life.qbic.business.offers.identifier.OfferId
import life.qbic.business.persons.affiliation.Affiliation
import life.qbic.business.persons.affiliation.AffiliationCategory
import spock.lang.Specification
/**
 * <b>Unit tests for the {@link ProductItem} class</b>
 *
 * @since 1.3.0
 */
class ProductItemSpec extends Specification {

  private static final Affiliation internalAffiliation = createAffiliation(AffiliationCategory.INTERNAL)
  private static final Affiliation externalAcademicAffiliation = createAffiliation(AffiliationCategory.EXTERNAL_ACADEMIC)
  private static final Affiliation externalAffiliation = createAffiliation(AffiliationCategory.EXTERNAL)

  def "Given an internal affiliation, an item shall reflect the product's unit price for internal customers"() {
    given: "An offer with selected internal affiliation"
    def offer = new OfferV2(internalAffiliation, new OfferId("test", "abcd", 1))

    and: "The product item belongs to this offer"
    ProductItem testItem = createItem()
    testItem.setOffer(offer)

    when: "We access the unit price"
    BigDecimal unitPrice = testItem.getUnitPrice()

    then: "We receive the internal unit price"
    unitPrice == testItem.getProduct().internalUnitPrice
  }

  // Unit price determination
  def "Given an external academic affiliation, an item shall reflect the product's unit price for external customers"() {
    given: "An offer with selected external academic affiliation"
    def offer = new OfferV2(externalAcademicAffiliation, new OfferId("test", "abcd", 1))

    and: "The product item belongs to this offer"
    ProductItem testItem = createItem()
    testItem.setOffer(offer)

    when: "We access the unit price"
    double unitPrice = testItem.getUnitPrice()

    then: "We receive the internal unit price"
    unitPrice == testItem.getProduct().externalUnitPrice
  }

  def "Given an external affiliation, an item shall reflect the product's unit price for external customers"() {
    given: "An offer with selected external academic affiliation"
    def offer = new OfferV2(externalAffiliation, new OfferId("test", "abcd", 1))

    and: "The product item belongs to this offer"
    ProductItem testItem = createItem()
    testItem.setOffer(offer)

    when: "We access the unit price"
    double unitPrice = testItem.getUnitPrice()

    then: "We receive the internal unit price"
    unitPrice == testItem.getProduct().externalUnitPrice
  }

  def "Given a product is a primary bioinformatics service, apply quantity discount to the unit price for every affiliation"() {
    given: "An offer for an internal customer"
    def product = new Product()
    product.setInternalUnitPrice(20.0)
    product.setExternalUnitPrice(40.0)
    product.setCategory("Primary Bioinformatics")
    def item = new ProductItem()
    item.setProduct(product)
    item.setQuantity(20.0)

    and: "A product item with a data analysis product"
    def offer = new OfferV2(affiliation as Affiliation, new OfferId("test", "abcd", 1))
    item.setOffer(offer)

    when: "We request the discount"
    double discountedUnitPrice = item.getUnitDiscountAmount()

    then: "The discounted unit price shall be smaller that the unit list price"
    discountedUnitPrice < item.getUnitPrice()
    item.hasDiscount()

    where:
    affiliation << [internalAffiliation, externalAcademicAffiliation, externalAffiliation]
  }

  // Discounting
  def "Given a product is neither a primary bioinformatics analysis service nor data storage , apply no discount to the unit price"() {
    given: "A product item with a product that is not a Primary Bioinformatics or Data Storage service"
    def product = new Product()
    product.setInternalUnitPrice(20.0)
    product.setExternalUnitPrice(40.0)
    product.setCategory(productCategory as String)
    def item = new ProductItem()
    item.setProduct(product)
    item.setQuantity(20.0)

    and: "A product item"
    def offer = new OfferV2(affiliation as Affiliation, new OfferId("test", "abcd", 1))
    item.setOffer(offer)

    when:
    BigDecimal discountedUnitPrice = item.getUnitDiscountAmount()

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
    def item = new ProductItem()
    item.setProduct(product)
    item.setQuantity(10.0)
    def offer = new OfferV2(internalAffiliation, new OfferId("test", "abc", 1))
    item.setOffer(offer)

    expect:
    item.getDiscountRate() > 0.0

  }

  def "Given a product is a data storage service and the customer is internal, apply 100% discount to the unit price"() {
    given: "A data storage service product item"
    def product = new Product()
    product.setCategory("Data Storage")
    product.setInternalUnitPrice(20.0)
    def item = new ProductItem()
    item.setProduct(product)
    item.setQuantity(20.0)

    and: "An offer for an internal customer"
    def offer = new OfferV2(internalAffiliation, new OfferId("test", "abcd", 1))
    item.setOffer(offer)

    expect: "a 100% discount shall be applied"
    item.unitDiscountAmount == item.unitPrice
    item.hasDiscount()
  }

  // DATA STORAGE DISCOUNT
  def "Given a product is a data storage service and the customer is external or external academic, apply 0% discount to the unit price"() {
    given: "A data storage service product item"
    def product = new Product()
    product.setCategory("Data Storage")
    product.setExternalUnitPrice(40.0)
    def item = new ProductItem()
    item.setProduct(product)
    item.setQuantity( 20.0)

    and: "An offer for an external customer"
    def offer = new OfferV2(affiliation, new OfferId("test", "abcd", 1))
    item.setOffer(offer)

    expect: "No discount shall be applied"
    item.unitDiscountAmount == 0
    !item.hasDiscount()

    where:
    affiliation << [externalAcademicAffiliation, externalAffiliation]
  }

  def "expect the data storage discount is 0 for external and external academic affiliations"() {
    expect: "the data storage discount is 0 for external and external academic"
    ProductItem.calculateDataStorageDiscountRate(AffiliationCategory.EXTERNAL_ACADEMIC,
            "Data Storage") == BigDecimal.ZERO

    ProductItem.calculateDataStorageDiscountRate(AffiliationCategory.EXTERNAL,
            "Data Storage") == BigDecimal.ZERO
  }

  def "expect the data storage discount is 100% for internal affiliation and data storage product"() {
    expect: "the data storage discount is 0 for external and external academic"
    ProductItem.calculateDataStorageDiscountRate(AffiliationCategory.INTERNAL, "Data Storage") == BigDecimal.ONE
  }

  def "expect the data storage discount is 0 for #productCategory products"() {
    expect:
    ProductItem.calculateDataStorageDiscountRate(AffiliationCategory.INTERNAL, productCategory) == BigDecimal.ZERO

    where: "the product category is one of"
    productCategory << ["Sequencing",
                        "Proteomics",
                        "Metabolomics",
                        "Primary Bioinformatics",
                        "Secondary Bioinformatics",
                        "External Service"]
  }

  def "expect the quantity discount is 0 for #productCategory products"() {
    expect:
    ProductItem.calculateQuantityDiscountRate(quantity, productCategory) == BigDecimal.ZERO
    where:
    quantity = 44.0

    and:
    productCategory << ["Sequencing",
                        "Proteomics",
                        "Metabolomics",
                        "Data Storage",
                        "External Service"]
  }
  // QUANTITY DISCOUNT
  def "expect the quantity discount is #expectedResult for primary and secondary bioinformatics products"() {
    expect:
    ProductItem.calculateQuantityDiscountRate(quantity, "Primary Bioinformatics") == expectedResult
    ProductItem.calculateQuantityDiscountRate(quantity, "Secondary Bioinformatics") == expectedResult
    where:
    quantity | expectedResult
    33.0     | 0.57
    44.0     | 0.61
    10000.0  | 0.82
  }

  def "expect the item sales price to be listPrice - discountAmount"() {
    given:
    def product = new Product()
    product.externalUnitPrice = unitPrice.doubleValue()
    product.internalUnitPrice = unitPrice.doubleValue()
    product.category = "Primary Bioinformatics"
    def item = new ProductItem()
    item.setProduct(product)
    item.setQuantity(quantity)

    and:
    def offer = new OfferV2(affiliation, new OfferId("test", "abc", 1))
    item.setOffer(offer)

    expect:
    item.getSalePrice() == item.getListPrice() - item.getDiscountAmount()

    where:
    unitPrice | quantity | affiliation
    1.01      | 22.01    | internalAffiliation
    33.34     | 44.0     | externalAcademicAffiliation
    10.0      | 10000.0  | externalAffiliation
  }

  static ProductItem createItem() {
    Product product = new Product()
    product.internalUnitPrice = 20.0
    product.externalUnitPrice = 40.0
    product.setCategory("Primary Bioinformatics")
    ProductItem item = new ProductItem()
    item.setProduct(product)
    item.setQuantity(10)
    return item
  }

  private static Affiliation createAffiliation(AffiliationCategory affiliationCategory) {
    return new Affiliation("Test orga", "", "Funnystreet", "72070", "Tuebingen", "Germany", affiliationCategory)
  }
}
