package life.qbic.business.offers

import life.qbic.business.offers.identifier.OfferId
import life.qbic.business.persons.affiliation.Affiliation
import life.qbic.business.persons.affiliation.AffiliationCategory
import life.qbic.business.products.Product
import life.qbic.business.products.ProductItem
import spock.lang.Ignore
import spock.lang.Specification

import java.math.RoundingMode

import static life.qbic.business.offers.OfferCalculus.*

/**
 * <b>short description</b>
 *
 * <p>detailed description</p>
 *
 * @since <version tag>
 */
@Ignore
class OfferCalculusSpec extends Specification {

  private static OfferV2 offer = new OfferV2(createAffiliation(AffiliationCategory.INTERNAL, "Germany"), new OfferId("test", 1))

  def "when the offers product items are converted to offer items, then every product item is represented in an offer item"() {
    given: "an offer with items"
    def item1 = new ProductItem(offer, createDataAnalysisProduct(), 4.5)
    def productItems = [item1]
    OfferV2 offer = new OfferV2()
    offer.setItems(productItems)
    and: "and a customer affiliation selected"
    offer.setSelectedCustomerAffiliation(affiliation)
    when: "the offers product items are converted to offer items"
    def ProductItems = createProductItems(offer)
    then: "every product item is represented in an offer item"
    !ProductItems.isEmpty()
    ProductItems.stream()
            .filter(ProductItem ->
                    productItems.stream()
                            .anyMatch(it -> equals(it, ProductItem))
            )
            .findAny()
            .isPresent()
    where:
    affiliation << [setupInternalAffiliation(), setupExternalAcademicAffiliation(), setupExternalAffiliation()]
  }

  def "when the offer's product items are converted to offer items, then the correct unit price is used based on the affiliation category"() {
    given:
    def item = new ProductItem(offer, createDataAnalysisProduct(), 4.5)
    def dsItem = new ProductItem(offer, createDataStorageProduct(), 4.5)
    def productItems = [item, dsItem]
    OfferV2 offer = new OfferV2()
    offer.setItems(productItems)
    offer.setSelectedCustomerAffiliation(affiliation)

    when: "the offer's product items are converted to offer items"
    def createdItems = createProductItems(offer)

    then: "the correct unit price is used based on the affiliation category"
    createdItems.each { it.getUnitPrice() == expectedPrice }

    where: "the affiliation category is #affiliationCategory"
    affiliation                        | expectedPrice
    setupInternalAffiliation()         | createDataAnalysisProduct().getInternalUnitPrice()
    setupExternalAcademicAffiliation() | createDataAnalysisProduct().getExternalUnitPrice()
    setupExternalAffiliation()         | createDataAnalysisProduct().getExternalUnitPrice()
  }


  //<editor-fold desc="Data Storage Discount">
  def "expect the data storage discount is 0 for external and external academic affiliations"() {
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
  }
  //</editor-fold>

  //<editor-fold desc="Quantity Discount">
  def "expect the quantity discount is 0 for #productCategory products"() {
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
  }
  //</editor-fold>

  //<editor-fold desc="Item Discount">
  def "expect the item discount is the maximum of quantity and data storage discount for data storage products"() {
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
  }
  //</editor-fold>

  //<editor-fold desc="Item discounted using offer creation">
  def "when an item is accounted for, then the correct discount is applied: #productCategory: #quantity * #unitPrice = #expectedDiscount"() {
    given: "an offer with at least one item"
    def offer = new OfferV2()
    offer.setSelectedCustomerAffiliation(setupInternalAffiliation())
    offer.setItems([item])
    when: "the offer is processed"
    def processedOffer = withGroupedProductItems(offer)
    ProductItem productItem = processedOffer.getDataAnalysisItems().first()
    then: "the discount per unit of that item is equal to the expected discount"
    productItem.getUnitDiscountAmount() == expectedDiscount
    and: "the total discount for that item is equal to the product of unit discount and quantity"
    productItem.getDiscountAmount() == expectedTotalDiscount

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

  }
  //</editor-fold>

