package life.qbic.business.products;

import groovy.transform.CompileStatic;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;
import life.qbic.business.offers.OfferV2;
import life.qbic.business.offers.QuantityDiscount;
import life.qbic.business.persons.affiliation.AffiliationCategory;

@Entity
@Table(name = "productitem", uniqueConstraints = {
    @UniqueConstraint(name = "id", columnNames = {"id"})
})
@CompileStatic
public class ProductItem {

  private static final List<String> DATA_GENERATION = Arrays.asList("Sequencing", "Proteomics", "Metabolomics");
  private static final List<String> DATA_ANALYSIS = Arrays.asList("Primary Bioinformatics", "Secondary Bioinformatics");
  private static final List<String> PROJECT_AND_DATA_MANAGEMENT = Arrays.asList("Project Management", "Data Storage");
  private static final List<String> EXTERNAL_SERVICES = Collections.singletonList("External Service");

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id", nullable = false)
  private Integer id;

  @ManyToOne(optional = false)
  @JoinColumn(name = "productId", nullable = false)
  private Product product;

  @Column(name = "quantity", nullable = false)
  private Double quantity;

  @ManyToOne
  @JoinColumn(name = "offerId")
  private OfferV2 offer;

  @Transient
  private boolean discountApplied;

  @Transient
  private BigDecimal unitListPrice;

  @Transient
  private BigDecimal unitDiscountPrice;

  public ProductItem(Product product, Double quantity) {
    this.product = product;
    this.quantity = quantity;
  }

  protected ProductItem() {}

  public void setOffer(OfferV2 offer) {
    this.offer = offer;
    refreshProductItem();
  }

  public Double getQuantity() {
    return quantity;
  }

  public void setQuantity(Double quantity) {
    this.quantity = quantity;
  }

  public Product getProduct() {
    return product;
  }

  public void setProduct(Product product) {
    this.product = product;
    refreshProductItem();
  }

  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  private void refreshProductItem() {
    if (offer == null || product == null) {
      // Nothing to do, when either offer or product is missing
      return;
    }
    AffiliationCategory affiliationCategory = offer.getSelectedCustomerAffiliation().getCategory();
    unitListPrice = determineUnitPrice(affiliationCategory, product);

    BigDecimal quantityDiscount =
        calculateQuantityDiscount(unitListPrice, BigDecimal.valueOf(quantity), product.getCategory());
    BigDecimal storageDiscount =
        calculateDataStorageDiscount(affiliationCategory, product.getCategory(), unitListPrice);
    unitDiscountPrice = quantityDiscount.max(storageDiscount);

    discountApplied = unitListPrice.subtract(unitDiscountPrice).compareTo(unitListPrice) < 0;
  }

  /**
   * <p>The relevant unit price for a product in an offer depends on the customer affiliation.
   * The current business policy is to use provide to base prices for internal and external customers,
   * two cover the cases where VAT of i.e. purchased consumables needs to be reflected in the
   * internal base price.</p>
   * @param affiliationCategory internal, external or external academic
   * @param product the product of interest
   * @return the numeric value of the determined base price
   */
  protected static BigDecimal determineUnitPrice(AffiliationCategory affiliationCategory, Product product) {
    if (affiliationCategory.getLabel().equalsIgnoreCase("internal")) {
      return BigDecimal.valueOf(product.getInternalUnitPrice()).setScale(2,
          RoundingMode.HALF_UP);
    }
    return BigDecimal.valueOf(product.getExternalUnitPrice())
        .setScale(2, RoundingMode.HALF_UP);
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
  protected static BigDecimal calculateQuantityDiscount(
      BigDecimal unitPrice, BigDecimal quantity, String productCategory) {
    QuantityDiscount quantityDiscount = new QuantityDiscount();
    BigDecimal discountTotal = quantityDiscount.apply(quantity, unitPrice);
    // Round up to the second digit
    return DATA_ANALYSIS.contains(productCategory)
        ? discountTotal.setScale(2, RoundingMode.UP)
        : BigDecimal.ZERO;
  }

  /**
   * <p>In some cases it is of interest to the client to quickly check
   * if a given product item has a discount.</p>
   * This method provides the information.
   * @return <code>true</code>, if there is discount applied to the product item, else <code>false</code>
   */
  public boolean hasDiscount() {
    return discountApplied;
  }

  /**
   * <p>If there is some discount applied to the product item,
   * clients might want to know the unit price discount value.</p>
   * This method returns the discount unit price value
   * @return between 0 for no discount or the value of {@link ProductItem#getUnitListPrice()} for 100% discount
   */
  public BigDecimal getUnitPriceDiscount() {
    return this.unitDiscountPrice;
  }

  /**
   * <p>Provides the relevant product unit price for the current offer.</p>
   * @return the numerical value of the unit list price
   */
  public BigDecimal getUnitListPrice() {
    return this.unitListPrice;
  }

  /**
   * <p>Provides the total item net price of the product item, which
   * includes discounts.</p>
   * @return the numerical value of the item's quantity multiplied with the discounted unit price.
   */
  public BigDecimal getItemTotalPrice() {
    BigDecimal discountedUnitPrice = unitListPrice.subtract(unitDiscountPrice);
    return discountedUnitPrice.multiply(BigDecimal.valueOf(quantity))
        .setScale(2, RoundingMode.HALF_UP);
  }

  /**
   * <p>Provides the relative discount in percentage.</p>
   *
   * <p>In case no discount has been applied, this will return 0, in case of full discount it will return 100.</p>
   * @return 0, 100, or in between, for no discount, full discount or partial discount applied
   */
  public BigDecimal getRelativeDiscount() {
    return unitDiscountPrice.divide(unitListPrice, 4, RoundingMode.HALF_UP)
        .multiply(new BigDecimal("100"));
  }
}
