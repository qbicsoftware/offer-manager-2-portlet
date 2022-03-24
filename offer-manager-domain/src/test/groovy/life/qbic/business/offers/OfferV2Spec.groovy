package life.qbic.business.offers

import life.qbic.business.persons.affiliation.Affiliation
import life.qbic.business.persons.affiliation.AffiliationCategory
import life.qbic.business.products.Product
import life.qbic.business.products.ProductItem
import spock.lang.Specification

import java.math.RoundingMode
import java.time.LocalDate

class OfferV2Spec extends Specification {

  def "when an item is added to the offer, it is also added to the corresponding group"() {
    given:
    OfferV2 offer = createOfferWithoutItems(AffiliationCategory.INTERNAL, "Germany")
    and: "some one product item of each category once"
    def dataGeneration = createDataGenerationItem(offer)
    def dataAnalysis = createDataAnalysisItem(offer)
    def dataManagement = createDataManagementItem(offer)
    def externalServices = createExternalServiceItem(offer)
    when:
    offer.addItems([dataGeneration, dataAnalysis, dataManagement, externalServices])
    then: "the offer contains the added item"
    offer.getItems().contains(dataGeneration)
    offer.getItems().contains(dataAnalysis)
    offer.getItems().contains(dataManagement)
    offer.getItems().contains(externalServices)
    and: "the offer groups contain the added item "
    offer.getDataGenerationItems().contains(dataGeneration)
    offer.getDataAnalysisItems().contains(dataAnalysis)
    offer.getDataManagementItems().contains(dataManagement)
    offer.getExternalServiceItems().contains(externalServices)
  }

  def "the group's sale price is the sum of its items sale prices."() {
    given:
    OfferV2 offer = createOfferWithoutItems(affiliationCategory as AffiliationCategory, "Germany")
    and: "some one product item of each category once"
    def dataGeneration = createDataGenerationItem(offer)
    def dataAnalysis = createDataAnalysisItem(offer)
    def dataManagement = createDataManagementItem(offer)
    def externalServices = createExternalServiceItem(offer)
    when:
    offer.addItems([dataGeneration, dataAnalysis, dataManagement, externalServices])
    then: "the groups overheads are the sum of the items overheads"
    offer.dataGenerationSalePrice == dataGeneration.salePrice
    offer.dataAnalysisSalePrice == dataAnalysis.salePrice
    offer.dataManagementSalePrice == dataManagement.salePrice
    offer.externalServiceSalePrice == externalServices.salePrice
    where:
    affiliationCategory << AffiliationCategory.values()
  }

  def "the offer's sale price is the sum of the group sale prices"() {
    given:
    OfferV2 offer = createOfferWithItemsOfEachCategory(affiliationCategory as AffiliationCategory, "Germany")
    expect: "the groups overheads are the sum of the items overheads"
    offer.getSalePrice() == offer.getDataGenerationSalePrice()
            .add(offer.getDataAnalysisSalePrice())
            .add(offer.getDataManagementSalePrice())
            .add(offer.getExternalServiceSalePrice())
    where:
    affiliationCategory << AffiliationCategory.values()
  }

  def "overhead ratio is #overheadPercentage% for customer affiliation with category #category"() {
    when: "the calculus determines the overhead ratio for an affiliation"
    OfferV2 offer = new OfferV2()
    offer.setSelectedCustomerAffiliation(affiliation)
    then: "the overhead ratio is #expectedRatio"
    offer.getOverheadRatio() == expectedRatio.doubleValue()
    where:
    category                              | expectedRatio
    AffiliationCategory.INTERNAL          | 0.0
    AffiliationCategory.EXTERNAL_ACADEMIC | 0.2
    AffiliationCategory.EXTERNAL          | 0.4

    overheadPercentage = expectedRatio * 100
    affiliation = new Affiliation("organization", "addressAddition", "street", "postalCode", "city", "country", category)
  }

  def "the group's overhead price is the group's sale price multiplied with the overhead rate"() {
    given:
    OfferV2 offer = createOfferWithItemsOfEachCategory(affiliationCategory as AffiliationCategory, "Germany")
    expect: "the groups overhead is the group's sale price multiplied with the overhead rate"
    offer.dataGenerationOverhead == offer.dataGenerationSalePrice * offer.overheadRatio
    offer.dataAnalysisOverhead == offer.dataAnalysisSalePrice * offer.overheadRatio
    offer.dataManagementOverhead == offer.dataManagementSalePrice * offer.overheadRatio
    offer.externalServiceOverhead == offer.externalServiceSalePrice * offer.overheadRatio
    where:
    affiliationCategory << AffiliationCategory.values()
  }

