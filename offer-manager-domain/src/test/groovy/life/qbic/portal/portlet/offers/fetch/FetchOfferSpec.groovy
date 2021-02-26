package life.qbic.portal.portlet.offers.fetch

import life.qbic.business.exceptions.DatabaseQueryException
import life.qbic.business.offers.fetch.FetchOffer
import life.qbic.business.offers.fetch.FetchOfferDataSource
import life.qbic.business.offers.fetch.FetchOfferOutput
import life.qbic.datamodel.dtos.business.Affiliation
import life.qbic.datamodel.dtos.business.Customer
import life.qbic.datamodel.dtos.business.Offer
import life.qbic.datamodel.dtos.business.OfferId
import life.qbic.datamodel.dtos.business.ProductItem
import life.qbic.datamodel.dtos.business.ProjectManager
import life.qbic.datamodel.dtos.business.services.PrimaryAnalysis
import life.qbic.datamodel.dtos.business.services.ProductUnit
import life.qbic.datamodel.dtos.business.services.ProjectManagement
import spock.lang.Shared
import spock.lang.Specification


/**
 * This test class tests for the {@link life.qbic.business.offers.fetch.FetchOffer} use case functionality
 *
 * Given information about an Offer a User wants to retrieve from the database
 *
 * @since: 1.0.0
 */
class FetchOfferSpec extends Specification {

    @Shared
    Date date
    @Shared
    Customer customer
    @Shared
    ProjectManager projectManager
    @Shared
    Affiliation selectedAffiliation
    @Shared
    String projectTitle
    @Shared
    String projectDescription
    @Shared
    List<ProductItem> items
    @Shared
    OfferId offerId

    def setup() {
        date = new Date(1000, 10, 10)
        customer = new Customer.Builder("Max", "Mustermann", "").build()
        projectManager = new ProjectManager.Builder("Max", "Mustermann", "").build()
        selectedAffiliation = new Affiliation.Builder("Universität Tübingen",
                "Auf der Morgenstelle 10",
                "72076",
                "Tübingen")
                .build()
        projectTitle = "Archer"
        projectDescription = "Cartoon Series"
        items = [
                new ProductItem(2, new PrimaryAnalysis("Basic RNAsq", "Just an" +
                        " example", 1.0, ProductUnit.PER_SAMPLE, "1")),
                new ProductItem(1, new ProjectManagement("Basic Management",
                        "Just an example", 10.0, ProductUnit.PER_DATASET, "1"))
        ]
        offerId = new OfferId("Conserved", "abcd", "2")
    }

    def "Database contained an Offer with the provided Id "() {
        given:
        FetchOfferOutput output = Mock()
        FetchOfferDataSource ds = Stub(FetchOfferDataSource.class)
        FetchOffer fetchOffer = new FetchOffer(ds, output)

        ds.fetchAllVersionsForOfferId(_ as OfferId) >> [new OfferId("Conserved", "abcd", "1")]
        ds.getOffer(_ as OfferId) >> Optional.of(new Offer.Builder(customer, projectManager, projectTitle, projectDescription, selectedAffiliation)
                .modificationDate(date).expirationDate(date).items([items[0]]).identifier(offerId)
                .build())

        when:
        fetchOffer.fetchOffer(offerId)

        then:
        1 * output.fetchedOffer(_)

    }

    def "Output returns failure Notification when the database throws an exception"() {
        given:
        FetchOfferOutput output = Mock()
        FetchOfferDataSource ds = Stub(FetchOfferDataSource.class)
        FetchOffer fetchOffer = new FetchOffer(ds, output)

        ds.getOffer(offerId) >> { throw new DatabaseQueryException("Could not retrieve Offer with OfferId $offerId from the Database") }

        when:
        fetchOffer.fetchOffer(offerId)

        then:
        1 * output.failNotification(_)
    }

    def "The retrieved Offer DTO contains the calculated results of the Offer entity method"() {
        given:
        FetchOfferOutput output = Mock()
        FetchOfferDataSource ds = Stub(FetchOfferDataSource.class)
        FetchOffer fetchOffer = new FetchOffer(ds, output)

        ds.fetchAllVersionsForOfferId(_ as OfferId) >> [new OfferId("Conserved", "abcd", "1")]
        ds.getOffer(_ as OfferId) >> Optional.of(new Offer.Builder(customer, projectManager, projectTitle, projectDescription, selectedAffiliation)
                .modificationDate(date).expirationDate(date).items([items[0]]).identifier(offerId)
                .build())

        when:
        fetchOffer.fetchOffer(offerId)

        then:
        // Checks if the Offer DTO Fields are filled by the Offer Entity during the Fetch Offer Use Case
        1 * output.fetchedOffer(_) >> { arguments ->
            final Offer retrievedOffer = arguments.get(0)
            assert retrievedOffer.getOverheads() != 0
            assert retrievedOffer.getNetPrice() != 0
            assert retrievedOffer.getTotalPrice() != 0
            assert retrievedOffer.getTaxes() != 0
        }
    }
}
