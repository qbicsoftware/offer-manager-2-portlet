package life.qbic.business.offers

import life.qbic.business.offers.identifier.OfferId
import life.qbic.business.persons.affiliation.Affiliation
import life.qbic.business.persons.affiliation.AffiliationCategory
import life.qbic.business.products.Product
import life.qbic.business.products.ProductItem
import spock.lang.Specification

import java.math.RoundingMode

class OfferV2Spec extends Specification {

  def "when an item is added to the offer, it is also added to the corresponding group"() {
    given:
    OfferV2 offer = new OfferV2(new Affiliation("", "", "", "", "", "Germany", AffiliationCategory.INTERNAL), new OfferId("test", 1))
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
    OfferV2 offer = new OfferV2()
    offer.setSelectedCustomerAffiliation(new Affiliation("", "", "", "", "", "Germany", AffiliationCategory.INTERNAL))
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
  }

  def "the offer's sale price is the sum of the group sale prices"() {
    given:
    OfferV2 offer = new OfferV2()
    offer.setSelectedCustomerAffiliation(new Affiliation("", "", "", "", "", "Germany", AffiliationCategory.INTERNAL))
    and: "some one product item of each category once"
    def dataGeneration = createDataGenerationItem(offer)
    def dataAnalysis = createDataAnalysisItem(offer)
    def dataManagement = createDataManagementItem(offer)
    def externalServices = createExternalServiceItem(offer)
    when:
    offer.addItems([dataGeneration, dataAnalysis, dataManagement, externalServices])
    then: "the groups overheads are the sum of the items overheads"
    offer.getSalePrice() == offer.getDataGenerationSalePrice()
            .add(offer.getDataAnalysisSalePrice())
            .add(offer.getDataManagementSalePrice())
            .add(offer.getExternalServiceSalePrice())


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
    OfferV2 offer = new OfferV2()
    offer.setSelectedCustomerAffiliation(new Affiliation("", "", "", "", "", "Germany", AffiliationCategory.EXTERNAL))
    and: "some one product item of each category once"
    def dataGeneration = createDataGenerationItem(offer)
    def dataAnalysis = createDataAnalysisItem(offer)
    def dataManagement = createDataManagementItem(offer)
    def externalServices = createExternalServiceItem(offer)
    when:
    offer.addItems([dataGeneration, dataAnalysis, dataManagement, externalServices])
    then: "the groups overhead is the group's sale price multiplied with the overhead rate"
    offer.dataGenerationOverhead == dataGeneration.salePrice * offer.overheadRatio
    offer.dataAnalysisOverhead == dataAnalysis.salePrice * offer.overheadRatio
    offer.dataManagementOverhead == dataManagement.salePrice * offer.overheadRatio
    offer.externalServiceOverhead == externalServices.salePrice * offer.overheadRatio
  }

  def "the offer's overhead is the sum of the group overheads"() {
    given:
    OfferV2 offer = new OfferV2()
    offer.setSelectedCustomerAffiliation(new Affiliation("", "", "", "", "", "Germany", AffiliationCategory.EXTERNAL))
    and: "some one product item of each category once"
    def dataGeneration = createDataGenerationItem(offer)
    def dataAnalysis = createDataAnalysisItem(offer)
    def dataManagement = createDataManagementItem(offer)
    def externalServices = createExternalServiceItem(offer)
    when:
    offer.addItems([dataGeneration, dataAnalysis, dataManagement, externalServices])
    then: "the offer overhead is the sum of the group overheads"
    offer.overhead == offer.dataGenerationOverhead
            .add(offer.dataAnalysisOverhead)
            .add(offer.dataManagementOverhead)
            .add(offer.externalServiceOverhead).doubleValue()
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
    OfferV2 offer = new OfferV2()
    offer.setSelectedCustomerAffiliation(new Affiliation("", "", "", "", "", country, AffiliationCategory.EXTERNAL))
    expect:
    offer.getVatRatio() == expected
    where:
    expected = 0.0
  }

