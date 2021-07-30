package life.qbic.business.products

import life.qbic.business.logging.Logger
import life.qbic.business.logging.Logging
import life.qbic.datamodel.dtos.business.services.Product
import life.qbic.datamodel.dtos.business.ProductCategory
import life.qbic.datamodel.dtos.business.facilities.Facility
import life.qbic.datamodel.dtos.business.services.*

/**
 * <h1>Converter for {@link life.qbic.datamodel.dtos.business.services.Product}</h1>
 * <br>
 * <p>Converts a product into its respective type e.g. {@link life.qbic.datamodel.dtos.business.services.Sequencing},
 * {@link life.qbic.datamodel.dtos.business.services.ProjectManagement},..</p>
 *
 * @since 1.0.0
 *
 */
class Converter {

    private static final Logging log = Logger.getLogger(this.class)

    /**
     * Creates a product DTO based on its products category without a version
     *
     * @param category The products category which determines what kind of products is created
     * @param description The description of the product
     * @param name The name of the product
     * @param internalUnitPrice The unit price for internal customers
     * @param externalUnitPrice The unit price for external customers
     * @param unit The unit in which the product is measured
     * @param serviceProvider The serviceProvider providing the product
     * @return a new product
     * @since 1.1.0
     */
    static Product createProduct(ProductCategory category, String name, String description, double internalUnitPrice, double externalUnitPrice, ProductUnit unit, Facility serviceProvider){
        long runningNumber = 0 //todo it should be possible to create products without a running number
        return createProductWithVersion(category, name, description, internalUnitPrice, externalUnitPrice, unit, runningNumber, serviceProvider)
    }

    /**
     * Creates a product DTO based on its products category with a version
     *
     * @param category The products category which determines what kind of products is created
     * @param description The description of the product
     * @param name The name of the product
     * @param internalUnitPrice The unit price of the product
     * @param unit The unit in which the product is measured
     * @param runningNumber The running version number of the product
     * @param serviceProvider The serviceProvider providing the product
     * @return a product
     * @since 1.1.0
     */
    static Product createProductWithVersion(ProductCategory category, String name, String description, double internalUnitPrice, double externalUnitPrice, ProductUnit unit, long runningNumber, Facility serviceProvider){
        Product product = null
        switch (category) {
            case ProductCategory.DATA_STORAGE:
                product = new DataStorage(name, description, internalUnitPrice, externalUnitPrice, unit, runningNumber, serviceProvider)
                break
            case ProductCategory.PRIMARY_BIOINFO:
                product = new PrimaryAnalysis(name, description, internalUnitPrice, externalUnitPrice, unit, runningNumber, serviceProvider)
                break
            case ProductCategory.PROJECT_MANAGEMENT:
                product = new ProjectManagement(name, description, internalUnitPrice, externalUnitPrice, unit, runningNumber, serviceProvider)
                break
            case ProductCategory.SECONDARY_BIOINFO:
                product = new SecondaryAnalysis(name, description, internalUnitPrice, externalUnitPrice, unit, runningNumber, serviceProvider)
                break
            case ProductCategory.SEQUENCING:
                product = new Sequencing(name, description, internalUnitPrice, externalUnitPrice, unit, runningNumber, serviceProvider)
                break
            case ProductCategory.PROTEOMIC:
                product = new ProteomicAnalysis(name, description, internalUnitPrice, externalUnitPrice, unit, runningNumber, serviceProvider)
                break
            case ProductCategory.METABOLOMIC:
                product = new MetabolomicAnalysis(name, description, internalUnitPrice, externalUnitPrice, unit, runningNumber, serviceProvider)
                break
            default:
                log.warn("Unknown product category $category")
        }
        if(!product) throw new IllegalArgumentException("Cannot parse product")
        return product
    }

    /**
     * Retrieves the category of the given product
     * @param product The product of a specific product category
     * @return the product category of the given product
     */
    static ProductCategory getCategory(Product product){
        if(product instanceof ProjectManagement) return ProductCategory.PROJECT_MANAGEMENT
        if(product instanceof Sequencing) return ProductCategory.SEQUENCING
        if(product instanceof PrimaryAnalysis) return ProductCategory.PRIMARY_BIOINFO
        if(product instanceof SecondaryAnalysis) return ProductCategory.SECONDARY_BIOINFO
        if(product instanceof DataStorage) return ProductCategory.DATA_STORAGE
        if(product instanceof ProteomicAnalysis) return ProductCategory.PROTEOMIC
        if(product instanceof MetabolomicAnalysis) return ProductCategory.METABOLOMIC

        throw  new IllegalArgumentException("Cannot parse category of the provided product ${product.toString()}")
    }

    static life.qbic.business.products.Product convertDTOtoProduct(Product product){
        ProductCategory category = getCategory(product)
        return new life.qbic.business.products.Product.Builder(category,
                    product.productName,
                    product.description,
                    product.internalUnitPrice,
                    product.externalUnitPrice,
                    product.unit,
                    product.serviceProvider)
                    .build()

    }

    static Product convertProductToDTO(life.qbic.business.products.Product product){
        return createProductWithVersion(product.category,product.name, product.description, product.internalUnitPrice, product.externalUnitPrice, product.unit, product.id.uniqueId, product.serviceProvider)
    }
}
