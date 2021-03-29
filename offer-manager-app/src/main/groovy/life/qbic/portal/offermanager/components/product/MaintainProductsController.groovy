package life.qbic.portal.offermanager.components.product

import life.qbic.business.logging.Logger
import life.qbic.business.logging.Logging
import life.qbic.business.products.Converter
import life.qbic.business.products.archive.ArchiveProductInput
import life.qbic.business.products.copy.CopyProductInput
import life.qbic.business.products.create.CreateProductInput
import life.qbic.datamodel.dtos.business.ProductCategory
import life.qbic.datamodel.dtos.business.ProductId
import life.qbic.datamodel.dtos.business.services.Product
import life.qbic.datamodel.dtos.business.services.ProductUnit

/**
 * <h1>Controls how the information flows into the use cases {@link life.qbic.business.products.create.CreateProduct} and {@link life.qbic.business.products.archive.ArchiveProduct}</h1>
 *
 * <p>This class allows to trigger the use cases and respectively create new products, copy or archive them.</p>
 *
 * @since 1.0.0
 *
 */
class MaintainProductsController {

    private final CreateProductInput createProductInput
    private final ArchiveProductInput archiveProductInput
    private final CopyProductInput copyProductInput
    private static final Logging log = Logger.getLogger(this.class)

    MaintainProductsController(CreateProductInput createProductInput,
                               ArchiveProductInput archiveProductInput,
                               CopyProductInput copyProductInput){
        this.createProductInput = createProductInput
        this.archiveProductInput = archiveProductInput
        this.copyProductInput = copyProductInput
    }

    /**
     * Triggers the creation of a product in the database
     *
     * @param category The products category which determines what kind of product is created
     * @param description The description of the product
     * @param name The name of the product
     * @param unitPrice The unit price of the product
     * @param unit The unit in which the product is measured
     */
    void createNewProduct(ProductCategory category, String description, String name, double unitPrice, ProductUnit unit){
        try {
            Product product = ProductConverter.createProduct(category, description, name, unitPrice, unit)
            createProductInput.create(product)
        } catch (Exception unexpected) {
            log.error("unexpected exception during create product call", unexpected)
            throw new IllegalArgumentException("Could not create products from provided arguments.")
        }
    }

    /**
     * Triggers the archiving of a product
     * @param productId The ProductId of the product that should be archived
     */
    void archiveProduct(ProductId productId){
        try{
            archiveProductInput.archive(productId)
        }catch(Exception unexpected){
            log.error("unexpected exception at archive product call", unexpected)
            throw new IllegalArgumentException("Could not archive products from provided arguments.")
        }
    }

    /**
     * Triggers the copy use case of a product
     *
     * @param category The products category which determines what kind of product is created
     * @param description The description of the product
     * @param name The name of the product
     * @param unitPrice The unit price of the product
     * @param unit The unit in which the product is measured
     */
    void copyProduct(ProductCategory category, String description, String name, double unitPrice, ProductUnit unit, ProductId productId){
        try{
       
            //ToDo how should the Id be provided to the Use Case?
            /**Product product = ProductConverter.createProduct(category, description, name, unitPrice, unit)
            copyProductInput.copyModified(product) */
        }catch(Exception unexpected){
            log.error("unexpected exception at copy product call", unexpected)
            throw new IllegalArgumentException("Could not copy product from provided arguments.")
        }
    }

    private static class ProductConverter{

        /**
         * Creates a product DTO based on its products category
         *
         * @param category The products category which determines what kind of products is created
         * @param description The description of the product
         * @param name The name of the product
         * @param unitPrice The unit price of the product
         * @param unit The unit in which the product is measured
         * @return
         */
        static Product createProduct(ProductCategory category, String description, String name, double unitPrice, ProductUnit unit){
            return Converter.createProduct(category,name, description, unitPrice,unit)
        }

    }

}
