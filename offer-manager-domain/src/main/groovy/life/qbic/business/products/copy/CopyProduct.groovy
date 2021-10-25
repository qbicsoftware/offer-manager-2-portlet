package life.qbic.business.products.copy

import life.qbic.business.Constants
import life.qbic.business.exceptions.DatabaseQueryException
import life.qbic.business.logging.Logger
import life.qbic.business.logging.Logging
import life.qbic.business.products.ProductEntity
import life.qbic.business.products.create.CreateProduct
import life.qbic.business.products.create.CreateProductDataSource
import life.qbic.business.products.create.CreateProductInput
import life.qbic.business.products.create.CreateProductOutput
import life.qbic.business.products.dtos.ProductDraft
import life.qbic.datamodel.dtos.business.ProductId
import life.qbic.datamodel.dtos.business.services.Product

/**
 * <h1>4.3.2 Copy Service Product</h1>
 * <br>
 * <p> Offer Administrators are allowed to create a new permutation of an existing product.
 * <br> New permutations can include changes in unit price, sequencing technology and other attributes of service products.
 * </p>
 *
 * @since: 1.0.0
 *
 */
class CopyProduct implements CopyProductInput, CreateProductOutput {

    private static final Logging log = Logger.getLogger(this.class)

    private final CopyProductDataSource dataSource
    private final CopyProductOutput output
    private final CreateProductInput createProduct

    /**
     * The only constructor for this use case
     * @param dataSource - a data source that provides mandatory functionality
     * @param output - an output that provides mandatory functionality
     * @param createProductInput - a CreateProduct use case that is used to create the product
     */
    CopyProduct(CopyProductDataSource dataSource, CopyProductOutput output, CreateProductDataSource createProductDataSource) {
        this.dataSource = dataSource
        this.output = output
        this.createProduct = new CreateProduct(createProductDataSource, this)
    }

    /**
     * {@inheritDoc}
     */
    @Override
    void copyModified(ProductDraft productDraft, ProductId originalProductId) {
        try {
            //1. retrieve product from db
            Product existingProduct = getExistingProduct(originalProductId)
            ProductEntity copiedProductEntity = ProductEntity.fromDraft(productDraft)
            //2. compare if there is a difference between the products in order
            if (theProductHasChanged(copiedProductEntity, existingProduct)) {
                //3. call the CreateProduct use case (new id is created here)
                createProduct.create(productDraft)
            } else {
                foundDuplicate(productDraft)
            }
        } catch (DatabaseQueryException databaseQueryException) {
            log.error("The to be copied product ${originalProductId.toString()} cannot be found in the database", databaseQueryException)
            output.failNotification("The to be copied product ${originalProductId.toString()} cannot be found in the database")
        } catch(Exception ignore){
            //there is no product present, this should not happen
            log.error("An unexpected during the project creation occurred.", ignore)
            output.failNotification("An unexpected during the project creation occurred. " +
                    "Please contact ${Constants.QBIC_HELPDESK_EMAIL}.")
        }
    }

    private Product getExistingProduct(ProductId productId){
        return dataSource.fetch(productId).get()
    }

    private static boolean theProductHasChanged(ProductEntity copiedProduct, Product originalProduct) {

        ProductEntity oldProduct = ProductEntity.fromDto(originalProduct)
        copiedProduct.checksum() != oldProduct.checksum()
    }

    /**
     * {@inheritDoc}
     */
    @Override
    void failNotification(String notification) {
        output.failNotification(notification)
    }

    /**
     * {@inheritDoc}
     */
    @Override
    void created(Product product) {
        output.copied(product)
    }

    /**
     * {@inhertDoc}
     */
    @Override
    void foundDuplicate(ProductDraft productDraft) {
        log.warn("A product with the same content like ${productDraft.name} already exists.")
        output.failNotification("A product with the same content like ${productDraft.name} already exists.")
    }
}
