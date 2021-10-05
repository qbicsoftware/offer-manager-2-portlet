package life.qbic.business.offers


import life.qbic.datamodel.dtos.business.Affiliation
import life.qbic.datamodel.dtos.business.AffiliationCategory
import life.qbic.datamodel.dtos.business.facilities.Facility
import life.qbic.datamodel.dtos.business.services.*
import spock.lang.Shared
import spock.lang.Specification

/**
 * Tests for the TaxOffice class
 *
 * @since 1.1.5
 */
class TaxOfficeSpec extends Specification{
    @Shared
    Affiliation internalAffiliation
    @Shared
    Affiliation externalAcademicAffiliation
    @Shared
    Affiliation externalAffiliation
    @Shared
    Product primaryAnalysis = new PrimaryAnalysis("", "", 0, ProductUnit.PER_FLOW_CELL, "1")
    @Shared
    Product secondaryAnalysis = new SecondaryAnalysis("", "", 0, ProductUnit.PER_FLOW_CELL, "1")
    @Shared
    Product sequencing = new Sequencing("", "", 0, ProductUnit.PER_FLOW_CELL, "1")
    @Shared
    Product proteomicAnalysis = new ProteomicAnalysis("", "", 0, ProductUnit.PER_FLOW_CELL, "1")
    @Shared
    Product metabolomicAnalysis = new MetabolomicAnalysis("", "", 0, ProductUnit.PER_FLOW_CELL, "1")
    @Shared
    Product dataStorage= new DataStorage("", "", 0, ProductUnit.PER_FLOW_CELL, "1")
    @Shared
    Product projectManagement = new ProjectManagement("", "", 0, ProductUnit.PER_FLOW_CELL, "1")
    @Shared
    Product externalService = new ExternalServiceProduct("", "", 0, 0, ProductUnit.PER_FLOW_CELL, 1L, Facility.CEGAT)

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
    }

    def "An internal customer has to pay no taxes on internal services"() {
        given:
        BigDecimal serviceCosts = BigDecimal.valueOf(20)
        TaxOffice taxOffice = new TaxOffice(internalAffiliation)

        when:
        BigDecimal taxesAmount = taxOffice.applyTaxes(serviceCosts, product)

        then:
        taxesAmount == BigDecimal.valueOf(0)

        where:
        product << [sequencing,
                    primaryAnalysis,
                    secondaryAnalysis,
                    dataStorage,
                    projectManagement,
                    proteomicAnalysis,
                    metabolomicAnalysis]
    }
def "An internal customer has to pay taxes on external services"() {
        given:
        BigDecimal serviceCosts = BigDecimal.valueOf(20)
        TaxOffice taxOffice = new TaxOffice(internalAffiliation)

        when:
        BigDecimal taxesAmount = taxOffice.applyTaxes(serviceCosts, product)

        then:
        taxesAmount == BigDecimal.valueOf(20*0.19)

        where:
        product << [externalService]
    }
    def "An external customer has to pay taxes on every service"() {
        given:
        BigDecimal serviceCosts = BigDecimal.valueOf(20)
        TaxOffice taxOffice = new TaxOffice(externalAcademicAffiliation)

        when:
        BigDecimal taxesAmount = taxOffice.applyTaxes(serviceCosts, product)

        then:
        taxesAmount == BigDecimal.valueOf(0.19 * 20)

        where:
        product << [sequencing,
                    primaryAnalysis,
                    secondaryAnalysis,
                    dataStorage,
                    projectManagement,
                    proteomicAnalysis,
                    metabolomicAnalysis,
                    externalService]
    }

    def "An external academic customer has to pay taxes on every service"() {
        given:
        BigDecimal serviceCosts = BigDecimal.valueOf(20)
        TaxOffice taxOffice = new TaxOffice(externalAffiliation)

        when:
        BigDecimal taxesAmount = taxOffice.applyTaxes(serviceCosts, product)

        then:
        taxesAmount == BigDecimal.valueOf(0.19 * 20)

        where:
        product << [sequencing,
                    primaryAnalysis,
                    secondaryAnalysis,
                    dataStorage,
                    projectManagement,
                    proteomicAnalysis,
                    metabolomicAnalysis,
                    externalService]
    }

}
