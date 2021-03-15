package life.qbic.portal.offermanager.components.products

import life.qbic.business.products.archive.ArchiveProductInput
import life.qbic.business.products.create.CreateProductInput

import life.qbic.datamodel.dtos.business.ProductCategory

import life.qbic.datamodel.dtos.business.services.DataStorage
import life.qbic.datamodel.dtos.business.services.PrimaryAnalysis
import life.qbic.datamodel.dtos.business.services.Product
import life.qbic.datamodel.dtos.business.services.ProductUnit
import life.qbic.datamodel.dtos.business.services.ProjectManagement
import life.qbic.datamodel.dtos.business.services.SecondaryAnalysis
import life.qbic.datamodel.dtos.business.services.Sequencing

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

    MaintainProductsController(CreateProductInput createProductInput,
                               ArchiveProductInput archiveProductInput){
        this.createProductInput = createProductInput
        this.archiveProductInput = archiveProductInput
    }

    /**
     * Triggers the creation of a products in the database
     *
     * @param category The products category which determines what kind of products is created
     * @param description The description of the products
     * @param name The name of the products
     * @param unitPrice The unit price of the products
     * @param unit The unit in which the products is measured
     */
    void createNewProduct(ProductCategory category, String description, String name, double unitPrice, ProductUnit unit){
        try {
            Product product = ProductConverter.createProduct(category, description, name, unitPrice, unit)
            createProductInput.create(product)
        } catch (Exception unexpected) {
            log.error("unexpected exception at call of create products", unexpected)
            throw new IllegalArgumentException("Could not create products from provided arguments.")
        }
    }


    private static class ProductConverter{

        /**
         * Creates a products DTO based on its products category
         *
         * @param category The products category which determines what kind of products is created
         * @param description The description of the products
         * @param name The name of the products
         * @param unitPrice The unit price of the products
         * @param unit The unit in which the products is measured
         * @return
         */
        static Product createProduct(ProductCategory category, String description, String name, double unitPrice, ProductUnit unit){
            Product product
            switch (category) {
                case "Data Storage":
                    //todo do we want to set the id manually to null or update the DTO constructor?
                    product = new DataStorage(name, description, unitPrice,unit, null)
                    break
                case "Primary Bioinformatics":
                    product = new PrimaryAnalysis(name, description, unitPrice,unit, null)
                    break
                case "Project Management":
                    product = new ProjectManagement(name, description, unitPrice,unit, null)
                    break
                case "Secondary Bioinformatics":
                    product = new SecondaryAnalysis(name, description, unitPrice,unit, null)
                    break
                case "Sequencing":
                    product = new Sequencing(name, description, unitPrice,unit, null)
                    break
            }
            if(!product) throw new IllegalArgumentException("Cannot parse products")

            return product
        }

    }

}