  def "the offer's overhead is the sum of the group overheads"() {
    given:
    OfferV2 offer = createOfferWithItemsOfEachCategory(affiliationCategory as AffiliationCategory, "Germany")
    expect: "the offer overhead is the sum of the group overheads"
    offer.overhead == offer.dataGenerationOverhead
            .add(offer.dataAnalysisOverhead)
            .add(offer.dataManagementOverhead)
            .add(offer.externalServiceOverhead).doubleValue()
    where:
    affiliationCategory << AffiliationCategory.values()
  }

  def "expect the price before vat being the sale price + the overhead"() {
    given: "an offer for external in Germany"
    OfferV2 offer = createOfferWithItemsOfEachCategory(affiliationCategory as AffiliationCategory, "Germany")
    expect:
    offer.priceBeforeTax == offer.salePrice + BigDecimal.valueOf(offer.overhead)
    where:
    affiliationCategory << AffiliationCategory.values()
  }


  def "The VAT for Germany applied is 19%"() {
    given:
    String country = "Germany"
    OfferV2 offer = new OfferV2()
    offer.setSelectedCustomerAffiliation(new Affiliation("", "", "", "", "", country, AffiliationCategory.EXTERNAL))
    expect:
    offer.getVatRatio() == expected
    where:
    expected = 0.19
  }

  def "The VAT outside of Germany applied is 0%"() {
    given:
    String country = "No man's land"
    OfferV2 offer = createOfferWithoutItems(affiliationCategory as AffiliationCategory, country)
    expect:
    offer.getVatRatio() == expected
    where:
    expected = 0.0
    affiliationCategory << AffiliationCategory.values()
  }

  def "expect the VAT ratio to be #vatRatioString for #country"() {
    given:
    OfferV2 offer = createOfferWithoutItems(affiliationCategory as AffiliationCategory, country)
    expect:
    offer.getVatRatio() == expected
    where:
    country            | expected
    "Germany"          | 0.19
    "some other place" | 0
    "France"           | 0
    and:
    vatRatioString = "${expected * 100}%"
    affiliationCategory << AffiliationCategory.values()
  }

  def "expect the tax amount to be the cost before tax multiplied with the VAT rate."() {
    given: "an offer for external in Germany"
    OfferV2 offer = createOfferWithItemsOfEachCategory(AffiliationCategory.EXTERNAL, "Germany")
    expect:
    offer.getTaxAmount() == ((offer.getSalePrice() + BigDecimal.valueOf(offer.getOverhead())) * offer.getVatRatio()).setScale(2, RoundingMode.HALF_UP)
  }

  def "expect the price after tax to be the price before tax + the tax amount"() {
    given: "an offer for external in Germany"
    OfferV2 offer = createOfferWithItemsOfEachCategory(affiliationCategory as AffiliationCategory, "Germany")
    expect:
    offer.getPriceAfterTax() == offer.priceBeforeTax + offer.getTaxAmount()
    where:
    affiliationCategory << AffiliationCategory.values()
  }

  def "expect the discount amount to be the sum of the items discount amounts"() {
    given:
    OfferV2 offer = createOfferWithoutItems(affiliationCategory as AffiliationCategory, "Germany")
    and: "some one product item of each category once"
    def dataGeneration = createDataGenerationItem(offer)
    def dataAnalysis = createDataAnalysisItem(offer)
    def dataManagement = createDataManagementItem(offer)
    def externalServices = createExternalServiceItem(offer)
    when:
    offer.addItems([dataGeneration, dataAnalysis, dataManagement, externalServices])
    then: "the offer overhead is the sum of the group overheads"
    offer.totalDiscountAmount == (dataGeneration.getDiscountAmount()
            .add(dataAnalysis.getDiscountAmount())
            .add(dataManagement.getDiscountAmount())
            .add(externalServices.getDiscountAmount())).setScale(2, RoundingMode.HALF_UP)
    where:
    affiliationCategory << AffiliationCategory.values()
  }

  def "two offers with identical affiliation and items with the same product and quantity have the same prices"() {
    given: "two offers"
    OfferV2 offerOne = createOfferWithItemsOfEachCategory(affiliationCategory as AffiliationCategory, "Germany")
    OfferV2 offerTwo = createOfferWithItemsOfEachCategory(affiliationCategory as AffiliationCategory, "Germany")
    expect:
    haveSamePrices(offerOne, offerTwo)
    where:
    affiliationCategory << AffiliationCategory.values()
  }

