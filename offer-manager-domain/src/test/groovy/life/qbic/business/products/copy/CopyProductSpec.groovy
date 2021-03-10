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
import spock.lang.Specification

/**
 * <h1>Archive Product tests</h1>
 *
 * <p>This Specification contains tests for the use ArchiveProduct use case</p>
 *
 * @since 1.0.0
 */
class CopyProductSpec extends Specification {

    CreateProductInput createProductInput
    CopyProductDataSource dataSource
    CopyProductOutput output
    CopyProduct useCase

    void setup() {
        createProductInput = Stub(CreateProductInput)
        dataSource = Stub(CopyProductDataSource)
        output = Mock(CopyProductOutput)
        useCase = new CopyProduct(dataSource, output, createProductInput)
    }

    def "FailNotification forwards received messages to the output"() {
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
