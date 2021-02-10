package life.qbic.business.offers

import groovy.time.TimeCategory
import life.qbic.datamodel.dtos.business.Affiliation
import life.qbic.datamodel.dtos.business.AffiliationCategory
import life.qbic.datamodel.dtos.business.Customer
import life.qbic.datamodel.dtos.business.ProductItem
import life.qbic.datamodel.dtos.business.ProjectManager
import life.qbic.datamodel.dtos.business.services.DataStorage
import life.qbic.datamodel.dtos.business.services.ProjectManagement
import life.qbic.business.offers.identifier.OfferId

import java.nio.charset.StandardCharsets
import java.security.MessageDigest

/**
 * Represents the Offer business model.
 *
 * This class should be used in the business context of offer creation.
 *
 * It holds all relevant business rules to compute the final prices for
 * offers based on the persons affiliation.
 *
 * @since 0.1.0
 */
class Offer {
    /**
     * Holds all available versions of an existing offer
     */
    private List<OfferId> availableVersions
    /**
     * Date on which the offer was lastly modified
     */
    private Date creationDate
    /**
     * The date on which the offer expires
     */
    private Date expirationDate
    /**
     * The customer for which this offer was created
     */
    private Customer customer
    /**
     * The QBiC project manager who was assigned to the project
     */
    private ProjectManager projectManager
    /**
     * The title of the project
     */
    private String projectTitle
    /**
     * A short description of the project
     */
    private String projectDescription
    /**
     * A list of items for which the customer will be charged
     */
    private List<ProductItem> items
    /**
     * The identifier for the offer which makes it distinguishable from other offers
     */
    private OfferId identifier
    /**
     * The affiliation of the customer selected for this offer
     */
    private Affiliation selectedCustomerAffiliation

    /*
     * Holds the determined overhead derived from the
     * customer's affiliation.
     */
    private double overhead

    /*
     * Holds the current VAT rate
     */
    private static final double VAT = 0.19

    private static Date calculateExpirationDate(Date date) {
        use (TimeCategory) {
            return date + 90.days
        }
    }

    static class Builder {

        Date creationDate
        Customer customer
        ProjectManager projectManager
        String projectTitle
        String projectDescription
        List<ProductItem> items
        OfferId identifier
        Affiliation selectedCustomerAffiliation
        List<OfferId> availableVersions

        Builder(Customer customer, ProjectManager projectManager, String projectTitle, String projectDescription, List<ProductItem> items, Affiliation selectedCustomerAffiliation) {
            this.customer = Objects.requireNonNull(customer, "Customer must not be null")
            this.projectManager = Objects.requireNonNull(projectManager, "Project Manager must not be null")
            this.projectTitle = Objects.requireNonNull(projectTitle, "Project Title must not be null")
            this.projectDescription = Objects.requireNonNull(projectDescription, "Project Description must not be null")
            this.items = []
            this.availableVersions = []
            this.creationDate = new Date()
            // Since the incoming item list is mutable we need to
            // copy all immutable items to out internal list
            items.each {this.items.add(it)}
            this.selectedCustomerAffiliation = Objects.requireNonNull(selectedCustomerAffiliation, "Customer Affiliation must not be null")
        }

        Builder creationDate(Date creationDate) {
            this.creationDate = creationDate
            return this
        }

        Builder identifier(OfferId identifier) {
            this.identifier = identifier
            return this
        }

        Builder availableVersions(List<OfferId> availableVersions) {
            this.availableVersions.addAll(availableVersions)
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
        this.expirationDate = calculateExpirationDate(builder.creationDate)
        this.creationDate = builder.creationDate
        this.projectManager = builder.projectManager
        this.projectDescription = builder.projectDescription
        this.projectTitle = builder.projectTitle
        this.selectedCustomerAffiliation = builder.selectedCustomerAffiliation
        this.overhead = determineOverhead()
        this.availableVersions = builder.availableVersions
                .stream()
                .map(id -> new OfferId(id)).collect()
        this.availableVersions.add(this.identifier)
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
     * For internal persons, this will be 0.
     *
     * @return The amount of VAT price based on all items in the offer.
     */
    double getTaxCosts() {
        if (selectedCustomerAffiliation.category.equals(AffiliationCategory.INTERNAL)) {
            return 0
        }
        return (calculateNetPrice() + getOverheadSum()) * VAT
    }

    Date getModificationDate() {
        return creationDate
    }

    Date getExpirationDate() {
        return expirationDate
    }

    Customer getCustomer() {
        return customer
    }

    ProjectManager getProjectManager() {
        return projectManager
    }

    String getProjectTitle() {
        return projectTitle
    }

    String getProjectDescription() {
        return projectDescription
    }

    List<ProductItem> getItems() {
        return items
    }

    OfferId getIdentifier() {
        return identifier
    }

    Affiliation getSelectedCustomerAffiliation() {
        return selectedCustomerAffiliation
    }

    void addAvailableVersion(OfferId offerId) {
        this.availableVersions.add(new OfferId(offerId))
    }

    void addAllAvailableVersions(Collection<OfferId> offerIdCollection) {
        offerIdCollection.each {addAvailableVersion(it)}
    }

    /**
     * Increases the version of an offer, resulting in a offer id with a new version tag.
     */
    void increaseVersion() {
        def copyIdentifier = new OfferId(this.identifier)
        identifier = getLatestVersion()
        identifier.increaseVersion()
        this.availableVersions.addAll(copyIdentifier, this.identifier)
    }

    private double calculateNetPrice() {
        double netSum = 0.0
        for (item in items) {
            netSum += item.quantity * item.product.unitPrice
        }
        return netSum
    }

    private double determineOverhead() {
        double overhead
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

    /**
     * Returns the latest available version for an offer.
     *
     * If there are no available versions in addition to the current one, this one is returned.
     *
     * @return The latest offer id
     */
    OfferId getLatestVersion() {
        return availableVersions.sort().last() ?: this.identifier
    }


    /**
    * Returns the checksum of the current Offer Object
    */
    String checksum(){
        //Use SHA-1 algorithm
        MessageDigest shaDigest = MessageDigest.getInstance("SHA-256")

        //SHA-1 checksum
        return getOfferChecksum(shaDigest, this)
    }


    /**
     * Compute the checksum for an offer
     * @param digest The digestor will digest the message that needs to be encrypted
     * @param offer Contains the offer information
     * @return a string that encrypts the offer object
     */
    private static String getOfferChecksum(MessageDigest digest, Offer offer)
    {
        //digest crucial offer characteristics
        digest.update(offer.projectTitle.getBytes(StandardCharsets.UTF_8))

        offer.items.each {item ->
            digest.update(item.product.toString().getBytes(StandardCharsets.UTF_8))
            digest.update(item.quantity.toString().getBytes(StandardCharsets.UTF_8))
        }
        digest.update(offer.customer.toString().getBytes(StandardCharsets.UTF_8))
        digest.update(offer.projectManager.toString().getBytes(StandardCharsets.UTF_8))

        digest.update(offer.selectedCustomerAffiliation.toString().getBytes(StandardCharsets.UTF_8))

        //Get the hash's bytes
        byte[] bytes = digest.digest()

        //This bytes[] has bytes in decimal format
        //Convert it to hexadecimal format
        StringBuilder sb = new StringBuilder()
        for(int i=0; i< bytes.length ;i++)
        {
            sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
        }

        //return complete hash
        return sb.toString()
    }
}
