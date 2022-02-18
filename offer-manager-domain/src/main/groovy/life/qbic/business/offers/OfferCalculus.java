package life.qbic.business.offers;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import life.qbic.business.persons.affiliation.Affiliation;
import life.qbic.business.persons.affiliation.AffiliationCategory;
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

  private BigDecimal overheadsDataAnalysis;
  private BigDecimal overheadsDataGeneration;
  private BigDecimal overheadsDataManagement;
  private BigDecimal overheadsExternalServices;
  private BigDecimal netSumDataAnalysis;
  private BigDecimal netSumDataManagement;
  private BigDecimal netSumDataGeneration;
  private BigDecimal netSumExternalServices;
  private BigDecimal totalNetPrice;
  private BigDecimal vatRatio;
  private BigDecimal totalVat;
  private BigDecimal totalCost;
  private BigDecimal totalDiscountAmount;

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
  OfferV2 groupItems(OfferV2 offer) {
    OfferV2 offerCopy = OfferV2.copy(offer);
    // Create OfferItems from ProductItems first
    List<OfferItem> offerItems = convertProductItems(offerCopy.getItems(), offerCopy.getSelectedCustomerAffiliation());
    // And then sort them into the correct categories
    for (OfferItem item : offerItems) {
      if (item.)
    }
    return null;
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

  public static OfferItem createWithInternalPrice(ProductItem item) { return null;}

  public static OfferItem createWithExternalPrice(ProductItem item) { return null;}

  private static OfferItem create(Double quantity, String description, String productName, Double unitPrice,
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
}
