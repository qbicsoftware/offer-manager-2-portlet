package life.qbic.portal.qoffer2.products

import groovy.sql.GroovyRowResult
import life.qbic.datamodel.dtos.business.services.AtomicProduct
import life.qbic.datamodel.dtos.business.services.Product
import life.qbic.portal.portlet.exceptions.DatabaseQueryException
import life.qbic.portal.qoffer2.database.ConnectionProvider
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
    connector.findAllAvailableProducts()

    then:
    thrown(DatabaseQueryException)
  }

  def "If the datasource select query for products is successful, a list of products returns"() {
    given: "some expected query results"
    GroovyMock(SqlExtensions, global: true)
    SqlExtensions.toRowResult(_ as ResultSet) >> new GroovyRowResult(
        ["id":id, "category":category, "description":description, "productName": productName,
         "unitPrice": unitPrice, "unit": unit])
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
    List<Product> result = connector.findAllAvailableProducts()

    then:
    result.size() == 1
    result.get(0) instanceof AtomicProduct
    result.get(0).description == "Sample QC with report"



    where: "available products information is as follows"
    id | category | description | productName | unitPrice | unit
    0 | "Primary Bioinformatics" | "Sample QC with report" | "Sample QC" | 49.99 | "Sample"

  }


}