  def "expect the VAT ratio to be #vatRatioString for #country"() {
    given:
    OfferV2 offer = new OfferV2()
    offer.setSelectedCustomerAffiliation(new Affiliation("", "", "", "", "", country, AffiliationCategory.EXTERNAL))
    expect:
    offer.getVatRatio() == expected
    where:
    country            | expected
    "Germany"          | 0.19
    "some other place" | 0
    "France"           | 0
    and:
    vatRatioString = "${expected * 100}%"
  }

  def "expect the tax amount to be the cost before tax multiplied with the VAT rate."() {
    given:
    String country = "Germany"
    OfferV2 offer = new OfferV2()
    offer.setSelectedCustomerAffiliation(new Affiliation("", "", "", "", "", country, AffiliationCategory.EXTERNAL))
    expect:
    offer.getTaxAmount() == offer.getSalePrice() * offer.getVatRatio()
  }

  def "expect the discount amount to be the sum of the items discount amounts"() {
    given:
    OfferV2 offer = new OfferV2()
    offer.setSelectedCustomerAffiliation(new Affiliation("", "", "", "", "", "Germany", AffiliationCategory.INTERNAL))
    and: "some one product item of each category once"
    def dataGeneration = createDataGenerationItem(offer)
    def dataAnalysis = createDataAnalysisItem(offer)
    def dataManagement = createDataManagementItem(offer)
    def externalServices = createExternalServiceItem(offer)
    when:
    offer.addItems([dataGeneration, dataAnalysis, dataManagement, externalServices])
    then: "the offer overhead is the sum of the group overheads"
    offer.totalDiscountAmount == (
            dataGeneration.getDiscountAmount()
                    .add(dataAnalysis.getDiscountAmount())
                    .add(dataManagement.getDiscountAmount())
                    .add(externalServices.getDiscountAmount())
    ).setScale(2, RoundingMode.HALF_UP)
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



  def "the offer discount is the sum of the item discounts"() {
//    def dataGenerationProductItem = createDataGenerationProductItem(20.9, 5.6)
//    def dataAnalysisProductItem = createDataAnalysisProductItem(20.9, 5.6)
//    def dataManagementProductItem = createDataManagementProductItem(20.9, 5.6)
//    def externalServicesProductItem = createExternalServicesProductItem(20.9, 5.6)
//
//    def dataGenerationOfferItem = affiliationCategory == AffiliationCategory.INTERNAL ? offerItemFromInternal(dataGenerationProductItem) : offerItemFromExternal(dataGenerationProductItem)
//    def dataAnalysisOfferItem = affiliationCategory == AffiliationCategory.INTERNAL ? offerItemFromInternal(dataAnalysisProductItem) : offerItemFromExternal(dataAnalysisProductItem)
//    def dataManagementOfferItem = affiliationCategory == AffiliationCategory.INTERNAL ? offerItemFromInternal(dataManagementProductItem) : offerItemFromExternal(dataManagementProductItem)
//    def externalServicesOfferItem = affiliationCategory == AffiliationCategory.INTERNAL ? offerItemFromInternal(externalServicesProductItem) : offerItemFromExternal(externalServicesProductItem)
//
//
//    OfferV2 offerWithItems = createFilledOffer(
//            [
//                    dataGenerationProductItem,
//                    dataAnalysisProductItem,
//                    dataManagementProductItem,
//                    externalServicesProductItem
//            ], affiliation)
//    when: "the net prices are recomputed"
//    OfferV2 offerWithNet = withTotalDiscount(offerWithItems)
//    then: "the offer discount is the sum of all its item's discounts"
//    offerWithNet.getTotalDiscountAmount() == [
//            dataGenerationOfferItem.getItemDiscount(),
//            dataAnalysisOfferItem.getItemDiscount(),
//            dataManagementOfferItem.getItemDiscount(),
//            externalServicesOfferItem.getItemDiscount()
//    ].stream().map(BigDecimal::valueOf).reduce(BigDecimal.ZERO, BigDecimal::add)
//
//    where: "the affiliation category varies"
//    affiliationCategory << AffiliationCategory.values()
//    affiliation = createAffiliation(affiliationCategory as AffiliationCategory, "Germany")
  }

  def "when overheads are computed for an offer, then the item group overhead price equals GroupNet * #expectedRatio for #affiliationCategory affiliations and the total overhead is the sum of the group overheads"() {
//    given: "an offer with items"
//    OfferV2 offerWithItems = createFilledOffer(
//            [
//                    createDataGenerationProductItem(20.9, 5.6),
//                    createDataAnalysisProductItem(20.9, 5.6),
//                    createDataManagementProductItem(20.9, 5.6),
//                    createExternalServicesProductItem(20.9, 5.6)
//            ], affiliation)
//
//    and: "the offer only contains invalid overheads"
//    offerWithItems.setOverhead(-1)
//    offerWithItems.setOverheadRatio(-1)
//    offerWithItems.setOverheadsDataAnalysis(-1.0)
//    offerWithItems.setOverheadsDataGeneration(-1.0)
//    offerWithItems.setOverheadsDataManagement(-1.0)
//    offerWithItems.setOverheadsExternalServices(-1.0)
//    when: "the overheads are recomputed"
//    OfferV2 offerWithOverheads = withOverheads(offerWithItems)
//
//    OfferV2 offerWithNetPrices = withNetPrices(offerWithItems)
//    then: "the group overheads are correct"
//    offerWithOverheads.getOverheadsDataGeneration() == (offerWithNetPrices.getNetSumDataGeneration() * expectedRatio).setScale(2, RoundingMode.HALF_UP)
//    offerWithOverheads.getOverheadsDataAnalysis() == (offerWithNetPrices.getNetSumDataAnalysis() * expectedRatio).setScale(2, RoundingMode.HALF_UP)
//    offerWithOverheads.getOverheadsDataManagement() == (offerWithNetPrices.getNetSumDataManagement() * expectedRatio).setScale(2, RoundingMode.HALF_UP)
//    offerWithOverheads.getOverheadsExternalServices() == (offerWithNetPrices.getNetSumExternalServices() * expectedRatio).setScale(2, RoundingMode.HALF_UP)
//
//    and: "the total offer overhead is correct"
//    def expectedOverhead = offerWithOverheads.getOverheadsDataGeneration()
//            .add(offerWithOverheads.getOverheadsDataAnalysis())
//            .add(offerWithOverheads.getOverheadsDataManagement())
//            .add(offerWithOverheads.getOverheadsExternalServices())
//            .doubleValue()
//    offerWithOverheads.getOverhead() == expectedOverhead
//
//    where:
//    affiliationCategory << AffiliationCategory.values()
//    affiliation = createAffiliation(affiliationCategory as AffiliationCategory, "Germany")
//    expectedRatio = overheadRatio(affiliationCategory as AffiliationCategory)
  }
  // Upon offer update:
/*
  def "when an offer is processed, then the total net is correct for #affiliationCategory affiliations"() {
    given: "an unprocessed offer with items"
    OfferV2 unprocessedOffer = createUnprocessedOffer(affiliationCategory as AffiliationCategory)
    and: "an offer with filled net prices"
    OfferV2 offerWithNets = withNetPrices(withGroupedOfferItems(unprocessedOffer))

    expect: "after processing the total net is correct"
    process(unprocessedOffer).getTotalNetPrice() == offerWithNets.getTotalNetPrice()

    where:
    affiliationCategory << AffiliationCategory.values()
  }

  def "when an offer is processed, then the total discount is correct for #affiliationCategory affiliations"() {
    given: "an unprocessed offer with items"
    OfferV2 unprocessedOffer = createUnprocessedOffer(affiliationCategory as AffiliationCategory)
    and: "an offer with filled net prices"
    OfferV2 offerWithTotalDiscount = withTotalDiscount(withGroupedOfferItems(unprocessedOffer))

    expect: "after processing the total net is correct"
    process(unprocessedOffer).getTotalDiscountAmount() == offerWithTotalDiscount.getTotalDiscountAmount()

    where:
    affiliationCategory << AffiliationCategory.values()
  }

  def "when an offer is processed, then the group nets are correct for #affiliationCategory affiliations"() {
    given: "an unprocessed offer with items"
    OfferV2 unprocessedOffer = createUnprocessedOffer(affiliationCategory as AffiliationCategory)
    and: "an offer with filled net prices"
    OfferV2 offerWithNets = withNetPrices(withGroupedOfferItems(unprocessedOffer))

    when: "the offer was processed"
    OfferV2 processedOffer = process(unprocessedOffer)

    then: "the group net prices are correct"
    processedOffer.getNetSumDataGeneration() == offerWithNets.getNetSumDataGeneration()
    processedOffer.getNetSumDataAnalysis() == offerWithNets.getNetSumDataAnalysis()
    processedOffer.getNetSumDataManagement() == offerWithNets.getNetSumDataManagement()
    processedOffer.getNetSumExternalServices() == offerWithNets.getNetSumExternalServices()

    where:
    affiliationCategory << AffiliationCategory.values()
  }

  def "when an offer is processed, then the group overheads are correct for #affiliationCategory affiliations"() {
    given: "an unprocessed offer with items"
    OfferV2 unprocessedOffer = createUnprocessedOffer(affiliationCategory as AffiliationCategory)
    and: "an offer with filled net prices"
    OfferV2 offerWithOverheads = withOverheads(withGroupedOfferItems(unprocessedOffer))

    when: "the offer was processed"
    OfferV2 processedOffer = process(unprocessedOffer)

    then: "the group net prices are correct"
    processedOffer.getOverheadsDataGeneration() == offerWithOverheads.getOverheadsDataGeneration()
    processedOffer.getOverheadsDataAnalysis() == offerWithOverheads.getOverheadsDataAnalysis()
    processedOffer.getOverheadsDataManagement() == offerWithOverheads.getOverheadsDataManagement()
    processedOffer.getOverheadsExternalServices() == offerWithOverheads.getOverheadsExternalServices()

    where:
    affiliationCategory << AffiliationCategory.values()
  }

  def "when an offer is processed, then the total overhead is correct for #affiliationCategory affiliations"() {
    given: "an unprocessed offer with items"
    OfferV2 unprocessedOffer = createUnprocessedOffer(affiliationCategory as AffiliationCategory)
    and: "an offer with filled net prices"
    OfferV2 offerWithOverheads = withOverheads(withGroupedOfferItems(unprocessedOffer))

    expect: "after processing the total net is correct"
    process(unprocessedOffer).getOverhead() == offerWithOverheads.getOverhead()

    where:
    affiliationCategory << AffiliationCategory.values()
  }

  def "when a german offer is processed, then the vat is #vatRatioString"() {
    given: "an unprocessed offer with items"
    OfferV2 unprocessedOffer = createUnprocessedOffer(affiliationCategory as AffiliationCategory, country)
    and: "an offer with filled VAT prices"
    OfferV2 offerWithVat = withVat(withNetPrices(withGroupedOfferItems(unprocessedOffer)))

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

  def "when a foreign offer is processed, then the vat is #vatRatioString for #country"() {
    given: "an unprocessed offer with items"
    OfferV2 unprocessedOffer = createUnprocessedOffer(affiliationCategory as AffiliationCategory, country)
    and: "an offer with filled VAT prices"
    OfferV2 offerWithVat = withVat(withNetPrices(withGroupedOfferItems(unprocessedOffer)))

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
  }*/
}
