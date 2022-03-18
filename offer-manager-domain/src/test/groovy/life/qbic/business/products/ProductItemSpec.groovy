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

    def "Given a product is a data storage service and the customer is internal, apply 100% discount to the unit price"() {

    }


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


