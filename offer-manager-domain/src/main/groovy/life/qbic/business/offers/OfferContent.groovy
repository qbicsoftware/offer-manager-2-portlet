package life.qbic.business.offers

import groovy.transform.EqualsAndHashCode
import life.qbic.datamodel.dtos.business.AcademicTitle
import life.qbic.datamodel.dtos.business.Affiliation
import life.qbic.datamodel.dtos.business.Customer
import life.qbic.datamodel.dtos.business.ProductItem
import life.qbic.datamodel.dtos.business.ProjectManager

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
    final String customerFirstName
    final String customerLastName
    final String customerTitle
    /**
     * The information for the affiliation of the customer selected for this offer
     */
    final String customerOrganisation
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

    /*Project Information*/
    /**
     * Date on which the offer was lastly modified
     */
    final String creationDate
    /**
     * The date on which the offer expires
     */
    final String expirationDate
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

    /*Overheads*/
    /**
     * The total overhead costs
     */
    final double overheadTotal
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
    final double overheadsPMandDS

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
     * The total costs of the offer
     */
    final double totalCost
    /**
     * The net costs of the offer
     */
    final double netCost
    /**
     * The total VAT costs of the offer
     */
    final double totalVat

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
        List<OfferItem> dataGenerationItems
        List<OfferItem> dataAnalysisItems
        List<OfferItem> dataManagementItems

        /*Overheads*/
        Double overheadTotal
        Double overheadsDataGeneration
        Double overheadsDataAnalysis
        Double overheadsPMandDS

        /*Prices*/
        Double netDataGeneration
        Double netDataAnalysis
        Double netPMandDS
        Double totalCost
        Double netCost
        Double totalVat

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
        Builder dataGenerationItems(List<OfferItem> dataGenerationItems){
            this.dataGenerationItems = dataManagementItems
            return this
        }
        Builder dataAnalysisItems(List<OfferItem> dataAnalysisItems){
            this.dataAnalysisItems = dataAnalysisItems
            return this
        }
        Builder dataManagementItems(List<OfferItem> dataManagementItems){
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
        Builder overheadsDataAnalysis(double overheadDA){
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
            //require all fields to be set before the object can be created
            if(dataGenerationItems == null) throw new NullPointerException("Missing data generation items")
            if(dataAnalysisItems == null) throw new NullPointerException("Missing data analysis items")
            if(dataManagementItems == null) throw new NullPointerException("Missing data management items")
            if(overheadTotal == null) throw new NullPointerException("Missing overhead total costs")
            if(overheadsDataAnalysis == null) throw new NullPointerException("Missing data analysis overhead costs")
            if(overheadsDataGeneration == null) throw new NullPointerException("Missing data generation overhead costs")
            if(overheadsPMandDS == null) throw new NullPointerException("Missing project management and data storage overhead costs")
            if(netDataGeneration == null) throw new NullPointerException("Missing net data generation costs")
            if(netDataAnalysis == null) throw new NullPointerException("Missing net data analysis costs")
            if(netPMandDS == null) throw new NullPointerException("Missing net project management and data storage costs")
            if(totalCost == null) throw new NullPointerException("Missing total costs")
            if(netCost == null) throw new NullPointerException("Missing net costs")
            if(totalVat == null) throw new NullPointerException("Missing total vat costs")

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

    List<OfferItem> getDataGenerationItems() {
        return Collections.unmodifiableList(dataGenerationItems)
    }

    List<OfferItem> getDataAnalysisItems() {
        return Collections.unmodifiableList(dataAnalysisItems)
    }

    List<OfferItem> getDataManagementItems() {
        return Collections.unmodifiableList(dataManagementItems)
    }
}