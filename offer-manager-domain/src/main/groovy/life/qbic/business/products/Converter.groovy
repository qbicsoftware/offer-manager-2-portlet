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
class Converter {

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
    static Product createProduct(ProductCategory category, String name, String description, double unitPrice, ProductUnit unit){
        String runningNumber = "0"
        return createProductWithVersion(category,name,description,unitPrice,unit,runningNumber)
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
     * @return
     */
    static Product createProductWithVersion(ProductCategory category, String name, String description, double unitPrice, ProductUnit unit, String runningNumber){
        Product product
        switch (category) {
            case "DATA_STORAGE":
                product = new DataStorage(name, description, unitPrice,unit, runningNumber)
                break
            case "PRIMARY_BIOINFO":
                product = new PrimaryAnalysis(name, description, unitPrice,unit, runningNumber)
                break
            case "PROJECT_MANAGEMENT":
                product = new ProjectManagement(name, description, unitPrice,unit, runningNumber)
                break
            case "SECONDARY_BIOINFO":
                product = new SecondaryAnalysis(name, description, unitPrice,unit, runningNumber)
                break
            case "SEQUENCING":
                product = new Sequencing(name, description, unitPrice,unit, runningNumber)
                break
            case "PROTEOMIC":
                product = new ProteomicAnalysis(name, description, unitPrice,unit, runningNumber)
                break
            case "METABOLOMIC":
                product = new MetabolomicAnalysis(name, description, unitPrice,unit, runningNumber)
                break
        }
        if(!product) throw new IllegalArgumentException("Cannot parse products")
        return product
    }
}
