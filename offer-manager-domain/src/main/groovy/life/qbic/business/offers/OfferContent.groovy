package life.qbic.business.offers

import groovy.transform.EqualsAndHashCode
import life.qbic.business.RefactorConverter
import life.qbic.business.persons.Person
import life.qbic.business.persons.affiliation.Affiliation

/**
 * <h1>A DTO containing the fields required in the offer pdf</h1>
 *
 * <p>The content of this class is based on all the fields that need be part in the final offer pdf</p>
 *
 * @since 1.1.0
 *
*/

@EqualsAndHashCode
class OfferContent {

    private static final RefactorConverter refactorConverter = new RefactorConverter()

    /*Person Information*/
    /**
     * The information for the customer for which this offer was created
     */
    final String customerFirstName
    final String customerLastName
    final String customerTitle
    /**
     * The information for the affiliation of the customer selected for this offer
     */
    final String customerOrganisation
    final String customerAddressAddition
    final String customerStreet
    final String customerPostalCode
    final String customerCity
    final String customerCountry
    /**
     * The information for the QBiC project manager who was assigned to the project
     */
    final String projectManagerFirstName
    final String projectManagerLastName
    final String projectManagerTitle
    final String projectManagerEmail
    /**
     * The information for the affiliation of the project manager selected for this offer
     */
    final String projectManagerOrganisation
    final String projectManagerStreet
    final String projectManagerPostalCode
    final String projectManagerCity
    final String projectManagerCountry

    /*Project Information*/
    /**
     * Date on which the offer was lastly modified
     */
    final Date creationDate
    /**
     * The date on which the offer expires
     */
    final Date expirationDate
    /**
     * The title of the project
     */
    final String projectTitle
    /**
     * A short objective of the project
     */
    final String projectObjective
    /**
     * A short description of the experimental design of the project
     */
    final String experimentalDesign
    /**
     * The identifier for the offer which makes it distinguishable from other offers
     */
    final String offerIdentifier

    /*Items*/
    /**
     * The items assigned to data generation section of the offer
     */
    final List<OfferItem> dataGenerationItems
    /**
     * The items assigned to data analysis section of the offer
     */
    final List<OfferItem> dataAnalysisItems
    /**
     * The items assigned to project management and data storage section of the offer
     */
    final List<OfferItem> dataManagementItems
    /**
     * The items assigned to external services of the offer
     */
    final List<OfferItem> externalServiceItems

    /*Overheads*/
    /**
     * The total overhead costs
     */
    final double overheadTotal
    /**
     * The overhead ratio applied to calculate the overhead costs
     */
    final double overheadRatio
    /**
     * The overhead costs for the data generation items
     */
    final double overheadsDataGeneration
    /**
     * The overhead costs for the data analysis items
     */
    final double overheadsDataAnalysis
    /**
     * The overhead costs for the project management and data storage items
     */
    final double overheadsProjectManagementAndDataStorage
    /**
     * The overhead costs for external services
     */
    final double overheadsExternalService

    /*Prices*/
    /**
     * The net costs for the data generation section on the offer
     */
    final double netDataGeneration
    /**
     * The net costs for the data analysis section on the offer
     */
    final double netDataAnalysis
    /**
     * The net costs for the project management and data storage section on the offer
     */
    final double netPMandDS
    /**
     * The net costs for external services
     */
    final double netExternalServices
    /**
     * The total costs of the offer
     */
    final double totalCost
    /**
     * The net costs of the offer
     */
    final double netCost
    /**
     * The net costs of the offer
     */
    final double netCostsWithOverheads
    /**
     * The total VAT costs of the offer
     */
    final double totalVat

    /**
     * The ratio/percentage of vat applied in the offer
     */
    final double vatRatio
    /**
     * The total discount amount that has been applied in the offer
     */
    final Double totalDiscountAmount

    /**
     * Returns a true copy of the data generation items.
     * @return
     */
    List<OfferItem> getDataGenerationItems() {
        return dataGenerationItems.collect()
    }

    /**
     * Returns a true copy of the data analysis items.
     * @return
     */
    List<OfferItem> getDataAnalysisItems() {
        return dataAnalysisItems.collect()
    }

    /**
     * Returns a true copy of the data management items.
     * @return
     */
    List<OfferItem> getDataManagementItems() {
        return dataManagementItems.collect()
    }

    /**
     * Returns a true copy of the external service items.
     * @return
     */
    List<OfferItem> getExternalServiceItems() {
        return externalServiceItems.collect()
    }

