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

  def "overhead ratio is #expectedRatio for customer affiliation with category #category"() {
    given: "an offer with the respective customer affiliation"
    OfferV2 offer = new OfferV2()
    offer.setSelectedCustomerAffiliation(new Affiliation("organization", "addressAddition", "street", "postalCode", "city", "country", category))
    when: "the calculus determines the overhead ratio for that offer"
    OfferV2 filledOffer = offerCalculus.overheadRatio(offer)
    then: "the overhead ratio is #expectedRatio"
    filledOffer.getOverheadRatio() == expectedRatio.doubleValue()
    where:
    category                              | expectedRatio
    AffiliationCategory.INTERNAL          | 0.0
    AffiliationCategory.EXTERNAL_ACADEMIC | 0.2
    AffiliationCategory.EXTERNAL          | 0.4
  }

  def "when the offers product items are converted to offer items, then every product item is represented in an offer item"() {
    given:
    def item1 = new ProductItem(createDataAnalysisProduct(), 4.5, 0.0, 0.0)
    def productItems = [item1]
    OfferV2 offer = new OfferV2()
    offer.setItems(productItems)
    when: "the offers product items are converted to offer items"
    def offerItems = offerCalculus.createOfferItems(offer)
    then: "every product item is represented in an offer item"
    offerItems.stream()
            .filter(offerItem ->
                    productItems.stream()
                            .anyMatch(it -> equals(it, offerItem))
            )
            .findAny()
            .isPresent()

  }

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

  Product createDataAnalysisProduct() {
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
}
