package life.qbic.business.offers


import life.qbic.business.offers.identifier.OfferId
import life.qbic.business.persons.Person
import life.qbic.business.persons.affiliation.Affiliation
import life.qbic.business.persons.affiliation.AffiliationCategory
import life.qbic.business.products.Product
import life.qbic.business.products.ProductItem
import life.qbic.datamodel.dtos.projectmanagement.ProjectCode
import life.qbic.datamodel.dtos.projectmanagement.ProjectIdentifier
import life.qbic.datamodel.dtos.projectmanagement.ProjectSpace

import javax.persistence.*
import java.math.RoundingMode
import java.time.LocalDate

/**
 * <b>New Offer class</b>
 *
 * <p>An offer entity holds crucial information about a customers quote request, such
 * as the service items needed for the research question at hand, the project's objective
 * and more.</p>
 *
 * @since 1.3.0
 */
@Entity
@Table(name = "offer")
class OfferV2 {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id")
  private int id

  /**
   * Date on which the offer was lastly modified
   */
  @Column(name = "creationDate")
  private LocalDate creationDate

  /**
   * The date on which the offer expires
   */
  @Column(name = "expirationDate")
  private LocalDate expirationDate

  /**
   * The customer for which this offer was created
   */
  @OneToOne(cascade = [CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH])
  @JoinColumn(name = "customerId")
  private Person customer

  /**
   * The QBiC project manager who was assigned to the project
   */
  @OneToOne(cascade = [CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH])
  @JoinColumn(name = "projectManagerId")
  private Person projectManager

  /**
   * The title of the project
   */
  @Column(name = "projectTitle")
  private String projectTitle

  /**
   * A short objective of the project
   */
  @Column(name = "projectObjective")
  private String projectObjective

  /**
   * A short description of the experimental design of the project
   */
  @Column(name = "experimentalDesign")
  @Convert(converter = OptionalStringConverter.class)
  private Optional<String> experimentalDesign

  /**
   * A list of items for which the customer will be charged
   */
  @OneToMany(mappedBy = "offer", cascade = [CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH])
  @Access(AccessType.PROPERTY)
  private List<ProductItem> items = []

  @Column(name = "offerId")
  private String offerId

  /**
   * The affiliation of the customer selected for this offer
   */
  @OneToOne(cascade = [CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH])
  @JoinColumn(name = "customerAffiliationId")
  private Affiliation selectedCustomerAffiliation

  /**
   * Holds the determined overhead total derived from the
   * customer's affiliation.
   */
  @Column(name = "overheads")
  private double overhead

  /**
   * The overhead ratio that is applied to calculate the total offer price. The ratio is chosen
   * based on the customer's affiliation.
   *  e.g. 0.4 or a 40% markup for external customers
   */
  @Transient
  private double overheadRatio

  @Column(name = "checksum")
  private String checksum

  /**
   * A project that has been created from this offer (optional)
   */
  @Column(name = "associatedProject")
  @Convert(converter = OptionalProjectIdentifierConverter.class)
  private ProjectIdentifier associatedProject

  @Transient
  private ItemGroup dataGenerationItems = new ItemGroup()
  @Transient
  private ItemGroup dataAnalysisItems = new ItemGroup()
  @Transient
  private ItemGroup dataManagementItems = new ItemGroup()
  @Transient
  private ItemGroup externalServiceItems = new ItemGroup()

  @Transient private BigDecimal dataAnalysisOverhead = BigDecimal.ZERO
  @Transient private BigDecimal dataGenerationOverhead = BigDecimal.ZERO
  @Transient private BigDecimal dataManagementOverhead = BigDecimal.ZERO
  @Transient private BigDecimal externalServiceOverhead = BigDecimal.ZERO
  @Transient private BigDecimal dataAnalysisSalePrice = BigDecimal.ZERO
  @Transient private BigDecimal dataGenerationSalePrice = BigDecimal.ZERO
  @Transient private BigDecimal dataManagementSalePrice = BigDecimal.ZERO
  @Transient private BigDecimal externalServiceSalePrice = BigDecimal.ZERO
  @Transient private BigDecimal salePrice = BigDecimal.ZERO
  @Transient private BigDecimal vatRatio = BigDecimal.ZERO
  @Transient private BigDecimal priceAfterTax = BigDecimal.ZERO
  @Transient private BigDecimal taxAmount = BigDecimal.ZERO
  @Transient private BigDecimal priceBeforeTax = BigDecimal.ZERO
  @Transient private BigDecimal discountAmount = BigDecimal.ZERO

  OfferV2() {}

