package life.qbic.business.products

import life.qbic.datamodel.dtos.business.ProductCategory
import life.qbic.datamodel.dtos.business.services.DataStorage
import life.qbic.datamodel.dtos.business.services.MetabolomicAnalysis
import life.qbic.datamodel.dtos.business.services.PrimaryAnalysis
import life.qbic.datamodel.dtos.business.services.Product
import life.qbic.datamodel.dtos.business.services.ProductUnit
import life.qbic.datamodel.dtos.business.services.ProjectManagement
import life.qbic.datamodel.dtos.business.services.ProteomicAnalysis
import life.qbic.datamodel.dtos.business.services.SecondaryAnalysis
import life.qbic.datamodel.dtos.business.services.Sequencing

/**
 * <h1>Converter for {@link life.qbic.datamodel.dtos.business.services.Product}</h1>
 * <br>
 * <p>Converts a product into its respective type e.g. {@link life.qbic.datamodel.dtos.business.services.Sequencing},
 * {@link life.qbic.datamodel.dtos.business.services.ProjectManagement},..</p>
 *
 * @since 1.0.0
 *
*/
class Converter implements ProductConverter {

    /**
     * {@inheritDoc}
     */
    @Override
    Product createProduct(ProductCategory category, String name, String description, double unitPrice, ProductUnit unit){
        long runningNumber = 0 //todo it should be possible to create products without a running number
        return createProductWithVersion(category,name,description,unitPrice,unit,runningNumber)
    }

    /**
     * {@inheritDoc}
     */
    @Override
    Product createProductWithVersion(ProductCategory category, String name, String description, double unitPrice, ProductUnit unit, long runningNumber){
        Product product
        switch (category) {
            case "DATA_STORAGE":
                product = new DataStorage(name, description, unitPrice,unit, runningNumber.toString())
                break
            case "PRIMARY_BIOINFO":
                product = new PrimaryAnalysis(name, description, unitPrice,unit, runningNumber.toString())
                break
            case "PROJECT_MANAGEMENT":
                product = new ProjectManagement(name, description, unitPrice,unit, runningNumber.toString())
                break
            case "SECONDARY_BIOINFO":
                product = new SecondaryAnalysis(name, description, unitPrice,unit, runningNumber.toString())
                break
            case "SEQUENCING":
                product = new Sequencing(name, description, unitPrice,unit, runningNumber.toString())
                break
            case "PROTEOMIC":
                product = new ProteomicAnalysis(name, description, unitPrice,unit, runningNumber.toString())
                break
            case "METABOLOMIC":
                product = new MetabolomicAnalysis(name, description, unitPrice,unit, runningNumber.toString())
                break
        }
        if(!product) throw new IllegalArgumentException("Cannot parse product")
        return product
    }

    /**
     * {@inheritDoc}
     */
    @Override
    ProductCategory getCategory(Product product){
        if(product instanceof ProjectManagement) return ProductCategory.PROJECT_MANAGEMENT
        if(product instanceof Sequencing) return ProductCategory.SEQUENCING
        if(product instanceof PrimaryAnalysis) return ProductCategory.PRIMARY_BIOINFO
        if(product instanceof SecondaryAnalysis) return ProductCategory.SECONDARY_BIOINFO
        if(product instanceof DataStorage) return ProductCategory.DATA_STORAGE
        if(product instanceof ProteomicAnalysis) return ProductCategory.PROTEOMIC
        if(product instanceof MetabolomicAnalysis) return ProductCategory.METABOLOMIC

        throw  new IllegalArgumentException("Cannot parse category of the provided product ${product.toString()}")
    }

    /**
     * {@inheritDoc}
     */
    @Override
    life.qbic.business.products.Product convertDTOtoProduct(Product product){
        ProductCategory category = getCategory(product)
        return new life.qbic.business.products.Product.Builder(category,
                                                                product.productName,
                                                                product.description,
                                                                product.unitPrice,
                                                                product.unit)
                                                                .build()

    }

    /**
     * {@inheritDoc}
     */
    @Override
    Product convertProductToDTO(life.qbic.business.products.Product product){
        return createProductWithVersion(product.category,product.name, product.description, product.unitPrice, product.unit, product.id.uniqueId)
    }
}
