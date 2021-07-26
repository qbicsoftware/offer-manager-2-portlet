package life.qbic.business.products

import life.qbic.business.logging.Logger
import life.qbic.business.logging.Logging
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
     * @param unitPrice The unit price of the product
     * @param unit The unit in which the product is measured
     * @return a product
     * @deprecated please us {@link life.qbic.business.products.Converter#createProduct(life.qbic.datamodel.dtos.business.ProductCategory, java.lang.String, java.lang.String, double, double, life.qbic.datamodel.dtos.business.services.ProductUnit, life.qbic.datamodel.dtos.business.facilities.Facility)} instead
     */
    @Deprecated
    static Product createProduct(ProductCategory category, String name, String description, double unitPrice, ProductUnit unit){
        long runningNumber = 0 //todo it should be possible to create products without a running number
        return createProductWithVersion(category,name,description,unitPrice,unit,runningNumber)
    }

    /**
     * Creates a product DTO based on its products category without a version
     *
     * @param category The products category which determines what kind of products is created
     * @param description The description of the product
     * @param name The name of the product
     * @param internalUnitPrice The unit price for internal customers
     * @param externalUnitPrice The unit price for external customers
     * @param unit The unit in which the product is measured
     * @param facility The facility providing the product
     * @return a new product
     * @since 1.1.0
     */
    static Product createProduct(ProductCategory category, String name, String description, double internalUnitPrice, double externalUnitPrice, ProductUnit unit, Facility facility){
        long runningNumber = 0 //todo it should be possible to create products without a running number
        return createProductWithVersion(category, name, description, internalUnitPrice, externalUnitPrice, unit, runningNumber, facility)
    }

    /**
     * Creates a product DTO based on its products category with a version
     *
     * @param category The products category which determines what kind of products is created
     * @param description The description of the product
     * @param name The name of the product
     * @param unitPrice The unit price of the product
     * @param unit The unit in which the product is measured
     * @param runningNumber The running version number of the product
     * @return a product
     * @deprecated please use {@link life.qbic.business.products.Converter#createProductWithVersion(life.qbic.datamodel.dtos.business.ProductCategory, java.lang.String, java.lang.String, double, double, life.qbic.datamodel.dtos.business.services.ProductUnit, long, life.qbic.datamodel.dtos.business.facilities.Facility) } instead
     */
    @Deprecated
    static Product createProductWithVersion(ProductCategory category, String name, String description, double unitPrice, ProductUnit unit, long runningNumber){
        Product product = null
        switch (category) {
            case ProductCategory.DATA_STORAGE:
                product = new DataStorage(name, description, unitPrice,unit, runningNumber.toString())
                break
            case ProductCategory.PRIMARY_BIOINFO:
                product = new PrimaryAnalysis(name, description, unitPrice,unit, runningNumber.toString())
                break
            case ProductCategory.PROJECT_MANAGEMENT:
                product = new ProjectManagement(name, description, unitPrice,unit, runningNumber.toString())
                break
            case ProductCategory.SECONDARY_BIOINFO:
                product = new SecondaryAnalysis(name, description, unitPrice,unit, runningNumber.toString())
                break
            case ProductCategory.SEQUENCING:
                product = new Sequencing(name, description, unitPrice,unit, runningNumber.toString())
                break
            case ProductCategory.PROTEOMIC:
                product = new ProteomicAnalysis(name, description, unitPrice,unit, runningNumber.toString())
                break
            case ProductCategory.METABOLOMIC:
                product = new MetabolomicAnalysis(name, description, unitPrice,unit, runningNumber.toString())
                break
            default:
                log.warn("Unknown product category $category")
        }
        if(!product) throw new IllegalArgumentException("Cannot parse product")
        return product
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
     * @param facility The facility providing the product
     * @return a product
     * @since 1.1.0
     */
    static Product createProductWithVersion(ProductCategory category, String name, String description, double internalUnitPrice, double externalUnitPrice, ProductUnit unit, long runningNumber, Facility facility){
        Product product = null
        switch (category) {
            case ProductCategory.DATA_STORAGE:
                product = new DataStorage(name, description, internalUnitPrice, externalUnitPrice, unit, runningNumber, facility)
                break
            case ProductCategory.PRIMARY_BIOINFO:
                product = new PrimaryAnalysis(name, description, internalUnitPrice, externalUnitPrice, unit, runningNumber, facility)
                break
            case ProductCategory.PROJECT_MANAGEMENT:
                product = new ProjectManagement(name, description, internalUnitPrice, externalUnitPrice, unit, runningNumber, facility)
                break
            case ProductCategory.SECONDARY_BIOINFO:
                product = new SecondaryAnalysis(name, description, internalUnitPrice, externalUnitPrice, unit, runningNumber, facility)
                break
            case ProductCategory.SEQUENCING:
                product = new Sequencing(name, description, internalUnitPrice, externalUnitPrice, unit, runningNumber, facility)
                break
            case ProductCategory.PROTEOMIC:
                product = new ProteomicAnalysis(name, description, internalUnitPrice, externalUnitPrice, unit, runningNumber, facility)
                break
            case ProductCategory.METABOLOMIC:
                product = new MetabolomicAnalysis(name, description, internalUnitPrice, externalUnitPrice, unit, runningNumber, facility)
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
        if (product.internalUnitPrice == 0 && product.externalUnitPrice == 0 && product.unitPrice != 0) { // we used an old constructor
            return new life.qbic.business.products.Product.Builder(category,
                    product.productName,
                    product.description,
                    product.unitPrice,
                    product.unit)
                    .build()
        } else if ((product.internalUnitPrice != 0 || product.externalUnitPrice != 0) && product.unitPrice == 0) { // we used the new constructor
            return new life.qbic.business.products.Product.Builder(category,
                    product.productName,
                    product.description,
                    product.internalUnitPrice,
                    product.externalUnitPrice,
                    product.unit,
                    product.serviceProvider)
                    .build()
        } else if (product.internalUnitPrice == 0 && product.externalUnitPrice == 0 && product.unitPrice == 0) { // we cannot determine which product version this is
            // we use the new product version
            return new life.qbic.business.products.Product.Builder(category,
                    product.productName,
                    product.description,
                    product.internalUnitPrice,
                    product.externalUnitPrice,
                    product.unit,
                    product.serviceProvider)
                    .build()
        }

    }

    static Product convertProductToDTO(life.qbic.business.products.Product product){
        Product result

        if (product.internalUnitPrice == 0 && product.externalUnitPrice == 0 && product.unitPrice != 0) { // we used an old constructor
            result = createProductWithVersion(product.category,product.name, product.description, product.unitPrice, product.unit, product.id.uniqueId)
        } else if ((product.internalUnitPrice != 0 || product.externalUnitPrice != 0) && product.unitPrice == 0) { // we used the new constructor
            result = createProductWithVersion(product.category,product.name, product.description, product.internalUnitPrice, product.externalUnitPrice, product.unit, product.id.uniqueId, product.facility)
        } else { // we cannot determine which product version this is
            result = createProductWithVersion(product.category,product.name, product.description, product.internalUnitPrice, product.externalUnitPrice, product.unit, product.id.uniqueId, product.facility)
        }
        return result
    }
}
