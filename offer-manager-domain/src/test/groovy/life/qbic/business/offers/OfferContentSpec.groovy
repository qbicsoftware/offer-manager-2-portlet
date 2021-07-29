package life.qbic.business.offers

import life.qbic.datamodel.dtos.business.AcademicTitleFactory
import life.qbic.datamodel.dtos.business.Affiliation
import life.qbic.datamodel.dtos.business.AffiliationCategory
import life.qbic.datamodel.dtos.business.Customer
import life.qbic.datamodel.dtos.business.ProjectManager
import spock.lang.Shared
import spock.lang.Specification

/**
 * <h1>Tests for the offercontent dto</h1>
 *
 * @since 1.1.0
 *
*/
class OfferContentSpec extends Specification{

    AcademicTitleFactory factory = new AcademicTitleFactory()

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

    final static List<OfferItem> items = [
            new OfferItem.Builder(2, "Just an example", "Basic RNAsq", 1.0, 1,
                    "QBiC","Sample", 1).build(),
            new OfferItem.Builder(2, "Just an example", "Basic RNAsq", 1.0, 1,
                    "QBiC","Dataset", 1).build(),

    ]

    final static List<OfferItem> items2 = [
            new OfferItem.Builder(4, "Just an example", "Basic RNAsq", 1.0, 1,
                    "QBiC","Sample", 1).build(),
            new OfferItem.Builder(2, "Just an example", "Basic RNAsq", 1.0, 1,
                    "QBiC","Dataset", 6).build(),

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
                ".mustermann@qbic.uni-tuebingen.de").affiliations([internalAffiliation, externalAcademicAffiliation]).build()
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
        one.customerFirstName == two.customerFirstName
        one.customerLastName == two.customerLastName
        one.customerTitle == two.customerTitle

        one.customerOrganisation == two.customerOrganisation
        one.customerStreet == two.customerStreet
        one.customerPostalCode == two.customerPostalCode
        one.customerCity == two.customerCity
        one.customerCountry == two.customerCountry
        /*Projectmanager*/
        one.projectManagerFirstName == two.projectManagerFirstName
        one.projectManagerLastName == two.projectManagerLastName
        one.projectManagerTitle == two.projectManagerTitle
        one.projectManagerEmail == two.projectManagerEmail

        /*Project Information*/
        one.creationDate == two.creationDate
        one.expirationDate == two.expirationDate
        one.projectTitle == two.projectTitle
        one.projectObjective == two.projectObjective
        one.experimentalDesign == two.experimentalDesign
        one.offerIdentifier == two.offerIdentifier

        /*Items*/
        one.dataGenerationItems == two.dataGenerationItems
        one.dataAnalysisItems == two.dataAnalysisItems
        one.dataManagementItems == two.dataManagementItems

        /*Overheads*/
        one.overheadTotal == two.overheadTotal
        one.overheadsDataGeneration == two.overheadsDataGeneration
        one.overheadsDataAnalysis == two.overheadsDataGeneration
        one.overheadsPMandDS == two.overheadsPMandDS