  def "expect the item net to be (#unitPrice - #unitDiscount) * #quantity = #expectedItemNet"() {
    expect:
    itemNetPrice(unitPrice, quantity, unitDiscount) == expectedItemNet

    where:
    unitPrice | quantity | unitDiscount
    100.33    | 33.0     | 57.19
    33.34     | 44.0     | 20.34
    10.0      | 10000.0  | 8.20
    100.33    | 33.5     | 100.33

    expectedItemNet = (unitPrice - unitDiscount) * quantity
  }


  // ## Groups

  def "the group net is the sum of the item nets"() {
    given: "an offer with items"

    def dataGenerationProductItem = createDataGenerationProductItem(20.9, 5.6)
    def dataAnalysisProductItem = createDataAnalysisProductItem(20.9, 5.6)
    def dataManagementProductItem = createDataManagementProductItem(20.9, 5.6)
    def externalServicesProductItem = createExternalServicesProductItem(20.9, 5.6)

    OfferV2 offerWithItems = createFilledOffer(
            [
                    dataGenerationProductItem,
                    dataAnalysisProductItem,
                    dataManagementProductItem,
                    externalServicesProductItem
            ], affiliation)
    when: "the net prices are recomputed"
    OfferV2 offerWithNet = withNetPrices(offerWithItems)
    then: "the computed group net prices are equal to the sum of the item nets in those groups"
    offerWithNet.getDataGenerationSalePrice() == itemNetPrice(dataGenerationProductItem)
    offerWithNet.getDataAnalysisSalePrice() == itemNetPrice(dataAnalysisProductItem)
    offerWithNet.getDataManagementSalePrice() == itemNetPrice(dataManagementProductItem)
    offerWithNet.getExternalServiceSalePrice() == itemNetPrice(externalServicesProductItem)
    and: "the offer net is the sum of all of its item's net prices"
    offerWithNet.getSalePrice() == offerWithNet.getDataGenerationSalePrice()
            .add(offerWithNet.getDataAnalysisSalePrice())
            .add(offerWithNet.getDataManagementSalePrice())
            .add(offerWithNet.getExternalServiceSalePrice())

    where: "the affiliation category varies"
    affiliationCategory << AffiliationCategory.values()
    affiliation = createAffiliation(affiliationCategory as AffiliationCategory, "Germany")
  }

  def "the offer net is the sum of the group nets"() {

    def dataGenerationProductItem = createDataGenerationProductItem(20.9, 5.6)
    def dataAnalysisProductItem = createDataAnalysisProductItem(20.9, 5.6)
    def dataManagementProductItem = createDataManagementProductItem(20.9, 5.6)
    def externalServicesProductItem = createExternalServicesProductItem(20.9, 5.6)

    OfferV2 offerWithItems = createFilledOffer(
            [
                    dataGenerationProductItem,
                    dataAnalysisProductItem,
                    dataManagementProductItem,
                    externalServicesProductItem
            ], affiliation)
    when: "the net prices are recomputed"
    OfferV2 offerWithNet = withNetPrices(offerWithItems)

    then: "the offer net is the sum of all of its item's net prices"
    offerWithNet.getSalePrice() == offerWithNet.getDataGenerationSalePrice()
            .add(offerWithNet.getDataAnalysisSalePrice())
            .add(offerWithNet.getDataManagementSalePrice())
            .add(offerWithNet.getExternalServiceSalePrice())

    where: "the affiliation category varies"
    affiliationCategory << AffiliationCategory.values()
    affiliation = createAffiliation(affiliationCategory as AffiliationCategory, "Germany")
  }

  def "the offer discount is the sum of the item discounts"() {

    def dataGenerationProductItem = createDataGenerationProductItem(20.9, 5.6)
    def dataAnalysisProductItem = createDataAnalysisProductItem(20.9, 5.6)
    def dataManagementProductItem = createDataManagementProductItem(20.9, 5.6)
    def externalServicesProductItem = createExternalServicesProductItem(20.9, 5.6)

    OfferV2 offerWithItems = createFilledOffer(
            [
                    dataGenerationProductItem,
                    dataAnalysisProductItem,
                    dataManagementProductItem,
                    externalServicesProductItem
            ], affiliation)
    when: "the net prices are recomputed"
    OfferV2 offerWithNet = withTotalDiscount(offerWithItems)
    then: "the offer discount is the sum of all its item's discounts"
    offerWithNet.getTotalDiscountAmount() == [
            dataGenerationProductItem.getItemDiscount(),
            dataAnalysisProductItem.getItemDiscount(),
            dataManagementProductItem.getItemDiscount(),
            externalServicesProductItem.getItemDiscount()
    ].stream().map(BigDecimal::valueOf).reduce(BigDecimal.ZERO, BigDecimal::add)

    where: "the affiliation category varies"
    affiliationCategory << AffiliationCategory.values()
    affiliation = createAffiliation(affiliationCategory as AffiliationCategory, "Germany")
  }

