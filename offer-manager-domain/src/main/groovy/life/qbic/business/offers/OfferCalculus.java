package life.qbic.business.offers;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import life.qbic.business.persons.affiliation.AffiliationCategory;
import life.qbic.business.products.Product;
import life.qbic.business.products.ProductItem;


/**
 * <b>OfferCalculus</b>
 *
 * <p>Not a spell! </p>
 *
 * <p>This class offers various methods to perform calculations in the process
 * of offer creation and incorporates central business rules.</p>
 *
 * @since 1.3.0
 */
class OfferCalculus {

  private static final BigDecimal OVERHEAD_RATIO_EXTERNAL_ACADEMIC = new BigDecimal("0.2");

  private static final BigDecimal OVERHEAD_RATIO_EXTERNAL = new BigDecimal("0.4");

  private static final List<String> DATA_GENERATION = Arrays.asList("Sequencing", "Proteomics", "Metabolomics");
  private static final List<String> DATA_ANALYSIS = Arrays.asList("Primary Bioinformatics", "Secondary Bioinformatics");
  private static final List<String> PROJECT_AND_DATA_MANAGEMENT = Arrays.asList("Project Management", "Data Storage");
  private static final List<String> EXTERNAL_SERVICES = Collections.singletonList("External Service");

  /**
   * Uses the {@link ProductItem} items provided in the offer and creates a list
   * of converted {@link OfferItem} items. The affiliation is used to determine,
   * if the customer has an internal or external affiliation. Based on the affiliation, the
   * internal or external unit price is used for the calculation.
   * @param offer the offer to use to access the product items
   * @return a list of prepared OfferItem items
   */
  protected static List<OfferItem> createOfferItems(OfferV2 offer) {
    AffiliationCategory affiliationCategory = offer.getSelectedCustomerAffiliation().getCategory();
    List<OfferItem> offerItems;
    // Discounting for internal customer is different, so we check the affiliation first
    if (affiliationCategory == AffiliationCategory.INTERNAL) {
      offerItems = createOfferItemsForInternals(offer.getItems());
    } else {
      offerItems = createOfferItemsForExternals(offer.getItems());
    }
    return offerItems;
  }

  /**
   * <p>Groups offer items based on their assigned category (data generation, data analysis, project
   * and data management or external services) and fills the offer.
   * The returned offer object is a full copy of the input offer and will contain the right
   * grouping of the given offer items.</p>
   * <p>If the offer items list passed is empty, this method tries to create the offer items first
   * based on the ProductItems in the offer. Internally, the method {@link OfferCalculus#createOfferItems(OfferV2)}
   * is used for this.</p>
   * @param offer the offer to use as basis for the calculations
   * @param offerItems the offer items to use to group the items into their belonging category
   * @return A copy of the input offer, with the offer items sorted in the categories.
   * @throws OfferCalculusException if anything goes wrong
   */
  protected static OfferV2 groupItems(OfferV2 offer, List<OfferItem> offerItems) throws OfferCalculusException {
    if (offerItems.isEmpty()) {
      offerItems = createOfferItems(offer);
    }
    OfferV2 offerCopy = OfferV2.copy(offer);
    List<OfferItem> ungroupedItems = new ArrayList<>();
    // And then sort them into the correct categories
    for (OfferItem item : offerItems) {
      String category = item.getCategory();
      if (DATA_GENERATION.contains(category)) {
        offerCopy.addDataGenerationItem(item);
        continue;
      }
      if (DATA_ANALYSIS.contains(category)) {
        offerCopy.addDataAnalysisItem(item);
        continue;
      }
      if (PROJECT_AND_DATA_MANAGEMENT.contains(category)) {
        offerCopy.addDataManagementItem(item);
        continue;
      }
      if (EXTERNAL_SERVICES.contains(category)) {
        offerCopy.addExternalServiceItem(item);
      } else {
        ungroupedItems.add(item);
      }
    }
    if (ungroupedItems.size() > 0) {
      throw new OfferCalculusException("Some offer items could not be sorted, because their category"
          + " was unknown. " + ungroupedItems);
    }
    return offerCopy;
  }

