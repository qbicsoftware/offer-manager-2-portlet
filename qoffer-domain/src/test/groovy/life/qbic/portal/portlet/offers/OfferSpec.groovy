package life.qbic.portal.portlet.offers

import life.qbic.datamodel.dtos.business.Affiliation
import life.qbic.datamodel.dtos.business.AffiliationCategory
import life.qbic.datamodel.dtos.business.Customer
import life.qbic.datamodel.dtos.business.ProductItem
import life.qbic.datamodel.dtos.business.ProjectManager
import life.qbic.datamodel.dtos.business.services.PrimaryAnalysis
import life.qbic.datamodel.dtos.business.services.ProductUnit
import life.qbic.datamodel.dtos.business.services.ProjectManagement
import life.qbic.portal.portlet.offers.Offer
import spock.lang.Shared
import spock.lang.Specification

/**
 * Test for the business rules in offer calculus at QBiC.
 *
 * @since 1.0.0
 */
class OfferSpec extends Specification {

    @Shared
    Affiliation externalAcademicAffiliation
    @Shared
    Affiliation externalAffiliation
    @Shared
    Affiliation internalAffiliation
    @Shared
    Customer customerWithAllAffiliations

    def setup() {
        internalAffiliation = new Affiliation.Builder("Uni Tübingen", "Auf der " +
                "Morgenstelle 10", "72076", "Tuebingen").category(AffiliationCategory.INTERNAL).build()
        externalAcademicAffiliation = new Affiliation.Builder("Uni Frankfurt",
                "Irgendwo im Nirgendwo 20", "12345", "Frankfurt").category(AffiliationCategory
                .EXTERNAL_ACADEMIC)
                .build()
         externalAffiliation = new Affiliation.Builder("Company Frankfurt",
                "Irgendwo " +
                        "im Nirgendwo 20", "12345", "Frankfurt").category(AffiliationCategory.EXTERNAL)
                .build()
        customerWithAllAffiliations = new Customer.Builder("Max", "Mustermann", "max" +
                ".mustermann@qbic.uni-tuebingen.de").affiliations([internalAffiliation, externalAffiliation]).build()
    }

    def "A customer with an internal affiliation shall pay no overheads"() {
        given:
        List<ProductItem> items = [new ProductItem(2, new PrimaryAnalysis("Basic RNAsq", "Just an" +
                " " +
                "example", 1.0, ProductUnit.PER_SAMPLE)),
                                  new ProductItem(1, new ProjectManagement("Basic Management",
                                          "Just an example", 10.0, ProductUnit.PER_DATASET))]
        and:
        ProjectManager manager  = new ProjectManager.Builder("Maxime", "Musterfrau", "max" +
                ".musterfrau@qbic.uni-tuebingen.de").build()

        Offer offer = new Offer.Builder(customerWithAllAffiliations, manager, "Awesome Project", "An awesome" +
                " project", items, internalAffiliation).build()

        when:
        double totalCosts = offer.getTotalCosts()

        then:
        totalCosts == (double) 10.0 + 2 * 1.0
    }

    def "A customer with an external academic affiliation shall pay 20% overheads and 19% VAT"() {
        given:
        List<ProductItem> items = [new ProductItem(2, new PrimaryAnalysis("Basic RNAsq", "Just an" +
                " " +
                "example", 1.0, ProductUnit.PER_SAMPLE)),
                                   new ProductItem(1, new ProjectManagement("Basic Management",
                                           "Just an example", 10.0, ProductUnit.PER_DATASET))]
        and:
        ProjectManager manager  = new ProjectManager.Builder("Maxime", "Musterfrau", "max" +
                ".musterfrau@qbic.uni-tuebingen.de").build()

        Offer offer = new Offer.Builder(customerWithAllAffiliations, manager, "Awesome Project", "An awesome" +
                " project", items, externalAcademicAffiliation).build()

        when:
        double overhead = offer.getOverheadSum()
        double taxes = offer.getVatSum()
        double totalCosts = offer.getTotalCosts()
        double netSum = offer.getTotalNetPrice()

        then:
        double expectedNetSum = (10.0 + (2 * 1.0 + 2 * 1.0 * 0.2))
        double expectedOverhead = 2 * 1.0 * 0.3
        netSum == expectedNetSum
        overhead == expectedOverhead
        totalCosts == (double) expectedNetSum + (expectedNetSum * 0.19)
    }

}
