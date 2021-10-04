package life.qbic.business.offers


import life.qbic.datamodel.dtos.business.Affiliation
import life.qbic.datamodel.dtos.business.AffiliationCategory
import life.qbic.datamodel.dtos.business.services.*
import spock.lang.Shared
import spock.lang.Specification

/**
 * <class short description - 1 Line!>
 *
 * <More detailed description - When to use, what it solves, etc.>
 *
 * @since <version tag>
 */
class TaxOfficeSpec extends Specification{
    @Shared
    Affiliation internalAffiliation
    @Shared
    Affiliation externalAcademicAffiliation
    @Shared
    Affiliation externalAffiliation

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
        BigDecimal taxesAmount = taxOffice.applyTaxes(serviceCosts, clazz)

        then:
        taxesAmount == BigDecimal.valueOf(0)

        where:
        clazz << [Sequencing,
                  PrimaryAnalysis,
                  SecondaryAnalysis,
                  DataStorage,
                  ProjectManagement,
                  ProteomicAnalysis,
                  MetabolomicAnalysis]
    }

    def "An external customer has to pay taxes on every service"() {
        given:
        BigDecimal serviceCosts = BigDecimal.valueOf(20)
        TaxOffice taxOffice = new TaxOffice(externalAcademicAffiliation)

        when:
        BigDecimal taxesAmount = taxOffice.applyTaxes(serviceCosts, clazz)

        then:
        taxesAmount == BigDecimal.valueOf(0.19 * 20)

        where:
        clazz << [Sequencing,
                  PrimaryAnalysis,
                  SecondaryAnalysis,
                  DataStorage,
                  ProjectManagement,
                  ProteomicAnalysis,
                  MetabolomicAnalysis]
    }

    def "An external academic customer has to pay taxes on every service"() {
        given:
        BigDecimal serviceCosts = BigDecimal.valueOf(20)
        TaxOffice taxOffice = new TaxOffice(externalAffiliation)

        when:
        BigDecimal taxesAmount = taxOffice.applyTaxes(serviceCosts, clazz)

        then:
        taxesAmount == BigDecimal.valueOf(0.19 * 20)

        where:
        clazz << [Sequencing,
                  PrimaryAnalysis,
                  SecondaryAnalysis,
                  DataStorage,
                  ProjectManagement,
                  ProteomicAnalysis,
                  MetabolomicAnalysis]
    }

}
