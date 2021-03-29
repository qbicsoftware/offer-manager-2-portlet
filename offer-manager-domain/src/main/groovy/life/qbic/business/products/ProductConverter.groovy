package life.qbic.business.products

import life.qbic.datamodel.dtos.business.ProductCategory
import life.qbic.datamodel.dtos.business.services.ProductUnit
import life.qbic.datamodel.dtos.business.services.Product

/**
 * Input interface for the {@link life.qbic.business.products.Converter} product converter
 *
 * @since: 1.0.0
 */
interface ProductConverter {

    /**
     * Creates a product DTO based on its products category without a version
     *
     * @param category The products category which determines what kind of products is created
     * @param description The description of the product
     * @param name The name of the product
     * @param unitPrice The unit price of the product
     * @param unit The unit in which the product is measured
     * @return
     */

    Product createProduct(ProductCategory category, String name, String description, double unitPrice, ProductUnit unit)

    /**
     * Creates a product DTO based on its products category with a version
     *
     * @param category The products category which determines what kind of products is created
     * @param description The description of the product
     * @param name The name of the product
     * @param unitPrice The unit price of the product
     * @param unit The unit in which the product is measured
     * @param runningNumber The running version number of the product
     * @return
     */

    Product createProductWithVersion(ProductCategory category, String name, String description, double unitPrice, ProductUnit unit, long runningNumber)

    /**
     * Retrieves the category of the given product
     * @param product The product of a specific product category
     * @return the product category of the given product
     */

    ProductCategory getCategory(life.qbic.datamodel.dtos.business.services.Product product)

    /**
     * Creates a product entity from the information provided in the product DTO
     * @param product A life.qbic.datamodel.dtos.business.services.Product DTO
     * @return the life.qbic.business.products.Product entity with the information provided in the original DTO
     */

    life.qbic.business.products.Product convertDTOtoProduct(life.qbic.datamodel.dtos.business.services.Product product)

    /**
     * Creates a product DTO from the information provided in the product entity
     * @param product A life.qbic.business.products.Product entity
     * @return the life.qbic.datamodel.dtos.business.services.Product DTO with the information provided in the original entity
     */

    Product convertProductToDTO(life.qbic.business.products.Product product)

}