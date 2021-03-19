package life.qbic.business.products.copy

import life.qbic.business.logging.Logger
import life.qbic.business.logging.Logging
import life.qbic.business.products.create.CreateProduct
import life.qbic.business.products.create.CreateProductDataSource
import life.qbic.business.products.create.CreateProductInput
import life.qbic.business.products.create.CreateProductOutput
import life.qbic.datamodel.dtos.business.services.Product
import org.aspectj.bridge.IMessage

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
    private static final int MAX_ATTEMPTS = 1

    private final CopyProductDataSource dataSource
    private final CopyProductOutput output
    private final CreateProductInput createProduct

    private int copyAttempt = 0

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
    void copyModified(Product product) {
        copyAttempt++
        //1. retrieve product from db
        Optional<Product> searchResult = dataSource.fetch(product.getProductId())

        if (searchResult.isPresent()) {
            //2. construct new product with missing information filled from the db
            Product existingProduct = searchResult.get()
            //todo check difference with checksum??
            //3. call the CreateProduct use case (new id is created here)
            createProduct.create(product)
        }
        //there is no product present
        failed("The provided product was not found. Please create a new one instead.")
    }

    /**
     * {@inheritDoc}
     */
    @Override
    void failNotification(String notification) {
        failed(notification)
    }

    /**
     * {@inheritDoc}
     */
    @Override
    void created(Product product) {
        succeeded(product)
    }

    /**
     *{@inhertDoc}
     */
    @Override
    void foundDuplicate(Product product) {
        // we end up here when the id we created already is present in the database upon creation
        // this should happen only in the case that someone else beat us in creating the product.
        // since this should be very rare we log a warning here
        log.warn("The generated product id \"$product.productId\" already exists.")
        if (copyAttempt < MAX_ATTEMPTS) {
            log.warn("Trying to copy the product again. Attempt no.$copyAttempt")
            copyModified(product)
        }
    }

    private void failed(String message) {
        copyAttempt = 0
        output.failNotification(message)
    }

    private void succeeded(Product product) {
        copyAttempt = 0
        output.copied(product)
    }
}
