package life.qbic.business.products.create

import life.qbic.datamodel.dtos.business.ProductId
import life.qbic.datamodel.dtos.business.services.AtomicProduct
import life.qbic.datamodel.dtos.business.services.Product
import spock.lang.Specification

/**
 * <h1>Tests for the CreateProduct use case</h1>
 *
 * <p>This specification contains tests for all steps of the create product use case</p>
 *
 * @since 1.0.0
 */
class CreateProductSpec extends Specification {
    CreateProductDataSource dataSource
    CreateProductOutput output
    ProductId productId
    Product product

    def setup() {
        dataSource = Stub(CreateProductDataSource)
        output = Mock(CreateProductOutput)
        productId = new ProductId("Test", "ABCD1234")
        product = new AtomicProduct("Test atomic item", "This is a test item", 0.5, ProductUnit.PER_SAMPLE, productId)
    }

    def "Create stores the provided product in the data source"() {
    }

    def "Create informs the output that an entry matching the provided product already exists"() {
    }

    def "Create sends a failure notification in case technical errors occur at the data source"() {
    }

    def "CreateDuplicate stores the provided product in the data source if no duplicate was found"() {
    }

    def "CreateDuplicate stores the provided product in the data source using a new product identifier"() {
    }

    def "CreateDuplicate sends a failure notification in case technical errors occur at the data source"() {
    }
}