  private static boolean haveSamePrices(OfferV2 offerOne, OfferV2 offerTwo) {
    def sameSellPrices = offerOne.salePrice == offerTwo.salePrice
            && offerOne.dataGenerationSalePrice == offerTwo.dataGenerationSalePrice
            && offerOne.dataAnalysisSalePrice == offerTwo.dataAnalysisSalePrice
            && offerOne.dataManagementSalePrice == offerTwo.dataManagementSalePrice
            && offerOne.externalServiceSalePrice == offerTwo.externalServiceSalePrice
    def sameOverheads = offerOne.overhead == offerTwo.overhead
            && offerOne.dataGenerationOverhead == offerTwo.dataGenerationOverhead
            && offerOne.dataAnalysisOverhead == offerTwo.dataAnalysisOverhead
            && offerOne.dataManagementOverhead == offerTwo.dataManagementOverhead
            && offerOne.externalServiceOverhead == offerTwo.externalServiceOverhead
            && offerOne.overheadRatio == offerTwo.overheadRatio
    def sameVats = offerOne.priceBeforeTax == offerTwo.priceBeforeTax
            && offerOne.priceAfterTax == offerTwo.priceAfterTax
            && offerOne.taxAmount == offerTwo.taxAmount
            && offerOne.vatRatio == offerTwo.vatRatio
    def sameTotalDiscountAmount = offerOne.totalDiscountAmount == offerTwo.totalDiscountAmount
    return sameSellPrices && sameOverheads && sameVats && sameTotalDiscountAmount
  }

  def "expect the prices to be updated after an affiliation change"() {
    given: "a reference offer and Affiliation"
    String country = "Germany"
    Affiliation referenceAffiliation = new Affiliation("", "", "", "", "", country, affiliationCategory as AffiliationCategory)
    OfferV2 referenceOffer = createOfferWithItemsOfEachCategory(referenceAffiliation.category, country)
    and: "an offer under test"
    OfferV2 testOffer = createOfferWithItemsOfEachCategory(AffiliationCategory.INTERNAL, country)
    when: "the affiliation's category is changed"
    testOffer.setSelectedCustomerAffiliation(referenceAffiliation)
    then: "the recomputed offer prices match the reference offer prices"
    haveSamePrices(referenceOffer, testOffer)
    where:
    affiliationCategory << AffiliationCategory.values()
  }

  def "expect an offer to have an expiration date of 90 days after upon creation"() {
    when: "an offer is created"
    def offer = createOfferWithoutItems(AffiliationCategory.INTERNAL, "no country")
    then: "the offer expiration date is set to 90 days in the future"
    offer.expirationDate == offer.creationDate.plusDays(90)
  }


  def "expect an offer to have the current time as the creation date upon creation"() {
    when: "an offer is created"
    def offer = createOfferWithoutItems(AffiliationCategory.INTERNAL, "no country")
    then: "the offer creation date is set to the Date of offer generation"
    offer.creationDate == LocalDate.now()
  }


  private static createDataGenerationItem(OfferV2 offer) {
    def category = "Sequencing"
    def product = new Product(category, 0.5, 1)
    return new ProductItem(offer, product, 10.0)
  }

  private static createDataAnalysisItem(OfferV2 offer) {
    def category = "Primary Bioinformatics"
    def product = new Product(category, 0.5, 1)
    return new ProductItem(offer, product, 10.0)
  }

  private static createDataManagementItem(OfferV2 offer) {
    def category = "Project Management"
    def product = new Product(category, 0.5, 1)
    return new ProductItem(offer, product, 10.0)
  }

  private static createExternalServiceItem(OfferV2 offer) {
    def category = "External Service"
    def product = new Product(category, 0.5, 1)
    return new ProductItem(offer, product, 10.0)
  }

  private static OfferV2 createOfferWithoutItems(AffiliationCategory affiliationCategory, String country) {
    OfferV2 offer = new OfferV2()
    offer.setSelectedCustomerAffiliation(new Affiliation("", "", "", "", "", country, affiliationCategory))
    return offer
  }

  private static OfferV2 createOfferWithItemsOfEachCategory(AffiliationCategory affiliationCategory, String country) {
    OfferV2 offer = new OfferV2()
    offer.setSelectedCustomerAffiliation(new Affiliation("", "", "", "", "", country, affiliationCategory))
    def dataGeneration = createDataGenerationItem(offer)
    def dataAnalysis = createDataAnalysisItem(offer)
    def dataManagement = createDataManagementItem(offer)
    def externalServices = createExternalServiceItem(offer)
    offer.setItems([dataGeneration, dataAnalysis, dataManagement, externalServices])
    return offer
  }
}
