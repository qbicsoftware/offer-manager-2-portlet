package life.qbic.business.offers.create

import life.qbic.business.exceptions.DatabaseQueryException
import life.qbic.business.offers.OfferExistsException
import life.qbic.business.offers.OfferV2
import life.qbic.business.offers.identifier.OfferId
import life.qbic.business.persons.Person
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

    private CreateOfferDataSource datasource = Mock()
    private CreateOfferOutput output = Mock()
    private OfferId offerId = new OfferId("projectPart", 1)
    private OfferV2 offerV2 = setupOfferV2()

    private OfferV2 setupOfferV2() {
        OfferV2 offer = new OfferV2(
                new Affiliation("", "", "", "", "", "Germany", AffiliationCategory.INTERNAL),
                offerId)
        offer.setCustomer(new Person("","","lastname","","",[]))
        return offer;
    }

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
        ArrayList<ProductItem> productItems = [mockProductItem(offerV2)]
        offerV2.setItems(productItems)
        when: "the offer is requested"
        CreateOffer createOffer = new CreateOffer(datasource, output)
        createOffer.createOffer(offerV2)
        then: "a failure notification is sent"
        1 * output.createdNewOffer(_) >> { arguments ->
            final OfferV2 storedOffer = arguments.get(0)
            assertNotNull(storedOffer.getOverhead())
            assertNotNull(storedOffer.getSalePrice())
            assertNotNull(storedOffer.getPriceAfterTax())
            assertNotNull(storedOffer.getTaxAmount())
        }
    }

    private OfferV2 existingOffer = offerV2
    private OfferV2 updatedOffer = offerCopyWithModifiedTitle()

    def "given a datasource providing an offer, when the use case updates the offer, then a success notification is sent"() {
        given: "a datasource providing an offer"
        datasource.getOffer(existingOffer.getIdentifier()) >> Optional.of(existingOffer)
        datasource.store(existingOffer) >> {throw new OfferExistsException("test exception")}
        when: "the use case updates the offer"
        CreateOffer createOffer = new CreateOffer(datasource, output)
        createOffer.updateOffer(updatedOffer)
        then: "a success notification is sent"
        1 * output.createdNewOffer({ it.getIdentifier() == updatedOffer.getIdentifier() })
        and: "no failure notification is sent"
        0 * output.failNotification(_)
    }

    def "given a datasource without offers, when the use case updates the offer, then a failure notification is sent"() {
        given: "a datasource without offers"
        datasource.getOffer(existingOffer.getIdentifier()) >> Optional.empty()
        when: "the use case updates the offer"
        CreateOffer createOffer = new CreateOffer(datasource, output)
        createOffer.updateOffer(updatedOffer)
        then: "a failure notification is sent"
        0 * output.createdNewOffer(_)
        and: "no success notification is sent"
        1 * output.failNotification(_)
    }

    def "given an existing offer with identical content, when the use case updates the offer, then a failure notification is sent"() {
        given: "a datasource with an identical offer"
        datasource.getOffer(existingOffer.getIdentifier()) >> Optional.of(existingOffer)
        datasource.store(_ as OfferV2) >> {
            args -> if (args.get(0).getIdentifier() == existingOffer.getIdentifier()) {
                throw  new OfferExistsException("offer exists")
            }
        }
        when: "the use case updates the offer"
        CreateOffer createOffer = new CreateOffer(datasource, output)
        createOffer.updateOffer(existingOffer)
        then: "a failure notification is sent"
        1 * output.failNotification(_)
        and: "no success notification is sent"
        0 * output.createdNewOffer(_)

    }

    def "given a datasource providing an offer, when the use case updates the offer, then the offer identifier increases its version by the latest plus one"() {
        given: "a datasource providing an offer"
        datasource.getOffer(existingOffer.getIdentifier()) >> Optional.of(existingOffer)
        and: "the provided offer is the latest version"
        datasource.fetchAllVersionsForOfferId(existingOffer.getIdentifier()) >> [existingOffer.getIdentifier()]
        when: "the use case updates the offer"
        CreateOffer createOffer = new CreateOffer(datasource, output)
        createOffer.updateOffer(existingOffer)
        then: "the offer identifier increases its version by one"
        1 * output.createdNewOffer({it.getIdentifier().getVersion() == existingOffer.getIdentifier().getVersion() + 1})
        and: "no failure is sent"
        0 * output.failNotification(_)
    }

    def "given a datasource providing an offer, when the use case updates the offer, then the offer prices are updated"() {
        given: "a datasource providing an offer"
        datasource.getOffer(existingOffer.getIdentifier()) >> Optional.of(existingOffer)
        and: "the provided offer is the latest version"
        datasource.fetchAllVersionsForOfferId(existingOffer.getIdentifier()) >> [existingOffer.getIdentifier()]
        when: "the use case updates the offer"
        CreateOffer createOffer = new CreateOffer(datasource, output)
        createOffer.updateOffer(existingOffer)
        then: "the offer costs are populated"
        1 * output.createdNewOffer(_) >> { arguments ->
            final OfferV2 storedOffer = arguments.get(0)
            assertNotNull(storedOffer.getOverhead())
            assertNotNull(storedOffer.getSalePrice())
            assertNotNull(storedOffer.getPriceAfterTax())
            assertNotNull(storedOffer.getTaxAmount())
        }
    }

    private OfferV2 offerCopyWithModifiedTitle() {
        def offer = OfferV2.copyOf(offerV2)
        offer.setProjectTitle("Different Project Title")
        return offer
    }


    private ProductItem mockProductItem(OfferV2 offer) {
        Product product = new Product()
        product.description = ""
        product.productName = ""
        product.serviceProvider = ""
        product.unit = ""
        product.setInternalUnitPrice(0.5)
        product.setExternalUnitPrice(1.0)
        product.setCategory(ProductCategory.PRIMARY_BIOINFO.getLabel())
        return new ProductItem(offer, product, 2)
    }
}
