package life.qbic.business.products.copy

import life.qbic.business.Constants
import life.qbic.business.exceptions.DatabaseQueryException
import life.qbic.business.logging.Logger
import life.qbic.business.logging.Logging
import life.qbic.business.products.Converter
import life.qbic.business.products.create.CreateProduct
import life.qbic.business.products.create.CreateProductDataSource
import life.qbic.business.products.create.CreateProductInput
import life.qbic.business.products.create.CreateProductOutput
import life.qbic.datamodel.dtos.business.services.Product
import org.aspectj.bridge.IMessage

import java.nio.charset.StandardCharsets
import java.security.MessageDigest

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
    void copyModified(Product product) {
        Optional<Product> searchResult
        try{
            //1. retrieve product from db
            searchResult = dataSource.fetch(product.getProductId())
            if (searchResult.isPresent()) {
                Product existingProduct = searchResult.get()
                //2. compare if there is a difference between the products in order
                if(getProductChecksum(product) != getProductChecksum(existingProduct)){
                    //3. call the CreateProduct use case (new id is created here)
                    createProduct.create(product)
                }else{
                    log.error("The product ${product.productName} is a duplicate of ${existingProduct.productName - existingProduct.productId}.")
                    output.failNotification("The product ${product.productName} is a duplicate of ${existingProduct.productName - existingProduct.productId}.")
                }
            }else{
                //there is no product present, this should not happen
                log.error("An unexpected during the project creation occurred.")
                output.failNotification("An unexpected during the project creation occurred. " +
                        "Please contact ${Constants.QBIC_HELPDESK_EMAIL}.")
            }
        }catch(DatabaseQueryException databaseQueryException){
            log.error("The copied product ${product.productId.toString()} cannot be found in the database", databaseQueryException)
            output.failNotification("The copied product ${product.productId.toString()} cannot be found in the database")
        }
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
     *{@inhertDoc}
     */
    @Override
    @Deprecated
    void foundDuplicate(Product product) {
        //todo what do do here?
    }


    /**
     * Compute the checksum for a product
     * @param digest The digestor will digest the message that needs to be encrypted
     * @param product Contains the product information
     * @return a string that encrypts the product object
     */
    private static String getProductChecksum(Product product)
    {
        MessageDigest digest = MessageDigest.getInstance("SHA-256")
        //digest crucial offer characteristics
        digest.update(product.productName.getBytes(StandardCharsets.UTF_8))

        digest.update(product.description.getBytes(StandardCharsets.UTF_8))

        digest.update(product.unit.value.getBytes(StandardCharsets.UTF_8))
        digest.update(product.unitPrice.toString().getBytes(StandardCharsets.UTF_8))
        digest.update(Converter.getCategory(product).toString().getBytes(StandardCharsets.UTF_8))

        //Get the hash's bytes
        byte[] bytes = digest.digest()

        //This bytes[] has bytes in decimal format
        //Convert it to hexadecimal format
        StringBuilder sb = new StringBuilder()
        for(int i=0; i< bytes.length ;i++)
        {
            sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
        }

        //return complete hash
        return sb.toString()
    }}
