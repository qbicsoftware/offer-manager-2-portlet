package life.qbic.portal.qoffer2.products

import life.qbic.datamodel.accounting.Product
import life.qbic.portal.qoffer2.database.ConnectionProvider
import spock.lang.Specification

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


}
