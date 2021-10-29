package life.qbic.business.products.create

import life.qbic.business.Constants
import life.qbic.business.exceptions.DatabaseQueryException
import life.qbic.business.logging.Logger
import life.qbic.business.logging.Logging
import life.qbic.business.products.ProductEntity
import life.qbic.business.products.dtos.ProductDraft
import life.qbic.datamodel.dtos.business.ProductId
import life.qbic.datamodel.dtos.business.services.Product

/**
 * <h1>4.3.0 Create Service Product</h1>
 * <br>
 * <p> When the service portfolio changed due to a business decision an Offer Administrator should be allowed to provide information on the new service offered and make it available to new offers upon creation.
 * </p>
 *
 * @since: 1.0.0
 *
 */
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
                String productIds = duplicateProducts.join(", ")
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
        ProductId createdProductId
        Product storedProduct
        createdProductId = dataSource.store(productDraft)
        //create product with new product ID
        if (createdProductId) {
            ProductEntity storedProductEntity = ProductEntity.fromDraft(productDraft)
            storedProductEntity.id(createdProductId)
            storedProduct = storedProductEntity.toFinalProduct()
            output.created(storedProduct)
            log.info("${storedProduct.productName} with identifier ${storedProduct.productId} was created successfully.")
        } else {
            log.error("The database could not return a ProductId for the generated Product.")
            output.failNotification("The created Product $productDraft.name did not return a ProductId")
        }
    }
}

