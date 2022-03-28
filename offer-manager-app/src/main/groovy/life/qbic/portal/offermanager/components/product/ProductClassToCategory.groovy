package life.qbic.portal.offermanager.components.product


import life.qbic.datamodel.dtos.business.ProductCategory
import life.qbic.datamodel.dtos.business.services.*

import java.util.function.Function

/**
 * <h1>ProductClassToCategory for {@link life.qbic.datamodel.dtos.business.services.Product}</h1>
 * <br>
 * <p>Converts a product into its respective type e.g. {@link life.qbic.datamodel.dtos.business.services.Sequencing},
 * {@link life.qbic.datamodel.dtos.business.services.ProjectManagement},..</p>
 *
 * @since 1.0.0
 *
 */
class ProductClassToCategory implements Function<Class<? extends Product>, ProductCategory> {

    /**
     * Retrieves the category of the given productClazz
     * @param productClazz The productClazz of a specific productClazz category
     * @return the productClazz category of the given productClazz
     */
    static ProductCategory getCategory(Class<? extends Product> productClazz) {
        String canonicalName = productClazz.getCanonicalName()

        if (canonicalName == ProjectManagement.getCanonicalName()) return ProductCategory.PROJECT_MANAGEMENT
        if (canonicalName == Sequencing.getCanonicalName()) return ProductCategory.SEQUENCING
        if (canonicalName == PrimaryAnalysis.getCanonicalName()) return ProductCategory.PRIMARY_BIOINFO
        if (canonicalName == SecondaryAnalysis.getCanonicalName()) return ProductCategory.SECONDARY_BIOINFO
        if (canonicalName == DataStorage.getCanonicalName()) return ProductCategory.DATA_STORAGE
        if (canonicalName == ProteomicAnalysis.getCanonicalName()) return ProductCategory.PROTEOMIC
        if (canonicalName == MetabolomicAnalysis.getCanonicalName()) return ProductCategory.METABOLOMIC
        if (canonicalName == ExternalServiceProduct.getCanonicalName()) return ProductCategory.EXTERNAL_SERVICE

        throw new IllegalArgumentException("Cannot parse category of the provided productClazz ${productClazz.toString()}")
    }

    @Override
    ProductCategory apply(Class<? extends Product> aClass) {
        return getCategory(aClass)
    }
}
