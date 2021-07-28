package life.qbic.portal.portlet.offers.create

import life.qbic.business.offers.Converter
import life.qbic.business.offers.create.CreateOffer
import life.qbic.business.offers.create.CreateOfferDataSource
import life.qbic.business.offers.create.CreateOfferOutput
import life.qbic.business.offers.identifier.ProjectPart
import life.qbic.business.offers.identifier.RandomPart
import life.qbic.business.offers.identifier.Version
import life.qbic.datamodel.dtos.business.*
import life.qbic.datamodel.dtos.business.facilities.Facility
import life.qbic.datamodel.dtos.business.services.*
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
class CreateOfferSpec extends Specification {
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

    def "Creating an offer is successful"(){
        given:
        CreateOfferOutput output = Mock(CreateOfferOutput.class)
        CreateOfferDataSource ds = Stub(CreateOfferDataSource.class)
        CreateOffer createOffer = new CreateOffer(ds,output)

        and:
        List<ProductItem> items = [
                new ProductItem(2, new PrimaryAnalysis("Basic RNAsq", "Just an" +
                        " example", 1.0, ProductUnit.PER_SAMPLE, "1")),
                new ProductItem(1, new ProjectManagement("Basic Management",
                        "Just an example", 10.0, 10.0, ProductUnit.PER_DATASET, 1, Facility.QBIC))
        ]

        Offer offer = new Offer.Builder(customer, projectManager, projectTitle, projectDescription, selectedAffiliation)
                .modificationDate(date).expirationDate(date).items([items[0]]).experimentalDesign("A design")
                .build()

        when:
        createOffer.createOffer(offer)

        then:
        1* output.createdNewOffer(_)
    }

    def "calculate offer price for internal affiliations correctly"(){
        given:
        CreateOfferOutput output = Mock(CreateOfferOutput)
        CreateOffer createOffer = new CreateOffer(Stub(CreateOfferDataSource),output)

        and:
        List<ProductItem> items = [new ProductItem(1,new Sequencing("This is a sequencing package", "a short description",1.4, 1.4, ProductUnit.PER_SAMPLE, 1, Facility.QBIC)),
                                   new ProductItem(1,new Sequencing("This is a sequencing package", "a short description",1.4, 1.4, ProductUnit.PER_SAMPLE, 1, Facility.QBIC))]
        when:
        createOffer.calculatePrice(items, new Affiliation.Builder("Test", "", "", "")
                .category(AffiliationCategory.INTERNAL).build())

        then:
        1 * output.calculatedPrice(2.8, 0, 0, 2.8)
    }

    def "Taxes for Affilations outside of Germany are set to 0"(){
        given:
        CreateOfferOutput output = Mock(CreateOfferOutput)
        CreateOffer createOffer = new CreateOffer(Stub(CreateOfferDataSource),output)

        and:
        List<ProductItem> items = [new ProductItem(1,new Sequencing("This is a sequencing package", "a short description",4, 4, ProductUnit.PER_SAMPLE, 1, Facility.QBIC)),
                                   new ProductItem(1,new Sequencing("This is a sequencing package", "a short description",4, 4, ProductUnit.PER_SAMPLE, 1, Facility.QBIC))]
        when:
        createOffer.calculatePrice(items, new Affiliation.Builder("Test", "", "", "").country("France")
                .category(AffiliationCategory.EXTERNAL).build())

        then:
        1 * output.calculatedPrice(8.0, 0, 3.2, 11.2)
    }


    def "Creating an Offer DTO from the Offer Entity works correctly"() {
        given:
        ProductItem primaryAnalysis = new ProductItem(2, new PrimaryAnalysis("Basic RNAsq", "Just an" +
                " example primary analysis", 1.0, 1.0, ProductUnit.PER_SAMPLE, 1, Facility.QBIC))
        ProductItem secondaryAnalysis = new ProductItem(1, new SecondaryAnalysis("Basic RNAsq", "Just an" +
                " example secondary analysis", 2.0, 2.0, ProductUnit.PER_SAMPLE, 1, Facility.QBIC))
        ProductItem sequencing = new ProductItem(3, new Sequencing("Basic Sequencing", "Just an" +
                "example sequencing", 3.0, 3.0, ProductUnit.PER_SAMPLE, 1, Facility.QBIC))
        ProductItem projectManagement = new ProductItem(1, new ProjectManagement("Basic Management",
                "Just an example", 10.0, 10.0, ProductUnit.PER_DATASET, 1, Facility.QBIC))
        ProductItem dataStorage = new ProductItem(2, new DataStorage("Data Storage",
                "Just an example", 20.0, 20.0, ProductUnit.PER_DATASET, 1, Facility.QBIC))
        List<ProductItem> items = [primaryAnalysis, projectManagement, sequencing, dataStorage, secondaryAnalysis]
        life.qbic.business.offers.identifier.OfferId offerId = new life.qbic.business.offers.identifier.OfferId (new RandomPart(), new ProjectPart("test"), new Version(0))
        and:
        life.qbic.business.offers.Offer offerEntity = new life.qbic.business.offers.Offer.Builder(customer, projectManager, "Awesome Project", "An " +
                "awesome project", items, selectedAffiliation).identifier(offerId).build()

        when:
        final offerDto  = Converter.convertOfferToDTO(offerEntity)

        then:
        offerDto.projectManager == offerEntity.getProjectManager()
        offerDto.customer == offerEntity.getCustomer()
        offerDto.projectTitle == offerEntity.getProjectTitle()
        offerDto.projectObjective == offerEntity.getProjectObjective()
        offerDto.selectedCustomerAffiliation == offerEntity.getSelectedCustomerAffiliation()
        offerDto.modificationDate == offerEntity.getModificationDate()
        offerDto.expirationDate == offerEntity.getExpirationDate()
        offerDto.items == offerEntity.getItems()
        offerDto.itemsWithOverhead == offerEntity.getOverheadItems()
        offerDto.itemsWithoutOverhead == offerEntity.getNoOverheadItems()
        offerDto.totalPrice == offerEntity.getTotalCosts()
        offerDto.overheads == offerEntity.getOverheadSum()
        offerDto.itemsWithOverheadNetPrice == offerEntity.getOverheadItemsNet()
        offerDto.itemsWithoutOverheadNetPrice == offerEntity.getNoOverheadItemsNet()
    }
}
