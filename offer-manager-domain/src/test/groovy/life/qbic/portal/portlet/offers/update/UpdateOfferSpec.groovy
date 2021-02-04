package life.qbic.portal.portlet.offers.update

import life.qbic.business.offers.update.UpdateOffer
import life.qbic.business.offers.update.UpdateOfferDataSource
import life.qbic.business.offers.update.UpdateOfferOutput
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
 * <short description>
 *
 * <detailed description>
 *
 * @since: 1.0.0
 * @author: Tobias Koch
 */
class UpdateOfferSpec extends Specification {
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


    def setup(){
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
    }

    def "Updated an offer is successful"(){
        given:
        UpdateOfferOutput output = Mock(UpdateOfferOutput.class)
        UpdateOfferDataSource ds = Stub(UpdateOfferDataSource.class)
        UpdateOffer updateOffer = new UpdateOffer(ds,output)

        and:
        List<ProductItem> items = [
                new ProductItem(2, new PrimaryAnalysis("Basic RNAsq", "Just an" +
                        " example", 1.0, ProductUnit.PER_SAMPLE, "1")),
                new ProductItem(1, new ProjectManagement("Basic Management",
                        "Just an example", 10.0, ProductUnit.PER_DATASET, "1"))
        ]

        Offer oldOffer = new Offer.Builder(customer, projectManager, projectTitle, projectDescription, selectedAffiliation)
                .modificationDate(date).expirationDate(date).items([items[0]]).identifier(new OfferId("Conserved","abcd","1"))
                .build()

        Offer newOffer = new Offer.Builder(customer, projectManager, projectTitle, projectDescription, selectedAffiliation)
                .modificationDate(date).expirationDate(date).items(items)
                .build()

        when:
        updateOffer.updateExistingOffer(newOffer,oldOffer)

        then:
        1* output.updatedOffer(_)
    }

    def "Unchanged offer does not lead to a database entry"(){
        given:
        UpdateOfferOutput output = Mock(UpdateOfferOutput.class)
        UpdateOfferDataSource ds = Stub(UpdateOfferDataSource.class)
        UpdateOffer updateOffer = new UpdateOffer(ds,output)

        and:
        List<ProductItem> items = [
                new ProductItem(2, new PrimaryAnalysis("Basic RNAsq", "Just an" +
                        " example", 1.0, ProductUnit.PER_SAMPLE, "1")),
                new ProductItem(1, new ProjectManagement("Basic Management",
                        "Just an example", 10.0, ProductUnit.PER_DATASET, "1"))
        ]

        Offer oldOffer = new Offer.Builder(customer, projectManager, projectTitle, projectDescription, selectedAffiliation)
                .modificationDate(date).expirationDate(date).items([items[0]]).identifier(new OfferId("Conserved","abcd","1"))
                .build()

        when:
        updateOffer.updateExistingOffer(oldOffer,oldOffer)

        then:
        0* output.updatedOffer(_)
        1* output.failNotification("An unchanged offer cannot be updated")
    }
}
