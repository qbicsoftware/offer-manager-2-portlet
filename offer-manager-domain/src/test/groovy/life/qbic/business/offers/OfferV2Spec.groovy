package life.qbic.business.offers


import spock.lang.Specification

class OfferV2Spec extends Specification {


  def "the offer net is the sum of the group nets"() {
//    def dataGenerationProductItem = createDataGenerationProductItem(20.9, 5.6)
//    def dataAnalysisProductItem = createDataAnalysisProductItem(20.9, 5.6)
//    def dataManagementProductItem = createDataManagementProductItem(20.9, 5.6)
//    def externalServicesProductItem = createExternalServicesProductItem(20.9, 5.6)
//
//    OfferV2 offerWithItems = createFilledOffer(
//            [
//                    dataGenerationProductItem,
//                    dataAnalysisProductItem,
//                    dataManagementProductItem,
//                    externalServicesProductItem
//            ], affiliation)
//    when: "the net prices are recomputed"
//    OfferV2 offerWithNet = withNetPrices(offerWithItems)
//
//    then: "the offer net is the sum of all of its item's net prices"
//    offerWithNet.getTotalNetPrice() == offerWithNet.getNetSumDataGeneration()
//            .add(offerWithNet.getNetSumDataAnalysis())
//            .add(offerWithNet.getNetSumDataManagement())
//            .add(offerWithNet.getNetSumExternalServices())
//
//    where: "the affiliation category varies"
//    affiliationCategory << AffiliationCategory.values()
//    affiliation = createAffiliation(affiliationCategory as AffiliationCategory, "Germany")
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


  def "overhead ratio is #overheadPercentage% for customer affiliation with category #category"() {
//    when: "the calculus determines the overhead ratio for an affiliation"
//    def ratio = overheadRatio(category)
//    then: "the overhead ratio is #expectedRatio"
//    ratio == expectedRatio
//    where:
//    category                              | expectedRatio
//    AffiliationCategory.INTERNAL          | 0.0
//    AffiliationCategory.EXTERNAL_ACADEMIC | 0.2
//    AffiliationCategory.EXTERNAL          | 0.4
//
//    overheadPercentage = expectedRatio * 100
//    affiliation = new Affiliation("organization", "addressAddition", "street", "postalCode", "city", "country", category)
  }

  def "The VAT for Germany applied is 19% vat(#netprice) = #expected"() {
//    when:
//    BigDecimal result = calcVat(netprice as BigDecimal, "Germany")
//
//    then:
//    result == expected
//
//    where:
//    netprice << [6.0, 1.5, 2.5]
//    expected = (0.19 * netprice).setScale(2, RoundingMode.HALF_UP)
  }

  def "The VAT outside of Germany applied is 0% vat(#netprice) = #expected"() {
//    when:
//    BigDecimal result = calcVat(netprice as BigDecimal, _ as String)
//
//    then:
//    result == expected
//
//    where:
//    netprice << [6.0, 1.5, 2.5]
//    expected = (0.0 * netprice).setScale(2, RoundingMode.HALF_UP)
  }

  def "expect the VAT ratio to be #vatRatioString for #country"() {
//    expect:
//    vatRatio(country as String) == expectedVatRatio
//    where:
//    country   | expectedVatRatio
//    "Germany" | 0.19
//    _         | 0
//
//    vatRatioString = "${expectedVatRatio * 100}%"
  }
}
