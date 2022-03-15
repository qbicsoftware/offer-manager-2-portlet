package life.qbic.business.offers.fetch


import life.qbic.business.offers.OfferV2
import life.qbic.business.offers.identifier.OfferId
import life.qbic.business.persons.affiliation.Affiliation
import life.qbic.business.persons.affiliation.AffiliationCategory
import life.qbic.business.products.Product
import life.qbic.business.products.ProductCategory
import life.qbic.business.products.ProductItem
import spock.lang.Specification

/**
 * This test class tests for the {@link life.qbic.business.offers.fetch.FetchOffer} use case functionality
 *
 * Given information about an Offer a User wants to retrieve from the database
 *
 * @since: 1.0.0
 */
class FetchOfferSpec extends Specification {
  private FetchOfferDataSource datasource = Stub()
  private FetchOfferOutput output = Mock()
  private OfferId offerId = new OfferId("projectPart", 1)
  private OfferV2 offerV2 = new OfferV2(new Affiliation("", "", "", "", "", "Germany", AffiliationCategory.INTERNAL))

  def "given the offer exists in the datasource, when the offer is requested, then the offer is returned"() {
    given: "the offer identifier and corresponding offer"
    offerV2.setItems([mockProductItem()])
    and: "the offer exists in the datasource"
    datasource.getOffer(offerId) >> Optional.of(offerV2)
    when: "the offer is requested"
    FetchOffer fetchOffer = new FetchOffer(datasource, output)
    fetchOffer.fetchOffer(offerId)
    then: "the offer is returned"
    1 * output.fetchedOffer({ OfferV2 it ->
      it.getSelectedCustomerAffiliation() == offerV2.getSelectedCustomerAffiliation()
      it.getIdentifier() == offerV2.getIdentifier()
    })
  }

  def "given a failing datasource, when the offer is requested, then a failure notice is returned"() {
    given: "a failing data source"
    datasource.getOffer(offerId) >> { throw new RuntimeException("Test exception") }
    when: "the offer is requested"
    FetchOffer fetchOffer = new FetchOffer(this.datasource, output)
    fetchOffer.fetchOffer(offerId)

    then: "a failure notice is returned"
    0 * output.fetchedOffer(_)
    1 * output.failNotification(_)
  }

  def "when the offer is requested, then the returned offer has price information loaded"() {
    given: "the offer identifier and corresponding offer"
    offerV2.setItems([mockProductItem()])
    and: "the offer exists in the datasource"
    datasource.getOffer(this.offerId) >> Optional.of(offerV2)
    when: "the offer is requested"
    FetchOffer fetchOffer = new FetchOffer(datasource, output)
    fetchOffer.fetchOffer(this.offerId)

    then: "the returned offer has price information loaded"
    1 * output.fetchedOffer(_) >> { arguments ->
            final OfferV2 retrievedOffer = arguments.get(0)
            println retrievedOffer.overhead
            assert retrievedOffer.getOverhead() >= 0
            assert retrievedOffer.getTotalNetPrice() >= 0
            assert retrievedOffer.getTotalCost() >= 0
            assert retrievedOffer.getTotalVat() >= 0
        }
  }

  private ProductItem mockProductItem() {
    Product product = new Product()
    product.description = ""
    product.productName = ""
    product.serviceProvider = ""
    product.unit = ""
    product.setInternalUnitPrice(0.5)
    product.setExternalUnitPrice(1.0)
    product.setCategory(ProductCategory.PRIMARY_BIOINFO.getLabel())
    return new ProductItem(product, 2)
  }
}
