package life.qbic.business.offers

import life.qbic.business.persons.affiliation.Affiliation
import life.qbic.business.persons.affiliation.AffiliationCategory
import life.qbic.business.products.Product
import life.qbic.business.products.ProductItem
import spock.lang.Specification

/**
 * <b>short description</b>
 *
 * <p>detailed description</p>
 *
 * @since <version tag>
 */
class OfferCalculusSpec extends Specification {
  OfferCalculus offerCalculus = new OfferCalculus()

  static final Affiliation internalAffiliation = setupInternalAffiliation()
  static final Affiliation externalAffiliation = setupExternalAffiliation()
  static final Affiliation externalAcademicAffiliation = setupExternalAcademicAffiliation()

  static final Product dataAnalysisProduct = createDataAnalysisProduct()
  static final Product dataStorageProduct = createDataStorageProduct()

  def "overhead ratio is #expectedRatio for customer affiliation with category #category"() {
    given: "an offer with the respective customer affiliation"
    OfferV2 offer = new OfferV2()
    offer.setSelectedCustomerAffiliation(new Affiliation("organization", "addressAddition", "street", "postalCode", "city", "country", category))
    when: "the calculus determines the overhead ratio for that offer"
    def ratio = OfferCalculus.overheadRatio(category)
    then: "the overhead ratio is #expectedRatio"
    ratio == expectedRatio
    where:
    category                              | expectedRatio
    AffiliationCategory.INTERNAL          | 0.0
    AffiliationCategory.EXTERNAL_ACADEMIC | 0.2
    AffiliationCategory.EXTERNAL          | 0.4
  }

  def "when the offers product items are converted to offer items, then every product item is represented in an offer item"() {
    given: "an offer with items"
    def item1 = new ProductItem(dataAnalysisProduct, 4.5, 0.0, 0.0)
    def productItems = [item1]
    OfferV2 offer = new OfferV2()
    offer.setItems(productItems)
    and: "and a customer affiliation selected"
    offer.setSelectedCustomerAffiliation(affiliation)
    when: "the offers product items are converted to offer items"
    def offerItems = offerCalculus.createOfferItems(offer)
    then: "every product item is represented in an offer item"
    !offerItems.isEmpty()
    offerItems.stream()
            .filter(offerItem ->
                    productItems.stream()
                            .anyMatch(it -> equals(it, offerItem))
            )
            .findAny()
            .isPresent()
    offerItems.stream().forEach(System.out::println)
    where:
    affiliation << [internalAffiliation, externalAcademicAffiliation, externalAffiliation]
  }

  // ## ITEMS
  //TODO test unit price determined by affiliation
  def "when the offer's product items are converted to offer items, then the correct unit price is used based on the affiliation category"() {
    given:
    def item = new ProductItem(dataAnalysisProduct, 4.5, 0.0, 0.0)
    def dsItem = new ProductItem(dataStorageProduct, 4.5, 0.0, 0.0)
    def productItems = [item, dsItem]
    OfferV2 offer = new OfferV2()
    offer.setItems(productItems)
    offer.setSelectedCustomerAffiliation(affiliation)

    when: "the offer's product items are converted to offer items"
    def offerItems = offerCalculus.createOfferItems(offer)

    then: "the correct unit price is used based on the affiliation category"
    offerItems.each { it.getUnitPrice() == expectedPrice }

    where: "the affiliation category is #affiliationCategory"
    affiliation                 | expectedPrice
    internalAffiliation         | dataAnalysisProduct.getInternalUnitPrice()
    externalAcademicAffiliation | dataAnalysisProduct.getExternalUnitPrice()
    externalAffiliation         | dataAnalysisProduct.getExternalUnitPrice()
  }

  // test discount factor determined by quantity
  // test quantity discount determined by discount factor, quantity and unit price
  // covered by QuantityDiscountSpec.groovy

  //TODO test quantity discount is rounded up
  def "when the quantity discount is computed, then returned discount is rounded up 2 digits after the comma"() {
    when: "the quantity discount is computed"
    def result = OfferCalculus.applyQuantityDiscount(unitPrice as BigDecimal, quantity as Integer)
    then: "returned discount is rounded up 2 digits after the comma"
    result == expectedResult

    where:
    unitPrice  | quantity | expectedResult
    100.33     | 33       | 57.19 //57.1881
    33.3333339 | 44       | 20.34 // 20.333333679
    10         | 10000    | 8.20 // 8.20
  }



