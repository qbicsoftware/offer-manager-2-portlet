package life.qbic.business.products.copy


import life.qbic.business.products.create.CreateProductInput
import life.qbic.business.products.create.CreateProductOutput
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
class CopyProduct implements CopyProductInput {

    private final CopyProductDataSource dataSource
    private final CopyProductOutput output
    private final CreateProductInput createProductInput

    /**
     * The only constructor for this use case
     * @param dataSource - a data source that provides mandatory functionality
     * @param output - an output that provides mandatory functionality
     * @param createProductInput - a CreateProduct use case that is used to create the product
     */
    CopyProduct(CopyProductDataSource dataSource, CopyProductOutput output, CreateProductInput createProductInput) {
        this.dataSource = dataSource
        this.output = output
        this.createProductInput = createProductInput
    }

    /**
     * creates a copy of an existing product
     * @param product The modified product information
     * @since 1.0.0
     */
    @Override
    void copyModified(Product product) {
        throw new RuntimeException("Not implemented")
        //TODO
        //1. retrieve product from db
        //2. construct new product with missing information filled from the db
        //3. change product identifier
        //4. call the CreateProduct use case
    }

    /**
     * Sends failure notifications that have been
     * recorded during the use case.
     * @param notification containing a failure message
     * @since 1.0.0
     */
    @Override
    void failNotification(String notification) {
        output.failNotification(notification)
    }

    /**
     * A product has been created in the database
     * @param product The product that has been created
     * @since 1.0.0
     */
    @Override
    void created(Product product) {
        output.copied(product)
    }

    /**
     * The product is already stored in the database
     * @param product The product for which a duplicate has been found
     * @since 1.0.0
     */
    @Override
    void foundDuplicate(Product product) {
        //this should never happen because we create a new id before calling the use case
        // -> pre-condition of a product that already exists
    }
}
