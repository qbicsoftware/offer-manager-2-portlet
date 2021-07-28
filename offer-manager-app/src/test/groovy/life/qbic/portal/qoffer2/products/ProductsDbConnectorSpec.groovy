package life.qbic.portal.qoffer2.products

import groovy.sql.GroovyRowResult
import life.qbic.business.exceptions.DatabaseQueryException
import life.qbic.datamodel.dtos.business.ProductId
import life.qbic.datamodel.dtos.business.facilities.Facility
import life.qbic.datamodel.dtos.business.services.AtomicProduct
import life.qbic.datamodel.dtos.business.services.PrimaryAnalysis
import life.qbic.datamodel.dtos.business.services.Product
import life.qbic.datamodel.dtos.business.services.ProductUnit
import life.qbic.portal.offermanager.dataresources.database.ConnectionProvider
import life.qbic.portal.offermanager.dataresources.products.ProductsDbConnector
import org.apache.groovy.sql.extensions.SqlExtensions
import spock.lang.Specification

import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.ResultSet
import java.sql.SQLException

/**
 * Tests for the correct behaviour of the {@link ProductsDbConnector}
 *
 * @since 1.0.0
 */
class ProductsDbConnectorSpec extends Specification {

  def "If the passed provider is null, raise a NullPointerException"() {
    given:
    ConnectionProvider provider = null

    when:
    new ProductsDbConnector(provider)

    then:
    thrown(NullPointerException)
  }

  def "If the datasource select query for products fails, throw a DataBaseQueryException"() {
    given:
    PreparedStatement statement = Mock(PreparedStatement)
    statement.executeQuery() >> { throw new SQLException() }
    Connection connection = Stub(Connection, {it.prepareStatement(_ as String) >> statement})
    ConnectionProvider provider = Stub(ConnectionProvider, {it.connect() >> connection})

    when:
    def connector = new ProductsDbConnector(provider)
    connector.listProducts()

    then:
    thrown(DatabaseQueryException)
  }

  def "If the datasource select query for products is successful, a list of products returns"() {
    given: "some expected query results"
    GroovyMock(SqlExtensions, global: true)
    SqlExtensions.toRowResult(_ as ResultSet) >> new GroovyRowResult(
        ["id"       :id, "category":category, "description":description, "productName": productName,
         "internalUnitPrice": internalUnitPrice, "externalUnitPrice": externalUnitPrice, "unit": unit, "productId": productId, "serviceProvider": serviceProvider])
    ResultSet resultSet = Stub(ResultSet, {
      it.next() >>> [true, false]
    })
    PreparedStatement statement = Stub(PreparedStatement, {
      it.executeQuery() >> resultSet
    })

    and: "a valid connection"
    Connection connection = Stub(Connection, {it.prepareStatement(_ as String) >> statement})
    ConnectionProvider provider = Stub(ConnectionProvider, {it.connect() >> connection})
    def connector = new ProductsDbConnector(provider)

    when: "the query is executed"
    List<Product> result = connector.listProducts()

    then:
    result.size() == 1
    result.get(0) instanceof AtomicProduct
    result.get(0).description == "Sample QC with report"

    where: "available products information is as follows"
    id | category | description | productName | internalUnitPrice |externalUnitPrice | unit | productId | serviceProvider
    0 | "Primary Bioinformatics" | "Sample QC with report" | "Sample QC" | 49.99 | 49.99 | "Sample" | "DS_1" | "QBIC"
  }

  def "Returns correct id for a given product"() {
    given: "some expected query results"
    String query = "SELECT id FROM product "+
            "WHERE category = ? AND description = ? AND productName = ? AND internalUnitPrice = ? AND externalUnitPrice = ? AND unit = ? AND serviceProvider = ?"

    and: "a connection returning the id of the product if it was found"
    GroovyMock(SqlExtensions, global: true)
    SqlExtensions.toRowResult(_ as ResultSet) >> new GroovyRowResult(["id":id])

    PreparedStatement preparedStatement = Mock (PreparedStatement, {
      it.setString(1, category) >> _
      it.setString(2, description) >> _
      it.setString(3, productName) >> _
      it.setDouble(4, internalUnitPrice) >> _
      it.setDouble(5, externalUnitPrice) >> _
      it.setString(6, unit.value) >> _
      it.setString(7, serviceProvider.name()) >> _

      it.executeQuery() >> Stub(ResultSet,{it.next() >>> [true, false]})
    })
    // the connection must only provide precompiled statements for the expected query template
    Connection connection = Stub( Connection, {
      it.prepareStatement(query) >> preparedStatement
    })

    //and: "a ConnectionProvider providing the stubbed connection"
    ConnectionProvider connectionProvider = Stub (ConnectionProvider, {it.connect() >> connection})

    //and: "an implementation of the SearchCustomerDataSource with this connection provider"
    ProductsDbConnector dataSource = new ProductsDbConnector(connectionProvider)

    when:
    int resultId = dataSource.findProductId(new PrimaryAnalysis(productName,description,internalUnitPrice, externalUnitPrice, unit, identifier, serviceProvider))

    then:
    resultId == id

    where: "available products information is as follows"
    id | category | description | productName | internalUnitPrice | externalUnitPrice | unit | identifier |serviceProvider
    0 | "Primary Bioinformatics" | "Sample QC with report" | "Sample QC" | 49.99 | 49.99 | ProductUnit.PER_SAMPLE | 1 | Facility.QBIC

  }

  def "Fetch(life.qbic.datamodel.dtos.business.ProductId) ignores rows with incomplete or uninterpretable information"() {
    given: "some expected query results"
    GroovyMock(SqlExtensions, global: true)
    SqlExtensions.toRowResult(_ as ResultSet) >> new GroovyRowResult(
            ["id"       :id, "category":category, "description":description, "productName": productName,
             "internalUnitPrice": internalUnitPrice, "externalUnitPrice": externalUnitPrice, "unit": unit, "productId": productId, "serviceProvider": serviceProvider])

    and: "a result set containing only 6 rows"
    ResultSet resultSet = Stub(ResultSet, {
      it.next() >>> [true, false]
    })
    PreparedStatement statement = Stub(PreparedStatement, {
      it.executeQuery() >> resultSet
    })

    and: "a valid connection"
    Connection connection = Stub(Connection, {it.prepareStatement(_ as String) >> statement})
    ConnectionProvider provider = Stub(ConnectionProvider, {it.connect() >> connection})
    def connector = new ProductsDbConnector(provider)

    when: "the query is executed"
    Optional<Product> result = connector.fetch(new ProductId("DS", "1"))

    then:
    ! result.isPresent()

    where: "available products information is as follows"
    id | category | description | productName | internalUnitPrice | externalUnitPrice | unit | productId | serviceProvider
    0 | "Unknown category" | "Sample QC with report" | "Sample QC" | 49.99 | 49.99 | "Sample" | "DS_1" | "QBIC"
    1 | "Primary Bioinformatics" | null | "Sample QC with report" | 49.99 | 49.99 | "Sample" | "DS_1" | "QBIC"
    2 | "Primary Bioinformatics" | "Sample QC with report" | null | 49.99 | 49.99 | "Sample" | "DS_1" | "QBIC"
    4 | "Primary Bioinformatics" | "Sample QC with report" | "Sample QC" |  49.99 |49.99 | "Unknown Unit" | "DS_1" | "QBIC"
    5 | "Primary Bioinformatics" | "Sample QC with report" | "Sample QC" | 49.99 | 49.99 | "Sample" | "This is some random string. Lorem ipsum" | "QBIC"
  }
}