  //TODO test item DS discount determined by quantity, item group, affiliation, unitPrice
  def "when a data storage item is accounted for an customer with internal affiliation, give 100% discount for this item"() {
    given: "an offer with at least one data storage service item"
    def offer = new OfferV2()
    offer.setSelectedCustomerAffiliation(internalAffiliation)
    offer.setItems([createDataStorageProductItem(quantityA, unitPriceA)])

    when:
    def processedOffer = OfferCalculus.groupItems(offer)

    then:
    processedOffer.getDataManagementItems()

    where:
    quantityA | unitPriceA | expectedDiscountA | expectedDiscountB
    10        | 10.0       | 100.0             | 400.0
    200       | 0.1        | 20.0              | 6.67 // 6.666
  }


  //TODO test item discount determined by quantity discount and ds discount

  //TODO test item discount per unit determined by item discount and quantity

  //TODO test item net price determined by item discount, quantity and unit price

  // ## Groups

  // ## Offer
  //TODO test overhead ratio determined by affiliation

  //TODO test VAT ratio determined by affiliation


  boolean equals(ProductItem productItem, OfferItem offerItem) {
    // check for product properties
    if (!(productItem.getProduct().getCategory().equals(offerItem.getCategory()))) {
      return false
    }
    if (!productItem.getProduct().getDescription().equals(offerItem.getProductDescription())) {
      return false
    }
    if (!productItem.getProduct().getProductName().equals(offerItem.getProductName())) {
      return false
    }
    if (!productItem.getProduct().serviceProvider.equals(offerItem.getServiceProvider())) {
      return false
    }
    if (!productItem.getProduct().getUnit().equals(offerItem.getUnit())) {
      return false
    }

    return productItem.getQuantity().equals(offerItem.getQuantity())
  }

  static Product createDataAnalysisProduct() {
    def daProduct = new Product()
    daProduct.setCategory("Primary Bioinformatics")
    daProduct.setActive(true)
    daProduct.setDescription("This is a primary bioinformatics product")
    daProduct.setExternalUnitPrice(0.25)
    daProduct.setInternalUnitPrice(0.5)
    daProduct.setProductId("PB_1")
    daProduct.setProductName("Awesome Bioinformatics")
    daProduct.setUnit("Brain Cell")
    daProduct.setServiceProvider("Bioinformatiker Model Z")
    return daProduct
  }

  static Product createDataStorageProduct() {
    def daProduct = new Product()
    daProduct.setCategory("Data Storage")
    daProduct.setActive(true)
    daProduct.setDescription("This is a primary bioinformatics product")
    daProduct.setExternalUnitPrice(0.25)
    daProduct.setInternalUnitPrice(0.5)
    daProduct.setProductId("PB_1")
    daProduct.setProductName("Awesome Bioinformatics")
    daProduct.setUnit("Brain Cell")
    daProduct.setServiceProvider("Bioinformatiker Model Z")
    return daProduct
  }

  static Product createDataStorageProduct(double unitPrice) {
    def daProduct = createDataStorageProduct()
    daProduct.setExternalUnitPrice(unitPrice)
    daProduct.setInternalUnitPrice(unitPrice)
    return daProduct
  }

  static ProductItem createDataStorageProductItem(double quantity, double unitPrice) {
    return new ProductItem(createDataStorageProduct(unitPrice), quantity, BigDecimal.ZERO, BigDecimal.ZERO)
  }

  static Affiliation setupInternalAffiliation() {
    new Affiliation("University of Tübingen",
            "",
            "Auf der Morgenstelle",
            "72076",
            "Tübingen",
            "Germany",
            AffiliationCategory.INTERNAL)
  }

  static Affiliation setupExternalAffiliation() {
    new Affiliation("Last Resort Tübingen",
            "",
            "Somewhere else",
            "72076",
            "Tübingen",
            "Germany",
            AffiliationCategory.EXTERNAL)
  }

  static Affiliation setupExternalAcademicAffiliation() {
    new Affiliation("University of Stuttgart",
            "Some Department",
            "Auf der Morgenstelle",
            "73210",
            "Stuttgart",
            "Germany",
            AffiliationCategory.EXTERNAL_ACADEMIC)
  }
}
