package life.qbic.business.offers;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import life.qbic.business.persons.affiliation.AffiliationCategory;
import life.qbic.datamodel.dtos.business.ProductItem;
import life.qbic.datamodel.dtos.business.services.Product;

/**
 * <b><class short description - 1 Line!></b>
 *
 * <p><More detailed description - When to use, what it solves, etc.></p>
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
     * @param offer
     * @return
     */
    OfferV2 groupItems(OfferV2 offer) {
        OfferV2 offerCopy = OfferV2.copy(offer);
        List<ProductItem> productItems = offerCopy.getItems();
        // Create OfferItems from ProductItems first

        // And then sort them into the correct categories

        return null;
    }

    private static OfferItem create(ProductItem productItem, AffiliationCategory affiliationCategory) {
        Product product = productItem.getProduct();
        double unitPrice = (affiliationCategory == AffiliationCategory.INTERNAL)
                ? product.getInternalUnitPrice()
                : product.getExternalUnitPrice();
        OfferItem offerItem = new OfferItem.Builder(productItem.getQuantity(),
            product.getDescription(), product.getProductName(), unitPrice,
            productItem.getQuantityDiscount(),
            calculateDiscountPerUnit(productItem),
            calculateDiscountPercentage(productItem),
            product.getServiceProvider().getLabel(),
            product.getUnit().getValue(),
            productItem.getTotalPrice())
            .build();

    }

    /**
     * Calculates the discount percentage for a product item. Note that this is not a ratio, but a number between 0 and 100.
     * @param productItem item for which the discount percentage should be calculated
     * @return the discount percentage based on quantity discount and item total cost
     */
    private static double calculateDiscountPercentage(ProductItem productItem) {
        BigDecimal totalPrice = BigDecimal.valueOf(productItem.getTotalPrice());
        if (totalPrice.compareTo(BigDecimal.ZERO) == 0) {
            //avoid division by 0
            //if a product has a total price of 0 set discount percentage to 0
            return BigDecimal.ZERO.doubleValue();
        }
        return (BigDecimal.valueOf(100.0).multiply(BigDecimal.valueOf(productItem.getQuantityDiscount()))).divide(totalPrice).doubleValue();
    }


    /**
     * Calculates the discount per unit for a product item
     * @param productItem item for which the discount per unit should be calculated
     * @return the discount per unit, if applicable, 0 otherwise
     */
    private static double calculateDiscountPerUnit(ProductItem productItem) {
        BigDecimal quantity = BigDecimal.valueOf(productItem.getQuantity());
        if (quantity.compareTo(BigDecimal.ZERO) == 0) {
            //avoid division by 0
            //if a a productItem has no quantity set discount to 0
            return BigDecimal.ZERO.doubleValue();
        }
        return BigDecimal.valueOf(productItem.getQuantityDiscount()).divide(quantity).doubleValue();
    }










}