  public static OfferV2 groupItems(OfferV2 offerV2) throws OfferCalculusException {
    return groupItems(offerV2, createOfferItems(offerV2));
  }

  /**
   * Create offer items based on a list of product items, assuming the selected customer affiliation
   * is internal
   * @param productItems the product items to use as information for the offer item creation
   * @return a collection of offer items
   */
  public static List<OfferItem> createOfferItemsForExternals(List<ProductItem> productItems) {
    return productItems.stream()
        .map( OfferCalculus::createOfferItemWithExternalPrice)
        .collect(Collectors.toList());
  }

  /**
   * Create offer items based on a list of product items, assuming the selected customer affiliation
   * is external or external academic
   * @param productItems the product items to use as information for the offer item creation
   * @return a collection of offer items
   */
  public static List<OfferItem> createOfferItemsForInternals(List<ProductItem> productItems) {
    return productItems.stream()
        .map( OfferCalculus::createOfferItemWithInternalPrice)
        .collect(Collectors.toList());
  }

  /**
   * Creates an {@link OfferItem} object with all its properties calculated based on the
   * given {@link ProductItem}. This method uses the <b>internal unit price</b>.
   * @param item the item that is going to be used to create the OfferItem instance.
   * @return the offer item based on the given product item
   */
  public static OfferItem createOfferItemWithInternalPrice(ProductItem item) {
    Double selectedUnitPrice = item.getProduct().getInternalUnitPrice();
    if (item.getProduct().getCategory().equalsIgnoreCase("data storage")) {
      return createWithUnitPriceAndFullStorageDiscount(item, selectedUnitPrice);
    }
    return createWithUnitPrice(item, selectedUnitPrice);
  }

  /**
   * Creates an offer item based on a given ProductItem and a selected unit price.
   *
   * @param item a {@link ProductItem} of category `Data Storage`
   * @param selectedUnitPrice the pre-selected unit price
   * @return a new instance of a {@link OfferItem} with the storage discount applied
   * @throws OfferCalculusException if the item product is not of category 'Data Storage'
   */
  public static OfferItem createWithUnitPriceAndFullStorageDiscount(ProductItem item, Double selectedUnitPrice) throws OfferCalculusException {
    String productCategory = item.getProduct().getCategory();
    if (!(productCategory.equalsIgnoreCase("data storage")))  {
      throw new OfferCalculusException(String.format("Product item must be of category 'Data Storage' but was '%s'.", productCategory));
    }
    OfferItem offerItem = createWithUnitPrice(item, selectedUnitPrice);
    double storageDiscount = offerItem.getItemTotal();

    // important to make sure that the unit price is rounded up to the second
    double storageDiscountPerUnit = offerItem.getUnitPrice();
    return new OfferItem.Builder(offerItem.getQuantity(),
        offerItem.getProductDescription(), offerItem.getProductName(), offerItem.getUnitPrice(),
        storageDiscount, storageDiscountPerUnit, offerItem.getDiscountPercentage(),
            offerItem.getServiceProvider(), offerItem.getUnit(), offerItem.getItemTotal())
        .setCategory(offerItem.getCategory()).build();
  }

