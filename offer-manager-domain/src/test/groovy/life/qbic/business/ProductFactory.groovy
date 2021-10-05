package life.qbic.business

import life.qbic.datamodel.dtos.business.facilities.Facility
import life.qbic.datamodel.dtos.business.services.DataStorage
import life.qbic.datamodel.dtos.business.services.ExternalServiceProduct
import life.qbic.datamodel.dtos.business.services.MetabolomicAnalysis
import life.qbic.datamodel.dtos.business.services.PrimaryAnalysis
import life.qbic.datamodel.dtos.business.services.Product
import life.qbic.datamodel.dtos.business.services.ProductUnit
import life.qbic.datamodel.dtos.business.services.ProjectManagement
import life.qbic.datamodel.dtos.business.services.ProteomicAnalysis
import life.qbic.datamodel.dtos.business.services.SecondaryAnalysis
import life.qbic.datamodel.dtos.business.services.Sequencing

/**
 * <b>Creates Products</b>
 *
 * <p>Creates products for testing purposes</p>
 *
 * @since 1.1.0
 */
class ProductFactory {
    static <T extends Product> T createProduct(Class clazz, String description, String name, double internalPrice, double externalPrice, Facility serviceProvider) {
        int runningNumber = 1
        ProductUnit productUnit = ProductUnit.PER_SAMPLE
        switch (clazz) {
            case DataStorage:
                return new DataStorage(name,
                        description,
                        internalPrice,
                        externalPrice,
                        productUnit,
                        runningNumber,
                        serviceProvider) as T
                break
            case MetabolomicAnalysis:
                return new MetabolomicAnalysis(name,
                        description,
                        internalPrice,
                        externalPrice,
                        productUnit,
                        runningNumber,
                        serviceProvider) as T
                break
            case PrimaryAnalysis:
                return new PrimaryAnalysis(name,
                        description,
                        internalPrice,
                        externalPrice,
                        productUnit,
                        runningNumber,
                        serviceProvider) as T
                break
            case ProjectManagement:
                return new ProjectManagement(name,
                        description,
                        internalPrice,
                        externalPrice,
                        productUnit,
                        runningNumber,
                        serviceProvider) as T
                break
            case ProteomicAnalysis:
                return new ProteomicAnalysis(name,
                        description,
                        internalPrice,
                        externalPrice,
                        productUnit,
                        runningNumber,
                        serviceProvider) as T
                break
            case SecondaryAnalysis:
                return new SecondaryAnalysis(name,
                        description,
                        internalPrice,
                        externalPrice,
                        productUnit,
                        runningNumber,
                        serviceProvider) as T
                break
            case Sequencing:
                return new Sequencing(name,
                        description,
                        internalPrice,
                        externalPrice,
                        productUnit,
                        runningNumber,
                        serviceProvider) as T
                break
            case ExternalServiceProduct:
                return new ExternalServiceProduct(name,
                        description,
                        internalPrice,
                        externalPrice,
                        productUnit,
                        runningNumber,
                        serviceProvider) as T
                break
        }
    }

    static <T extends Product> T createProduct(Class clazz, double internalPrice, double externalPrice) {
        int runningNumber = 1
        String name = "Test product"
        String description = "Product for testing purposes"
        Facility serviceProvider = Facility.QBIC
        ProductUnit productUnit = ProductUnit.PER_SAMPLE
        switch (clazz) {
            case DataStorage:
                return new DataStorage(name,
                        description,
                        internalPrice,
                        externalPrice,
                        productUnit,
                        runningNumber,
                        serviceProvider) as T
                break
            case MetabolomicAnalysis:
                return new MetabolomicAnalysis(name,
                        description,
                        internalPrice,
                        externalPrice,
                        productUnit,
                        runningNumber,
                        serviceProvider) as T
                break
            case PrimaryAnalysis:
                return new PrimaryAnalysis(name,
                        description,
                        internalPrice,
                        externalPrice,
                        productUnit,
                        runningNumber,
                        serviceProvider) as T
                break
            case ProjectManagement:
                return new ProjectManagement(name,
                        description,
                        internalPrice,
                        externalPrice,
                        productUnit,
                        runningNumber,
                        serviceProvider) as T
                break
            case ProteomicAnalysis:
                return new ProteomicAnalysis(name,
                        description,
                        internalPrice,
                        externalPrice,
                        productUnit,
                        runningNumber,
                        serviceProvider) as T
                break
            case SecondaryAnalysis:
                return new SecondaryAnalysis(name,
                        description,
                        internalPrice,
                        externalPrice,
                        productUnit,
                        runningNumber,
                        serviceProvider) as T
                break
            case Sequencing:
                return new Sequencing(name,
                        description,
                        internalPrice,
                        externalPrice,
                        productUnit,
                        runningNumber,
                        serviceProvider) as T
                break
            case ExternalServiceProduct:
                return new ExternalServiceProduct(name,
                        description,
                        internalPrice,
                        externalPrice,
                        productUnit,
                        runningNumber,
                        serviceProvider) as T
                break
        }
    }
}
