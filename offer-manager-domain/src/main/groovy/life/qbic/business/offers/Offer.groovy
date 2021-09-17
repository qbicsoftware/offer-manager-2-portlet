package life.qbic.business.offers

import groovy.time.TimeCategory
import life.qbic.business.logging.Logger
import life.qbic.business.logging.Logging
import life.qbic.business.offers.identifier.OfferId
import life.qbic.business.offers.identifier.ProjectPart
import life.qbic.business.offers.identifier.RandomPart
import life.qbic.business.offers.identifier.Version
import life.qbic.datamodel.dtos.business.*
import life.qbic.datamodel.dtos.business.services.DataStorage
import life.qbic.datamodel.dtos.business.services.PrimaryAnalysis
import life.qbic.datamodel.dtos.business.services.ProjectManagement
import life.qbic.datamodel.dtos.business.services.SecondaryAnalysis
import life.qbic.datamodel.dtos.projectmanagement.ProjectIdentifier

import java.nio.charset.StandardCharsets
import java.security.MessageDigest
import java.util.function.Function
import java.util.function.Predicate


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
     * A short objective of the project
     */
    private String projectObjective
    /**
     * A short description of the experimental design of the project
     */
    private Optional<String> experimentalDesign
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
    /**
     * A list of items for which an overhead cost is applicable
     */
    @Deprecated
    private List<ProductItem> itemsWithOverhead
    /**
     * A list of items for which an overhead cost is not applicable
     */
    @Deprecated
    private List<ProductItem> itemsWithoutOverhead
    /**
     * The net price of all items for which an overhead cost is applicable, without overhead and taxes
     */
    @Deprecated
    private double itemsWithOverheadNetPrice
    /**
     * The net price of all items for which an overhead cost is not applicable, without overhead and taxes
     */
    @Deprecated
    private double itemsWithoutOverheadNetPrice

    /**
     * Holds the determined overhead total derived from the
     * customer's affiliation.
     */
    private double overhead

    /**
    * The overhead ratio that is applied to calculate the total offer price. The ratio is chosen
    * based on the customer's affiliation.
     *  e.g. 0.4 or a 40% markup for external customers
    */
    private double overheadRatio

    /**
     * Holds the current VAT rate
     */
    private static final double VAT = 0.19

    /**
     * Holds the Country for which VAT is applicable
     */
    private static final String countryWithVat = "Germany"

    /**
     * Holds the AffiliationCategory for which VAT is non-applicable
     */
    private static final AffiliationCategory noVatCategory = AffiliationCategory.INTERNAL

    /**
     * A project that has been created from this offer (optional)
     */
    private Optional<ProjectIdentifier> associatedProject

    private static Date calculateExpirationDate(Date date) {
        use (TimeCategory) {
            return date + 90.days
        }
    }

    private final QuantityDiscount quantityDiscount = new QuantityDiscount()

    private final Function<ProductItem, BigDecimal> cataloguePrice = { ProductItem it -> calculateCataloguePriceForItem(it) }

    private final Function<BigDecimal, BigDecimal> dataStorageDiscount = new DataStorageDiscount()

    private final Predicate<ProductItem> dataStorageApplicable = {
        it.product instanceof DataStorage && \
                    selectedCustomerAffiliation.category == AffiliationCategory.INTERNAL
    }

    private static Logging log = Logger.getLogger(this.class)

    static class Builder {

        Date creationDate
        Customer customer
        ProjectManager projectManager
        String projectTitle
        String projectObjective
        Optional<String> experimentalDesign
        List<ProductItem> items
        OfferId identifier
        Affiliation selectedCustomerAffiliation
        List<OfferId> availableVersions
        double overheadRatio
        Optional<ProjectIdentifier> associatedProject

        Builder(Customer customer, ProjectManager projectManager, String projectTitle, String projectObjective, List<ProductItem> items, Affiliation selectedCustomerAffiliation) {
            this.customer = Objects.requireNonNull(customer, "Customer must not be null")
            this.projectManager = Objects.requireNonNull(projectManager, "Project Manager must not be null")
            this.projectTitle = Objects.requireNonNull(projectTitle, "Project Title must not be null")
            this.projectObjective = Objects.requireNonNull(projectObjective, "Project Objective must not be null")
            this.items = []
            this.availableVersions = []
            this.creationDate = new Date()
            this.experimentalDesign = Optional.empty()
            // Since the incoming item list is mutable we need to
            // copy all immutable items to out internal list
            items.each {this.items.add(it)}
            this.selectedCustomerAffiliation = Objects.requireNonNull(selectedCustomerAffiliation, "Customer Affiliation must not be null")
            this.associatedProject = Optional.empty()
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

        Builder overheadRatio(double overheadRatio){
            this.overheadRatio = overheadRatio
            return this
        }

        Builder associatedProject(ProjectIdentifier associatedProject) {
            this.associatedProject = Optional.of(associatedProject)
            return this
        }

        Builder experimentalDesign(Optional<String> experimentalDesign){
            this.experimentalDesign = experimentalDesign
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
        this.expirationDate = calculateExpirationDate(builder.creationDate)
        this.creationDate = builder.creationDate
        this.projectManager = builder.projectManager
        this.projectObjective = builder.projectObjective

        if (builder.experimentalDesign.isPresent()) {
            this.experimentalDesign = builder.experimentalDesign
        } else {
            this.experimentalDesign = Optional.empty()
        }

        this.projectTitle = builder.projectTitle
        this.selectedCustomerAffiliation = builder.selectedCustomerAffiliation
        this.overhead = determineOverhead()
        this.availableVersions = builder.availableVersions
                .stream()
                .map(id -> new OfferId(id)).collect()
        this.availableVersions.add(this.identifier)
        this.itemsWithOverhead = getOverheadItems()
        this.itemsWithoutOverhead = getNoOverheadItems()
        this.itemsWithOverheadNetPrice = getOverheadItemsNet()
        this.itemsWithoutOverheadNetPrice = getNoOverheadItemsNet()
        this.overheadRatio = determineOverhead()
        if (builder.associatedProject.isPresent()) {
            this.associatedProject = Optional.of(builder.associatedProject.get())
        } else {
            this.associatedProject = Optional.empty()
        }
        builder.items.each {this.items.add(finaliseProductItem(it))}
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
     * The total net price for the current offer including overheads.
     *
     * Note: Does <strong>not include</strong> taxes.
     *
     * @return The net offer price including overheads
     */
    double getTotalNetPriceWithOverheads() {
        return BigDecimal.valueOf(calculateNetPrice() + getOverheadSum())
    }

    /**
     * The overhead price amount of all service items without VAT.
     *
     * @return The calculated overhead amount of the selected items.
     */
    BigDecimal getOverheadSum() {
        if (items.empty) {
            return 0
        }
        return items.sum {BigDecimal.valueOf(calculateItemOverhead(it))} as BigDecimal
    }

    private BigDecimal determineUnitPrice(ProductItem item) {
        BigDecimal unitPrice = selectedCustomerAffiliation.category == AffiliationCategory.INTERNAL ? item.product.internalUnitPrice as BigDecimal: item.product.externalUnitPrice as BigDecimal
        return unitPrice
    }

    private BigDecimal calculateItemNet(ProductItem item) {
        BigDecimal unitPrice = determineUnitPrice(item)
        BigDecimal totalItemPrice = unitPrice * item.quantity
        return totalItemPrice - Math.max(quantityDiscountAmountForProductItem(item),
                storageDiscountAmountForProductItem(item))
    }

    private BigDecimal calculateItemOverhead(ProductItem item) {
        return calculateItemNet(item) * overhead
    }

    /**
     * This method returns the net cost of all product items for which no overhead cost is calculated
     *
     * @return net cost of product items without overhead cost
     */
    @Deprecated
    double getNoOverheadItemsNet() {
        double costNoOverheadItemsNet = 0
        items.each {
            // No overheads are assigned for data storage and project management
            if (it.product instanceof DataStorage || it.product instanceof ProjectManagement) {
                costNoOverheadItemsNet += calculateItemNet(it)
            }
        }
        return costNoOverheadItemsNet
    }

    /**
     * This method returns the net cost of product items for which an overhead cost is calculated
     *
     * @return net cost of product items with overhead cost
     */
    @Deprecated
    double getOverheadItemsNet() {
        double costOverheadItemsNet = 0
        items.each {
            if (it.product instanceof ProjectManagement || it.product instanceof DataStorage) {
                // No overheads are assigned for data storage and project management
            }
            else {
                costOverheadItemsNet += calculateItemNet(it)
            }
        }
        return costOverheadItemsNet
    }

    /**
     * The tax price on all items net price including overheads.
     *
     * For internal affiliated customers and customers outside of Germany, this will be set to 0.
     *
     * @return The amount of VAT price based on all items in the offer.
     */
    double getTaxCosts() {
        if (!selectedCustomerAffiliation.category.equals(noVatCategory) && selectedCustomerAffiliation.country.equals(countryWithVat)) {
            return (calculateNetPrice() + getOverheadSum()) * VAT
        }
        else {
            return 0
        }
    }

    /**
     * This method returns all ProductItems for which an Overhead cost is calculated
     *
     * @return ProductItem list containing all ProductItems with overhead cost
     */
    @Deprecated
    List<ProductItem> getOverheadItems() {
        List<ProductItem> listOverheadProductItem = []
        items.each {
            if (it.product instanceof DataStorage || it.product instanceof ProjectManagement){
                // No overheads are assigned for data storage and project management
            }
            else {
                listOverheadProductItem.add(it)
            }
        }
        return listOverheadProductItem
    }

    /**
     * This method returns all ProductItems for which no Overhead cost is calculated
     *
     * @return ProductItem list containing all ProductItems without overhead cost
     */
    @Deprecated
    List<ProductItem> getNoOverheadItems(){
        List<ProductItem> listNoOverheadProductItem = []
        items.each {
            if (it.product instanceof DataStorage || it.product instanceof ProjectManagement) {
                listNoOverheadProductItem.add(it)
            }
        }
        return listNoOverheadProductItem
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

    String getProjectObjective() {
        return projectObjective
    }

    Optional<String> getExperimentalDesign(){
        return experimentalDesign
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

    double getOverheadRatio() {
        return overheadRatio
    }

    Optional<ProjectIdentifier> getAssociatedProject() {
        return associatedProject
    }

    static double getVAT() {
        return VAT
    }

    static String getCountryWithVat() {
        return countryWithVat
    }

    static AffiliationCategory getNoVatCategory() {
        return noVatCategory
    }

    /**
     * Returns a deep copy of all available offer versions.
     *
     * @return A list of available offer versions
     */
    List<OfferId> getAvailableVersions() {
        return availableVersions.stream()
                .map(offerId -> new OfferId(
                        new RandomPart(offerId.randomPart),
                        new ProjectPart(offerId.projectPart),
                        new Version(offerId.version)
                )).collect()
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

    /**
     * This method returns the total discount amount that was applied the offer.
     *
     * It is expected to return the sum of all individual item discount amounts that have been
     * determined by the number of samples an individual data analysis service has been provided for.
     *
     * @return the total discount amount applied in the offer
     * @since 1.1.0
     */
    double getTotalDiscountAmount(){
        return calculateTotalDiscountAmount()
    }

    private BigDecimal calculateTotalDiscountAmount() {
        return items.sum{ BigDecimal.valueOf(it.quantityDiscount) } as BigDecimal
    }

    private BigDecimal calculateCataloguePriceForItem(ProductItem item) {
        return BigDecimal.valueOf(item.quantity * determineUnitPrice(item))
    }

    private BigDecimal quantityDiscountAmountForProductItem(ProductItem productItem) {
        BigDecimal discount = 0
        if (productItem.product instanceof PrimaryAnalysis
                || productItem.product instanceof SecondaryAnalysis) {
            BigDecimal unitPrice = determineUnitPrice(productItem)
            BigDecimal unitPriceDiscount = quantityDiscount.apply(productItem.quantity as Integer,
                    unitPrice)
            discount = unitPriceDiscount * productItem.quantity
            discount = discount.round(2)
        }
        return discount
    }

    private BigDecimal calculateNetPrice() {
        if (items.empty) {
            return 0
        }
        return items.sum {BigDecimal.valueOf(it.totalPrice - it.quantityDiscount)}
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
        double netPrice = calculateNetPrice()
        final double overhead = getOverheadSum()
        return netPrice + overhead + getTaxCosts()
    }

    private ProductItem finaliseProductItem(ProductItem item) {
        BigDecimal totalItemQuantityDiscount = quantityDiscountAmountForProductItem(item)
        BigDecimal totalDataStorageDiscount = storageDiscountAmountForProductItem(item)
        BigDecimal totalItemCosts = calculateCataloguePriceForItem(item)
        // we determine the max discount available for the product item and assign it
        BigDecimal maxDiscount = totalItemQuantityDiscount.max(totalDataStorageDiscount)
        return new ProductItem(item.quantity, item.product, totalItemCosts, maxDiscount)
    }

    private BigDecimal storageDiscountAmountForProductItem(ProductItem item) {
        BigDecimal discount = BigDecimal.ZERO
        if (dataStorageApplicable.test(item)) {
            try {
                discount = cataloguePrice.andThen(dataStorageDiscount).apply(item)
            } catch (IllegalArgumentException e) {
                log.error("Negative values for product Items are not applicable for a discount, $e.message", e)
            }
        }
        return discount
    }

    /**
     * Calculates the VAT costs of an offer depending on the customers affiliation country and category
     * @return the vat costs for an offer
     */
    double determineTaxCost() {
        return isVatCountry() && !isNoVatAffiliation() ? VAT : 0.0
    }

    /**
     * Determines if the customers affiliation is within germany
     * @return true if the country of the customer is within
     */
    boolean isVatCountry(){
        return selectedCustomerAffiliation.getCountry() == countryWithVat

    }

    /**
     * Determines if the customers affiliation is excluded for VAT
     * @return true if the affiliation category of the customer is internal
     */
    private boolean isNoVatAffiliation(){
        return selectedCustomerAffiliation.getCategory() == noVatCategory
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
        //Use SHA-2 algorithm
        MessageDigest shaDigest = MessageDigest.getInstance("SHA-256")

        //SHA-2 checksum
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
        digest.update(offer.projectObjective.getBytes(StandardCharsets.UTF_8))
        if(offer.experimentalDesign.isPresent()) digest.update(offer.experimentalDesign.get().getBytes(StandardCharsets.UTF_8))

        offer.items.each {item ->
            digest.update(item.product.productName.getBytes(StandardCharsets.UTF_8))
            digest.update(item.quantity.toString().getBytes(StandardCharsets.UTF_8))
        }
        digest.update(offer.customer.lastName.getBytes(StandardCharsets.UTF_8))
        digest.update(offer.projectManager.lastName.getBytes(StandardCharsets.UTF_8))

        digest.update(offer.selectedCustomerAffiliation.getOrganisation().toString().getBytes(StandardCharsets
                .UTF_8))
        digest.update(offer.selectedCustomerAffiliation.getStreet().toString().getBytes(StandardCharsets
                .UTF_8))

        //Get the hash's bytes
        byte[] bytes = digest.digest()

        //This bytes[] has bytes in decimal format
        //Convert it to hexadecimal format
        StringBuilder sb = new StringBuilder()
        for(int i=0; i< bytes.length ;i++)
        {
            sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1))
        }

        //return complete hash
        return sb.toString()
    }
}