  /**
   * Constructor for an OfferV2 entity on which price calculation can be performed on.
   * @param items
   * @param selectedCustomerAffiliation
   */
  OfferV2(Affiliation selectedCustomerAffiliation, OfferId identifier) {
    this.setSelectedCustomerAffiliation(selectedCustomerAffiliation)
    this.identifier = identifier
  }

  /**
   * Adds the item to the corresponding item group based on the product category.
   * @param item the added item
   */
  private void addItemToGroup(ProductItem item) {
    List<String> dataGenerationCategories = Arrays.asList("Sequencing", "Proteomics", "Metabolomics")
    List<String> dataAnalysisCategories = Arrays.asList("Primary Bioinformatics", "Secondary Bioinformatics")
    List<String> projectManagementCategories = Arrays.asList("Project Management", "Data Storage")
    List<String> externalServiceCategories = Collections.singletonList("External Service")

    String category = item.getProduct().getCategory()
    if (dataGenerationCategories.contains(category)) {
      this.dataGenerationItems.add(item)
    } else if (dataAnalysisCategories.contains(category)) {
      this.dataAnalysisItems.add(item)
    } else if (projectManagementCategories.contains(category)) {
      this.dataManagementItems.add(item)
    } else if (externalServiceCategories.contains(category)) {
      this.externalServiceItems.add(item)
    }
  }

  /**
   * Determines the VAT ratio. The VAT ratio for Germany is 0.19. For all
   * other countries, the VAT ratio is 0.
   *
   * @param country the country for which to get the VAT ratio
   * @return the VAT ratio for the provided country
   */
  protected static BigDecimal vatRatio(String country) {
    Map<String, BigDecimal> vatRatios = new HashMap<>()
    vatRatios.put("Germany", BigDecimal.valueOf(0.19))
    return vatRatios.getOrDefault(country, BigDecimal.ZERO)
  }

  BigDecimal getPriceAfterTax() {
    return priceAfterTax
  }

  BigDecimal getTaxAmount() {
    return taxAmount
  }

  BigDecimal getPriceBeforeTax() {
    return priceBeforeTax
  }
/**
 * Thread-safe request of the current offer content's SHA-256 checksum
 * @return the checksum at the time of request execution
 */
  String getChecksum() {
    String checksum
    synchronized (this) {
      checksum = new OfferChecksumSupplier(this).get()
    }
    return checksum
  }

  BigDecimal getDataAnalysisOverhead() {
    return dataAnalysisOverhead
  }

  BigDecimal getDataGenerationOverhead() {
    return dataGenerationOverhead
  }

  BigDecimal getDataManagementOverhead() {
    return dataManagementOverhead
  }

  BigDecimal getExternalServiceOverhead() {
    return externalServiceOverhead
  }

  BigDecimal getDataAnalysisSalePrice() {
    return dataAnalysisSalePrice
  }

  BigDecimal getDataGenerationSalePrice() {
    return dataGenerationSalePrice
  }

  BigDecimal getDataManagementSalePrice() {
    return dataManagementSalePrice
  }

  BigDecimal getExternalServiceSalePrice() {
    return externalServiceSalePrice
  }

  BigDecimal getSalePrice() {
    return salePrice
  }

  BigDecimal getVatRatio() {
    return vatRatio
  }

  BigDecimal getTotalVat() {
    //FIXME
    return taxAmount
  }

  BigDecimal getTotalCost() {
    //FIXME
    return priceAfterTax
  }

  BigDecimal getTotalDiscountAmount() {
    return discountAmount
  }

  LocalDate getCreationDate() {
    return creationDate
  }

  void setCreationDate(LocalDate creationDate) {
    this.creationDate = creationDate
  }

  LocalDate getExpirationDate() {
    return expirationDate
  }

  void setExpirationDate(LocalDate expirationDate) {
    this.expirationDate = expirationDate
  }

  Person getCustomer() {
    return customer
  }

  void setCustomer(Person customer) {
    this.customer = customer
  }

  Person getProjectManager() {
    return projectManager
  }

  void setProjectManager(Person projectManager) {
    this.projectManager = projectManager
  }

  String getProjectTitle() {
    return projectTitle
  }

  void setProjectTitle(String projectTitle) {
    this.projectTitle = projectTitle
  }

  String getProjectObjective() {
    return projectObjective
  }

  void setProjectObjective(String projectObjective) {
    this.projectObjective = projectObjective
  }

  Optional<String> getExperimentalDesign() {
    return experimentalDesign
  }

  void setExperimentalDesign(Optional<String> experimentalDesign) {
    this.experimentalDesign = experimentalDesign
  }

  OfferId getIdentifier() {
    return OfferId.from(offerId)
  }

  void setIdentifier(OfferId identifier) {
    this.offerId = identifier.toString()
  }

  Affiliation getSelectedCustomerAffiliation() {
    return selectedCustomerAffiliation
  }