  def "when overheads are computed for an offer, then the item group overhead price equals GroupNet * #expectedRatio for #affiliationCategory affiliations and the total overhead is the sum of the group overheads"() {
    given: "an offer with items"
    OfferV2 offerWithItems = createFilledOffer(
            [
                    createDataGenerationProductItem(20.9, 5.6),
                    createDataAnalysisProductItem(20.9, 5.6),
                    createDataManagementProductItem(20.9, 5.6),
                    createExternalServicesProductItem(20.9, 5.6)
            ], affiliation)

    and: "the offer only contains invalid overheads"
//    offerWithItems.setOverhead(-1)
//    offerWithItems.setOverheadRatio(-1)
//    offerWithItems.setOverheadsDataAnalysis(-1.0)
//    offerWithItems.setOverheadsDataGeneration(-1.0)
//    offerWithItems.setOverheadsDataManagement(-1.0)
//    offerWithItems.setOverheadsExternalServices(-1.0)
    when: "the overheads are recomputed"
    OfferV2 offerWithOverheads = withOverheads(offerWithItems)

    OfferV2 offerWithNetPrices = withNetPrices(offerWithItems)
    then: "the group overheads are correct"
    offerWithOverheads.getDataGenerationOverhead() == (offerWithNetPrices.getDataGenerationSalePrice() * expectedRatio).setScale(2, RoundingMode.HALF_UP)
    offerWithOverheads.getDataAnalysisOverhead() == (offerWithNetPrices.getDataAnalysisSalePrice() * expectedRatio).setScale(2, RoundingMode.HALF_UP)
    offerWithOverheads.getDataManagementOverhead() == (offerWithNetPrices.getDataManagementSalePrice() * expectedRatio).setScale(2, RoundingMode.HALF_UP)
    offerWithOverheads.getExternalServiceOverhead() == (offerWithNetPrices.getExternalServiceSalePrice() * expectedRatio).setScale(2, RoundingMode.HALF_UP)

    and: "the total offer overhead is correct"
    def expectedOverhead = offerWithOverheads.getDataGenerationOverhead()
            .add(offerWithOverheads.getDataAnalysisOverhead())
            .add(offerWithOverheads.getDataManagementOverhead())
            .add(offerWithOverheads.getExternalServiceOverhead())
            .doubleValue()
    offerWithOverheads.getOverhead() == expectedOverhead

    where:
    affiliationCategory << AffiliationCategory.values()
    affiliation = createAffiliation(affiliationCategory as AffiliationCategory, "Germany")
    expectedRatio = overheadRatio(affiliationCategory as AffiliationCategory)
  }


  def "overhead ratio is #overheadPercentage% for customer affiliation with category #category"() {
    when: "the calculus determines the overhead ratio for an affiliation"
    def ratio = overheadRatio(category)
    then: "the overhead ratio is #expectedRatio"
    ratio == expectedRatio
    where:
    category                              | expectedRatio
    AffiliationCategory.INTERNAL          | 0.0
    AffiliationCategory.EXTERNAL_ACADEMIC | 0.2
    AffiliationCategory.EXTERNAL          | 0.4

    overheadPercentage = expectedRatio * 100
    affiliation = new Affiliation("organization", "addressAddition", "street", "postalCode", "city", "country", category)
  }

  def "The VAT for Germany applied is 19%: vat(#netprice) = #expected"() {
    when:
    BigDecimal result = calcVat(netprice as BigDecimal, "Germany")

    then:
    result == expected

    where:
    netprice << [6.0, 1.5, 2.5]
    expected = (0.19 * netprice).setScale(2, RoundingMode.HALF_UP)
  }