  /**
   * Creates an {@link OfferItem} object with all its properties calculated based on the
   * given {@link ProductItem}. This method uses the <b>external unit price</b>.
   * @param item the item that is going to be used to create the OfferItem instance.
   * @return the offer item based on the given product item
   */
  public static OfferItem createOfferItemWithExternalPrice(ProductItem item) {
    Double selectedUnitPrice = item.getProduct().getExternalUnitPrice();
    return createWithUnitPrice(item, selectedUnitPrice);
  }
  /*
  Creates an OfferItem based on a ProductItem and a pre-selected unit price.
   */
  private static OfferItem createWithUnitPrice(ProductItem item, Double unitPrice) {
    String category = item.getProduct().getCategory();
    BigDecimal unitPriceAfterDiscount;
    BigDecimal totalDiscount;
    Double productQuantity = item.getQuantity();

    if (DATA_GENERATION.contains(category)) {
      unitPriceAfterDiscount = applyQuantityDiscount(BigDecimal.valueOf(unitPrice),
          productQuantity.intValue());
      totalDiscount = unitPriceAfterDiscount.multiply(BigDecimal.valueOf(productQuantity));
    } else {
      // Without discount, the unit after discount is equal to the original unit price
      unitPriceAfterDiscount = BigDecimal.valueOf(unitPrice);
      totalDiscount = BigDecimal.ZERO;
    }

    Product product = item.getProduct();
    // Calculate the discount percentage
    Double discountPercentage = calcDiscountPercentage(BigDecimal.valueOf(unitPrice), unitPriceAfterDiscount).doubleValue();
    // Get the final item price, excluding potential discount
    Double totalPrice = unitPrice * productQuantity;
    return createOfferItem(productQuantity, product.getDescription(), product.getProductName(),
        unitPrice,
        totalDiscount.doubleValue(), unitPriceAfterDiscount.doubleValue(),
        discountPercentage, product.getServiceProvider(),
        product.getUnit(), totalPrice, product.getCategory());
  }

  private static BigDecimal formatCurrency(BigDecimal value) {
    return value.setScale(2, RoundingMode.HALF_UP);
  }

  private static BigDecimal calcDiscountPercentage(BigDecimal originalPrice, BigDecimal discountedPrice) {
    BigDecimal difference = originalPrice.subtract(discountedPrice);
    if (difference.equals(BigDecimal.ZERO)) {
      return BigDecimal.ZERO;
    }
    return difference.divide(originalPrice);
  }

  private static OfferItem createOfferItem(Double quantity, String description, String productName, Double unitPrice,
      Double quantityDiscount, Double unitDiscount, Double discountPercentage, String serviceProvider,
      String unit, Double totalPrice, String productCategory) {

    return new OfferItem.Builder(quantity, description, productName, unitPrice,
        quantityDiscount, unitDiscount, discountPercentage,
        serviceProvider, unit, totalPrice)
        .setCategory(productCategory).build();
  }

  /**
   * Calculates the discounted unit price based on the quantity.
   * @param unitPrice the pre-selected uni price
   * @param quantity the quantity amount of the product
   * @return the discounted final price
   */
  public static BigDecimal applyQuantityDiscount(BigDecimal unitPrice, Integer quantity) {
    QuantityDiscount quantityDiscount = new QuantityDiscount();
    BigDecimal discountTotal = quantityDiscount.apply(quantity, unitPrice);
    // Round up to the second digit
    return discountTotal.setScale(2, RoundingMode.UP);
  }

  /**
   * Calculates the overhead sum for data analysis items based on the given {@link AffiliationCategory}.
   *
   * Remember, that for internal customers, we apply 0%, for external academic 20%, and
   * for external non-academic 40% overhead.
   *
   * @param items a collection of {@link OfferItem}
   * @param affiliationCategory the {@link AffiliationCategory} the overhead needs to be applied
   * @return the overhead sum that applies, based on the category and item collection
   */
  public BigDecimal overheadsDataAnalysis (List<OfferItem> items, AffiliationCategory affiliationCategory) {
    if (affiliationCategory == AffiliationCategory.INTERNAL) {
      return BigDecimal.ZERO;
    }
    if (affiliationCategory == AffiliationCategory.EXTERNAL_ACADEMIC) {
      return overheadsDataAnalysis(items, OVERHEAD_RATIO_EXTERNAL_ACADEMIC);
    }
    return overheadsDataAnalysis(items, OVERHEAD_RATIO_EXTERNAL);
  }

