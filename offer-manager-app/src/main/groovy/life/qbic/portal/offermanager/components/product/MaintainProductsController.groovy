package life.qbic.portal.offermanager.components.product

import life.qbic.business.RefactorConverter
import life.qbic.business.logging.Logger
import life.qbic.business.logging.Logging
import life.qbic.business.products.ProductDraft
import life.qbic.business.products.archive.ArchiveProductInput
import life.qbic.business.products.create.CreateProductInput
import life.qbic.datamodel.dtos.business.ProductCategory
import life.qbic.datamodel.dtos.business.ProductId
import life.qbic.datamodel.dtos.business.facilities.Facility
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
    private static final Logging log = Logger.getLogger(this.class)

    MaintainProductsController(CreateProductInput createProductInput,
                               ArchiveProductInput archiveProductInput){
        this.createProductInput = createProductInput
        this.archiveProductInput = archiveProductInput
    }

    /**
     * Triggers the creation of a product in the database
     *
     * @param category The products category which determines what kind of product is created
     * @param description The description of the product
     * @param name The name of the product
     * @param internalUnitPrice The unit price of the product for internal affiliations
     * @param externalUnitPrice The unit price of the product for external affiliations
     * @param unit The unit in which the product is measured
     * @param facility The facility providing the product
     * @since 1.1.0
     */
    void createNewProduct(ProductCategory category, String description, String name, double internalUnitPrice, double externalUnitPrice, ProductUnit unit, Facility facility){
        RefactorConverter converter = new RefactorConverter()
        try {
            ProductDraft productDraft = ProductDraft.create(converter.toProductCategory(category),
                    name, description, internalUnitPrice, externalUnitPrice, unit.value, facility.getLabel())
            createProductInput.create(productDraft)
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
}
