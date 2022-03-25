package life.qbic.business.products;

import static java.util.Objects.requireNonNull;

import groovy.transform.CompileStatic;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
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
import life.qbic.business.offers.QuantityDiscountFactor;
import life.qbic.business.persons.affiliation.AffiliationCategory;

@Entity
@Table(name = "productitem", uniqueConstraints = {
    @UniqueConstraint(name = "id", columnNames = {"id"})
})
@CompileStatic
public class ProductItem {

  private static final List<String> DATA_ANALYSIS = Arrays.asList(
      "Primary Bioinformatics",
      "Secondary Bioinformatics");

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id", nullable = false)
  private int id;

  @ManyToOne(optional = false)
  @JoinColumn(name = "productId", nullable = false)
  private Product product;

  @Column(name = "quantity", nullable = false)
  private Double quantity;

  @ManyToOne
  @JoinColumn(name = "offerId", nullable = false)
  private OfferV2 offer;

  @Transient
  private BigDecimal unitPrice;

  @Transient
  private BigDecimal discountRate;

  public ProductItem(OfferV2 offer, Product product, Double quantity) {
    this.offer = requireNonNull(offer, "Offer must not be null");
    this.product = requireNonNull(product, "Product must not be null");
    this.quantity = requireNonNull(quantity, "Quantity must not be null");
    refreshProductItem();
  }

  protected ProductItem() {
    //needed for hibernate
  }

  public Double getQuantity() {
    return quantity;
  }

  public Product getProduct() {
    return product;
  }

  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  protected void refresh() {
    refreshProductItem();
  }

  private void refreshProductItem() {
    validateObjectState();
    AffiliationCategory affiliationCategory = offer.getSelectedCustomerAffiliation().getCategory();
    unitPrice = determineUnitPrice(affiliationCategory, product);
    discountRate = getDiscountRate(affiliationCategory, BigDecimal.valueOf(quantity),
        product.getCategory());
  }

  private void validateObjectState() {
    requireNonNull(offer, "Offer must not be null");
    requireNonNull(product, "Product must not be null");
    requireNonNull(quantity, "Quantity must not be null");
    requireNonNull(offer.getSelectedCustomerAffiliation(),
        "Offer must have a customer affiliation selected");
    requireNonNull(offer.getSelectedCustomerAffiliation().getCategory(),
        "Selected customer affiliation must provide a category.");
    requireNonNull(product.getInternalUnitPrice(), "Product must have an internal unit price");
    requireNonNull(product.getExternalUnitPrice(), "Product must have an external unit price");
  }

  private BigDecimal getDiscountRate(AffiliationCategory affiliationCategory, BigDecimal quantity,
      String productCategory) {
    BigDecimal quantityDiscountRate =
        calculateQuantityDiscountRate(quantity, productCategory);
    BigDecimal storageDiscountRate =
        calculateDataStorageDiscountRate(affiliationCategory, productCategory);
    return quantityDiscountRate.max(storageDiscountRate);
  }

  /**
   * <p>The relevant unit price for a product in an offer depends on the customer's affiliation.
   * The current business policy is to provide base prices for internal and external customer
   * affiliations. In some cases VAT of i.e. purchased consumables can be reflected in the internal
   * base price.</p>
   *
   * @param affiliationCategory internal, external or external academic
   * @param product             the product of interest
   * @return the determined base price
   */
  protected static BigDecimal determineUnitPrice(AffiliationCategory affiliationCategory,
      Product product) {
    if (affiliationCategory == AffiliationCategory.INTERNAL) {
      return BigDecimal.valueOf(product.getInternalUnitPrice()).setScale(2,
          RoundingMode.HALF_UP);
    }
    return BigDecimal.valueOf(product.getExternalUnitPrice())
        .setScale(2, RoundingMode.HALF_UP);
  }

