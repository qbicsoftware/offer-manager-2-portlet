package life.qbic.business.products.copy

import life.qbic.business.exceptions.DatabaseQueryException
import life.qbic.business.products.create.CreateProduct
import life.qbic.business.products.create.CreateProductDataSource
import life.qbic.business.products.create.CreateProductInput
import life.qbic.datamodel.dtos.business.ProductId
import life.qbic.datamodel.dtos.business.services.AtomicProduct
import life.qbic.datamodel.dtos.business.services.Product
import life.qbic.datamodel.dtos.business.services.ProductUnit
import org.apache.tools.ant.taskdefs.Copy
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
    @Shared ProductId productId = new ProductId("Test", "ABCD1234")
    @Shared Product product = new AtomicProduct("test product", "this is a test product", 0.5, ProductUnit.PER_GIGABYTE, productId)

    def "FailNotification forwards received messages to the output"() {
        given: "a CreateProductDataSource that throws and exception"
        createProductDataSource.store(_ as Product) >> {throw new DatabaseQueryException("Test exception")}
        and: "a copy use case with this datasource"
        CopyProduct useCase = new CopyProduct(dataSource, output, createProductDataSource)

        when: "a modified product is copied"
        useCase.copyModified(product)

        then: "a fail notification is received"
        1 * output.failNotification(_ as String)
        and: "no output is registered"
        0 * output.copied(_)
        noExceptionThrown()

    }

    def "Created notification leads to Copied call"() {
    }

    def "FoundDuplicate is ignored and does not cause any action in the output"() {
    }

    def "CopyModified rejects non existent products"() {
    }

    def "CopyModified creates a new product with the provided information and a new identifier"() {
    }



}