  void setSelectedCustomerAffiliation(Affiliation selectedCustomerAffiliation) {
    this.selectedCustomerAffiliation = selectedCustomerAffiliation
    recomputeWithAffiliation()
  }

  private void recomputeWithAffiliation() {
    aggregateCosts()
  }

  private void updateVatRatio() {
    this.vatRatio = vatRatio(selectedCustomerAffiliation.country)
  }

  private void updatePriceBeforeTax() {
    this.priceBeforeTax = BigDecimal.valueOf(overhead).add(salePrice)
  }

  private void updateOverheadAmount() {
    this.dataGenerationOverhead = dataGenerationSalePrice * overheadRatio
    this.dataAnalysisOverhead = dataAnalysisSalePrice * overheadRatio
    this.dataManagementOverhead = dataManagementSalePrice * overheadRatio
    this.externalServiceOverhead = externalServiceSalePrice * overheadRatio
    this.overhead = dataGenerationOverhead
            .add(dataAnalysisOverhead)
            .add(dataManagementOverhead)
            .add(externalServiceOverhead)
  }

  double getOverhead() {
    return overhead
  }

  protected void setOverhead(double overhead) {
    this.overhead = overhead
  }

  double getOverheadRatio() {
    return overheadRatio
  }

  Optional<ProjectIdentifier> getAssociatedProject() {
    return Optional.ofNullable(associatedProject)
  }

  void setAssociatedProject(ProjectIdentifier associatedProject) {
    this.associatedProject = associatedProject
  }

  List<ProductItem> getItems() {
    return items.collect()
  }

  void setItems(List<ProductItem> items) {
    clearItems()
    addItems(items)
  }

  private void clearItems() {
    items.clear()
    dataManagementItems.clear()
    dataAnalysisItems.clear()
    dataGenerationItems.clear()
    externalServiceItems.clear()
  }

  void addItems(List<ProductItem> items) {
    items.forEach(it -> addItem(it.getProduct(), it.getQuantity()))
    aggregateCosts()
  }

  private void aggregateCosts() {
    //ATTENTION: TEMPORAL COUPLING! Do not change the order
    updateSalePrices()
    //-
    updateOverhead()
    updatePriceBeforeTax()
    updateVat()
    updatePriceAfterTax()
    //-
    updateDiscountAmount()
  }

  private void updateVat() {
    updateVatRatio()
    updateVatAmount()
  }

  private void updateOverhead() {
    updateOverheadRatio()
    updateOverheadAmount()
  }

  private void updatePriceAfterTax() {
    this.priceAfterTax = priceBeforeTax.add(taxAmount)
  }

  private void updateVatAmount() {
    this.taxAmount = priceBeforeTax.multiply(vatRatio).setScale(2, RoundingMode.HALF_UP)
  }

  /**
   * This method does not recompute the prices! For internal use only!
   * @param product the product for which an item shall be added
   * @param quantity the amount of the product
   */
  private void addItem(Product product, Double quantity) {
    ProductItem productItem = new ProductItem(this, product, quantity)
    this.addItemToGroup(productItem)
    this.items.add(productItem)
  }

  private void updateSalePrices() {
    dataGenerationSalePrice = dataGenerationItems.getSalePrice()
    dataAnalysisSalePrice = dataAnalysisItems.getSalePrice()
    dataManagementSalePrice = dataManagementItems.getSalePrice()
    externalServiceSalePrice = externalServiceItems.getSalePrice()
    salePrice = dataGenerationSalePrice
            .add(dataAnalysisSalePrice)
            .add(dataManagementSalePrice)
            .add(externalServiceSalePrice)
  }


  List<ProductItem> getDataManagementItems() {
    // Create a copy of the item collection
    return dataManagementItems.iterator().collect()
  }

  List<ProductItem> getDataAnalysisItems() {
    return dataAnalysisItems.iterator().collect()
  }

  List<ProductItem> getDataGenerationItems() {
    return dataGenerationItems.iterator().collect()
  }

  List<ProductItem> getExternalServiceItems() {
    return externalServiceItems.iterator().collect()
  }

  static OfferV2 copyOf(OfferV2 original) {
    return original.copy()
  }

  String getOfferId() {
    return offerId
  }

  String setOfferId(String offerId) {
    this.offerId = offerId
  }

