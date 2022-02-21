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
 * <b><class short description - 1 Line!></b>
 *
 * <p><More detailed description - When to use, what it solves, etc.>
 *
 * @since <version tag>
 */
class OfferCalculus {

  private List<OfferItem> dataAnalysisItems = new ArrayList<>();
  private List<OfferItem> dataGenerationItems = new ArrayList<>();
  private List<OfferItem> dataManagementItems = new ArrayList<>();
  private List<OfferItem> externalServiceItems = new ArrayList<>();

  /*Overheads*/
  Double overheadTotal;
  Double overheadsDataGeneration;
  Double overheadsDataAnalysis;
  Double overheadsProjectManagementAndDataStorage;
  Double overheadsExternalServices;
  Double overheadRatio;

  /*Prices*/
  Double netDataGeneration;
  Double netDataAnalysis;
  Double netPMandDS;
  Double netExternalServices;
  Double totalCost;
  Double netCostsWithOverheads;
  Double netCost;
  Double totalVat;
  Double vatRatio;
  Double totalDiscountAmount;

  private static final List<String> DATA_GENERATION = Arrays.asList("Sequencing", "Proteomics", "Metabolomics");
  private static final List<String> DATA_ANALYSIS = Arrays.asList("Primary Bioinformatics", "Secondary Bioinformatics");
  private static final List<String> PROJECT_AND_DATA_MANAGEMENT = Arrays.asList("Project Management", "Data Storage");
  private static final List<String> EXTERNAL_SERVICES = Collections.singletonList("External Service");

   /**
   * Determines the overhead ratio for this offer based on the customer affiliation.
   * @param offer the offer for which the overhead ratio should be determined
   * @return a copy of the provided offer with the overhead ratio filled.
   */
  OfferV2 overheadRatio(OfferV2 offer) {
      OfferV2 result = OfferV2.copy(offer);
      BigDecimal overheadRatio;
      switch (offer.getSelectedCustomerAffiliation().getCategory()) {
          case INTERNAL:
              overheadRatio = BigDecimal.ZERO;
              break;
          case EXTERNAL_ACADEMIC:
              overheadRatio = BigDecimal.valueOf(0.2);
              break;
          default:
              overheadRatio = BigDecimal.valueOf(0.4);
      }
      result.setOverheadRatio(overheadRatio.doubleValue());
      return result;
  }

  /**
   * Groups the offer's product items into its offer items
   *
   * @param offer
   * @return
   */

  List<OfferItem> createOfferItems(OfferV2 offer) {
    AffiliationCategory affiliationCategory = offer.getSelectedCustomerAffiliation().getCategory();
    List<OfferItem> offerItems;
    // Discounting for internal customer is different, so we check the affiliation first
    if (affiliationCategory == AffiliationCategory.INTERNAL) {
      offerItems = convertProductItemsForInternals(offer.getItems());
    } else {
      offerItems = convertProductItemsForExternals(offer.getItems());
    }
    return offerItems;
  }

  OfferV2 groupItems(OfferV2 offer, List<OfferItem> offerItems) throws OfferCalculusException {
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

  public static List<OfferItem> convertProductItemsForExternals(List<ProductItem> productItems) {
    return productItems.stream()
        .map( OfferCalculus::createWithExternalPrice )
        .collect(Collectors.toList());
  }

  public static List<OfferItem> convertProductItemsForInternals(List<ProductItem> productItems) {
    return productItems.stream()
        .map( OfferCalculus::createWithInternalPrice )
        .collect(Collectors.toList());
  }

  /**
   * Creates an {@link OfferItem} object with all its properties calculated based on the
   * given {@link ProductItem}. This method uses the <b>internal unit price</b>.
   * @param item the item that is going to be used to create the OfferItem instance.
   * @return
   */
  public static OfferItem createWithInternalPrice(ProductItem item) {
    Double selectedUnitPrice = item.getProduct().getInternalUnitPrice();
    return createWithUnitPrice(item, selectedUnitPrice);
  }

  /**
   * Creates an {@link OfferItem} object with all its properties calculated based on the
   * given {@link ProductItem}. This method uses the <b>external unit price</b>.
   * @param item the item that is going to be used to create the OfferItem instance.
   * @return
   */
  public static OfferItem createWithExternalPrice(ProductItem item) {
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
    } else if (category.equalsIgnoreCase("Data Storage")) {
      unitPriceAfterDiscount = BigDecimal.valueOf(unitPrice);
      totalDiscount = unitPriceAfterDiscount.multiply(BigDecimal.valueOf(productQuantity));
    } else {
      // Without discount, the unit after discount is equal to the original unit price
      unitPriceAfterDiscount = BigDecimal.valueOf(unitPrice);
      totalDiscount = BigDecimal.ZERO;
    }

    Product product = item.getProduct();
    // Calculate the discount percentage
    Double discountPercentage = calcDiscountPercentage(BigDecimal.valueOf(unitPrice), unitPriceAfterDiscount).doubleValue();
    // Get the final item price, including potential discount
    Double totalPrice = unitPriceAfterDiscount.multiply(BigDecimal.valueOf(productQuantity)).doubleValue();
    return createOfferItem(productQuantity, product.getDescription(), product.getProductName(),
        unitPrice,
        totalDiscount.doubleValue(), unitPriceAfterDiscount.doubleValue(),
        discountPercentage, product.getServiceProvider(),
        product.getUnit(), totalPrice, product.getCategory());
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

  public static BigDecimal applyStorageDiscount(BigDecimal itemTotalPrice, AffiliationCategory affiliationCategory) {
    if(affiliationCategory == AffiliationCategory.INTERNAL) {
      // 100% discount for internal customers for storage
      return itemTotalPrice;
    }
    return BigDecimal.ZERO;
  }

  /**
   * Calculates the discounted unit price based on the quantity.
   * @param unitPrice
   * @param quantity
   * @return
   */
  public static BigDecimal applyQuantityDiscount(BigDecimal unitPrice, Integer quantity) {
    QuantityDiscount quantityDiscount = new QuantityDiscount();
    BigDecimal discountTotal = quantityDiscount.apply(quantity, unitPrice);
    // Round up to the second digit
    return discountTotal.setScale(2, RoundingMode.UP);
  }

  public BigDecimal determineUnitPrice(ProductItem item, AffiliationCategory affiliationCategory) {
    return affiliationCategory == AffiliationCategory.INTERNAL ?
        BigDecimal.valueOf(item.getProduct().getInternalUnitPrice())
        : BigDecimal.valueOf(item.getProduct().getExternalUnitPrice());
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
