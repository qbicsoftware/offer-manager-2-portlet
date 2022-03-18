package life.qbic.business.products;

import groovy.transform.CompileStatic;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Arrays;
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

  private static final List<String> DATA_ANALYSIS = Arrays.asList(
      "Primary Bioinformatics",
      "Secondary Bioinformatics");

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
  private BigDecimal unitPrice;

  @Transient
  private BigDecimal unitDiscountPrice;

  public ProductItem(Product product, Double quantity) {
    this.product = product;
    this.quantity = quantity;
  }

  protected ProductItem() {
  }

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
      // TODO clarify whether a reset on already set fields is required in those cases as the productitems is in an indetermined state here.
      return;
    }
    AffiliationCategory affiliationCategory = offer.getSelectedCustomerAffiliation().getCategory();
    unitPrice = determineUnitPrice(affiliationCategory, product);

    BigDecimal quantityDiscount =
        calculateQuantityDiscount(unitPrice, BigDecimal.valueOf(quantity), product.getCategory());
    BigDecimal storageDiscount =
        calculateDataStorageDiscount(affiliationCategory, product.getCategory(), unitPrice);
    unitDiscountPrice = quantityDiscount.max(storageDiscount);
  }

  /**
   * <p>The relevant unit price for a product in an offer depends on the customer's affiliation.
   * The current business policy is to provide base prices for internal and external customer affiliations.
   * In some cases VAT of i.e. purchased consumables can be reflected in the internal base price.</p>
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
   * Calculate the storage discount for a given unit price. For internal affiliations, this is set
   * to 100%, for other affiliations this is set to 0%. Data storage discount is only applicable on
   * data storage products.
   *
   * @param affiliationCategory the considered affiliation category for discount determination
   * @param productCategory     the considered product category for discount determination
   * @param unitPrice           the discounted unit price
   * @return the amount of discount per unit for the provided unit price
   */
  protected static BigDecimal calculateDataStorageDiscount(
      AffiliationCategory affiliationCategory, String productCategory, BigDecimal unitPrice) {
    if (productCategory.equalsIgnoreCase("data storage")
        && affiliationCategory == AffiliationCategory.INTERNAL) {
      return unitPrice; // full discount
    }
    return BigDecimal.ZERO;
  }

  /**
   * Calculates the discounted unit price based on the quantity.
   *
   * @param unitPrice the pre-selected unit price
   * @param quantity  the quantity of the product
   * @return the discounted unit price
   */
  protected static BigDecimal calculateQuantityDiscount(
      BigDecimal unitPrice, BigDecimal quantity, String productCategory) {
    QuantityDiscount quantityDiscount = new QuantityDiscount();
    BigDecimal unitDiscount = quantityDiscount.apply(quantity, unitPrice);
    // Round up to the second digit
    return DATA_ANALYSIS.contains(productCategory)
        ? unitDiscount.setScale(2, RoundingMode.UP)
        : BigDecimal.ZERO;
  }

  /**
   * <p>Check whether a discount can be applied to this item.</p>
   *
   * @return <code>true</code>, if there is discount applied to the product item, else
   * <code>false</code>
   */
  public boolean hasDiscount() {
    return unitPrice.subtract(unitDiscountPrice).compareTo(unitPrice) < 0;
  }

  /**
   * Returns the amount of money discounted from the unit price.
   *
   * @return between 0 for no discount or the value of {@link ProductItem#getUnitPrice()} for
   * 100% discount
   */
  public BigDecimal getUnitDiscountPrice() {
    return this.unitDiscountPrice;
  }

  /**
   * <p>Provides the relevant product unit price given the current offer.</p>
   *
   * @return the numerical value of the unit list price
   */
  public BigDecimal getUnitPrice() {
    return this.unitPrice;
  }

  /**
   * <p>Provides the total item net price of the product item, including discounts.</p>
   *
   * @return the discounted item's unit price multiplied with its quantity.
   */
  public BigDecimal getItemNetPrice() {
    BigDecimal discountedUnitPrice = unitPrice.subtract(unitDiscountPrice);
    return discountedUnitPrice.multiply(BigDecimal.valueOf(quantity))
        .setScale(2, RoundingMode.HALF_UP);
  }

  /**
   * <p>Provides the relative discount in percentage.</p>
   *
   * <p>When no discount applies, this method returns 0. When full discount applies, this method returns 100.</p>
   *
   * @return a value in the range [0, 100]. The discounted percentage of the list price.
   */
  public BigDecimal getDiscountPercentage() {
    return unitDiscountPrice.divide(unitPrice, 4, RoundingMode.HALF_UP)
        .multiply(new BigDecimal("100"));
  }
}