  private OfferV2 copy() {
    OfferV2 offerCopy = new OfferV2()

    //sorted alphabetically
    offerCopy.setAssociatedProject(this.getAssociatedProject()?.orElse(null))
    offerCopy.setCreationDate(this.getCreationDate())
    offerCopy.setCustomer(this.getCustomer())
    offerCopy.setExperimentalDesign(this.getExperimentalDesign())
    offerCopy.setExpirationDate(this.getExpirationDate())
    offerCopy.setOfferId(this.getOfferId())
    offerCopy.setProjectManager(this.getProjectManager())
    offerCopy.setProjectObjective(this.getProjectObjective())
    offerCopy.setProjectTitle(this.getProjectTitle())
    offerCopy.setSelectedCustomerAffiliation(this.getSelectedCustomerAffiliation())
    offerCopy.discountAmount = this.getTotalDiscountAmount()
    offerCopy.salePrice = this.getSalePrice()
    offerCopy.priceAfterTax = this.getTotalCost()
    offerCopy.vatRatio = this.getVatRatio()

    offerCopy.dataAnalysisSalePrice = this.getDataAnalysisSalePrice()
    offerCopy.dataGenerationSalePrice = this.getDataGenerationSalePrice()
    offerCopy.dataManagementSalePrice = this.getDataManagementSalePrice()
    offerCopy.externalServiceSalePrice = this.getExternalServiceSalePrice()
    offerCopy.setOverhead(this.getOverhead())
    offerCopy.overheadRatio = this.getOverheadRatio()
    offerCopy.dataAnalysisOverhead = this.getDataAnalysisOverhead()
    offerCopy.dataGenerationOverhead = this.getDataGenerationOverhead()
    offerCopy.dataManagementOverhead = this.getDataManagementOverhead()
    offerCopy.externalServiceOverhead = (this.getExternalServiceOverhead())
    //fields
    offerCopy.setItems(this.getItems())

    return offerCopy
  }

  /**
   * Updates the overhead ratio and any price depending on it
   * <ul>
   *   <li>overhead costs</li>
   *   <li>price before tax</li>
   *   <li>tax amount</li>
   *   <li>price after tax</li>
   * </ul>
   */
  private void updateOverheadRatio() {
    def category = selectedCustomerAffiliation.getCategory()
    this.overheadRatio = determineOverheadRate(category)
  }

  /*
  item prices
  group prices

   */

  private static BigDecimal determineOverheadRate(AffiliationCategory category) {
    if (category == AffiliationCategory.INTERNAL) {
      return BigDecimal.ZERO
    }
    if (category == AffiliationCategory.EXTERNAL_ACADEMIC) {
      return new BigDecimal("0.2")
    }
    return new BigDecimal("0.4")
  }
  //Note this is only used for Reporting and not in the offer
  private void updateDiscountAmount() {
    this.discountAmount = dataGenerationItems.discountAmount
            .add(dataAnalysisItems.discountAmount)
            .add(dataManagementItems.discountAmount)
            .add(externalServiceItems.discountAmount)
  }

  private static class ItemGroup extends ArrayList<ProductItem> {
    BigDecimal getSalePrice() {
      this.stream().map(ProductItem::getSalePrice).reduce(BigDecimal.ZERO, BigDecimal::add)
    }

    BigDecimal getDiscountAmount() {
      return this.stream().map(ProductItem::getDiscountAmount).reduce(BigDecimal.ZERO, BigDecimal::add)
    }
  }


  private static class OptionalStringConverter implements AttributeConverter<Optional<String>, String> {

    @Override
    String convertToDatabaseColumn(Optional<String> s) {
      return s.orElse("")
    }

    @Override
    Optional<String> convertToEntityAttribute(String s) {
      // If the String is null or empty, we return an empty Optional
      return s ? Optional.of(s) : Optional.empty() as Optional<String>
    }
  }

  private static class OptionalProjectIdentifierConverter implements AttributeConverter<ProjectIdentifier, String> {

    @Override
    String convertToDatabaseColumn(ProjectIdentifier projectIdentifier) {
      if (projectIdentifier) {
        return projectIdentifier.toString()
      } else {
        return null
      }
    }

    @Override
    ProjectIdentifier convertToEntityAttribute(String projectIdentifier) {
      ProjectIdentifier identifier = null
      if (!projectIdentifier) {
        return identifier
      }
      try {
        /*
        A full openBIS project ID has the format: '/<space>/<project>', where
        <space> and <project> are placeholders for real space and project names.
         */
        def splittedIdentifier = projectIdentifier.split("/")
        if (splittedIdentifier.length != 3) {
          throw new RuntimeException(
                  "Project identifier has an unexpected number of separators: ${projectIdentifier}. " +
                          "The expected format must follow this schema: \'/<space>/<project>\'")
        }
        def space = new ProjectSpace(splittedIdentifier[1])
        def code = new ProjectCode(splittedIdentifier[2])
        identifier = new ProjectIdentifier(space, code)
      } catch (Exception ignored) {
        throw new IllegalArgumentException(String.format("Cannot parse %s to a project identifier.", projectIdentifier))
      }
      return identifier
    }
  }
}