  /**
   * Calculates the overhead sum for a given collection of offer items and a pre-defined
   * overhead ratio.
   * <strong>Note</strong>: this method will filter the item list by category of type {@link OfferCalculus#DATA_ANALYSIS}.
   * @param items a collection of offer items the overheads are calculated
   * @param overheadRatio the overhead ratio (between 0 >= x <= 1)
   * @return the overhead sum that applies, based on the overhead ratio and item collection
   */
  public BigDecimal overheadsDataAnalysis(List<OfferItem> items, BigDecimal overheadRatio) {
    List<OfferItem> itemsDataAnalysis = items.stream().filter(offerItem -> DATA_ANALYSIS.contains(offerItem.getCategory())).collect(
        Collectors.toList());
    return overheads(itemsDataAnalysis, overheadRatio);
  }

  /**
   * Calculates the overhead sum for a given collection of offer items and a pre-defined overhead
   * ratio.
   * <strong>Note</strong>: this method will NOT apply any product category filter.
   * @param items a collection of offer items
   * @param overheadRatio the overhead ratio to apply
   * @return the overhead sum based on the overhead ratio and items
   */
  public BigDecimal overheads(List<OfferItem> items, BigDecimal overheadRatio) {
    return items.stream()
        .map( OfferItem::getItemTotal )
        .filter( (x) -> x > 0  )
        .map(BigDecimal::valueOf)
        .map( itemPrice -> itemPrice.multiply(overheadRatio) )
        .reduce(BigDecimal.ZERO, BigDecimal::add);
  }

  /**
   * Calculates the overhead sum for a given collection of offer items and a pre-selected affiliation category.
   *
   * Since this method uses the {@link OfferCalculus#overheadsProjectManagementAndStorage(List, BigDecimal)} method,
   * a filter for the product category matching {@link OfferCalculus#PROJECT_AND_DATA_MANAGEMENT} is applied.
   *
   * Remember that for internal affiliations, we charge 0% overhead, 20% for external academic and 40% for external.
   *
   * @param items a collection of offer items
   * @param affiliationCategory a pre-selected affiliation category
   * @return the overhead sum based on the items and affiliation category
   */
  public BigDecimal overheadsProjectManagementAndStorage(List<OfferItem> items, AffiliationCategory affiliationCategory) {
    if (affiliationCategory == AffiliationCategory.INTERNAL) {
      return BigDecimal.ZERO;
    }
    if (affiliationCategory == AffiliationCategory.EXTERNAL_ACADEMIC) {
      return overheadsProjectManagementAndStorage(items, OVERHEAD_RATIO_EXTERNAL_ACADEMIC);
    }
    return overheadsProjectManagementAndStorage(items, OVERHEAD_RATIO_EXTERNAL);
  }

  /**
   * Calculates the overhead sum for a given collection of offer items and a pre-defined
   * overhead ratio.
   * <strong>Note</strong>: this method will filter the item list by category of type {@link OfferCalculus#PROJECT_AND_DATA_MANAGEMENT}.
   * @param items a collection of offer items the overheads are calculated
   * @param overheadRatio the overhead ratio (between 0 >= x <= 1)
   * @return the overhead sum that applies, based on the overhead ratio and item collection
   */
  public BigDecimal overheadsProjectManagementAndStorage(List<OfferItem> items, BigDecimal overheadRatio) {
    List<OfferItem> itemsDataAnalysis = items.stream().filter(offerItem -> PROJECT_AND_DATA_MANAGEMENT.contains(offerItem.getCategory())).collect(
        Collectors.toList());
    return overheads(itemsDataAnalysis, overheadRatio);
  }

