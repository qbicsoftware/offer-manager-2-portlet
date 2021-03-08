package life.qbic.business.products.archive

import life.qbic.business.exceptions.DatabaseQueryException
import life.qbic.datamodel.dtos.business.ProductId
import life.qbic.datamodel.dtos.business.services.Product
import life.qbic.datamodel.dtos.business.services.ProductUnit
import spock.lang.Specification

/**
 * <h1>Archive Product tests</h1>
 *
 * <p>This Specification contains tests for the use ArchiveProduct use case</p>
 *
 * @since 1.0.0
 */
class ArchiveProductSpec extends Specification {

    def "archive provides the product from the datasource to the output"() {

        given: "a product identifier and associated product"
        ProductId productId = new ProductId("Test", "Test1234")
        Product product = new Product("Test", "Test Description", 0, ProductUnit.PER_SAMPLE, productId){}

        and: "an output and datasource that returns the product for a given productId"
        ArchiveProductDataSource dataSource = Stub()
        ArchiveProductOutput output = Mock()
        dataSource.fetch(productId) >> Optional.of(product)

        and: "an instance of the ArchiveProduct use case"
        ArchiveProduct archiveProduct = new ArchiveProduct(dataSource, output)

        when: "the use case archives with the productId"
        archiveProduct.archive(productId)

        then: "the output will receive the product"
        1 * output.archived(product)
    }

    def "archive provides fail notification when no product was found "() {

        given: "a product identifier"
        ProductId productId = new ProductId("Test", "Test1234")

        and: "an output and datasource that cannot find the id"
        ArchiveProductDataSource dataSource = Stub()
        ArchiveProductOutput output = Mock()
        dataSource.fetch(productId) >> Optional.empty()

        and: "an instance of the ArchiveProduct use case"
        ArchiveProduct archiveProduct = new ArchiveProduct(dataSource, output)

        when: "the use case archives with the productId"
        archiveProduct.archive(productId)

        then: "the output will receive a failure notification"
        1 * output.failNotification(_ as String)
    }

    def "archive product outputs a fail notification in case the database query fails for technical reasons"() {
        given: "a product identifier and associated product"
        ProductId productId = new ProductId("Test", "Test1234")
        Product product = new Product("Test", "Test Description", 0, ProductUnit.PER_SAMPLE, productId){}

        and: "an output and datasource that fails for technical reasons"
        ArchiveProductDataSource dataSource = Stub()
        ArchiveProductOutput output = Mock()
        dataSource.fetch(productId) >> { throw new DatabaseQueryException("Something went wrong") }

        and: "the use case under test"
        ArchiveProduct archiveProduct = new ArchiveProduct(dataSource, output)

        when: "an instance of the ArchiveProduct use case"
        archiveProduct.archive(productId)

        then: "the output will receive a failure notification"
        1 * output.failNotification(_ as String)
    }
}
