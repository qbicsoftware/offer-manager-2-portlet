package life.qbic.portal.qoffer2.products

import life.qbic.datamodel.dtos.business.services.PrimaryAnalysis
import life.qbic.datamodel.dtos.business.services.Product
import life.qbic.datamodel.dtos.business.services.ProductUnit
import life.qbic.datamodel.dtos.business.services.Sequencing
import life.qbic.portal.offermanager.dataresources.database.ConnectionProvider
import life.qbic.portal.offermanager.dataresources.products.ProductsDbConnector
import life.qbic.portal.offermanager.dataresources.products.ProductsResourcesService
import spock.lang.Specification

import java.sql.Connection

/**
 * <h1>Tests the functionality of the {@link life.qbic.portal.offermanager.dataresources.ResourcesService} of products {@link life.qbic.portal.offermanager.dataresources.products.ProductsResourcesService}</h1>
 *
 * @since 1.0.0
 *
*/
class ProductResourceServiceSpec extends Specification{


    def "Products can be removed from the list"(){
        given: "a list of products"
        Sequencing sequencing = new Sequencing("test product", "this is a test sequencing product", 0.5, ProductUnit.PER_GIGABYTE, "123")
        PrimaryAnalysis primaryAnalysis = new PrimaryAnalysis("test product", "this is a test analysis product", 0.5, ProductUnit.PER_GIGABYTE, "123")

        PrimaryAnalysis primaryAnalysisCopy = new PrimaryAnalysis("test product", "this is a test analysis product", 0.5, ProductUnit.PER_GIGABYTE, "123")

        and: "the database session is mocked"
        // the connection must only provide precompiled statements for the expected query template
        Connection connection = Stub( Connection)

        //and: "a ConnectionProvider providing the stubbed connection"
        ConnectionProvider connectionProvider = Stub (ConnectionProvider, {it.connect() >> connection})

        and: "a resource service"
        ProductsResourcesService resourcesService = new ProductsResourcesService(new ProductsDbConnector(connectionProvider))

        when: "a product is removed"
        resourcesService.addToResource(sequencing)
        resourcesService.addToResource(primaryAnalysis)
        resourcesService.removeFromResource(primaryAnalysisCopy)

        then: "the list does not longer contain the removed product"
        resourcesService.iterator().toList().size() == 1
    }
}