  def "The VAT outside of Germany applied is 0%: vat(#netprice) = #expected"() {
    when:
    BigDecimal result = calcVat(netprice as BigDecimal, _ as String)

    then:
    result == expected

    where:
    netprice << [6.0, 1.5, 2.5]
    expected = (0.0 * netprice).setScale(2, RoundingMode.HALF_UP)
  }

  def "expect the VAT ratio to be #vatRatioString for #country"() {
    expect:
    vatRatio(country as String) == expectedVatRatio
    where:
    country   | expectedVatRatio
    "Germany" | 0.19
    _         | 0

    vatRatioString = "${expectedVatRatio * 100}%"
  }


  def "when an offer is processed, then the total net is correct for #affiliationCategory affiliations"() {
    given: "an unprocessed offer with items"
    OfferV2 unprocessedOffer = createUnprocessedOffer(affiliationCategory as AffiliationCategory)
    and: "an offer with filled net prices"
    OfferV2 offerWithNets = withNetPrices(withGroupedProductItems(unprocessedOffer))

    expect: "after processing the total net is correct"
    process(unprocessedOffer).getSalePrice() == offerWithNets.getSalePrice()

    where:
    affiliationCategory << AffiliationCategory.values()
  }

  def "when an offer is processed, then the total discount is correct for #affiliationCategory affiliations"() {
    given: "an unprocessed offer with items"
    OfferV2 unprocessedOffer = createUnprocessedOffer(affiliationCategory as AffiliationCategory)
    and: "an offer with filled net prices"
    OfferV2 offerWithTotalDiscount = withTotalDiscount(withGroupedProductItems(unprocessedOffer))

    expect: "after processing the total net is correct"
    process(unprocessedOffer).getTotalDiscountAmount() == offerWithTotalDiscount.getTotalDiscountAmount()

    where:
    affiliationCategory << AffiliationCategory.values()
  }

  def "when an offer is processed, then the group nets are correct for #affiliationCategory affiliations"() {
    given: "an unprocessed offer with items"
    OfferV2 unprocessedOffer = createUnprocessedOffer(affiliationCategory as AffiliationCategory)
    and: "an offer with filled net prices"
    OfferV2 offerWithNets = withNetPrices(withGroupedProductItems(unprocessedOffer))

    when: "the offer was processed"
    OfferV2 processedOffer = process(unprocessedOffer)

    then: "the group net prices are correct"
    processedOffer.getDataGenerationSalePrice() == offerWithNets.getDataGenerationSalePrice()
    processedOffer.getDataAnalysisSalePrice() == offerWithNets.getDataAnalysisSalePrice()
    processedOffer.getDataManagementSalePrice() == offerWithNets.getDataManagementSalePrice()
    processedOffer.getExternalServiceSalePrice() == offerWithNets.getExternalServiceSalePrice()

    where:
    affiliationCategory << AffiliationCategory.values()
  }

  def "when an offer is processed, then the group overheads are correct for #affiliationCategory affiliations"() {
    given: "an unprocessed offer with items"
    OfferV2 unprocessedOffer = createUnprocessedOffer(affiliationCategory as AffiliationCategory)
    and: "an offer with filled net prices"
    OfferV2 offerWithOverheads = withOverheads(withGroupedProductItems(unprocessedOffer))

    when: "the offer was processed"
    OfferV2 processedOffer = process(unprocessedOffer)

    then: "the group net prices are correct"
    processedOffer.getDataGenerationOverhead() == offerWithOverheads.getDataGenerationOverhead()
    processedOffer.getDataAnalysisOverhead() == offerWithOverheads.getDataAnalysisOverhead()
    processedOffer.getDataManagementOverhead() == offerWithOverheads.getDataManagementOverhead()
    processedOffer.getExternalServiceOverhead() == offerWithOverheads.getExternalServiceOverhead()

    where:
    affiliationCategory << AffiliationCategory.values()
  }

