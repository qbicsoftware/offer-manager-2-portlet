package life.qbic.business.offers;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import life.qbic.business.persons.affiliation.AffiliationCategory;
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

  private static final BigDecimal VAT_RATIO_GERMANY = new BigDecimal("0.19");

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
    // Discounting for an internal customer is different, so we check the affiliation first.
    if (affiliationCategory == AffiliationCategory.INTERNAL) {
      offerItems = createOfferItems(offer.getItems(), OfferCalculus::offerItemFromInternal);
    } else {
      offerItems = createOfferItems(offer.getItems(), OfferCalculus::offerItemFromExternal);
    }
    return offerItems;
  }

  /**
   * Process the provided offer and fills it with calculated information. The only requirement for
   * this is that the offer contains product items.
   *
   * @param offer an offer containing product items
   * @return an offer with all computable fields set
   */
  public static OfferV2 process(OfferV2 offer) {
    // we always need to group the items to calculate anything
    OfferV2 preparedOffer = withGroupedOfferItems(OfferV2.copyOf(offer));

    /* order of calculation
     *calc net costs----calc VAT----\
     *calc overhead-------------calc total costs
     */
    OfferV2 offerWithPrices = withVat(withNetPrices(preparedOffer));
    OfferV2 offerWithPricesAndOverheads = withOverheads(offerWithPrices);
    OfferV2 processedOffer = withTotalCosts(offerWithPricesAndOverheads);
    return processedOffer;
  }

  /**
   * Calculates and fills the vat for an offer. Requires filled net prices {@link
   * #withNetPrices(OfferV2)}
   *
   * @param offer an offer with filled net prices
   * @return an offer with vat prices calculated and filled
   */
  protected static OfferV2 withVat(OfferV2 offer) {
    OfferV2 offerCopy = OfferV2.copyOf(offer);
    BigDecimal vatRatio = vatRatio(offer.getSelectedCustomerAffiliation().getCountry());
    offerCopy.setVatRatio(vatRatio);
    offerCopy.setTotalVat(calcVat(offerCopy.getTotalNetPrice(), vatRatio));
    return offerCopy;
  }

  /**
   * Calculates the VAT amount for a given net price. The rate is calculated by {@link
   * #vatRatio(String)}
   *
   * @param totalNetPrice the VAT is calculated for this price
   * @param country the country the vat shall be calculated for
   * @return the VAT amount the provided country
   * @see #calcVat(BigDecimal, BigDecimal)
   */
  public static BigDecimal calcVat(BigDecimal totalNetPrice, String country) {
    BigDecimal vatRatio = vatRatio(country);
    return calcVat(totalNetPrice, vatRatio);
  }

  /**
   * Determines the VAT ratio. The VAT ratio for Germany is {@link #VAT_RATIO_GERMANY}. For all
   * other countries, the VAT ratio is 0.
   *
   * @param country the country for which to get the VAT ratio
   * @return the VAT ratio for the provided country
   */
  protected static BigDecimal vatRatio(String country) {
    if (country.equalsIgnoreCase("Germany")) {
      return VAT_RATIO_GERMANY;
    }
    return BigDecimal.ZERO;
  }

  /**
   * Calculates the vat provided a total net price and a vat ratio
   *
   * @param totalNetPrice the total net price for which to calculate the VAT
   * @param vatRatio the VAT ratio applied
   * @return the VAT amount for the provided vat Ratio
   * @see #calcVat(BigDecimal, String)
   */
  protected static BigDecimal calcVat(BigDecimal totalNetPrice, BigDecimal vatRatio) {
    return roundToCurrency(totalNetPrice.multiply(vatRatio));
  }

  /**
   * Applies rounding to the provided value to 2 decimal points
   *
   * @param value the value to round
   * @return a value rounded to 2 decimal points
   */
  protected static BigDecimal roundToCurrency(BigDecimal value) {
    return value.setScale(2, RoundingMode.HALF_UP);
  }

  /**
   * Calculates and fills the offer with the total cost. Requires the offer to have overheads, net
   * prices and VAT filled
   *
   * @param offer an offer with filled overheads, net prices and VAT
   * @return an offer with a total price
   */
  protected static OfferV2 withTotalCosts(OfferV2 offer) {
    OfferV2 offerCopy = OfferV2.copyOf(offer);
    BigDecimal offerOverhead = BigDecimal.valueOf(offerCopy.getOverhead());
    BigDecimal offerNet = offerCopy.getTotalNetPrice();
    BigDecimal offerVat = offerCopy.getTotalVat();
    offerCopy.setTotalCost(addTotalCosts(offerOverhead, offerNet, offerVat));
    return offerCopy;
  }

  /**
   * adds the total costs up
   *
   * @param offerOverheads the total overhead for an offer
   * @param offerNet the total net for an offer
   * @param offerVat the total vat for an offer
   * @return the total costs of for an offer
   */
  protected static BigDecimal addTotalCosts(
      BigDecimal offerOverheads, BigDecimal offerNet, BigDecimal offerVat) {
    return offerNet.add(offerOverheads).add(offerVat);
  }

  /**
   * Calculates the net prices for all service categories, and the overall offer net costs
   *
   * @param offer the offer with the items to calculate the net sums
   * @return a copy of the input offer with the final net prices
   */
  public static OfferV2 withNetPrices(OfferV2 offer) {
    OfferV2 offerCopy = OfferV2.copyOf(offer);

    BigDecimal dataAnalysisNetTotal = netSum(offerCopy.getDataAnalysisItems());
    BigDecimal dataGenerationNetTotal = netSum(offerCopy.getDataGenerationItems());
    BigDecimal projectAndDataManagementNetTotal = netSum(offerCopy.getDataManagementItems());
    BigDecimal externalServicesNetTotal = netSum(offerCopy.getExternalServiceItems());

    BigDecimal totalNet =
        dataAnalysisNetTotal
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

  /**
   * Converts all product items of an offer into offer items and passes them to {@link
   * #withGroupedOfferItems(OfferV2, List)}
   *
   * @param offerV2 an offer with product items
   * @return an offer with filled offer items
   * @throws OfferCalculusException the product category could not be determined for one or more
   *     product items in the provided offer
   */
  public static OfferV2 withGroupedOfferItems(OfferV2 offerV2) throws OfferCalculusException {
    return withGroupedOfferItems(offerV2, createOfferItems(offerV2));
  }

  /**
   * Calculates and fills the overhead information of an offer. The overheads are calculated for
   * each product category and added up after rounding. Determines the overhead ratio based on the
   * offer's affiliation {@link #overheadRatio(AffiliationCategory)}.
   *
   * @param offer an offer with a selected customer affiliation
   * @return an offer with filled overhead information
   */
  public static OfferV2 withOverheads(OfferV2 offer) {
    AffiliationCategory affiliationCategory = offer.getSelectedCustomerAffiliation().getCategory();
    BigDecimal overheadRatio = overheadRatio(affiliationCategory);

    OfferV2 offerCopy = OfferV2.copyOf(offer);

    BigDecimal overheadDataAnalysis =
        roundToCurrency(overheads(offer.getDataAnalysisItems(), overheadRatio));
    BigDecimal overheadDataGeneration =
        roundToCurrency(overheads(offer.getDataGenerationItems(), overheadRatio));
    BigDecimal overheadProjectManagementDataStorage =
        roundToCurrency(overheads(offer.getDataManagementItems(), overheadRatio));
    BigDecimal overheadExternalServices =
        roundToCurrency(overheads(offer.getExternalServiceItems(), overheadRatio));

    // ATTENTION: due to the rounding of individual prices for the groups to currencies,
    // we need to add the rounded numbers.
    // This may give a different result from offerNet * overheadRatio !
    BigDecimal totalOverheads =
        roundToCurrency(
            overheadDataAnalysis
                .add(overheadDataGeneration)
                .add(overheadProjectManagementDataStorage)
                .add(overheadExternalServices));

    offerCopy.setOverheadRatio(overheadRatio.doubleValue());
    offerCopy.setOverheadsDataAnalysis(overheadDataAnalysis);
    offerCopy.setOverheadsDataGeneration(overheadDataGeneration);
    offerCopy.setOverheadsDataManagement(overheadProjectManagementDataStorage);
    offerCopy.setOverheadsExternalServices(overheadExternalServices);
    offerCopy.setOverhead(totalOverheads.doubleValue());

    return offerCopy;
  }

  /**
   * Calculates the net costs of a group of offer items.
   *
   * @param items the items to calculate the net sum for
   * @return the net sum including discounts.
   */
  public static BigDecimal netSum(List<OfferItem> items) {
    return items.stream()
        .map(OfferItem::getItemNet)
        .map(BigDecimal::valueOf)
        .reduce(BigDecimal.ZERO, BigDecimal::add);
  }

  /**
   * Determines the overhead ratio applied to a given {@link AffiliationCategory}
   *
   * <ul>
   *   <li>For <b>internal</b> affiliations the overhead ratio is 0%.
   *   <li>For <b>external academic</b> affiliations the overhead ratio is {@link
   *       #OVERHEAD_RATIO_EXTERNAL_ACADEMIC}.
   *   <li>For <b>external</b> affiliations the overhead ratio is {@link #OVERHEAD_RATIO_EXTERNAL}.
   * </ul>
   *
   * @param category the affiliation category determining the overhead raio
   * @return the applicable overhead ratio
   */
  protected static BigDecimal overheadRatio(AffiliationCategory category) {
    if (category == AffiliationCategory.INTERNAL) {
      return BigDecimal.ZERO;
    }
    if (category == AffiliationCategory.EXTERNAL_ACADEMIC) {
      return OVERHEAD_RATIO_EXTERNAL_ACADEMIC;
    }
    return OVERHEAD_RATIO_EXTERNAL;
  }

  /**
   * Calculates the overhead sum for a given collection of offer items and a pre-defined overhead
   * ratio. <strong>Note</strong>: this method will NOT apply any product category filter.
   *
   * @param items a collection of offer items
   * @param overheadRatio the overhead ratio to apply
   * @return the overhead sum based on the overhead ratio and items
   */
  public static BigDecimal overheads(List<OfferItem> items, BigDecimal overheadRatio) {
    if (overheadRatio.compareTo(BigDecimal.ZERO) < 0
        || overheadRatio.compareTo(BigDecimal.ONE) > 0) {
      throw new IllegalArgumentException(
          "Overhead ratio must be between 0 and 1. Provided ratio: " + overheadRatio);
    }
    return items.stream()
        .map(OfferItem::getItemNet)
        .filter((x) -> x > 0)
        .map(BigDecimal::valueOf)
        .map(itemPrice -> itemPrice.multiply(overheadRatio))
        .reduce(BigDecimal.ZERO, BigDecimal::add);
  }

  /**
   * Groups offer items based on their assigned category (data generation, data analysis, project
   * and data management or external services) and fills the offer. The returned offer object is a
   * full copy of the input offer and will contain the right grouping of the given offer items.
   *
   * <p>If the offer items list passed is empty, this method tries to create the offer items first
   * based on the ProductItems in the offer. Internally, the method {@link
   * OfferCalculus#createOfferItems(OfferV2)} is used for this.
   *
   * @param offer the offer to use as basis for the calculations
   * @param offerItems the offer items to use to group the items into their belonging category
   * @return A copy of the input offer, with the offer items sorted in the categories.
   * @throws OfferCalculusException if anything goes wrong
   */
  protected static OfferV2 withGroupedOfferItems(OfferV2 offer, List<OfferItem> offerItems)
      throws OfferCalculusException {
    if (offerItems.isEmpty()) {
      offerItems = createOfferItems(offer);
    }
    OfferV2 offerCopy = OfferV2.copyOf(offer);
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

  /**
   * Creates an {@link OfferItem} object with all its properties calculated based on the given
   * {@link ProductItem}. This method uses the <b>external unit price</b>.
   *
   * @param item the item from which to create an OfferItem instance.
   * @return the offer item based on the given product item
   */
  protected static OfferItem offerItemFromExternal(ProductItem item) {
    Double unitPrice = item.getProduct().getExternalUnitPrice();
    AffiliationCategory affiliationCategory = AffiliationCategory.EXTERNAL;

    return createOfferItem(
        item.getQuantity(),
        unitPrice,
        item.getProduct().getCategory(),
        affiliationCategory,
        item.getProduct().getDescription(),
        item.getProduct().getProductName(),
        item.getProduct().getServiceProvider(),
        item.getProduct().getUnit());
  }

  /**
   * Creates an {@link OfferItem} object with all its properties calculated based on the given
   * {@link ProductItem}. This method uses the <b>internal unit price</b>.
   *
   * @param item the item from which to create an OfferItem instance.
   * @return the offer item based on the given product item
   */
  protected static OfferItem offerItemFromInternal(ProductItem item) {
    Double unitPrice = item.getProduct().getInternalUnitPrice();
    AffiliationCategory affiliationCategory = AffiliationCategory.INTERNAL;

    return createOfferItem(
        item.getQuantity(),
        unitPrice,
        item.getProduct().getCategory(),
        affiliationCategory,
        item.getProduct().getDescription(),
        item.getProduct().getProductName(),
        item.getProduct().getServiceProvider(),
        item.getProduct().getUnit());
  }

  /**
   * Creates an offer item based on a set of a minimal set of parameters that are used for
   * calculation or are written as is in case no calculation is necessary
   *
   * @param quantity the quantity for the new offer item
   * @param unitPrice the price per unit of this offer item
   * @param productCategory the product category this offer item belongs to
   * @param affiliationCategory the affiliation category used for calculation
   * @param description the product description
   * @param productName the product name
   * @param serviceProvider the provider of the offered product
   * @param unit the unit in which the quantity is counted
   * @return a complete offer item with calculated netPrice, listPrice and discounts
   */
  protected static OfferItem createOfferItem(
      double quantity,
      double unitPrice,
      String productCategory,
      AffiliationCategory affiliationCategory,
      String description,
      String productName,
      String serviceProvider,
      String unit) {
    BigDecimal roundedQuantity = roundToCurrency(BigDecimal.valueOf(quantity));
    BigDecimal roundedUnitPrice = roundToCurrency(BigDecimal.valueOf(unitPrice));
    BigDecimal listPrice = roundToCurrency(roundedQuantity.multiply(roundedUnitPrice));

    BigDecimal unitDiscount =
        roundToCurrency(
            calculateUnitDiscount(
                productCategory, affiliationCategory, roundedUnitPrice, roundedQuantity));

    BigDecimal netPrice =
        roundToCurrency(itemNetPrice(roundedUnitPrice, roundedQuantity, unitDiscount));

    // we have percentage thus a precision of 0.1234 is required -> 12.34 %
    BigDecimal discountPercentage = unitDiscount.divide(roundedUnitPrice, 4, RoundingMode.HALF_UP);

    BigDecimal totalDiscount = roundToCurrency(roundedQuantity.multiply(unitDiscount));

    return new OfferItem.Builder(
            roundedQuantity.doubleValue(),
            description,
            productName,
            roundedUnitPrice.doubleValue(),
            totalDiscount.doubleValue(),
            unitDiscount.doubleValue(),
            discountPercentage.doubleValue(),
            serviceProvider,
            unit,
            listPrice.doubleValue(),
            netPrice.doubleValue())
        .setCategory(productCategory)
        .build();
  }

  /**
   * Calculates the quantity discount, and the data storage discount. The applied discount per unit
   * is the maximum discount applicable.
   *
   * @param productCategory the category of the product for which the unit discount is calculated
   * @param affiliationCategory the considered affiliation category
   * @param unitPrice the price per unit that shall be discounted by the result of this method
   * @param quantity the considered quantity
   * @return the amount of discount per unit
   */
  protected static BigDecimal calculateUnitDiscount(
      String productCategory,
      AffiliationCategory affiliationCategory,
      BigDecimal unitPrice,
      BigDecimal quantity) {
    BigDecimal dataStorageDiscount =
        calculateDataStorageDiscount(affiliationCategory, productCategory, unitPrice);
    BigDecimal quantityDiscount = calculateQuantityDiscount(unitPrice, quantity, productCategory);

    return dataStorageDiscount.max(quantityDiscount);
  }

  /**
   * Calculate the storage discount for a given unit price. For internal affiliations, this is set
   * to 100%, for other affiliations this is set to 0%. Data storage discount is only applied for
   * data storage products.
   *
   * @param affiliationCategory the considered affiliation category for discount determination
   * @param productCategory the product category to be discounted
   * @param unitPrice the unit price to be discounted
   * @return the amount of discount per unit for the provided unit price
   */
  protected static BigDecimal calculateDataStorageDiscount(
      AffiliationCategory affiliationCategory, String productCategory, BigDecimal unitPrice) {
    if (productCategory.equalsIgnoreCase("data storage")
        && affiliationCategory == AffiliationCategory.INTERNAL) {
      return unitPrice;
    }
    return BigDecimal.ZERO;
  }

  /**
   * Calculates the discounted unit price based on the quantity.
   *
   * @param unitPrice the pre-selected uni price
   * @param quantity the quantity amount of the product
   * @return the discounted final price
   */
  public static BigDecimal calculateQuantityDiscount(
      BigDecimal unitPrice, BigDecimal quantity, String productCategory) {
    QuantityDiscount quantityDiscount = new QuantityDiscount();
    BigDecimal discountTotal = quantityDiscount.apply(quantity, unitPrice);
    // Round up to the second digit
    return DATA_ANALYSIS.contains(productCategory)
        ? discountTotal.setScale(2, RoundingMode.UP)
        : BigDecimal.ZERO;
  }

  /**
   * Create offer items using the provided converter
   *
   * @param productItems the product items to be converted into offer items
   * @param converter the mapping function from product item to offer item
   * @return a list of offer items representing converted product items
   */
  protected static List<OfferItem> createOfferItems(
      List<ProductItem> productItems, Function<ProductItem, OfferItem> converter) {
    return productItems.stream().map(converter).collect(Collectors.toList());
  }

  /**
   * Calculates the net price of a given item. <b>Note</b>: Potential discount will be considered in
   * the calculation and subtracted from the total item price.
   *
   * @param item the item to calculate the net sum for
   * @return the net sum of the provided item
   */
  public static BigDecimal itemNetPrice(OfferItem item) {
    BigDecimal unitPrice = BigDecimal.valueOf(item.getUnitPrice());
    BigDecimal unitDiscount = BigDecimal.valueOf(item.getUnitDiscount());
    BigDecimal quantity = BigDecimal.valueOf(item.getQuantity());
    return itemNetPrice(unitPrice, quantity, unitDiscount);
  }

  /**
   * Calculates the net price given the price and discount per unit and the quantity. The net price
   * is calculated as (unitPrice - unitDiscount) * quantity
   *
   * @param unitDiscount the discount per unit
   * @param quantity the amount of units to consider
   * @param unitPrice the price per unit
   * @return the net sum of the provided item
   * @see #itemNetPrice(OfferItem)
   */
  protected static BigDecimal itemNetPrice(
      BigDecimal unitPrice, BigDecimal quantity, BigDecimal unitDiscount) {
    return unitPrice.subtract(unitDiscount).multiply(quantity).setScale(2, RoundingMode.HALF_UP);
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
