package life.qbic.business.offers

import groovy.transform.EqualsAndHashCode
import life.qbic.business.offers.identifier.OfferId
import life.qbic.datamodel.dtos.business.AcademicTitle
import life.qbic.datamodel.dtos.business.Affiliation
import life.qbic.datamodel.dtos.business.Customer
import life.qbic.datamodel.dtos.business.ProductItem
import life.qbic.datamodel.dtos.business.ProjectManager
import life.qbic.datamodel.dtos.projectmanagement.ProjectCode

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

    /*Person Information*/
    /**
     * The information for the customer for which this offer was created
     */
    private final String customerFirstName
    private final String customerLastName
    private final String customerTitle
    /**
     * The information for the affiliation of the customer selected for this offer
     */
    private final String customerOrganisation
    private final String customerStreet
    private final String customerPostalCode
    private final String customerCity
    private final String customerCountry
    /**
     * The information for the QBiC project manager who was assigned to the project
     */
    private final String projectManagerFirstName
    private final String projectManagerLastName
    private final String projectManagerTitle
    private final String projectManagerEmail

    /*Project Information*/
    /**
     * Date on which the offer was lastly modified
     */
    private final String creationDate
    /**
     * The date on which the offer expires
     */
    private final String expirationDate
    /**
     * The title of the project
     */
    private final String projectTitle
    /**
     * A short objective of the project
     */
    private final String projectObjective
    /**
     * A short description of the experimental design of the project
     */
    private final String experimentalDesign
    /**
     * The identifier for the offer which makes it distinguishable from other offers
     */
    private final String offerIdentifier

    /*Items*/
    //todo add the offeritem here!!
    /**
     * The items assigned to data generation section of the offer
     */
    private final List<ProductItem> dataGenerationItems
    /**
     * The items assigned to data analysis section of the offer
     */
    private final List<ProductItem> dataAnalysisItems
    /**
     * The items assigned to project management and data storage section of the offer
     */
    private final List<ProductItem> dataManagementItems

    /*Overheads*/
    /**
     * The total overhead costs
     */
    private final double overheadTotal
    /**
     * The overhead costs for the data generation items
     */
    private final double overheadsDataGeneration
    /**
     * The overhead costs for the data analysis items
     */
    private final double overheadsDataAnalysis
    /**
     * The overhead costs for the project management and data storage items
     */
    private final double overheadsPMandDS

    /*Prices*/
    /**
     * The net costs for the data generation section on the offer
     */
    private final double netDataGeneration
    /**
     * The net costs for the data analysis section on the offer
     */
    private final double netDataAnalysis
    /**
     * The net costs for the project management and data storage section on the offer
     */
    private final double netPMandDS
    /**
     * The total costs of the offer
     */
    private final double totalCost
    /**
     * The net costs of the offer
     */
    private final double netCost
    /**
     * The total VAT costs of the offer
     */
    private final double totalVat

    static class Builder {
        /*Person Information*/
        String customerFirstName
        String customerLastName
        String customerTitle
        String customerOrganisation
        String customerStreet
        String customerPostalCode
        String customerCity
        String customerCountry
        String projectManagerFirstName
        String projectManagerLastName
        String projectManagerTitle
        String projectManagerEmail

        /*Project Information*/
        String creationDate
        String expirationDate
        String projectTitle
        String projectObjective
        String experimentalDesign
        String offerIdentifier

        /*Items*/
        //todo use offeritem
        List<ProductItem> dataGenerationItems
        List<ProductItem> dataAnalysisItems
        List<ProductItem> dataManagementItems

        /*Overheads*/
        double overheadTotal
        double overheadsDataGeneration
        double overheadsDataAnalysis
        double overheadsPMandDS

        /*Prices*/
        double netDataGeneration
        double netDataAnalysis
        double netPMandDS
        double totalCost
        double netCost
        double totalVat
        Builder(Customer customer, Affiliation customerAffiliation, ProjectManager projectManager, String creationDate, String expirationDate, String projectTitle,
        String projectObjective, String experimentalDesign, String offerIdentifier){
            /*Customer*/
            customerFirstName = Objects.requireNonNull(customer.firstName,"Customer must not be null")
            customerLastName = Objects.requireNonNull(customer.lastName, "Customer must not be null")
            String customerTitle = customer.title == AcademicTitle.NONE ? "" : customer.title
            this.customerTitle = Objects.requireNonNull(customerTitle, "Customer must not be null")

            customerOrganisation = Objects.requireNonNull(customerAffiliation.organisation, "Customer affiliation must not be null")
            customerStreet = Objects.requireNonNull(customerAffiliation.street, "Customer affiliation must not be null")
            customerPostalCode = Objects.requireNonNull(customerAffiliation.postalCode, "Customer affiliation  must not be null")
            customerCity = Objects.requireNonNull(customerAffiliation.city, "Customer affiliation must not be null")
            customerCountry = Objects.requireNonNull(customerAffiliation.country, "Customer affiliation must not be null")
            /*Projectmanager*/
            projectManagerFirstName = Objects.requireNonNull(projectManager.firstName, "Projectmanager musst not be null")
            projectManagerLastName = Objects.requireNonNull(projectManager.lastName, "Projectmanager must not be null")
            String projectManagerTitle = projectManager.title == AcademicTitle.NONE ? "" : projectManager.title
            this.projectManagerTitle = Objects.requireNonNull(projectManagerTitle, "Projectmanager must not be null")
            projectManagerEmail = Objects.requireNonNull(projectManager.emailAddress, "Projectmanager must not be null")

            /*Projectinformation*/
            this.creationDate = Objects.requireNonNull(creationDate, "Creation date must not be null")
            this.expirationDate = Objects.requireNonNull(expirationDate, "Expiration date must not be null")
            this.projectTitle = Objects.requireNonNull(projectTitle, "Project title must not be null")
            this.projectObjective = Objects.requireNonNull(projectObjective, "Project objective must not be null")
            this.experimentalDesign = Objects.requireNonNull(experimentalDesign, "Experimental design must not be  null")
            this.offerIdentifier = Objects.requireNonNull(offerIdentifier, "Offer identifier must not be null")

        }
        Builder dataGenerationItems(List<ProductItem> dataGenerationItems){
            this.dataGenerationItems = dataManagementItems
            return this
        }
        Builder dataAnalysisItems(List<ProductItem> dataAnalysisItems){
            this.dataAnalysisItems = dataAnalysisItems
            return this
        }
        Builder dataManagementItems(List<ProductItem> dataManagementItems){
            this.dataManagementItems = dataManagementItems
            return this
        }
        Builder overheadTotal(double overheadTotal){
            this.overheadTotal = overheadTotal
            return this
        }
        Builder overheadsDataGeneration(double overheadDG){
            this.overheadsDataGeneration = overheadDG
            return this
        }
        Builder overheadDataAnalysis(double overheadDA){
            this.overheadsDataAnalysis = overheadDA
            return this
        }
        Builder overheadsPMandDS(double overheadPMandDS){
            this.overheadsPMandDS = overheadPMandDS
            return this
        }
        Builder netDataGeneration(double net){
            this.netDataGeneration = net
            return this
        }
        Builder netDataAnalysis(double net){
            this.netDataAnalysis = net
            return this
        }
        Builder netProjectManagementAndDataStorage(double net){
            this.netPMandDS = net
            return this
        }
        Builder totalCost(double total){
            this.totalCost = total
            return this
        }
        Builder netCost(double net){
            this.netCost = net
            return this
        }
        Builder totalVat(double vat){
            this.totalVat = vat
            return this
        }

        OfferContent build(){
            return new OfferContent(this)
        }
    }

    private OfferContent(Builder builder){
        /*Person Information*/
        /*Customer*/
        customerFirstName = builder.customerFirstName
        customerLastName = builder.customerLastName
        customerTitle = builder.customerTitle

        customerOrganisation = builder.customerOrganisation
        customerStreet = builder.customerStreet
        customerPostalCode = builder.customerPostalCode
        customerCity = builder.customerCity
        customerCountry = builder.customerCountry
        /*Projectmanager*/
        projectManagerFirstName = builder.projectManagerFirstName
        projectManagerLastName = builder.projectManagerLastName
        projectManagerTitle = builder.projectManagerTitle
        projectManagerEmail = builder.projectManagerEmail

        /*Project Information*/
        creationDate = builder.creationDate
        expirationDate = builder.expirationDate
        projectTitle = builder.projectTitle
        projectObjective = builder.projectObjective
        experimentalDesign = builder.experimentalDesign
        offerIdentifier = builder.offerIdentifier

        /*Items*/
        dataGenerationItems = builder.dataGenerationItems
        dataAnalysisItems = builder.dataAnalysisItems
        dataManagementItems = builder.dataManagementItems

        /*Overheads*/
        overheadTotal = builder.overheadTotal
        overheadsDataGeneration = builder.overheadsDataGeneration
        overheadsDataAnalysis = builder.overheadsDataGeneration
        overheadsPMandDS = builder.overheadsPMandDS

        /*Prices*/
        netDataGeneration = builder.netDataGeneration
        netDataAnalysis = builder.netDataAnalysis
        netPMandDS = builder.netPMandDS
        totalCost = builder.totalCost
        netCost = builder.netCost
        totalVat = builder.totalVat
    }

}