  def "when an offer is processed, then the total overhead is correct for #affiliationCategory affiliations"() {
    given: "an unprocessed offer with items"
    OfferV2 unprocessedOffer = createUnprocessedOffer(affiliationCategory as AffiliationCategory)
    and: "an offer with filled net prices"
    OfferV2 offerWithOverheads = withOverheads(withGroupedProductItems(unprocessedOffer))

    expect: "after processing the total net is correct"
    process(unprocessedOffer).getOverhead() == offerWithOverheads.getOverhead()

    where:
    affiliationCategory << AffiliationCategory.values()
  }

  def "when an offer for a customer in Germany is processed, then the vat is #vatRatioString"() {
    given: "an unprocessed offer with items"
    OfferV2 unprocessedOffer = createUnprocessedOffer(affiliationCategory as AffiliationCategory, country)
    and: "an offer with filled VAT prices"
    OfferV2 offerWithVat = withVat(withNetPrices(withGroupedProductItems(unprocessedOffer)))

    when: "the offer is processed"

    OfferV2 processedOffer = process(unprocessedOffer)
    then: "the vat ratio is correct"
    processedOffer.getVatRatio() == expectedVatRatio
    and: "the calculated vat is correct"
    processedOffer.getTotalVat() == offerWithVat.getTotalVat()
    where:
    country = "Germany"
    expectedVatRatio = vatRatio(country)
    affiliationCategory << AffiliationCategory.values()
    vatRatioString = "${expectedVatRatio*100.0}%"

  }

  def "when a offer for a customer outside of Germany is processed, then the vat is #vatRatioString for #country"() {
    given: "an unprocessed offer with items"
    OfferV2 unprocessedOffer = createUnprocessedOffer(affiliationCategory as AffiliationCategory, country)
    and: "an offer with filled VAT prices"
    OfferV2 offerWithVat = withVat(withNetPrices(withGroupedProductItems(unprocessedOffer)))

    when: "the offer is processed"

    OfferV2 processedOffer = process(unprocessedOffer)
    then: "the vat ratio is correct"
    processedOffer.getVatRatio() == expectedVatRatio
    and: "the calculated vat is correct"
    processedOffer.getTotalVat() == offerWithVat.getTotalVat()

    where:
    affiliationCategory << AffiliationCategory.values()
    country = "Not-Germany"
    expectedVatRatio = vatRatio(country)
    vatRatioString = "${expectedVatRatio*100}%"
  }

  OfferV2 createUnprocessedOffer(AffiliationCategory affiliationCategory, String country) {
    def dataGenerationProductItem = createDataGenerationProductItem(20.9, 5.6)
    def dataAnalysisProductItem = createDataAnalysisProductItem(20.9, 5.6)
    def dataManagementProductItem = createDataManagementProductItem(20.9, 5.6)
    def externalServicesProductItem = createExternalServicesProductItem(20.9, 5.6)
    def productItems = [
            dataGenerationProductItem,
            dataAnalysisProductItem,
            dataManagementProductItem,
            externalServicesProductItem
    ]
    def affiliation = createAffiliation(affiliationCategory, country)
    OfferV2 unprocessedOffer = new OfferV2()
    unprocessedOffer.setItems(productItems)
    unprocessedOffer.setSelectedCustomerAffiliation(affiliation)
    return unprocessedOffer
  }

  OfferV2 createUnprocessedOffer(AffiliationCategory affiliationCategory) {
    createUnprocessedOffer(affiliationCategory, "Germany")
  }


  OfferV2 createFilledOffer(List<ProductItem> productItems, Affiliation affiliation) {
    OfferV2 offerV2 = new OfferV2()
    offerV2.setSelectedCustomerAffiliation(affiliation)
    offerV2.setItems(productItems)
    OfferV2 processedOffer = withGroupedProductItems(offerV2)
    return processedOffer
  }

  boolean equals(ProductItem productItem, ProductItem ProductItem) {
    // check for product properties
    if (!(productItem.getProduct().getCategory().equals(ProductItem.getCategory()))) {
      return false
    }
    if (!productItem.getProduct().getDescription().equals(ProductItem.getProductDescription())) {
      return false
    }
    if (!productItem.getProduct().getProductName().equals(ProductItem.getProductName())) {
      return false
    }
    if (!productItem.getProduct().serviceProvider.equals(ProductItem.getServiceProvider())) {
      return false
    }
    if (!productItem.getProduct().getUnit().equals(ProductItem.getUnit())) {
      return false
    }

    return productItem.getQuantity().equals(ProductItem.getQuantity())
  }

