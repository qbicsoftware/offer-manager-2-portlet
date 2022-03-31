package life.qbic.business.products
/**
 * <b>A draft for a product</b>
 *
 * <p>This data transfer object contains information necessary to create a product.</p>
 *
 * @since 1.2.1
 */
class ProductDraft {
  final ProductCategory category
  final String name
  final String description
  final double internalUnitPrice
  final double externalUnitPrice
  final String unit
  final String serviceProvider

  private ProductDraft(ProductCategory category, String name, String description, double internalUnitPrice, double externalUnitPrice, String unit, String serviceProvider) {
    this.category = category
    this.name = name
    this.description = description
    this.internalUnitPrice = internalUnitPrice
    this.externalUnitPrice = externalUnitPrice
    this.unit = unit
    this.serviceProvider = serviceProvider
  }

  static ProductDraft create(ProductCategory category, String name, String description, double internalUnitPrice, double externalUnitPrice, String unit, String serviceProvider) {
    return new ProductDraft(category,
            name,
            description,
            internalUnitPrice,
            externalUnitPrice,
            unit,
            serviceProvider)
  }

  @Override
  public String toString() {
    return "ProductDraft{" +
            "category=" + category +
            ", name='" + name + '\'' +
            ", description='" + description + '\'' +
            ", internalUnitPrice=" + internalUnitPrice +
            ", externalUnitPrice=" + externalUnitPrice +
            ", unit='" + unit + '\'' +
            ", serviceProvider='" + serviceProvider + '\'' +
            '}';
  }
}