  /**
   * Calculate the storage discount rate for a given unit price. For internal affiliations, this is
   * set to 100%, for other affiliations this is set to 0%. Data storage discount is only applicable
   * on data storage products.
   *
   * @param affiliationCategory the considered affiliation category for discount determination
   * @param productCategory     the considered product category for discount determination
   * @return the discount rate for the provided product. In range [0,1]
   */
  protected static BigDecimal calculateDataStorageDiscountRate(
      AffiliationCategory affiliationCategory, String productCategory) {
    if (productCategory.equalsIgnoreCase("data storage")
        && affiliationCategory == AffiliationCategory.INTERNAL) {
      return BigDecimal.ONE; // full discount
    }
    return BigDecimal.ZERO;
  }

  /**
   * Calculates the discounted unit price based on the quantity.
   *
   * @param quantity the quantity of the product
   * @return the discounted unit price
   */
  protected static BigDecimal calculateQuantityDiscountRate(
      BigDecimal quantity, String productCategory) {
    QuantityDiscountFactor quantityDiscount = new QuantityDiscountFactor();
    BigDecimal discountRate = quantityDiscount.apply(quantity);
    // Round up to the second digit
    return DATA_ANALYSIS.contains(productCategory)
        ? discountRate.setScale(4, RoundingMode.HALF_UP)
        : BigDecimal.ZERO;
  }

  /**
   * <p>Check whether a discount can be applied to this item.</p>
   *
   * @return <code>true</code>, if there is discount applied to the product item, else
   * <code>false</code>
   */
  public boolean hasDiscount() {
    refresh();
    return discountRate.compareTo(BigDecimal.ZERO) > 0;
  }

  /**
   * Returns the amount of money discounted from the unit price.
   *
   * @return between 0 for no discount and {@link ProductItem#getUnitPrice()} for 100% discount
   */
  public BigDecimal getUnitDiscountAmount() {
    refresh();
    return this.unitPrice.multiply(this.discountRate).setScale(2, RoundingMode.HALF_UP);
  }

  /**
   * Returns the amount of money discounted from the list price.
   *
   * @return between 0 for no discount and {@link ProductItem#getListPrice()} for 100% discount.
   */
  public BigDecimal getDiscountAmount() {
    refresh();
    return getUnitDiscountAmount().multiply(BigDecimal.valueOf(quantity))
        .setScale(2, RoundingMode.HALF_UP);
  }

  /**
   * <p>Provides the relevant product unit price given the current offer.</p>
   *
   * @return the numerical value of the unit list price
   */
  public BigDecimal getUnitPrice() {
    refresh();
    return this.unitPrice;
  }

  /**
   * Provides the list price defined by the unit price multiplied by the quantity. This price does
   * not account for discounts.
   *
   * @return the list price of this item.
   */
  public BigDecimal getListPrice() {
    refresh();
    return this.unitPrice.multiply(BigDecimal.valueOf(quantity)).setScale(2, RoundingMode.HALF_UP);
  }

  /**
   * <p>Provides the total item's sale price including discounts.</p>
   *
   * @return the discounted item's unit price multiplied with its quantity minus applied discounts.
   */
  public BigDecimal getSalePrice() {
    refresh();
    return getListPrice().subtract(getDiscountAmount()).setScale(2, RoundingMode.HALF_UP);
  }

  /**
   * <p>Provides the discount rate. The rate is a value between 0 and 1 inclusive, e.g. 0.15 for
   * 15% discount.</p>
   * <p>When no discount applies, this method returns 0. When full discount applies, this method
   * returns 1.</p>
   *
   * @return value in the range [0, 1]
   */
  public BigDecimal getDiscountRate() {
    refresh();
    return discountRate;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ProductItem that = (ProductItem) o;
    return product.equals(that.product) && quantity.equals(that.quantity) && offer.equals(
        that.offer);
  }

  @Override
  public int hashCode() {
    return Objects.hash(product, quantity, offer);
  }
}
