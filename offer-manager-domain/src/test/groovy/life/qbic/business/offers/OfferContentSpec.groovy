package life.qbic.business.offers

import life.qbic.datamodel.dtos.business.Affiliation
import life.qbic.datamodel.dtos.business.AffiliationCategory
import life.qbic.datamodel.dtos.business.Customer
import life.qbic.datamodel.dtos.business.ProductItem
import life.qbic.datamodel.dtos.business.ProjectManager
import life.qbic.datamodel.dtos.business.services.PrimaryAnalysis
import life.qbic.datamodel.dtos.business.services.ProductUnit
import life.qbic.datamodel.dtos.business.services.ProjectManagement
import spock.lang.Shared
import spock.lang.Specification

/**
 * <h1>Tests for the offercontent dto</h1>
 *
 * @since 1.1.0
 *
*/
class OfferContentSpec extends Specification{

    @Shared
    Affiliation externalAcademicAffiliation
    @Shared
    Affiliation externalAffiliation
    @Shared
    Affiliation internalAffiliation
    @Shared
    Customer customerWithAllAffiliations
    @Shared
    ProjectManager projectManager
    @Shared
    ProjectManager projectManager2

    final List<OfferItem> items = [
            new OfferItem.Builder(2, "Just an example", "Basic RNAsq", 1.0, 1,
                    "QBiC","Sample", 1).build(),
            new OfferItem.Builder(2, "Just an example", "Basic RNAsq", 1.0, 1,
                    "QBiC","Dataset", 1).build(),

    ]

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
        projectManager  = new ProjectManager.Builder("Maxime", "Musterfrau", "max" +
                ".musterfrau@qbic.uni-tuebingen.de").build()
        projectManager2  = new ProjectManager.Builder("Max", "Mustermann", "max" +
                ".mustermann@qbic.uni-tuebingen.de").build()
    }

    def "An OfferContent with equal content is equal"(){
        when: "two offercontents with the exact same content"
        OfferContent one = new OfferContent.Builder(customerWithAllAffiliations,externalAcademicAffiliation,projectManager,
                "2021-10-11","2022-10-11","title","description",
                "experimental design","O_greiner_ksma_1")
        .totalVat(3333)
        .netCost(222)
        .totalCost(2222)
        .netDataAnalysis(111)
        .netDataGeneration(222)
        .netProjectManagementAndDataStorage(2)
        .overheadsPMandDS(33)
        .overheadsDataGeneration(444)
        .overheadsDataAnalysis(33)
        .overheadTotal(333)
        .dataManagementItems(items)
        .dataAnalysisItems([])
        .dataGenerationItems(items)
        .build()

        OfferContent two = new OfferContent.Builder(customerWithAllAffiliations,externalAcademicAffiliation,projectManager,
                "2021-10-11","2022-10-11","title","description",
                "experimental design","O_greiner_ksma_1")
                .totalVat(3333)
                .netCost(222)
                .totalCost(2222)
                .netDataAnalysis(111)
                .netDataGeneration(222)
                .netProjectManagementAndDataStorage(2)
                .overheadsPMandDS(33)
                .overheadsDataGeneration(444)
                .overheadsDataAnalysis(33)
                .overheadTotal(333)
                .dataManagementItems(items)
                .dataAnalysisItems([])
                .dataGenerationItems(items)
                .build()

        then: "both are equal"
        one == two
    }

    def "An OfferContent with different content are not equal"(){
        when: "two offercontents with the exact same content"
        OfferContent one = new OfferContent.Builder(customerWithAllAffiliations,externalAcademicAffiliation,projectManager,
                "2021-10-11","2022-10-11","title","description",
                "experimental design","O_greiner_ksma_1")
                .totalVat(3333)
                .netCost(222)
                .totalCost(111)
                .netDataAnalysis(111)
                .netDataGeneration(222)
                .netProjectManagementAndDataStorage(2)
                .overheadsPMandDS(33)
                .overheadsDataGeneration(444)
                .overheadsDataAnalysis(33)
                .overheadTotal(333)
                .dataManagementItems(items)
                .dataAnalysisItems(items)
                .dataGenerationItems(items)
                .build()

        OfferContent two = new OfferContent.Builder(customerWithAllAffiliations,externalAcademicAffiliation,projectManager,
                "2021-10-11","2022-10-11","title","description",
                "experimental design","O_greiner_ksma_1")
                .totalVat(3333)
                .netCost(222)
                .totalCost(2222)
                .netDataAnalysis(111)
                .netDataGeneration(222)
                .netProjectManagementAndDataStorage(0)
                .overheadsPMandDS(33)
                .overheadsDataGeneration(444)
                .overheadsDataAnalysis(33)
                .overheadTotal(333)
                .dataManagementItems(items)
                .dataAnalysisItems(items)
                .dataGenerationItems([])
                .build()

        then: "both are equal"
        one != two
    }
}