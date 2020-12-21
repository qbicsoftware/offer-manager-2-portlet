package life.qbic.portal.portlet.offers

import life.qbic.datamodel.dtos.business.Affiliation
import life.qbic.datamodel.dtos.business.AffiliationCategory
import life.qbic.datamodel.dtos.business.Customer
import life.qbic.datamodel.dtos.business.OfferId
import life.qbic.datamodel.dtos.business.ProductItem
import life.qbic.datamodel.dtos.business.ProjectManager
import life.qbic.datamodel.dtos.business.services.DataStorage
import life.qbic.datamodel.dtos.business.services.ProjectManagement

/**
 * Represents the Offer business model.
 *
 * This class should be used in the business context of offer creation.
 *
 * It holds all relevant business rules to compute the final prices for
 * offers based on the customers affiliation.
 *
 * @since 0.1.0
 */
class Offer {

    /**
     * Date on which the offer was lastly modified
     */
    final Date modificationDate
    /**
     * The date on which the offer expires
     */
    final Date expirationDate
    /**
     * The customer for which this offer was created
     */
    final Customer customer
    /**
     * The QBiC project manager who was assigned to the project
     */
    final ProjectManager projectManager
    /**
     * The title of the project
     */
    final String projectTitle
    /**
     * A short description of the project
     */
    final String projectDescription
    /**
     * A list of items for which the customer will be charged
     */
    final List<ProductItem> items
    /**
     * The identifier for the offer which makes it distinguishable from other offers
     */
    final OfferId identifier
    /**
     * The affiliation of the customer selected for this offer
     */
    final Affiliation selectedCustomerAffiliation

    /*
     * Holds the determined overhead derived from the
     * customer's affiliation.
     */
    private final double overhead

    /*
     * Holds the current VAT rate
     */
    private static final double VAT = 0.19

    static class Builder {

        Date modificationDate
        Date expirationDate
        Customer customer
        ProjectManager projectManager
        String projectTitle
        String projectDescription
        List<ProductItem> items
        OfferId identifier
        Affiliation selectedCustomerAffiliation

        Builder(Customer customer, ProjectManager projectManager, String projectTitle, String projectDescription, List<ProductItem> items, Affiliation selectedCustomerAffiliation) {
            this.customer = Objects.requireNonNull(customer, "Customer must not be null")
            this.projectManager = Objects.requireNonNull(projectManager, "Project Manager must not be null")
            this.projectTitle = Objects.requireNonNull(projectTitle, "Project Title must not be null")
            this.projectDescription = Objects.requireNonNull(projectDescription, "Project Description must not be null")
            this.items = []
            // Since the incoming item list is mutable we need to
            // copy all immutable items to out internal list
            items.each {this.items.add(it)}
            this.selectedCustomerAffiliation = Objects.requireNonNull(selectedCustomerAffiliation, "Customer Affiliation must not be null")
        }

        Builder modificationDate(Date modificationDate) {
            this.modificationDate = modificationDate
            return this
        }

        Builder expirationDate(Date expirationDate) {
            this.expirationDate = expirationDate
            return this
        }

        Builder identifier(OfferId identifier) {
            this.identifier = identifier
            return this
        }

        Offer build() {
            return new Offer(this)
        }
    }

    private Offer(Builder builder) {
        this.customer = builder.customer
        this.identifier = builder.identifier
        this.items = []
        builder.items.each {this.items.add(it)}
        this.expirationDate = builder.expirationDate
        this.modificationDate = builder.modificationDate
        this.projectManager = builder.projectManager
        this.projectDescription = builder.projectDescription
        this.projectTitle = builder.projectTitle
        this.selectedCustomerAffiliation = builder.selectedCustomerAffiliation
        this.overhead = determineOverhead()
    }

    /**
     * The total costs for the current offer.
     *
     * @return The total costs in the currency of the selected items.
     */
    double getTotalCosts() {
        calculateTotalCosts()
    }

    /**
     * The total net price for the current offer.
     *
     * Note: Does <strong>not include</strong> overheads and taxes.
     *
     * @return The net offer price
     */
    double getTotalNetPrice() {
        return calculateNetPrice()
    }

    /**
     * The overhead price amount of all service items without VAT.
     *
     * Service items of type data storage and project management
     * are <strong>excluded</strong> from he calculation.
     *
     * @return The calculated overhead amount of the selected items.
     */
    double getOverheadSum() {
        double overheadSum = 0
        for (ProductItem item : items) {
            if (item.product instanceof DataStorage || item.product instanceof ProjectManagement) {
                // No overheads are assigned for data storage and project management
            } else {
                overheadSum += item.quantity * item.product.unitPrice * this.overhead
            }
        }
        return overheadSum
    }

    /**
     * The tax price on all items net price including overheads.
     *
     * For internal customers, this will be 0.
     *
     * @return The amount of VAT price based on all items in the offer.
     */
    double getTaxCosts() {
        if (selectedCustomerAffiliation.category.equals(AffiliationCategory.INTERNAL)) {
            return 0
        }
        return (calculateNetPrice() + getOverheadSum()) * VAT
    }

    private double calculateNetPrice() {
        double netSum = 0.0
        for (item in items) {
            netSum += item.quantity * item.product.unitPrice
        }
        return netSum
    }

    private double determineOverhead() {
        double overhead = 0.0
        switch(selectedCustomerAffiliation.category) {
            case AffiliationCategory.INTERNAL:
                overhead = 0.0
                break
            case AffiliationCategory.EXTERNAL_ACADEMIC:
                overhead = 0.2
                break
            case AffiliationCategory.EXTERNAL:
                overhead = 0.4
                break
            default:
                overhead = 0.4
        }
        return overhead
    }

    private double calculateTotalCosts(){
        final double netPrice = calculateNetPrice()
        final double overhead = getOverheadSum()
        return netPrice + overhead + getTaxCosts()
    }
}