  static Product createProduct(String category) {
    def daProduct = new Product()
    daProduct.setCategory(category)
    daProduct.setActive(true)
    daProduct.setDescription("This is a sequencing product")
    daProduct.setExternalUnitPrice(0.25)
    daProduct.setInternalUnitPrice(0.5)
    daProduct.setProductId("RandomProduct_1")
    daProduct.setProductName("Awesome Bioinformatics")
    daProduct.setUnit("Brain Cell")
    daProduct.setServiceProvider("Bioinformatiker Model Z")
    return daProduct
  }

  static Product createDtaGenerationProduct(double unitPrice) {
    def daProduct = createProduct("Sequencing")
    daProduct.setExternalUnitPrice(unitPrice)
    daProduct.setInternalUnitPrice(unitPrice)
    return daProduct
  }

  static Product createExternalServicesProduct(double unitPrice) {
    def daProduct = createProduct("External Service")
    daProduct.setExternalUnitPrice(unitPrice)
    daProduct.setInternalUnitPrice(unitPrice)
    return daProduct
  }


  static Product createDataAnalysisProduct() {
    return createProduct("Primary Bioinformatics")
  }

  static Product createDataStorageProduct() {
    return createProduct("Data Storage")

  }

  static Product createDataAnalysisProduct(double unitPrice) {
    def daProduct = createDataAnalysisProduct()
    daProduct.setExternalUnitPrice(unitPrice)
    daProduct.setInternalUnitPrice(unitPrice)
    return daProduct
  }

  static Product createDataStorageProduct(double unitPrice) {
    def daProduct = createDataStorageProduct()
    daProduct.setExternalUnitPrice(unitPrice)
    daProduct.setInternalUnitPrice(unitPrice)
    return daProduct
  }

  static ProductItem createDataStorageProductItem(double quantity, double unitPrice) {
    return new ProductItem(offer, createDataStorageProduct(unitPrice), quantity)
  }

  static ProductItem createDataStorageProductItem(double quantity, double internalUnitPrice, double externalUnitPrice) {
    def product = createDataStorageProduct()
    product.setInternalUnitPrice(internalUnitPrice)
    product.setExternalUnitPrice(externalUnitPrice)
    return new ProductItem(offer, product, quantity)
  }

  static ProductItem createDataAnalysisProductItem(double quantity, double unitPrice) {
    return new ProductItem(offer, createDataAnalysisProduct(unitPrice), quantity)
  }

  static ProductItem createDataAnalysisProductItem(double quantity, double internalUnitPrice, double externalUnitPrice) {
    def product = createDataAnalysisProduct()
    product.setInternalUnitPrice(internalUnitPrice)
    product.setExternalUnitPrice(externalUnitPrice)
    return new ProductItem(offer, product, quantity)
  }

  static ProductItem createDataGenerationProductItem(double quantity, double unitPrice) {
    return new ProductItem(offer, createDtaGenerationProduct(unitPrice), quantity)
  }


  static ProductItem createDataManagementProductItem(double quantity, double unitPrice) {
    return createDataStorageProductItem(quantity, unitPrice)
  }

  static ProductItem createExternalServicesProductItem(double quantity, double unitPrice) {
    return new ProductItem(offer, createExternalServicesProduct(unitPrice), quantity)
  }

  static Affiliation createAffiliation(AffiliationCategory affiliationCategory, String country) {
    new Affiliation("University of Tübingen",
            "",
            "Auf der Morgenstelle",
            "72076",
            "Tübingen",
            country,
            affiliationCategory)
  }

  static Affiliation setupInternalAffiliation() {
    createAffiliation(AffiliationCategory.INTERNAL, "Germany")
  }

  static Affiliation setupExternalAffiliation() {
    createAffiliation(AffiliationCategory.EXTERNAL, "Germany")
  }

  static Affiliation setupExternalAcademicAffiliation() {
    createAffiliation(AffiliationCategory.EXTERNAL_ACADEMIC, "Germany")
  }

}
