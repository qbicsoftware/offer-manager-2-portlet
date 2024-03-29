package life.qbic.business.products.create

import life.qbic.business.RefactorConverter
import life.qbic.business.exceptions.DatabaseQueryException
import life.qbic.business.products.Product
import life.qbic.business.products.ProductCategory
import life.qbic.business.products.ProductDraft
import spock.lang.Specification

/**
 * <h1>Tests for the {@link CreateProduct} use case</h1>
 *
 * <p>This specification contains tests for all steps of the {@link CreateProduct} use case</p>
 *
 * @since 1.0.0
 */
class CreateProductSpec extends Specification {

    CreateProductOutput output = Mock(CreateProductOutput)
    ProductCategory category = ProductCategory.forLabel("Sequencing")
    ProductDraft productDraft = ProductDraft.create(category, "test product", "this is a test product", 0.5, 0.5, "Gigabyte", "CEGAT")
    Product product = new RefactorConverter().toProduct(productDraft)

    def "Create stores the provided product in the data source"() {
        given: "a data source that stores a product"
        CreateProductDataSource dataSource = Stub(CreateProductDataSource)
        dataSource.store(productDraft) >> { product }

        and: "an instance of the use case"
        CreateProduct createProduct = new CreateProduct(dataSource, output)

        when: "the create method is called"
        createProduct.create(productDraft)

        then: "the output is informed and no failure notification is send"
        1 * output.created(product)
        0 * output.foundDuplicates(_)
        0 * output.failNotification(_)
    }

    def "Create sends a failure notification if the datasource returns null"() {
        given: "a data source that stores a product"
        CreateProductDataSource dataSource = Stub(CreateProductDataSource)
        dataSource.store(productDraft) >> { null }

        and: "an instance of the use case"
        CreateProduct createProduct = new CreateProduct(dataSource, output)

        when: "the create method is called"
        createProduct.create(productDraft)

        then: "the output is informed and no failure notification is send"
        0 * output.created(_)
        0 * output.foundDuplicates(_)
        1 * output.failNotification(_)
    }

    def "Create informs the output that multiple entries matching the provided product already exists"() {
        given: "a data source that detects multiple duplicate entries"
        CreateProductDataSource dataSource = Stub(CreateProductDataSource)
        String dataStatus = ""
        dataSource.findDuplicateProducts(productDraft) >> {
            dataStatus = "not stored"
            [product, product, product]
        }

        and: "an instance of the use case"
        CreateProduct createProduct = new CreateProduct(dataSource, output)

        when: "the create method is called"
        createProduct.create(productDraft)

        then: "the output is informed and no failure notification is send"
        1 * output.foundDuplicates(_)
        0 * output.created(_)
        0 * output.failNotification(_)
        and: "the data was not stored in the database"
        dataStatus == "not stored"
    }

    def "Create sends a failure notification in case technical errors occur at the data source"() {
        given: "a data source that stores a product"
        CreateProductDataSource dataSource = Stub(CreateProductDataSource)
        String dataStatus = ""
        dataSource.store(productDraft) >> {
            dataStatus = "stored"
            throw new DatabaseQueryException("This is a test")
        }

        and: "an instance of the use case"
        CreateProduct createProduct = new CreateProduct(dataSource, output)

        when: "the create method is called"
        createProduct.create(productDraft)

        then: "the output is send a failure notification"
        0 * output.created(_)
        0 * output.foundDuplicates(_)
        1 * output.failNotification(_ as String)
        and: "the data was stored"
        dataStatus == "stored"
    }

}
