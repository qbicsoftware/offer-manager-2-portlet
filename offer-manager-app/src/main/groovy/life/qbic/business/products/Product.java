package life.qbic.business.products;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Lob;
import javax.persistence.Table;

@Entity
@Table(name = "product", indexes = {
    @Index(name = "UQ_product_productId", columnList = "productId", unique = true)
})
public class Product {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id", nullable = false)
  private Integer id;

  @Lob
  @Column(name = "category")
  private String category;

  @Column(name = "description", length = 2500)
  private String description;

  @Column(name = "productName", length = 500)
  private String productName;

  @Column(name = "internalUnitPrice", nullable = false)
  private Double internalUnitPrice;

  @Column(name = "externalUnitPrice", nullable = false)
  private Double externalUnitPrice;

  @Lob
  @Column(name = "unit")
  private String unit;

  @Column(name = "productId", length = 45)
  private String productId;

  @Lob
  @Column(name = "serviceProvider", nullable = false)
  private String serviceProvider;

  @Column(name = "active", nullable = false)
  private Boolean active = false;

  public Boolean getActive() {
    return active;
  }

  public void setActive(Boolean active) {
    this.active = active;
  }

  public String getServiceProvider() {
    return serviceProvider;
  }

  public void setServiceProvider(String serviceProvider) {
    this.serviceProvider = serviceProvider;
  }

  public String getProductId() {
    return productId;
  }

  public void setProductId(String productId) {
    this.productId = productId;
  }

  public String getUnit() {
    return unit;
  }

  public void setUnit(String unit) {
    this.unit = unit;
  }

  public Double getExternalUnitPrice() {
    return externalUnitPrice;
  }

  public void setExternalUnitPrice(Double externalUnitPrice) {
    this.externalUnitPrice = externalUnitPrice;
  }

  public Double getInternalUnitPrice() {
    return internalUnitPrice;
  }

  public void setInternalUnitPrice(Double internalUnitPrice) {
    this.internalUnitPrice = internalUnitPrice;
  }

  public String getProductName() {
    return productName;
  }

  public void setProductName(String productName) {
    this.productName = productName;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public String getCategory() {
    return category;
  }

  public void setCategory(String category) {
    this.category = category;
  }

  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }
}