  /**
   * Calculates the overhead sum for a given collection of offer items and a pre-selected affiliation category.
   *
   * Since this method uses the {@link OfferCalculus#overheadsDataGeneration(List, BigDecimal)} method,
   * a filter for the product category matching {@link OfferCalculus#DATA_GENERATION} is applied.
   *
   * Remember that for internal affiliations, we charge 0% overhead, 20% for external academic and 40% for external.
   *
   * @param items a collection of offer items
   * @param affiliationCategory a pre-selected affiliation category
   * @return the overhead sum based on the items and affiliation category
   */
  public BigDecimal overheadsDataGeneration(List<OfferItem> items, AffiliationCategory affiliationCategory) {
    if (affiliationCategory == AffiliationCategory.INTERNAL) {
      return BigDecimal.ZERO;
    }
    if (affiliationCategory == AffiliationCategory.EXTERNAL_ACADEMIC) {
      return overheadsDataGeneration(items, OVERHEAD_RATIO_EXTERNAL_ACADEMIC);
    }
    return overheadsDataGeneration(items, OVERHEAD_RATIO_EXTERNAL);
  }

  /**
   * Calculates the overhead sum for a given collection of offer items and a pre-defined
   * overhead ratio.
   * <strong>Note</strong>: this method will filter the item list by category of type {@link OfferCalculus#DATA_GENERATION}.
   * @param items a collection of offer items the overheads are calculated
   * @param overheadRatio the overhead ratio (between 0 >= x <= 1)
   * @return the overhead sum that applies, based on the overhead ratio and item collection
   */
  public BigDecimal overheadsDataGeneration(List<OfferItem> items, BigDecimal overheadRatio) {
    List<OfferItem> itemsDataAnalysis = items.stream().filter(offerItem -> DATA_GENERATION.contains(offerItem.getCategory())).collect(
        Collectors.toList());
    return overheads(itemsDataAnalysis, overheadRatio);
  }

  /**
   * Calculates the overhead sum for a given collection of offer items and a pre-selected affiliation category.
   *
   * Since this method uses the {@link OfferCalculus#overheadsExternalServices(List, BigDecimal)} method,
   * a filter for the product category matching {@link OfferCalculus#EXTERNAL_SERVICES} is applied.
   *
   * Remember that for internal affiliations, we charge 0% overhead, 20% for external academic and 40% for external.
   *
   * @param items a collection of offer items
   * @param affiliationCategory a pre-selected affiliation category
   * @return the overhead sum based on the items and affiliation category
   */
  public BigDecimal overheadsExternalServices(List<OfferItem> items, AffiliationCategory affiliationCategory) {
    if (affiliationCategory == AffiliationCategory.INTERNAL) {
      return BigDecimal.ZERO;
    }
    if (affiliationCategory == AffiliationCategory.EXTERNAL_ACADEMIC) {
      return overheadsExternalServices(items, OVERHEAD_RATIO_EXTERNAL_ACADEMIC);
    }
    return overheadsExternalServices(items, OVERHEAD_RATIO_EXTERNAL);
  }

  /**
   * Calculates the overhead sum for a given collection of offer items and a pre-defined
   * overhead ratio.
   * <strong>Note</strong>: this method will filter the item list by category of type {@link OfferCalculus#EXTERNAL_SERVICES}.
   * @param items a collection of offer items the overheads are calculated
   * @param overheadRatio the overhead ratio (between 0 >= x <= 1)
   * @return the overhead sum that applies, based on the overhead ratio and item collection
   */
  public BigDecimal overheadsExternalServices(List<OfferItem> items, BigDecimal overheadRatio) {
    List<OfferItem> itemsDataAnalysis = items.stream().filter(offerItem -> EXTERNAL_SERVICES.contains(offerItem.getCategory())).collect(
        Collectors.toList());
    return overheads(itemsDataAnalysis, overheadRatio);
  }

