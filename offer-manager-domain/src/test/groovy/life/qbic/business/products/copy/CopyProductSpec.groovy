package life.qbic.business.products.copy

import life.qbic.business.exceptions.DatabaseQueryException
import life.qbic.business.products.create.CreateProduct
import life.qbic.business.products.create.CreateProductDataSource
import life.qbic.business.products.create.CreateProductInput
import life.qbic.business.products.create.ProductExistsException
import life.qbic.datamodel.dtos.business.ProductId
import life.qbic.datamodel.dtos.business.services.AtomicProduct
import life.qbic.datamodel.dtos.business.services.Product
import life.qbic.datamodel.dtos.business.services.ProductUnit
import org.apache.tools.ant.taskdefs.Copy
import spock.lang.IgnoreRest
import spock.lang.Shared
import spock.lang.Specification

/**
 * <h1>Archive Product tests</h1>
 *
 * <p>This Specification contains tests for the use ArchiveProduct use case</p>
 *
 * @since 1.0.0
 */
class CopyProductSpec extends Specification {


    @Shared CreateProductDataSource createProductDataSource = Stub(CreateProductDataSource)
    @Shared CopyProductDataSource dataSource = Stub(CopyProductDataSource)
    @Shared CopyProductOutput output = Mock(CopyProductOutput)
    @Shared Product product = new AtomicProduct("test product", "this is a test product", 0.5, ProductUnit.PER_GIGABYTE, new ProductId("Test", "ABCD1234"))

    def "FailNotification forwards received messages to the output"() {
        given: "a CreateProductDataSource that throws a DatabaseQueryException"
        createProductDataSource.store(product) >> {throw new DatabaseQueryException("Test exception")}
        and: "a copy use case with this datasource"
        CopyProduct copyProduct = new CopyProduct(dataSource, output, createProductDataSource)

        when: "copyModified is called"
        copyProduct.copyModified(product)

        then: "a fail notification is received in the output"
        1 * output.failNotification(_ as String)
        and: "no output is registered"
        0 * output.copied(_)
        noExceptionThrown()
    }

    def "a duplicated entry leads to fail notification"() {
        given: "a CreateProductDataSource that throws a ProductExistsException"
        createProductDataSource.store(product) >> {throw new ProductExistsException(product.getProductId(), "Test exception")}
        and: "a copy use case with this datasource"
        CopyProduct copyProduct = new CopyProduct(dataSource, output, createProductDataSource)

        when: "copyModified is called"
        copyProduct.copyModified(product)

        then: "a fail notification is recieved in the output"
        1 * output.failNotification(_ as String)
        and: "no other output is registered"
        0 * output.copied(_)
        noExceptionThrown()
    }

    @IgnoreRest
    def "CopyModified rejects non existent products"() {
        given: "A product that is not in the database"
        dataSource.fetch(product.getProductId()) >> Optional<Product>.empty()
        and: "a copy use case"
        CopyProduct copyProduct = new CopyProduct(dataSource, output, createProductDataSource)

        when: "copyModified is called with the unknown product"
        copyProduct.copyModified(product)

        then: "fail notification is created"
        1 * output.failNotification(_ as String)
        and: "no other output is registered"
        0 * output.copied(_)
        noExceptionThrown()
    }

    def "CopyModified creates a new product with the provided information and a new identifier"() {
        given: "a data source that finds the product"
        dataSource.fetch(product.getProductId()) >> product
        and: "a CreateProductDataSource that stores all products except the found one"
        createProductDataSource.store(product) >> {throw new ProductExistsException(product.getProductId(), "Tried to store exact copy")}
        and: "a copy use case"
        CopyProduct copyProduct = new CopyProduct(dataSource, output, createProductDataSource)

        when: "copyModified is called with the product"
        copyProduct.copyModified(product)

        then: "the product is copied with a new id"
        1 * output.copied(_ as Product)
        //todo how to verify that the information is correct?
        and: "no other output method is called"
        0 * output.failNotification(_)
        noExceptionThrown()
    }



}
