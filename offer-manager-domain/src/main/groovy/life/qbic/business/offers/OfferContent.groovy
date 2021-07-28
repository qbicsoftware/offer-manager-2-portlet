package life.qbic.business.offers

import life.qbic.business.offers.identifier.OfferId
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
class OfferContent {

    /*Person Information*/
    /**
     * The customer for which this offer was created
     */
    final Customer customer
    /**
     * The affiliation of the customer selected for this offer
     */
    final Affiliation selectedCustomerAffiliation
    /**
     * The QBiC project manager who was assigned to the project
     */
    final ProjectManager projectManager

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
    final Optional<String> experimentalDesign
    /**
     * The identifier for the offer which makes it distinguishable from other offers
     */
    final OfferId identifier

    /*Items*/
    /**
     * The items assigned to data generation section of the offer
     */
    final List<ProductItem> dataGenerationItems
    /**
     * The items assigned to data analysis section of the offer
     */
    final List<ProductItem> dataAnalysisItems
    /**
     * The items assigned to project management and data storage section of the offer
     */
    final List<ProductItem> dataManagementItems

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

}