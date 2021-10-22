package life.qbic.business.products.copy

import life.qbic.business.exceptions.DatabaseQueryException
import life.qbic.business.products.create.CreateProductDataSource
import life.qbic.business.products.create.ProductExistsException
import life.qbic.business.products.dtos.ProductDraft
import life.qbic.datamodel.dtos.business.ProductCategory
import life.qbic.datamodel.dtos.business.ProductCategoryFactory
import life.qbic.datamodel.dtos.business.ProductId
import life.qbic.datamodel.dtos.business.facilities.Facility
import life.qbic.datamodel.dtos.business.services.Product
import life.qbic.datamodel.dtos.business.services.ProductUnit
import life.qbic.datamodel.dtos.business.services.Sequencing
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
    ProductCategoryFactory productCategoryFactory = new ProductCategoryFactory()
    ProductCategory category = productCategoryFactory.getForString("Sequencing")
    ProductId originalProductId = new ProductId("SE", "2")
    ProductId createdProductId = new ProductId("SE", "3")
    ProductDraft productDraft = ProductDraft.create(category, "test product", "this is a test product", 0.5, 0.5, ProductUnit.PER_GIGABYTE, Facility.CEGAT)
    Product differentProduct = new Sequencing("Different Product", "this is a test product with different content", 0.5, 0.5,
            ProductUnit.PER_GIGABYTE, 1, Facility.CEGAT)
    Product duplicateProduct = new Sequencing("test product", "this is a test product", 0.5, 0.5,
            ProductUnit.PER_GIGABYTE, 1, Facility.CEGAT)


    def "FailNotification forwards received messages to the output"() {
        given: "a CreateProductDataSource that throws a DatabaseQueryException"
        dataSource.fetch(originalProductId) >> { Optional.of(differentProduct) }
        createProductDataSource.store(productDraft) >> { throw new DatabaseQueryException("Test exception") }
        and: "a copy use case with this datasource"
        CopyProduct copyProduct = new CopyProduct(dataSource, output, createProductDataSource)

        when: "copyModified is called"
        copyProduct.copyModified(productDraft, originalProductId)

        then: "a fail notification is received in the output"
        1 * output.failNotification(_ as String)
        and: "no output is registered"
        0 * output.copied(_)
        noExceptionThrown()
    }

    def "CopyProduct copies a Product that differs from the original"() {
        given: "a CreateProductDataSource that stores a product"
        dataSource.fetch(originalProductId) >> { Optional.of(differentProduct) }
        createProductDataSource.store(productDraft) >> { createdProductId }
        and: "a copy use case with this datasource"
        CopyProduct copyProduct = new CopyProduct(dataSource, output, createProductDataSource)

        when: "copyModified is called"
        copyProduct.copyModified(productDraft, originalProductId)

        then: "The copied product is stored in the database"
        1 * output.copied(_)
        and: "no exception is thrown"
        noExceptionThrown()
    }

    def "A duplicated entry leads to fail notification"() {
        given: "a CreateProductDataSource that throws a ProductExistsException"
        dataSource.fetch(originalProductId) >> { Optional.of(duplicateProduct) }
        createProductDataSource.store(productDraft) >> { throw new ProductExistsException(duplicateProduct.getProductId(), "Test exception") }
        and: "a copy use case with this datasource"
        CopyProduct copyProduct = new CopyProduct(dataSource, output, createProductDataSource)

        when: "copyModified is called"
        copyProduct.copyModified(productDraft, originalProductId)

        then: "a fail notification is received in the output"
        1 * output.failNotification(_ as String)
        and: "no other output is registered"
        0 * output.copied(_)
        noExceptionThrown()
    }

    def "CopyModified rejects non existent products"() {
        given: "A product that is not in the database"
        dataSource.fetch(originalProductId) >> { Optional.empty() }
        dataSource.fetch(differentProduct.getProductId()) >> Optional<Product>.empty()
        and: "a copy use case"
        CopyProduct copyProduct = new CopyProduct(dataSource, output, createProductDataSource)

        when: "copyModified is called with the unknown product"
        copyProduct.copyModified(productDraft, originalProductId)

        then: "fail notification is created"
        1 * output.failNotification(_ as String)

        and: "no other output is registered"
        0 * output.copied(_)
        noExceptionThrown()
    }

}
