package life.qbic.business.products.copy

import life.qbic.business.exceptions.DatabaseQueryException
import life.qbic.business.products.create.CreateProduct
import life.qbic.business.products.create.CreateProductInput
import life.qbic.datamodel.dtos.business.ProductId
import life.qbic.datamodel.dtos.business.services.AtomicProduct
import life.qbic.datamodel.dtos.business.services.Product
import life.qbic.datamodel.dtos.business.services.ProductUnit
import org.apache.tools.ant.taskdefs.Copy
import spock.lang.Specification

/**
 * <h1>Archive Product tests</h1>
 *
 * <p>This Specification contains tests for the use ArchiveProduct use case</p>
 *
 * @since 1.0.0
 */
class CopyProductSpec extends Specification {
    CopyProductDataSource dataSource
    CopyProductOutput output
    ProductId productId
    Product product
    CreateProductInput createProduct

    def setup() {
        dataSource = Stub(CopyProductDataSource)
        output = Mock(CopyProductOutput)
        productId = new ProductId("Test", "ABCD1234")
        product = new AtomicProduct("Test atomic item", "This is a test item", 0.5, ProductUnit.PER_SAMPLE, productId)
        createProduct = Mock(CreateProductInput)
    }

    def "CopyModified forwards a modified of the product to the output"() {
        given: "a use case instance"
        CopyProduct copyProduct = new CopyProduct(dataSource, output)

        when: "the copy method is called"
        copyProduct.copyModified(productId)

        then: "the output receives the provided product and no fails"
        1 * output.copied(product)
        0 * output.failNotification(_)
    }

    def "Copy sends a fail notification if there is no product with the provided id"() {
        given: "a data source that returns no entries for the given id"
        dataSource.fetch(productId) >> Optional.empty()
        and: "a use case instance"
        CopyProduct copyProduct = new CopyProduct(dataSource, output)
        when: "the copy method is called"
        copyProduct.copy(productId)
        then: "the use case sends a failure notification"
        1 * output.failNotification(_ as String)
        0 * output.copied(_)
    }

    def "Copy sends a fail notification if the database fails for technical reasons"(){
        given: "a data source that throws a $DatabaseQueryException"
        dataSource.fetch(productId) >> {throw new DatabaseQueryException("This is a test")}
        and: "a use case instance"
        CopyProduct copyProduct = new CopyProduct(dataSource, output)
        when: "the copy method is called"
        copyProduct.copy(productId)
        then: "the use case sends a failure notification"
        1 * output.failNotification(_ as String)
        0 * output.copied(_)
    }
}