  public OfferV2 calculateOverheads(OfferV2 offerV2) {
    OfferV2 offerCopy = OfferV2.copy(offerV2);
    AffiliationCategory affiliationCategory = offerV2.getSelectedCustomerAffiliation().getCategory();
    BigDecimal overheadDataAnalysis = overheadsDataAnalysis(offerV2.getDataAnalysisItems(), affiliationCategory);
    BigDecimal overheadDataGeneration = overheadsDataGeneration(offerV2.getDataGenerationItems(), affiliationCategory);
    BigDecimal overheadProjectManagementDataStorage = overheadsProjectManagementAndStorage(offerV2.getDataManagementItems(), affiliationCategory);
    BigDecimal overheadExternalServices = overheadsExternalServices(offerV2.getExternalServiceItems(), affiliationCategory);
    BigDecimal totalOverheads = overheadDataAnalysis.add(overheadDataGeneration)
        .add(overheadProjectManagementDataStorage).add(overheadExternalServices);

    // Set the overhead properties
    offerCopy.setOverheadsDataAnalysis(overheadDataAnalysis);
    offerCopy.setOverheadsDataGeneration(overheadDataGeneration);
    offerCopy.setOverheadsDataManagement(overheadProjectManagementDataStorage);
    offerCopy.setOverheadsExternalServices(overheadExternalServices);
    offerCopy.setOverhead(totalOverheads.doubleValue());
    // Lastly the overhead ratio that has been applied
    offerCopy.setOverheadRatio(overheadRatio(affiliationCategory).doubleValue());

    return offerCopy;
  }

  protected BigDecimal overheadRatio(AffiliationCategory category) {
    if (category == AffiliationCategory.INTERNAL) {
      return BigDecimal.ZERO;
    }
    if (category == AffiliationCategory.EXTERNAL_ACADEMIC) {
      return OVERHEAD_RATIO_EXTERNAL_ACADEMIC;
    }
    return OVERHEAD_RATIO_EXTERNAL;
  }

  /**
   * <p>Calculates the sum of the net price of every item.</p>
   *
   * <b>Note</b>: Potential discount will be considered in the calculation and subtracted from the total item price.
   * @param items the items to calculate the net sum for
   * @return the net sum including discounts.
   */
  public static BigDecimal netSum(List<OfferItem> items) {
    return items.stream()
        .map(OfferCalculus::netPrice)
        .reduce(BigDecimal.ZERO, BigDecimal::add);
  }

  /**
   * <p>Calculates the net price of a given item.</p>
   *
   * <b>Note</b>: Potential discount will be considered in the calculation and subtracted from the total item price.
   * @param item the item to calculate the net sum for
   * @return the net sum of the provided item
   */
  public static BigDecimal netPrice(OfferItem item) {
    return BigDecimal.valueOf(item.getItemTotal())
        .subtract(BigDecimal.valueOf(item.getQuantityDiscount()));
  }

  /**
   * <p>Calculates the net prices for all service categories</p>
   * @param offer the offer with the items to calculate the net sums
   * @return a copy of the input offer with the final net prices
   */
  public static OfferV2 calcNetPrices(OfferV2 offer) {
    OfferV2 offerCopy = OfferV2.copy(offer);

    BigDecimal dataAnalysisNetTotal = netSum(offerCopy.getDataAnalysisItems());
    BigDecimal dataGenerationNetTotal = netSum(offerCopy.getDataGenerationItems());
    BigDecimal projectAndDataManagementNetTotal = netSum(offerCopy.getDataManagementItems());
    BigDecimal externalServicesNetTotal = netSum(offerCopy.getDataAnalysisItems());

    BigDecimal totalNet = dataAnalysisNetTotal
        .add(dataGenerationNetTotal)
        .add(projectAndDataManagementNetTotal)
        .add(externalServicesNetTotal);

    offerCopy.setNetSumDataAnalysis(dataAnalysisNetTotal);
    offerCopy.setNetSumDataGeneration(dataGenerationNetTotal);
    offerCopy.setNetSumDataManagement(projectAndDataManagementNetTotal);
    offerCopy.setNetSumExternalServices(externalServicesNetTotal);

    offerCopy.setTotalNetPrice(totalNet);

    return offerCopy;
  }



  static class OfferCalculusException extends RuntimeException {
    OfferCalculusException() {
      super();
    }

    OfferCalculusException(String message) {
      super(message);
    }

    OfferCalculusException(String message, Throwable t) {
      super(message, t);
    }
  }
}