    OfferContent(String customerFirstName, String customerLastName, String customerTitle, String customerOrganisation, String customerAddressAddition, String customerStreet, String customerPostalCode, String customerCity, String customerCountry, String projectManagerFirstName, String projectManagerLastName, String projectManagerTitle, String projectManagerEmail, String projectManagerOrganisation, String projectManagerStreet, String projectManagerPostalCode, String projectManagerCity, String projectManagerCountry, Date creationDate, Date expirationDate, String projectTitle, String projectObjective, String experimentalDesign, String offerIdentifier, List<OfferItem> dataGenerationItems, List<OfferItem> dataAnalysisItems, List<OfferItem> dataManagementItems, List<OfferItem> externalServiceItems, double overheadTotal, double overheadRatio, double overheadsDataGeneration, double overheadsDataAnalysis, double overheadsProjectManagementAndDataStorage, double overheadsExternalService, double netDataGeneration, double netDataAnalysis, double netPMandDS, double netExternalServices, double totalCost, double netCost, double netCostsWithOverheads, double totalVat, double vatRatio, Double totalDiscountAmount) {
        this.customerFirstName = customerFirstName
        this.customerLastName = customerLastName
        this.customerTitle = customerTitle
        this.customerOrganisation = customerOrganisation
        this.customerAddressAddition = customerAddressAddition
        this.customerStreet = customerStreet
        this.customerPostalCode = customerPostalCode
        this.customerCity = customerCity
        this.customerCountry = customerCountry
        this.projectManagerFirstName = projectManagerFirstName
        this.projectManagerLastName = projectManagerLastName
        this.projectManagerTitle = projectManagerTitle
        this.projectManagerEmail = projectManagerEmail
        this.projectManagerOrganisation = projectManagerOrganisation
        this.projectManagerStreet = projectManagerStreet
        this.projectManagerPostalCode = projectManagerPostalCode
        this.projectManagerCity = projectManagerCity
        this.projectManagerCountry = projectManagerCountry
        this.creationDate = creationDate
        this.expirationDate = expirationDate
        this.projectTitle = projectTitle
        this.projectObjective = projectObjective
        this.experimentalDesign = experimentalDesign
        this.offerIdentifier = offerIdentifier
        this.dataGenerationItems = dataGenerationItems
        this.dataAnalysisItems = dataAnalysisItems
        this.dataManagementItems = dataManagementItems
        this.externalServiceItems = externalServiceItems
        this.overheadTotal = overheadTotal
        this.overheadRatio = overheadRatio
        this.overheadsDataGeneration = overheadsDataGeneration
        this.overheadsDataAnalysis = overheadsDataAnalysis
        this.overheadsProjectManagementAndDataStorage = overheadsProjectManagementAndDataStorage
        this.overheadsExternalService = overheadsExternalService
        this.netDataGeneration = netDataGeneration
        this.netDataAnalysis = netDataAnalysis
        this.netPMandDS = netPMandDS
        this.netExternalServices = netExternalServices
        this.totalCost = totalCost
        this.netCost = netCost
        this.netCostsWithOverheads = netCostsWithOverheads
        this.totalVat = totalVat
        this.vatRatio = vatRatio
        this.totalDiscountAmount = totalDiscountAmount
    }

    /**
     * Converts to OfferContent from OfferV2
     *
     *
     * @param offer OfferV2 containing the information to be translated into the OfferContent
     * @return OfferContent
     */
    static OfferContent from(OfferV2 offer) {
        String offerIdString = offer.getOfferId()
        Person customer = offer.getCustomer()
        Affiliation affiliation = offer.getSelectedCustomerAffiliation()
        Person projectManager = offer.getProjectManager()
        Date creationDate = refactorConverter.toUtilDate(offer.getCreationDate())
        Date expirationDate = refactorConverter.toUtilDate(offer.getExpirationDate())
        String projectTitle = offer.getProjectTitle()
        String projectObjective = offer.getProjectObjective()
        String experimentalDesign = offer.getExperimentalDesign().orElse("")
        List<OfferItem> dataGenerationItems = offer.dataGenerationItems.stream().map(refactorConverter::toOfferItem).collect()
        List<OfferItem> dataAnalysisItems = offer.dataAnalysisItems.stream().map(refactorConverter::toOfferItem).collect()
        List<OfferItem> dataManagementItems = offer.dataManagementItems.stream().map(refactorConverter::toOfferItem).collect()
        List<OfferItem> externalServiceItems = offer.externalServiceItems.stream().map(refactorConverter::toOfferItem).collect()


        def projectManagerAffiliation = projectManager.affiliations.first()
        new OfferContent(customer.firstName,
                customer.lastName,
                customer.title,
                affiliation.organization,
                affiliation.getAddressAddition(),
                affiliation.getStreet(),
                affiliation.getPostalCode(),
                affiliation.city,
                affiliation.country,
                projectManager.firstName,
                projectManager.lastName,
                projectManager.title,
                projectManager.email,
                projectManagerAffiliation.organization,
                projectManagerAffiliation.street,
                projectManagerAffiliation.postalCode,
                projectManagerAffiliation.city,
                projectManagerAffiliation.country,
                creationDate,
                expirationDate,
                projectTitle,
                projectObjective,
                experimentalDesign,
                offerIdString,
                dataGenerationItems,
                dataAnalysisItems,
                dataManagementItems,
                externalServiceItems,
                offer.overhead,
                offer.overheadRatio,
                offer.dataGenerationOverhead.doubleValue(),
                offer.dataAnalysisOverhead.doubleValue(),
                offer.dataManagementOverhead.doubleValue(),
                offer.externalServiceOverhead.doubleValue(),
                offer.dataGenerationSalePrice.doubleValue(),
                offer.dataAnalysisSalePrice.doubleValue(),
                offer.dataManagementSalePrice.doubleValue(),
                offer.externalServiceSalePrice.doubleValue(),
                offer.priceAfterTax.doubleValue(),
                offer.salePrice.doubleValue(),
                offer.priceBeforeTax.doubleValue(),
                offer.totalVat.doubleValue(),
                offer.vatRatio.doubleValue(),
                offer.totalDiscountAmount.doubleValue()
        )
    }
}
