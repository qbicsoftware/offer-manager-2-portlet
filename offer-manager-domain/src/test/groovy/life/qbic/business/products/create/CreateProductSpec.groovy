package life.qbic.business.products.create

import life.qbic.business.exceptions.DatabaseQueryException
import life.qbic.datamodel.dtos.business.ProductId
import life.qbic.datamodel.dtos.business.services.AtomicProduct
import life.qbic.datamodel.dtos.business.services.Product
import life.qbic.datamodel.dtos.business.services.ProductUnit
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
        dataSource = Mock(CreateProductDataSource)
        output = Mock(CreateProductOutput)
        productId = new ProductId("Test", "ABCD1234")
        product = new AtomicProduct("test product", "this is a test product", 0.5, ProductUnit.PER_GIGABYTE, productId)
    }

    def "Create stores the provided product in the data source"() {
        given: "a data source that stores a product"
        dataSource.store(product) >> void
        and: "an instance of the use case"
        CreateProduct createProduct = new CreateProduct(dataSource, output)

        when: "the create method is called"
        createProduct.create(product)

        then: "the database is tasked to store the product"
        1 * dataSource.store(product)
        and: "the output is informed and no failure notification is send"
        1 * output.created(product)
        0 * output.foundDuplicate(_)
        0 * output.failNotification(_)
    }

    def "Create informs the output that an entry matching the provided product already exists"() {
        given: "a data source that detects a duplicate entry"
        dataSource.store(product) >> {throw new ProductExistsException(productId)}
        and: "an instance of the use case"
        CreateProduct createProduct = new CreateProduct(dataSource, output)

        when: "the create method is called"
        createProduct.create(product)

        then: "the database is tasked to store the product"
        1 * dataSource.store(product)
        and: "the output is informed and no failure notification is send"
        0 * output.created(_)
        1 * output.foundDuplicate(product)
        0 * output.failNotification(_)
    }

    def "Create sends a failure notification in case technical errors occur at the data source"() {
        given: "a data source that stores a product"
        dataSource.store(product) >> {throw new DatabaseQueryException("This is a test")}
        and: "an instance of the use case"
        CreateProduct createProduct = new CreateProduct(dataSource, output)

        when: "the create method is called"
        createProduct.create(product)

        then: "the database is tasked to store the product"
        1 * dataSource.store(product)
        and: "the output is send a failure notification"
        0 * output.created(_)
        0 * output.foundDuplicate(_)
        1 * output.failNotification(_ as String)
    }
}
