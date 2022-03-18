package life.qbic.business.products;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import life.qbic.business.offers.OfferV2;

@Entity
@Table(name = "productitem", uniqueConstraints = {
    @UniqueConstraint(name = "id", columnNames = {"id"})
})
public class ProductItem {

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

  public ProductItem(Product product, Double quantity) {
    this.product = product;
    this.quantity = quantity;
  }

  protected ProductItem() {
  }

  public void setOffer(OfferV2 offer) {
    this.offer = offer;
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
  }

  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public double getUnitPrice() {
    return 0;
  }
}
