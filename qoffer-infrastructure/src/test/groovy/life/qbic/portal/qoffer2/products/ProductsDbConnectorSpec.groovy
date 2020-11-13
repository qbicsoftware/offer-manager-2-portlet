package life.qbic.portal.qoffer2.products

import life.qbic.datamodel.accounting.Product
import life.qbic.portal.portlet.exceptions.DatabaseQueryException
import life.qbic.portal.qoffer2.database.ConnectionProvider
import spock.lang.Specification

import java.sql.Connection
import java.sql.PreparedStatement
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

  def "If the datasource select query for packages fails, throw a DataBaseQueryException"() {
    given:
    PreparedStatement statement = Mock(PreparedStatement)
    statement.executeQuery() >> { throw new SQLException() }
    Connection connection = Stub(Connection, {it.prepareStatement(_ as String) >> statement})
    ConnectionProvider provider = Stub(ConnectionProvider, {it.connect() >> connection})

    when:
    def connector = new ProductsDbConnector(provider)
    connector.findAllAvailablePackages()

    then:
    thrown(DatabaseQueryException)

  }


}