        /*Prices*/
        one.netDataGeneration == two.netDataGeneration
        one.netDataAnalysis == two.netDataAnalysis
        one.netPMandDS == two.netPMandDS
        one.totalCost == two.totalCost
        one.netCost == two.netCost
        one.totalVat == two.totalVat
    }

    def "An OfferContent with different content are not equal"(){
        when: "two offercontents with the exact same content"

        internalAffiliation = new Affiliation.Builder("Uni Tübingen", "Auf der " +
                "Morgenstelle 10", "72076", "Tuebingen").category(AffiliationCategory.INTERNAL).build()
        externalAcademicAffiliation = new Affiliation.Builder("organisation",
                "street", "77777", "City").category(AffiliationCategory
                .EXTERNAL_ACADEMIC)
                .build()
        customerWithAllAffiliations = new Customer.Builder("name", "last", "mail@i.de")
                .affiliations([internalAffiliation, externalAcademicAffiliation])
                .title(factory.getForString("Dr."))
                .build()
        projectManager  = new ProjectManager.Builder("pm name", "pm last", "mail@i.de")
                .title(factory.getForString("None"))
                .build()

        OfferContent one = new OfferContent.Builder(customerWithAllAffiliations,externalAcademicAffiliation,projectManager,
                "2021-10-11","2022-10-11","title","description",
                "experimental design","oabcd1")
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


        Affiliation internalAffiliation = new Affiliation.Builder("Uni Tübingen", "Auf der " +
                "Morgenstelle 10", "72076", "Tuebingen").category(AffiliationCategory.INTERNAL).build()
        Affiliation externalAcademicAffiliation2 = new Affiliation.Builder(customerOrganisation,
                customerStreet, customerPostalCode, customerCity).category(AffiliationCategory
                .EXTERNAL_ACADEMIC)
                .build()
        Customer customerWithAllAffiliations2 = new Customer.Builder(customerFirstName, customerLastName, "mail@i.de")
                .affiliations([internalAffiliation, externalAcademicAffiliation2])
                .title(factory.getForString(projectManagerTitle)).build()
        ProjectManager projectManager2  = new ProjectManager.Builder(projectManagerFirstName, projectManagerLastName, projectManagerEmail)
                .title(factory.getForString(projectManagerTitle)).build()

        OfferContent two = new OfferContent.Builder(customerWithAllAffiliations2,externalAcademicAffiliation2,projectManager2,
                creationDate,expirationDate,projectTitle,projectObjective,
                experimentalDesign,offerIdentifier)
                .totalVat(totalVat)
                .netCost(netCost)
                .totalCost(totalCost)
                .netDataAnalysis(netDataAnalysis)
                .netDataGeneration(netDataGeneration)
                .netProjectManagementAndDataStorage(netPMandDS)
                .overheadsPMandDS(overheadsPMandDS)
                .overheadsDataGeneration(overheadsDataGeneration)
                .overheadsDataAnalysis(overheadsDataAnalysis)
                .overheadTotal(overheadTotal)
                .dataManagementItems(dataManagementItems)
                .dataAnalysisItems(dataAnalysisItems)
                .dataGenerationItems(dataGenerationItems)
                .build()

        then: "both are equal"
        one != two

        where:
        customerFirstName | customerLastName | customerTitle | customerOrganisation | customerStreet | customerPostalCode |customerCity
        |customerCountry | projectManagerFirstName | projectManagerLastName | projectManagerTitle | projectManagerEmail | creationDate
        | expirationDate | projectTitle | projectObjective | experimentalDesign | offerIdentifier | dataGenerationItems |dataAnalysisItems
        | dataManagementItems | overheadTotal | overheadsDataGeneration | overheadsDataAnalysis | overheadsPMandDS | netDataGeneration
        | netDataAnalysis | netPMandDS | totalCost | netCost | totalVat
        "name_" | "last" | "Dr." | "organisation" | "street" | "77777" | "City"
                | "Country" | "pm name" | "pm last" | "None" | "mail@i.de" | "2021-11-11"
                | "2022-11-11" | "title" | "objective" | "design" | "oabcd1" | items |items
                | items | 333 | 444 | 444 | 444 | 444
                | 444 | 444 | 444 | 444 | 444
        "name" | "last_" | "Dr." | "organisation" | "street" | "77777" | "City"
                | "Country" | "pm name" | "pm last" | "None" | "mail@i.de" | "2021-11-11"
                | "2022-11-11" | "title" | "objective" | "design" | "oabcd1" | items |items
                | items | 333 | 444 | 444 | 444 | 444
                | 444 | 444 | 444 | 444 | 444
        "name" | "last" | "None" | "organisation" | "street" | "77777" | "City"
                | "Country" | "pm name" | "pm last" | "None" | "mail@i.de" | "2021-11-11"
                | "2022-11-11" | "title" | "objective" | "design" | "oabcd1" | items |items
                | items | 333 | 444 | 444 | 444 | 444
                | 444 | 444 | 444 | 444 | 444
        "name" | "last" | "Dr." | "orga" | "street" | "77777" | "City"
                | "Country" | "pm name" | "pm last" | "None" | "mail@i.de" | "2021-11-11"
                | "2022-11-11" | "title" | "objective" | "design" | "oabcd1" | items |items
                | items | 333 | 444 | 444 | 444 | 444
                | 444 | 444 | 444 | 444 | 444
        "name" | "last" | "Dr." | "organisation" | "Strasse" | "77777" | "City"
                | "Country" | "pm name" | "pm last" | "None" | "mail@i.de" | "2021-11-11"
                | "2022-11-11" | "title" | "objective" | "design" | "oabcd1" | items |items
                | items | 333 | 444 | 444 | 444 | 444
                | 444 | 444 | 444 | 444 | 444
        "name" | "last" | "Dr." | "organisation" | "street" | "55555" | "City"
                | "Country" | "pm name" | "pm last" | "None" | "mail@i.de" | "2021-11-11"
                | "2022-11-11" | "title" | "objective" | "design" | "oabcd1" | items |items
                | items | 333 | 444 | 444 | 444 | 444
                | 444 | 444 | 444 | 444 | 444
        "name" | "last" | "Dr." | "organisation" | "street" | "77777" | "Tübingen"
                | "Country" | "pm name" | "pm last" | "None" | "mail@i.de" | "2021-11-11"
                | "2022-11-11" | "title" | "objective" | "design" | "oabcd1" | items |items
                | items | 333 | 444 | 444 | 444 | 444
                | 444 | 444 | 444 | 444 | 444
        "name" | "last" | "Dr." | "organisation" | "street" | "77777" | "City"
                | "Germany" | "pm name" | "pm last" | "None" | "mail@i.de" | "2021-11-11"
                | "2022-11-11" | "title" | "objective" | "design" | "oabcd1" | items |items
                | items | 333 | 444 | 444 | 444 | 444
                | 444 | 444 | 444 | 444 | 444
        "name" | "last" | "Dr." | "organisation" | "street" | "77777" | "City"
                | "Country" | "name" | "pm last" | "None" | "mail@i.de" | "2021-11-11"
                | "2022-11-11" | "title" | "objective" | "design" | "oabcd1" | items |items
                | items | 333 | 444 | 444 | 444 | 444
                | 444 | 444 | 444 | 444 | 444
        "name" | "last" | "Dr." | "organisation" | "street" | "77777" | "City"
                | "Country" | "pm name" | "last" | "None" | "mail@i.de" | "2021-11-11"
                | "2022-11-11" | "title" | "objective" | "design" | "oabcd1" | items |items
                | items | 333 | 444 | 444 | 444 | 444
                | 444 | 444 | 444 | 444 | 444
        "name" | "last" | "Dr." | "organisation" | "street" | "77777" | "City"
                | "Country" | "pm name" | "pm last" | "Dr." | "mail@i.de" | "2021-11-11"
                | "2022-11-11" | "title" | "objective" | "design" | "oabcd1" | items |items
                | items | 333 | 444 | 444 | 444 | 444
                | 444 | 444 | 444 | 444 | 444
        "name" | "last" | "Dr." | "organisation" | "street" | "77777" | "City"
                | "Country" | "pm name" | "pm last" | "None" | "mail2@i.de" | "2021-11-11"
                | "2022-11-11" | "title" | "objective" | "design" | "oabcd1" | items |items
                | items | 333 | 444 | 444 | 444 | 444
                | 444 | 444 | 444 | 444 | 444
        "name" | "last" | "Dr." | "organisation" | "street" | "77777" | "City"
                | "Country" | "pm name" | "pm last" | "None" | "mail@i.de" | "2021-12-11"
                | "2022-11-11" | "title" | "objective" | "design" | "oabcd1" | items |items
                | items | 333 | 444 | 444 | 444 | 444
                | 444 | 444 | 444 | 444 | 444
        "name" | "last" | "Dr." | "organisation" | "street" | "77777" | "City"
                | "Country" | "pm name" | "pm last" | "None" | "mail@i.de" | "2021-11-11"
                | "2023-11-11" | "title" | "objective" | "design" | "oabcd1" | items |items
                | items | 333 | 444 | 444 | 444 | 444
                | 444 | 444 | 444 | 444 | 444
        "name" | "last" | "Dr." | "organisation" | "street" | "77777" | "City"
                | "Country" | "pm name" | "pm last" | "None" | "mail@i.de" | "2021-11-11"
                | "2022-11-11" | "Title" | "objective" | "design" | "oabcd1" | items |items
                | items | 333 | 444 | 444 | 444 | 444
                | 444 | 444 | 444 | 444 | 444
        "name" | "last" | "Dr." | "organisation" | "street" | "77777" | "City"
                | "Country" | "pm name" | "pm last" | "None" | "mail@i.de" | "2021-11-11"
                | "2022-11-11" | "title" | "Objective" | "design" | "oabcd1" | items |items
                | items | 333 | 444 | 444 | 444 | 444
                | 444 | 444 | 444 | 444 | 444
        "name" | "last" | "Dr." | "organisation" | "street" | "77777" | "City"
                | "Country" | "pm name" | "pm last" | "None" | "mail@i.de" | "2021-11-11"
                | "2022-11-11" | "title" | "objective" | "experimental design" | "oabcd1" | items |items
                | items | 333 | 444 | 444 | 444 | 444
                | 444 | 444 | 444 | 444 | 444
        "name" | "last" | "Dr." | "organisation" | "street" | "77777" | "City"
                | "Country" | "pm name" | "pm last" | "None" | "mail@i.de" | "2021-11-11"
                | "2022-11-11" | "title" | "objective" | "design" | "Oabcd2" | items |items
                | items | 333 | 444 | 444 | 444 | 444
                | 444 | 444 | 444 | 444 | 444
        "name" | "last" | "Dr." | "organisation" | "street" | "77777" | "City"
                | "Country" | "pm name" | "pm last" | "None" | "mail@i.de" | "2021-11-11"
                | "2022-11-11" | "title" | "objective" | "design" | "oabcd1" | [] |items
                | items | 333 | 444 | 444 | 444 | 444
                | 444 | 444 | 444 | 444 | 444
        "name" | "last" | "Dr." | "organisation" | "street" | "77777" | "City"
                | "Country" | "pm name" | "pm last" | "None" | "mail@i.de" | "2021-11-11"
                | "2022-11-11" | "title" | "objective" | "design" | "oabcd1" | items |items
                | items | 333 | 444 | 444 | 444 | 444
                | 444 | 444 | 444 | 444 | 444
        "name_" | "last" | "Dr." | "organisation" | "street" | "77777" | "City"
                | "Country" | "pm name" | "pm last" | "None" | "mail@i.de" | "2021-11-11"
                | "2022-11-11" | "title" | "objective" | "design" | "oabcd1" | items | []
                | items | 333 | 444 | 444 | 444 | 444
                | 444 | 444 | 444 | 444 | 444
        "name" | "last" | "Dr." | "organisation" | "street" | "77777" | "City"
                | "Country" | "pm name" | "pm last" | "None" | "mail@i.de" | "2021-11-11"
                | "2022-11-11" | "title" | "objective" | "design" | "oabcd1" | items |items
                | items2 | 333 | 444 | 444 | 444 | 444
                | 444 | 444 | 444 | 444 | 444
        "name" | "last" | "Dr." | "organisation" | "street" | "77777" | "City"
                | "Country" | "pm name" | "pm last" | "None" | "mail@i.de" | "2021-11-11"
                | "2022-11-11" | "title" | "objective" | "design" | "oabcd1" | items |items
                | items | 3331 | 444 | 444 | 444 | 444
                | 444 | 444 | 444 | 444 | 444
        "name" | "last" | "Dr." | "organisation" | "street" | "77777" | "City"
                | "Country" | "pm name" | "pm last" | "None" | "mail@i.de" | "2021-11-11"
                | "2022-11-11" | "title" | "objective" | "design" | "oabcd1" | items |items
                | items | 333 | 4444 | 444 | 444 | 444
                | 444 | 444 | 444 | 444 | 444
        "name" | "last" | "Dr." | "organisation" | "street" | "77777" | "City"
                | "Country" | "pm name" | "pm last" | "None" | "mail@i.de" | "2021-11-11"
                | "2022-11-11" | "title" | "objective" | "design" | "oabcd1" | items |items
                | items | 333 | 444 | 0 | 444 | 444
                | 444 | 444 | 444 | 444 | 444
        "name" | "last" | "Dr." | "organisation" | "street" | "77777" | "City"
                | "Country" | "pm name" | "pm last" | "None" | "mail@i.de" | "2021-11-11"
                | "2022-11-11" | "title" | "objective" | "design" | "oabcd1" | items |items
                | items | 333 | 444 | 444 | 4444 | 444
                | 444 | 444 | 444 | 444 | 444
        "name" | "last" | "Dr." | "organisation" | "street" | "77777" | "City"
                | "Country" | "pm name" | "pm last" | "None" | "mail@i.de" | "2021-11-11"
                | "2022-11-11" | "title" | "objective" | "design" | "oabcd1" | items |items
                | items | 333 | 444 | 444 | 444 | 4464
                | 444 | 444 | 444 | 444 | 444
        "name" | "last" | "Dr." | "organisation" | "street" | "77777" | "City"
                | "Country" | "pm name" | "pm last" | "None" | "mail@i.de" | "2021-11-11"
                | "2022-11-11" | "title" | "objective" | "design" | "oabcd1" | items |items
                | items | 333 | 444 | 444 | 444 | 444
                | 4444 | 444 | 444 | 444 | 444
        "name" | "last" | "Dr." | "organisation" | "street" | "77777" | "City"
                | "Country" | "pm name" | "pm last" | "None" | "mail@i.de" | "2021-11-11"
                | "2022-11-11" | "title" | "objective" | "design" | "oabcd1" | items |items
                | items | 333 | 444 | 444 | 444 | 444
                | 444 | 4441 | 444 | 444 | 444
        "name" | "last" | "Dr." | "organisation" | "street" | "77777" | "City"
                | "Country" | "pm name" | "pm last" | "None" | "mail@i.de" | "2021-11-11"
                | "2022-11-11" | "title" | "objective" | "design" | "oabcd1" | items |items
                | items | 333 | 444 | 444 | 444 | 444
                | 444 | 444 | 4441 | 444 | 444
        "name" | "last" | "Dr." | "organisation" | "street" | "77777" | "City"
                | "Country" | "pm name" | "pm last" | "None" | "mail@i.de" | "2021-11-11"
                | "2022-11-11" | "title" | "objective" | "design" | "oabcd1" | items |items
                | items | 333 | 444 | 444 | 444 | 444
                | 444 | 444 | 444 | 4441 | 444
        "name" | "last" | "Dr." | "organisation" | "street" | "77777" | "City"
                | "Country" | "pm name" | "pm last" | "None" | "mail@i.de" | "2021-11-11"
                | "2022-11-11" | "title" | "objective" | "design" | "oabcd1" | items |items
                | items | 333 | 444 | 444 | 444 | 444
                | 444 | 444 | 444 | 444 | 4441
    }
}