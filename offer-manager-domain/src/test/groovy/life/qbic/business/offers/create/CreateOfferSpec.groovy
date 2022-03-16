package life.qbic.business.offers.create

import life.qbic.business.exceptions.DatabaseQueryException
import life.qbic.business.offers.OfferExistsException
import life.qbic.business.offers.OfferV2
import life.qbic.business.offers.identifier.OfferId
import life.qbic.business.persons.affiliation.Affiliation
import life.qbic.business.persons.affiliation.AffiliationCategory
import life.qbic.business.products.Product
import life.qbic.business.products.ProductCategory
import life.qbic.business.products.ProductItem
import spock.lang.Specification

import static org.junit.jupiter.api.Assertions.assertNotNull

/**
 * <short description>
 *
 * <detailed description>
 *
 * @since 1.0.0
 * @author Tobias Koch
 */
class CreateOfferSpec extends Specification {

    private CreateOfferDataSource datasource = Stub()
    private CreateOfferOutput output = Mock()
    private OfferId offerId = new OfferId("projectPart", 1)
    private OfferV2 offerV2 = new OfferV2(
            new Affiliation("", "", "", "", "", "Germany", AffiliationCategory.INTERNAL),
            offerId)

    def "given the offer does not exist in the datasource, when the offer is created, then a OfferExistsException is thrown"() {
        given: "the offer does not exist in the datasource"
        datasource = Mock()
        datasource.store(_ as OfferV2) >> {throw new RuntimeException("unable to store this")}
        datasource.store({ OfferV2 it -> it.identifier == offerId } as OfferV2) >> void
        when: "the offer is requested"
        CreateOffer createOffer = new CreateOffer(datasource, output)
        createOffer.createOffer(offerV2)
        then: "the datasource is queried"
        1 * datasource.store({ OfferV2 it -> it.identifier == offerId })

        and: "the offer is returned"
        1 * output.createdNewOffer({ OfferV2 it -> it.identifier == offerId })
    }

    def "given the offer exists in the datasource, when the offer is created, then a failure notification is sent"() {
        given: "the offer exists in the datasource"
        datasource = Stub()
        datasource.store(_ as OfferV2) >> {throw new OfferExistsException("offer supposedly exists")}
        when: "the offer is requested"
        CreateOffer createOffer = new CreateOffer(datasource, output)
        createOffer.createOffer(offerV2)
//        then:"the database is queried"
//        1 * datasource.store({ OfferV2 it -> it.identifier == offerId } as OfferV2)
        then: "a failure notification is sent"
        1 * output.failNotification(_)
    }

    def "given a failing datasource, when the offer is created, then a failure notification is sent"() {
        given: "the offer exists in the datasource"
        datasource = Mock()
        datasource.store(_ as OfferV2) >> {throw new DatabaseQueryException()}
        when: "the offer is requested"
        CreateOffer createOffer = new CreateOffer(datasource, output)
        createOffer.createOffer(offerV2)
        then: "a failure notification is sent"
        1 * output.failNotification(_)
    }


    def "when the offer is created, then the stored offer has price information loaded"() {
        given: "the offer with items"
        ArrayList<ProductItem> productItems = [mockProductItem()]
        offerV2.setItems(productItems)
        when: "the offer is requested"
        CreateOffer createOffer = new CreateOffer(datasource, output)
        createOffer.createOffer(offerV2)
        then: "a failure notification is sent"
        1 * output.createdNewOffer(_) >> { arguments ->
            final OfferV2 storedOffer = arguments.get(0)
            assertNotNull(storedOffer.getOverhead())
            assertNotNull(storedOffer.getTotalNetPrice())
            assertNotNull(storedOffer.getTotalCost())
            assertNotNull(storedOffer.getTotalVat())
        }
    }

    private OfferV2 existingOffer = offerV2
    private OfferV2 updatedOffer = {
        def offer = OfferV2.copyOf(offerV2)
        offer.setProjectTitle("Different Project Title")
        return offer
    } as OfferV2

    def "given a datasource providing an offer, when the use case updates the offer, then a success notification is sent"() {
        given: "a datasource providing an offer"
        datasource.getOffer(existingOffer.getIdentifier())
        when: "the use case updates the offer"
        CreateOffer createOffer = new CreateOffer(datasource, output)
        createOffer.updateOffer(updatedOffer)
        then: "a success notification is sent"
        and: "no failure notification is sent"
    }

    def "given a datasource without offers, when the use case updates the offer, then a failure notification is sent"() {
        given: "a datasource without offers"
        when: "the use case updates the offer"
        then: "a failure notification is sent"
        and: "no success notification is sent"
    }

    def "given an existing offer with identical content, when the use case updates the offer, then a failure notification is sent"() {
        given: "a datasource with an identical offer"
        when: "the use case updates the offer"
        then: "a failure notification is sent"
        and: "no success notification is sent"
    }

    def "given a datasource providing an offer, when the use case updates the offer, then the offer identifier increases its version by one"() {
        given: "a datasource providing an offer"
        when: "the use case updates the offer"
        then: "the offer identifier increases its version by one"
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
