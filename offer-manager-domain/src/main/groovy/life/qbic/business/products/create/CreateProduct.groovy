package life.qbic.business.products.create

import groovy.transform.CompileStatic
import life.qbic.business.Constants
import life.qbic.business.exceptions.DatabaseQueryException
import life.qbic.business.logging.Logger
import life.qbic.business.logging.Logging
import life.qbic.business.products.Product
import life.qbic.business.products.ProductDraft

/**
 * <h1>4.3.0 Create Service Product</h1>
 * <br>
 * <p> When the service portfolio changed due to a business decision an Offer Administrator should be allowed to provide information on the new service offered and make it available to new offers upon creation.
 * </p>
 *
 * @since: 1.0.0
 *
 */
@CompileStatic
class CreateProduct implements CreateProductInput {
  private final CreateProductDataSource dataSource
  private final CreateProductOutput output
  private static final Logging log = Logger.getLogger(this.class)

  CreateProduct(CreateProductDataSource dataSource, CreateProductOutput output) {
    this.dataSource = dataSource
    this.output = output
  }

  @Override
  void create(ProductDraft productDraft) {
    try {
      List<Product> duplicateProducts = getDuplicateProducts(productDraft)
      if (duplicateProducts.isEmpty()) {
        storeProduct(productDraft)
      } else {
        Product duplicateProduct = duplicateProducts.first()
        List<String> duplicateProductIds = []
        duplicateProducts.forEach { Product product ->
          duplicateProductIds << product.getProductId()
        }
        String productIds = duplicateProductIds.join(", ")
        log.info("Found multiple products for ${duplicateProduct.productName} : ${productIds}")
        output.foundDuplicates(duplicateProducts)
      }
    } catch (DatabaseQueryException databaseQueryException) {
      log.error("Product creation failed", databaseQueryException)
      output.failNotification("Could not create new product $productDraft.name")
    } catch (Exception exception) {
      log.error("An unexpected error occured during product creation.", exception)
      output.failNotification("An unexpected error occured during product creation. " +
              "Please contact ${Constants.QBIC_HELPDESK_EMAIL}.")
    }
  }

  private List<Product> getDuplicateProducts(ProductDraft productDraft) {
    List<Product> duplicateProducts = dataSource.findDuplicateProducts(productDraft)
    return duplicateProducts
  }

  private void storeProduct(ProductDraft productDraft) {
    Product storedProduct = dataSource.store(productDraft)
    //create product with new product ID
    if (storedProduct) {
      output.created(storedProduct)
      log.info("${storedProduct.productName} with product identifier ${storedProduct.productId} was created successfully.")
    } else {
      log.error("The database could not create a product for '$productDraft.name'.")
      output.failNotification("The created Product $productDraft.name did not return a ProductId")
    }
  }
}
