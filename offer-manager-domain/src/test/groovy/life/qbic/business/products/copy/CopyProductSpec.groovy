package life.qbic.business.products.copy

import life.qbic.business.exceptions.DatabaseQueryException
import life.qbic.business.products.create.CreateProduct
import life.qbic.business.products.create.CreateProductDataSource
import life.qbic.business.products.create.CreateProductInput
import life.qbic.business.products.create.ProductExistsException
import life.qbic.datamodel.dtos.business.ProductCategory
import life.qbic.datamodel.dtos.business.ProductId
import life.qbic.datamodel.dtos.business.services.AtomicProduct
import life.qbic.datamodel.dtos.business.services.Product
import life.qbic.datamodel.dtos.business.services.ProductUnit
import life.qbic.datamodel.dtos.business.services.Sequencing
import org.apache.tools.ant.taskdefs.Copy
import spock.lang.IgnoreRest
import spock.lang.Shared
import spock.lang.Specification

/**
 * <h1>{@link CopyProduct} tests</h1>
 *
 * <p>This Specification contains tests for the {@link CopyProduct} use case</p>
 *
 * @since 1.0.0
 */
class CopyProductSpec extends Specification {


    CreateProductDataSource createProductDataSource = Stub(CreateProductDataSource)
    CopyProductDataSource dataSource = Stub(CopyProductDataSource)
    CopyProductOutput output = Mock(CopyProductOutput)
    Product product = new Sequencing("test product", "this is a test product", 0.5,
            ProductUnit.PER_GIGABYTE,"1")

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

    def "A duplicated entry leads to fail notification"() {
        given: "a CreateProductDataSource that throws a ProductExistsException"
        createProductDataSource.store(product) >> {throw new ProductExistsException(product.getProductId(), "Test exception")}
        and: "a copy use case with this datasource"
        CopyProduct copyProduct = new CopyProduct(dataSource, output, createProductDataSource)

        when: "copyModified is called"
        copyProduct.copyModified(product)

        then: "a fail notification is received in the output"
        1 * output.failNotification(_ as String)
        and: "no other output is registered"
        0 * output.copied(_)
        noExceptionThrown()
    }

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

}
